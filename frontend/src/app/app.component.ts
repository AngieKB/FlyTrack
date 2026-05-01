import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { ToastService, Toast } from './services/toast.service';

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

  constructor(
    private router: Router,
    private toastService: ToastService   // ← inyectamos el servicio
  ) {}

  ngOnInit() {
    this.updateClock();
    this.clockInterval = setInterval(() => this.updateClock(), 1000);

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.updateTitleFromRoute(event.urlAfterRedirects);
      });

    // Suscripción a los toasts del servicio
    this.toastService.toasts$.subscribe(toasts => {
      this.toasts = toasts;
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

  // El método showToast se elimina; ya no se usa directamente
}
