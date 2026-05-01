import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { ToastService } from '../../services/toast.service';  // ← importación corregida

@Component({
  standalone: true,
  selector: 'app-gates',
  templateUrl: './gates.component.html',
  imports: [CommonModule, FormsModule]
})
export class GatesComponent implements OnInit {
  gates: any[] = [];
  loading = true;
  gateModalOpen = false;
  modalTitle = 'Nueva Puerta';
  editingGate: any = {};

  gateCode = '';
  terminal = '';
  available = true;

  private toast = inject(ToastService);  // ← cambia AppComponent por ToastService

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadGates();
  }

  loadGates() {
    this.loading = true;
    this.api.get<any[]>('/gates').subscribe({
      next: (data) => { this.gates = data; this.loading = false; },
      error: (err) => {
        this.toast.showToast('Error cargando puertas: ' + err.message, 'error');
        this.loading = false;
      }
    });
  }

  openModal(gate?: any) {
    if (gate) {
      this.editingGate = gate;
      this.modalTitle = 'Editar Puerta';
      this.gateCode = gate.gateCode || '';
      this.terminal = gate.terminal || '';
      this.available = gate.available;
    } else {
      this.resetForm();
      this.modalTitle = 'Nueva Puerta';
    }
    this.gateModalOpen = true;
  }

  closeModal() {
    this.gateModalOpen = false;
    this.resetForm();
  }

  resetForm() {
    this.editingGate = {};
    this.gateCode = '';
    this.terminal = '';
    this.available = true;
  }

  saveGate() {
    const body = {
      gateCode: this.gateCode,
      terminal: this.terminal,
      available: this.available
    };

    const request = this.editingGate.id
      ? this.api.put('/gates/' + this.editingGate.id, body)
      : this.api.post('/gates', body);

    request.subscribe({
      next: () => {
        this.toast.showToast(this.editingGate.id ? 'Puerta actualizada' : 'Puerta creada', 'success');
        this.closeModal();
        this.loadGates();
      },
      error: (err) => this.toast.showToast('Error: ' + err.message, 'error')
    });
  }

  deleteGate(id: number) {
    if (!confirm('¿Eliminar esta puerta?')) return;
    this.api.delete('/gates/' + id).subscribe({
      next: () => {
        this.toast.showToast('Puerta eliminada', 'success');
        this.loadGates();
      },
      error: (err) => this.toast.showToast('Error: ' + err.message, 'error')
    });
  }
}
