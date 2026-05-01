import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent),
    data: { title: 'Panel de Control' }
  },
  {
    path: 'flights',
    loadComponent: () =>
      import('./pages/flights/flights.component').then(m => m.FlightsComponent),
    data: { title: 'Gestión de Vuelos' }
  },
  {
    path: 'gates',
    loadComponent: () =>
      import('./pages/gates/gates.component').then(m => m.GatesComponent),
    data: { title: 'Gestión de Puertas' }
  },
  {
    path: 'passengers',
    loadComponent: () =>
      import('./pages/passengers/passengers.component').then(m => m.PassengersComponent),
    data: { title: 'Gestión de Pasajeros' }
  },
  {
    path: 'baggage',
    loadComponent: () =>
      import('./pages/baggage/baggage.component').then(m => m.BaggageComponent),
    data: { title: 'Reportes de Equipaje' }
  },
  {
    path: 'notifications',
    loadComponent: () =>
      import('./pages/notifications/notifications.component').then(m => m.NotificationsComponent),
    data: { title: 'Notificaciones' }
  },
  { path: '**', redirectTo: 'dashboard' }
];
