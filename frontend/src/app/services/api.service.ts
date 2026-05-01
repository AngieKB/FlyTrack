import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  get<T>(path: string): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}`);
  }

  post<T>(path: string, body: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, body, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  put<T>(path: string, body: any): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${path}`, body, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  patch<T>(path: string, body?: any): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}${path}`, body ?? {}, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  delete(path: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}${path}`);
  }
}
