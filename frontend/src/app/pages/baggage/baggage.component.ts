import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AppComponent } from '../../app.component';

@Component({
  standalone: true,
  selector: 'app-baggage',
  templateUrl: './baggage.component.html',
  imports: [CommonModule, FormsModule]
})
export class BaggageComponent implements OnInit {
  baggageReports: any[] = [];
  loading = true;
  baggageModalOpen = false;

  description = '';
  baggageTag = '';
  status = 'REPORTED';
  passengerId: number | null = null;

  private app = inject(AppComponent);

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadBaggage();
  }

  loadBaggage() {
    this.loading = true;
    this.api.get<any[]>('/baggage-reports').subscribe({
      next: (data) => { this.baggageReports = data; this.loading = false; },
      error: (err) => { this.app.showToast('Error cargando reportes: ' + err.message, 'error'); this.loading = false; }
    });
  }

  saveBaggage() {
    const body = {
      description: this.description,
      baggageTag: this.baggageTag,
      status: this.status,
      passengerId: this.passengerId || null
    };

    this.api.post('/baggage-reports', body).subscribe({
      next: () => {
        this.app.showToast('Reporte creado', 'success');
        this.closeModal();
        this.loadBaggage();
      },
      error: (err) => this.app.showToast('Error: ' + err.message, 'error')
    });
  }

  updateStatus(id: number, status: string) {
    this.api.patch(`/baggage-reports/${id}/status/${status}`).subscribe({
      next: () => { this.app.showToast('Estado actualizado', 'success'); this.loadBaggage(); },
      error: (err) => this.app.showToast('Error: ' + err.message, 'error')
    });
  }

  deleteBaggage(id: number) {
    if (!confirm('¿Eliminar este reporte?')) return;
    this.api.delete('/baggage-reports/' + id).subscribe({
      next: () => { this.app.showToast('Reporte eliminado', 'success'); this.loadBaggage(); },
      error: (err) => this.app.showToast('Error: ' + err.message, 'error')
    });
  }

  openModal() {
    this.description = '';
    this.baggageTag = '';
    this.status = 'REPORTED';
    this.passengerId = null;
    this.baggageModalOpen = true;
  }

  closeModal() {
    this.baggageModalOpen = false;
  }

  statusBadge(status: string): string {
    const map: Record<string, string> = {
      REPORTED: 'badge-amber', IN_REVIEW: 'badge-blue', RESOLVED: 'badge-green', CLOSED: 'badge-gray'
    };
    return `badge ${map[status] || 'badge-gray'}`;
  }

  formatDate(dt: string): string {
    if (!dt) return '—';
    return new Date(dt).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' });
  }
}
