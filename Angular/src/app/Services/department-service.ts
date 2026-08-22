import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Department } from '../dashboards/admin/Models/Departments';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    'http://localhost:3000/api/admin/departments';

  // CREATE
  create(department: Department): Observable<Department> {
    return this.http.post<Department>(
      this.apiUrl,
      department
    );
  }

  // GET ALL
  getAll(): Observable<Department[]> {
    return this.http.get<Department[]>(
      this.apiUrl
    );
  }

  // GET BY ID
  getById(id: number): Observable<Department> {
    return this.http.get<Department>(
      `${this.apiUrl}/${id}`
    );
  }

  // GET BY NAME
  getByName(name: string): Observable<Department> {
    return this.http.get<Department>(
      `${this.apiUrl}/name/${encodeURIComponent(name)}`
    );
  }

  // SEARCH
  search(keyword: string): Observable<Department[]> {

    const params = new HttpParams()
      .set('keyword', keyword);

    return this.http.get<Department[]>(
      `${this.apiUrl}/search`,
      { params }
    );
  }

  // UPDATE
  update(
    id: number,
    department: Department
  ): Observable<Department> {

    return this.http.put<Department>(
      `${this.apiUrl}/${id}`,
      department
    );
  }

  // DELETE
  delete(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}