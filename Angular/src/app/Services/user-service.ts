import { Injectable, inject } from '@angular/core';
import {
  HttpClient,
  HttpParams
} from '@angular/common/http';

import { Observable } from 'rxjs';
import { UserResponse } from './auth-service';

// =========================================================
// ROLE
// =========================================================

export type Role =
  | 'ADMIN'
  | 'MANAGER'
  | 'EMPLOYEE';

// =========================================================
// EMPLOYEE PROFILE
// =========================================================

export type EmployeeProfile =
  | 'INTERN'
  | 'JUNIOR'
  | 'SENIOR'
  | 'TECH_LEAD'
  | 'PROJECT_MANAGER'
  | 'DEPARTMENT_MANAGER'
  | 'OTHER';

// =========================================================
// DEPARTMENT
// =========================================================

export interface Department {

  id?: number;

  name: string;
}

// =========================================================
// USER
// =========================================================

export interface User {

  id?: number;

  name: string;

  familyName: string;

  email: string;

  password?: string;

  cin: string;

  age?: number;

  gender?: string;

  phoneNumber?: string;

  address?: string;

  urlImage?: string;

  role: Role;

  employeeProfile?: EmployeeProfile;

  department?: Department;
}

export interface ProfileRequest {
  name: string;
  familyName: string;
  email: string;
  phoneNumber: string;
  address: string;
  urlImage?: string;
}

// =========================================================
// USER SERVICE
// =========================================================

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private http = inject(HttpClient);

  // =======================================================
  // API URL
  // =======================================================

  private apiUrl =
    'http://localhost:3000/api/admin/users';

  private profileUrl = 'http://localhost:3000/api/profile';

  getProfile(): Observable<UserResponse> {
    return this.http.get<UserResponse>(this.profileUrl);
  }

  updateProfile(profile: ProfileRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(this.profileUrl, profile);
  }

  // =======================================================
  // CREATE USER
  // POST /api/admin/users
  // =======================================================

  create(user: User): Observable<User> {

    return this.http.post<User>(
      this.apiUrl,
      user
    );
  }

  // =======================================================
  // GET ALL USERS
  // GET /api/admin/users
  // =======================================================

  getAll(): Observable<User[]> {

    return this.http.get<User[]>(
      this.apiUrl
    );
  }

  // =======================================================
  // GET USER BY ID
  // GET /api/admin/users/{id}
  // =======================================================

  getById(id: number): Observable<User> {

    return this.http.get<User>(
      `${this.apiUrl}/${id}`
    );
  }

  // =======================================================
  // GET USER BY EMAIL
  // GET /api/admin/users/email/{email}
  // =======================================================

  getByEmail(email: string): Observable<User> {

    return this.http.get<User>(
      `${this.apiUrl}/email/${encodeURIComponent(email)}`
    );
  }
getAllDepartments() {
  return this.http.get<{ id: number; name: string }[]>('/api/departments');
}

createWithImage(formData: FormData) {
  return this.http.post<User>(`${this.apiUrl}`, formData);
}

updateWithImage(id: number, formData: FormData) {
  return this.http.put<User>(`${this.apiUrl}/${id}`, formData);
}
  // =======================================================
  // GET USER BY CIN
  // GET /api/admin/users/cin/{cin}
  // =======================================================

  getByCin(cin: string): Observable<User> {

    return this.http.get<User>(
      `${this.apiUrl}/cin/${encodeURIComponent(cin)}`
    );
  }

  // =======================================================
  // SEARCH USERS
  // GET /api/admin/users/search?keyword=...
  // =======================================================

  search(keyword: string): Observable<User[]> {

    const params = new HttpParams()
      .set('keyword', keyword);

    return this.http.get<User[]>(
      `${this.apiUrl}/search`,
      {
        params
      }
    );
  }

  // =======================================================
  // GET USERS BY NAME
  // GET /api/admin/users/name/{name}
  // =======================================================

  getByName(name: string): Observable<User[]> {

    return this.http.get<User[]>(
      `${this.apiUrl}/name/${encodeURIComponent(name)}`
    );
  }

  // =======================================================
  // GET USERS BY FAMILY NAME
  // GET /api/admin/users/family-name/{familyName}
  // =======================================================

  getByFamilyName(
    familyName: string
  ): Observable<User[]> {

    return this.http.get<User[]>(
      `${this.apiUrl}/family-name/${encodeURIComponent(familyName)}`
    );
  }

  // =======================================================
  // GET USERS BY ROLE
  // GET /api/admin/users/role/{role}
  // =======================================================

  getByRole(
    role: Role
  ): Observable<User[]> {

    return this.http.get<User[]>(
      `${this.apiUrl}/role/${role}`
    );
  }

  // =======================================================
  // GET USERS BY DEPARTMENT
  // GET /api/admin/users/department/{departmentName}
  // =======================================================

  getByDepartment(
    departmentName: string
  ): Observable<User[]> {

    return this.http.get<User[]>(
      `${this.apiUrl}/department/${encodeURIComponent(departmentName)}`
    );
  }

  // =======================================================
  // UPDATE USER
  // PUT /api/admin/users/{id}
  // =======================================================

  update(
    id: number,
    user: User
  ): Observable<User> {

    return this.http.put<User>(
      `${this.apiUrl}/${id}`,
      user
    );
  }

  // =======================================================
  // UPDATE PASSWORD
  // PUT /api/admin/users/{id}/password
  // =======================================================

  updatePassword(
    id: number,
    newPassword: string
  ): Observable<User> {

    return this.http.put<User>(
      `${this.apiUrl}/${id}/password`,
      newPassword,
      {
        headers: {
          'Content-Type': 'text/plain'
        }
      }
    );
  }

  // =======================================================
  // DELETE USER
  // DELETE /api/admin/users/{id}
  // =======================================================

  delete(id: number): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }

  // =======================================================
  // CHECK USER EXISTS
  // GET /api/admin/users/{id}/exists
  // =======================================================

  exists(id: number): Observable<boolean> {

    return this.http.get<boolean>(
      `${this.apiUrl}/${id}/exists`
    );
  }
}