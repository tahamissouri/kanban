import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BoardService } from '../../core/services/board.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../shared/components/toast/toast.service';
import { Board } from '../../core/models/board.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  boards  = signal<Board[]>([]);
  loading = signal(true);

  showCreateModal  = signal(false);
  newBoardName     = '';
  createLoading    = signal(false);

  constructor(
    private boardSvc: BoardService,
    public  auth:     AuthService,
    private router:   Router,
    private toast:    ToastService,
  ) {}

  ngOnInit(): void { this.loadBoards(); }

  loadBoards(): void {
    this.loading.set(true);
    this.boardSvc.getAll().subscribe({
      next:  b => { this.boards.set(b); this.loading.set(false); },
      error: e => { this.toast.error(e.error?.message || 'Could not load boards'); this.loading.set(false); },
    });
  }

  openBoard(board: Board): void {
    this.router.navigate(['/board', board.id]);
  }

  openCreateModal(): void {
    this.newBoardName = '';
    this.showCreateModal.set(true);
  }

  submitCreate(): void {
    const name = this.newBoardName.trim();
    if (!name) return;
    this.createLoading.set(true);
    this.boardSvc.create({ name }).subscribe({
      next: () => {
        this.toast.success('Board created');
        this.showCreateModal.set(false);
        this.createLoading.set(false);
        this.loadBoards();
      },
      error: e => {
        this.toast.error(e.error?.message || 'Create failed');
        this.createLoading.set(false);
      },
    });
  }

  deleteBoard(board: Board, event: Event): void {
    event.stopPropagation();
    if (!confirm(`Delete board "${board.name}"? This cannot be undone.`)) return;
    this.boardSvc.delete(board.id).subscribe({
      next:  () => { this.toast.success('Board deleted'); this.loadBoards(); },
      error: e => this.toast.error(e.error?.message || 'Delete failed'),
    });
  }

  isOwner(board: Board): boolean {
    return board.owner.id === this.auth.currentUserId();
  }
}
