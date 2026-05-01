import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AppComponent } from '../../app.component'; // Para usar el toast
import { inject } from '@angular/core';

@Component({
  standalone: true,
  selector: 'app-flights',
  templateUrl: './flights.component.html',
  imports: [CommonModule, FormsModule]
})
export class FlightsComponent implements OnInit {
  flights: any[] = [];
  searchTerm = '';
  loading = true;
  flightModalOpen = false;
  modalTitle = 'Nuevo Vuelo';
  editingFlight: any = {};

  // Form fields
  flightNumber = '';
  airline = '';
  origin = '';
  destination = '';
  departureTime = '';
  arrivalTime = '';
  status = 'SCHEDULED';
  gateId: number | null = null;

  private app = inject(AppComponent);

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadFlights();
  }

  get filteredFlights() {
    if (!this.searchTerm) return this.flights;
    const term = this.searchTerm.toLowerCase();
    return this.flights.filter((f: any) =>
      Object.values(f).some(val => String(val).toLowerCase().includes(term))
    );
  }

  loadFlights() {
    this.loading = true;
    this.api.get<any[]>('/flights').subscribe({
      next: (data) => {
        this.flights = data;
        this.loading = false;
      },
      error: (err) => {
        this.app.showToast('Error cargando vuelos: ' + err.message, 'error');
        this.loading = false;
      }
    });
  }

  openModal(flight?: any) {
    if (flight) {
      this.editingFlight = flight;
      this.modalTitle = 'Editar Vuelo';
      this.flightNumber = flight.flightNumber || '';
      this.airline = flight.airline || '';
      this.origin = flight.origin || '';
      this.destination = flight.destination || '';
      this.departureTime = flight.departureTime ? flight.departureTime.slice(0, 16) : '';
      this.arrivalTime = flight.arrivalTime ? flight.arrivalTime.slice(0, 16) : '';
      this.status = flight.status || 'SCHEDULED';
      this.gateId = flight.gateId || null;
    } else {
      this.resetForm();
      this.modalTitle = 'Nuevo Vuelo';
    }
    this.flightModalOpen = true;
  }

  closeModal() {
    this.flightModalOpen = false;
    this.resetForm();
  }

  resetForm() {
    this.editingFlight = {};
    this.flightNumber = '';
    this.airline = '';
    this.origin = '';
    this.destination = '';
    this.departureTime = '';
    this.arrivalTime = '';
    this.status = 'SCHEDULED';
    this.gateId = null;
  }

  saveFlight() {
    const body = {
      flightNumber: this.flightNumber,
      airline: this.airline,
      origin: this.origin,
      destination: this.destination,
      departureTime: this.departureTime,
      arrivalTime: this.arrivalTime,
      status: this.status,
      gateId: this.gateId || null
    };

    const request = this.editingFlight.id
      ? this.api.put('/flights/' + this.editingFlight.id, body)
      : this.api.post('/flights', body);

    request.subscribe({
      next: () => {
        this.app.showToast(this.editingFlight.id ? 'Vuelo actualizado' : 'Vuelo creado', 'success');
        this.closeModal();
        this.loadFlights();
      },
      error: (err) => this.app.showToast('Error: ' + err.message, 'error')
    });
  }

  deleteFlight(id: number) {
    if (!confirm('¿Eliminar este vuelo?')) return;
    this.api.delete('/flights/' + id).subscribe({
      next: () => {
        this.app.showToast('Vuelo eliminado', 'success');
        this.loadFlights();
      },
      error: (err) => this.app.showToast('Error: ' + err.message, 'error')
    });
  }

  statusBadge(status: string): string {
    const map: Record<string, string> = {
      SCHEDULED: 'badge-blue', BOARDING: 'badge-amber', DEPARTED: 'badge-purple',
      ARRIVED: 'badge-green', DELAYED: 'badge-amber', CANCELLED: 'badge-red'
    };
    return `badge ${map[status] || 'badge-gray'}`;
  }

  formatDate(dt: string): string {
    if (!dt) return '—';
    return new Date(dt).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' });
  }
}
