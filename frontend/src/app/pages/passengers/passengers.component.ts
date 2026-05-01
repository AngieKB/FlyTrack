import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { ToastService } from '../../services/toast.service';  // ← importación corregida

@Component({
  standalone: true,
  selector: 'app-passengers',
  templateUrl: './passengers.component.html',
  imports: [CommonModule, FormsModule]
})
export class PassengersComponent implements OnInit {
  passengers: any[] = [];
  searchTerm = '';
  loading = true;
  passengerModalOpen = false;
  modalTitle = 'Nuevo Pasajero';
  editingPassenger: any = {};

  firstName = '';
  lastName = '';
  email = '';
  documentNumber = '';
  seatNumber = '';
  flightId: number | null = null;

  private toast = inject(ToastService);  // ← cambia AppComponent por ToastService

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadPassengers();
  }

  get filteredPassengers() {
    if (!this.searchTerm) return this.passengers;
    const term = this.searchTerm.toLowerCase();
    return this.passengers.filter((p: any) =>
      Object.values(p).some(val => String(val).toLowerCase().includes(term))
    );
  }

  loadPassengers() {
    this.loading = true;
    this.api.get<any[]>('/passengers').subscribe({
      next: (data) => { this.passengers = data; this.loading = false; },
      error: (err) => {
        this.toast.showToast('Error cargando pasajeros: ' + err.message, 'error');
        this.loading = false;
      }
    });
  }

  openModal(passenger?: any) {
    if (passenger) {
      this.editingPassenger = passenger;
      this.modalTitle = 'Editar Pasajero';
      this.firstName = passenger.firstName || '';
      this.lastName = passenger.lastName || '';
      this.email = passenger.email || '';
      this.documentNumber = passenger.documentNumber || '';
      this.seatNumber = passenger.seatNumber || '';
      this.flightId = passenger.flightId || null;
    } else {
      this.resetForm();
      this.modalTitle = 'Nuevo Pasajero';
    }
    this.passengerModalOpen = true;
  }

  closeModal() {
    this.passengerModalOpen = false;
    this.resetForm();
  }

  resetForm() {
    this.editingPassenger = {};
    this.firstName = '';
    this.lastName = '';
    this.email = '';
    this.documentNumber = '';
    this.seatNumber = '';
    this.flightId = null;
  }

  savePassenger() {
    const body = {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      documentNumber: this.documentNumber,
      seatNumber: this.seatNumber,
      flightId: this.flightId || null
    };

    const request = this.editingPassenger.id
      ? this.api.put('/passengers/' + this.editingPassenger.id, body)
      : this.api.post('/passengers', body);

    request.subscribe({
      next: () => {
        this.toast.showToast(this.editingPassenger.id ? 'Pasajero actualizado' : 'Pasajero registrado', 'success');
        this.closeModal();
        this.loadPassengers();
      },
      error: (err) => this.toast.showToast('Error: ' + err.message, 'error')
    });
  }

  deletePassenger(id: number) {
    if (!confirm('¿Eliminar este pasajero?')) return;
    this.api.delete('/passengers/' + id).subscribe({
      next: () => {
        this.toast.showToast('Pasajero eliminado', 'success');
        this.loadPassengers();
      },
      error: (err) => this.toast.showToast('Error: ' + err.message, 'error')
    });
  }
}
