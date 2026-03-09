import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import {
  CdkDragDrop,
  DragDropModule,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';

import { BoardService }  from '../../core/services/board.service';
import { ColumnService } from '../../core/services/column.service';
import { CardService }   from '../../core/services/card.service';
import { AuthService }   from '../../core/services/auth.service';
import { ToastService }  from '../../shared/components/toast/toast.service';

import { Board }                          from '../../core/models/board.model';
import { Column }                         from '../../core/models/column.model';
import { Card }                           from '../../core/models/card.model';

@Component({
  selector: 'app-board',
  standalone: true,
  imports: [CommonModule, FormsModule, DragDropModule],
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss'],
})
export class BoardComponent implements OnInit {
  // ── State ──────────────────────────────────────────────────────────
  board   = signal<Board | null>(null);
  columns = signal<Column[]>([]);
  cards   = signal<Map<number, Card[]>>(new Map());
  loading = signal(true);

  // ── Add column inline ──────────────────────────────────────────────
  addingColumn = signal(false);
  newColName   = '';

  // ── Add card inline (null = none open, colId = open) ─────────────
  addingCardColId = signal<number | null>(null);
  newCardTitle    = '';

  // ── Card detail / edit modal ───────────────────────────────────────
  editCard  = signal<Card | null>(null);
  editColId = signal<number | null>(null);
  editTitle = '';
  editDesc  = '';

  // ── Members modal ──────────────────────────────────────────────────
  showMembers  = signal(false);
  newMemberId  = '';
  memberError  = signal<string | null>(null);

  constructor(
    private route:    ActivatedRoute,
    private router:   Router,
    public  auth:     AuthService,
    private boardSvc: BoardService,
    private colSvc:   ColumnService,
    private cardSvc:  CardService,
    private toast:    ToastService,
  ) {}

  // ─────────────────────────────────────────────────────────────────
  get boardId(): number {
    return Number(this.route.snapshot.paramMap.get('id'));
  }

  ngOnInit(): void { this.load(); }

  // ── Loading ───────────────────────────────────────────────────────
  load(): void {
    this.loading.set(true);
    this.boardSvc.getAll().subscribe({
      next: allBoards => {
        const b = allBoards.find(x => x.id === this.boardId);
        if (!b) { this.router.navigate(['/']); return; }
        this.board.set(b);
        this.loadColumns();
      },
      error: () => this.router.navigate(['/']),
    });
  }

  loadColumns(): void {
    this.colSvc.getAll(this.boardId).subscribe({
      next: cols => {
        const sorted = [...cols].sort((a, b) => a.position - b.position);
        this.columns.set(sorted);
        if (sorted.length === 0) { this.loading.set(false); return; }

        forkJoin(sorted.map(c => this.cardSvc.getAll(this.boardId, c.id))).subscribe({
          next: allCards => {
            const map = new Map<number, Card[]>();
            sorted.forEach((col, i) =>
              map.set(col.id, [...allCards[i]].sort((a, b) => a.position - b.position))
            );
            this.cards.set(map);
            this.loading.set(false);
          },
          error: () => this.loading.set(false),
        });
      },
      error: () => this.loading.set(false),
    });
  }

  // ── Helpers ────────────────────────────────────────────────────────
  getCards(colId: number): Card[] {
    return this.cards().get(colId) ?? [];
  }

  getColumnDropIds(): string[] {
    return this.columns().map(c => 'col-' + c.id);
  }

  isOwner(): boolean {
    return this.board()?.owner.id === this.auth.currentUserId();
  }

  // ── Drag & drop ───────────────────────────────────────────────────
  onCardDrop(event: CdkDragDrop<Card[]>, destCol: Column): void {
    const map = new Map(this.cards());
    const srcColId = Number(event.previousContainer.id.replace('col-', ''));
    const destColId = destCol.id;

    if (event.previousContainer === event.container) {
      const colCards = [...(map.get(destColId) ?? [])];

      moveItemInArray(colCards, event.previousIndex, event.currentIndex);
      colCards.forEach((c, i) => (c.position = i));

      map.set(destColId, colCards);
      this.cards.set(map);

      this._persistCards(colCards, destColId);
      return;
    }

    const srcCards = [...(map.get(srcColId) ?? [])];
    const destCards = [...(map.get(destColId) ?? [])];

    transferArrayItem(srcCards, destCards, event.previousIndex, event.currentIndex);

    srcCards.forEach((c, i) => (c.position = i));
    destCards.forEach((c, i) => (c.position = i));

    map.set(srcColId, srcCards);
    map.set(destColId, destCards);
    this.cards.set(map);

    this._persistCards(srcCards, srcColId);
    this._persistCards(destCards, destColId);
  }

  private _persistCards(cards: Card[], colId: number): void {
    cards.forEach(card => {
      this.cardSvc
          .update(this.boardId, colId, card.id, {
            title: card.title,
            description: card.description ?? '',
            position: card.position,
            columnId: colId,
          })
          .subscribe({
            error: () => this.toast.error(`Could not save position for "${card.title}"`)
          });
    });
  }

  // ── Columns ────────────────────────────────────────────────────────
  submitAddColumn(): void {
    const name = this.newColName.trim();
    if (!name) return;
    const pos = this.columns().length;
    this.colSvc.create(this.boardId, { name, position: pos }).subscribe({
      next: () => {
        this.newColName = '';
        this.addingColumn.set(false);
        this.loadColumns();
        this.toast.success('Column added');
      },
      error: e => this.toast.error(e.error?.message || 'Failed to add column'),
    });
  }

  renameColumn(col: Column, el: EventTarget | null): void {
    const newName = (el as HTMLElement)?.textContent?.trim() ?? '';
    if (!newName || newName === col.name) return;
    this.colSvc.update(this.boardId, col.id, { name: newName, position: col.position }).subscribe({
      next: () => { col.name = newName; this.toast.success('Column renamed'); },
      error: e => { this.toast.error(e.error?.message || 'Rename failed'); },
    });
  }

  deleteColumn(col: Column): void {
    if (!confirm(`Delete column "${col.name}" and all its cards?`)) return;
    this.colSvc.delete(this.boardId, col.id).subscribe({
      next:  () => { this.toast.success('Column deleted'); this.loadColumns(); },
      error: e => this.toast.error(e.error?.message || 'Delete failed'),
    });
  }

  // ── Cards ──────────────────────────────────────────────────────────
  submitAddCard(col: Column): void {
    const title = this.newCardTitle.trim();
    if (!title) return;
    const pos = this.getCards(col.id).length;
    this.cardSvc.create(this.boardId, col.id, { title, description: '', position: pos ,columnId:col.id}).subscribe({
      next: () => {
        this.newCardTitle = '';
        this.addingCardColId.set(null);
        this.loadColumns();
        this.toast.success('Card added');
      },
      error: e => this.toast.error(e.error?.message || 'Failed to add card'),
    });
  }

  openCardDetail(card: Card, colId: number): void {
    this.editCard.set({ ...card });
    this.editColId.set(colId);
    this.editTitle = card.title;
    this.editDesc  = card.description ?? '';
  }

  saveCard(): void {
    const card  = this.editCard();
    const colId = this.editColId();
    if (!card || !colId || !this.editTitle.trim()) return;
    this.cardSvc
      .update(this.boardId, colId, card.id, {
        title: this.editTitle.trim(),
        description: this.editDesc,
        position: card.position,
        columnId: colId
      })
      .subscribe({
        next: () => { this.toast.success('Card saved'); this.editCard.set(null); this.loadColumns(); },
        error: e => this.toast.error(e.error?.message || 'Save failed'),
      });
  }

  deleteCard(card: Card, colId: number): void {
    if (!confirm('Delete this card?')) return;
    this.cardSvc.delete(this.boardId, colId, card.id).subscribe({
      next:  () => { this.toast.success('Card deleted'); this.editCard.set(null); this.loadColumns(); },
      error: e => this.toast.error(e.error?.message || 'Delete failed'),
    });
  }

  // ── Members ────────────────────────────────────────────────────────
  submitAddMember(): void {
    const uid = Number(this.newMemberId);
    if (!uid) { this.memberError.set('Enter a valid user ID'); return; }
    this.memberError.set(null);
    this.boardSvc.addMember(this.boardId, uid).subscribe({
      next: () => {
        this.toast.success('Member added');
        this.newMemberId = '';
        this.load();
      },
      error: e => this.memberError.set(e.error?.message || 'Failed to add member'),
    });
  }
}
