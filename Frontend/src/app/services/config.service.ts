import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of } from 'rxjs';
import { environment } from '../environment';

export interface Config {
  lambdaKey: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private configUrl = environment.apiUrl + '/config'; // Adjust this if your endpoint has a different base path

  constructor(private http: HttpClient) { }

  getConfig(): Observable<Config> {
    return this.http.get<{lambdaKey: string}>(this.configUrl).pipe(
      map(response => ({
        lambdaKey: response.lambdaKey
      })),
      catchError(error => {
        console.error('Failed to load configuration:', error);
        // Return a default config or rethrow the error as needed
        return of({
          lambdaKey: ''
        });
      })
    );
  }
}