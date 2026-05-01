import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { forkJoin } from 'rxjs';

@Component({
  standalone: true,
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  imports: [CommonModule]
})
export class DashboardComponent implements OnInit {
  flightsCount = 0;
  gatesCount = 0;
  passengersCount = 0;
  baggageCount = 0;
  recentFlights: any[] = [];
  recentNotifications: any[] = [];
  loading = true;

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    forkJoin({
      flights: this.api.get<any[]>('/flights'),
      gates: this.api.get<any[]>('/gates/available'),
      passengers: this.api.get<any[]>('/passengers'),
      baggage: this.api.get<any[]>('/baggage-reports'),
      notifications: this.api.get<any[]>('/notifications')
    }).subscribe({
      next: ({ flights, gates, passengers, baggage, notifications }) => {
        this.flightsCount = flights.length;
        this.gatesCount = gates.length;
        this.passengersCount = passengers.length;
        this.baggageCount = baggage.length;
        this.recentFlights = flights.slice(0, 5);
        this.recentNotifications = notifications.filter((n: any) => !n.read).slice(0, 5);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  statusBadge(status: string): string {
    const map: Record<string, string> = {
      SCHEDULED: 'badge-blue',
      BOARDING: 'badge-amber',
      DEPARTED: 'badge-purple',
      ARRIVED: 'badge-green',
      DELAYED: 'badge-amber',
      CANCELLED: 'badge-red',
      REPORTED: 'badge-amber',
      IN_REVIEW: 'badge-blue',
      RESOLVED: 'badge-green',
      CLOSED: 'badge-gray',
      DELAY: 'badge-amber',
      GATE_CHANGE: 'badge-blue',
      CANCELLATION: 'badge-red',
      GENERAL: 'badge-gray'
    };
    return `badge ${map[status] || 'badge-gray'}`;
  }

  formatDate(dt: string): string {
    if (!dt) return '—';
    return new Date(dt).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' });
  }
}
