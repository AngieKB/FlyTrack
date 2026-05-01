import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AppComponent } from '../../app.component';

@Component({
  standalone: true,
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  imports: [CommonModule, FormsModule]
})
export class NotificationsComponent implements OnInit {
  notifications: any[] = [];
  loading = true;
  notifModalOpen = false;

  message = '';
  type = 'DELAY';
  flightId: number | null = null;

  private app = inject(AppComponent);

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    this.loading = true;
    this.api.get<any[]>('/notifications').subscribe({
      next: (data) => { this.notifications = data; this.loading = false; },
      error: (err) => { this.app.showToast('Error cargando notificaciones: ' + err.message, 'error'); this.loading = false; }
    });
  }

  saveNotification() {
    const body = {
      message: this.message,
      type: this.type,
      flightId: this.flightId || null,
      read: false
    };

    this.api.post('/notifications', body).subscribe({
      next: () => {
        this.app.showToast('Notificación creada', 'success');
        this.closeModal();
        this.loadNotifications();
      },
      error: (err) => this.app.showToast('Error: ' + err.message, 'error')
    });
  }

  markRead(id: number) {
    this.api.patch('/notifications/' + id + '/read').subscribe({
      next: () => { this.app.showToast('Marcado como leído', 'success'); this.loadNotifications(); },
      error: (err) => this.app.showToast('Error: ' + err.message, 'error')
    });
  }

  deleteNotif(id: number) {
    if (!confirm('¿Eliminar esta notificación?')) return;
    this.api.delete('/notifications/' + id).subscribe({
      next: () => { this.app.showToast('Notificación eliminada', 'success'); this.loadNotifications(); },
      error: (err) => this.app.showToast('Error: ' + err.message, 'error')
    });
  }

  openModal() {
    this.message = '';
    this.type = 'DELAY';
    this.flightId = null;
    this.notifModalOpen = true;
  }

  closeModal() {
    this.notifModalOpen = false;
  }

  statusBadge(type: string): string {
    const map: Record<string, string> = {
      DELAY: 'badge-amber', GATE_CHANGE: 'badge-blue', CANCELLATION: 'badge-red',
      BOARDING: 'badge-purple', GENERAL: 'badge-gray'
    };
    return `badge ${map[type] || 'badge-gray'}`;
  }

  formatDate(dt: string): string {
    if (!dt) return '—';
    return new Date(dt).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' });
  }
}
