import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container">
      @for (t of toast.toasts(); track t.id) {
        <div class="toast anim-slide-up" [class]="t.type">
          <span class="toast-icon">{{ icon(t.type) }}</span>
          <span class="toast-msg">{{ t.message }}</span>
          <button class="btn-icon" (click)="toast.dismiss(t.id)" aria-label="Dismiss">×</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      bottom: 24px;
      right: 24px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 8px;
      pointer-events: none;
      max-width: 340px;
    }
    .toast {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 12px 14px;
      font-size: 13px;
      color: var(--text);
      box-shadow: var(--shadow);
      display: flex;
      align-items: center;
      gap: 10px;
      pointer-events: auto;

      &.success { border-left: 3px solid var(--success); }
      &.error   { border-left: 3px solid var(--danger);  }
      &.info    { border-left: 3px solid var(--accent);  }
    }
    .toast-icon { font-size: 14px; flex-shrink: 0; }
    .toast-msg  { flex: 1; line-height: 1.4; }
  `],
})
export class ToastComponent {
  constructor(public toast: ToastService) {}

  icon(type: string): string {
    return type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
  }
}
