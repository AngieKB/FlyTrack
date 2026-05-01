import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { ToastService } from '../../services/toast.service';  // ← importación corregida

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

  private toast = inject(ToastService);  // ← inyecta ToastService

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    this.loadBaggage();
  }

  loadBaggage() {
    this.loading = true;
    this.api.get<any[]>('/baggage-reports').subscribe({
      next: (data) => {
        this.baggageReports = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.toast.showToast('Error cargando reportes: ' + err.message, 'error'); // ← corregido
        this.loading = false;
      }
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
        this.toast.showToast('Reporte creado', 'success');   // ← corregido
        this.closeModal();
        this.loadBaggage();
      },
      error: (err) => this.toast.showToast('Error: ' + err.message, 'error')  // ← corregido
    });
  }

  updateStatus(id: number, status: string) {
    this.api.patch(`/baggage-reports/${id}/status/${status}`).subscribe({
      next: () => {
        this.toast.showToast('Estado actualizado', 'success');
        this.loadBaggage();
      },
      error: (err) => this.toast.showToast('Error: ' + err.message, 'error')
    });
  }

  deleteBaggage(id: number) {
    if (!confirm('¿Eliminar este reporte?')) return;
    this.api.delete('/baggage-reports/' + id).subscribe({
      next: () => {
        this.toast.showToast('Reporte eliminado', 'success');
        this.loadBaggage();
      },
      error: (err) => this.toast.showToast('Error: ' + err.message, 'error')
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
    this.cdr.detectChanges();
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
