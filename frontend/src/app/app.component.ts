import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

interface Toast {
  message: string;
  type: 'success' | 'error';
}

@Component({
  standalone: true,
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [CommonModule, RouterModule]
})
export class AppComponent implements OnInit, OnDestroy {
  pageTitle = 'Panel de <span>Control</span>';
  clock = '';
  toasts: Toast[] = [];
  private clockInterval: any;

  constructor(private router: Router) {}

  ngOnInit() {
    this.updateClock();
    this.clockInterval = setInterval(() => this.updateClock(), 1000);

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.updateTitleFromRoute(event.urlAfterRedirects);
      });
  }

  ngOnDestroy() {
    clearInterval(this.clockInterval);
  }

  private updateClock() {
    this.clock = new Date().toLocaleTimeString('es-CO');
  }

  private updateTitleFromRoute(url: string) {
    const segment = url.split('/')[1] || 'dashboard';
    const titles: Record<string, string> = {
      dashboard: 'Panel de <span>Control</span>',
      flights: 'Gestión de <span>Vuelos</span>',
      gates: 'Gestión de <span>Puertas</span>',
      passengers: 'Gestión de <span>Pasajeros</span>',
      baggage: 'Reportes de <span>Equipaje</span>',
      notifications: '<span>Notificaciones</span>'
    };
    this.pageTitle = titles[segment] || segment;
  }

  showToast(message: string, type: 'success' | 'error') {
    const toast: Toast = { message, type };
    this.toasts.push(toast);
    setTimeout(() => {
      this.toasts = this.toasts.filter(t => t !== toast);
    }, 3500);
  }
}
