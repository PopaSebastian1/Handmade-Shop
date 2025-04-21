import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../google-auth/environment';
import { IRole } from '../../models/role.interface';
import { Role } from '../../models/role.model';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private baseUrl = `${environment.apiUrl}/roles`;

  constructor(private http: HttpClient) { }

  createRole(role: Role): Observable<Role> {
    return this.http.post<IRole>(this.baseUrl, role.toDTO()).pipe(
      map(response => Role.fromDTO(response))
    );
  }

  getRoleById(id: number): Observable<Role> {
    return this.http.get<IRole>(`${this.baseUrl}/${id}`).pipe(
      map(response => Role.fromDTO(response))
    );
  }

  getAllRoles(): Observable<Role[]> {
    return this.http.get<IRole[]>(this.baseUrl).pipe(
      map(roles => roles.map(role => Role.fromDTO(role)))
    );
  }

  updateRole(id: number, role: Role): Observable<Role> {
    return this.http.put<IRole>(`${this.baseUrl}/${id}`, role.toDTO()).pipe(
      map(response => Role.fromDTO(response))
    );
  }

  deleteRole(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}