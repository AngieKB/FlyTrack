import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
  message: string;
  type: 'success' | 'error';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toastsSubject = new BehaviorSubject<Toast[]>([]);
  toasts$ = this.toastsSubject.asObservable();

  showToast(message: string, type: 'success' | 'error') {
    const toast: Toast = { message, type };
    this.toastsSubject.next([...this.toastsSubject.getValue(), toast]);
    setTimeout(() => {
      const current = this.toastsSubject.getValue().filter(t => t !== toast);
      this.toastsSubject.next(current);
    }, 3500);
  }
}
