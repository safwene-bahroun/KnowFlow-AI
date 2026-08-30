import {
  Injectable,
  inject,
  PLATFORM_ID
} from '@angular/core';

import {
  isPlatformBrowser
} from '@angular/common';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable,
  tap
} from 'rxjs';


// ============================
// REGISTER REQUEST
// ============================

export interface RegisterRequest {
  name: string;
  familyName: string;
  email: string;
  password: string;
  cin: string;

  phoneNumber?: string;
  urlImage?: string;
  address?: string;

  age: number;
  gender: string;

  employeeProfile: string;

  departmentId?: number;
}


// ============================
// LOGIN REQUEST
// ============================

export interface LoginRequest {
  email: string;
  password: string;
}


// ============================
// FORGOT PASSWORD REQUEST
// ============================

export interface ForgotPasswordRequest {
  email: string;
}


// ============================
// RESET PASSWORD REQUEST
// ============================

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;  // ✅ correspond au backend
}

// ============================
// AUTH RESPONSE
// ============================

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  user: UserResponse;
}


// ============================
// USER RESPONSE
// ============================

export interface UserResponse {
  id: number;
  name: string;
  familyName: string;
  email: string;
  cin?: string;
  phoneNumber?: string;
  urlImage?: string;
  address?: string;
  age?: number;
  role: string;
  employeeProfile: string;
  gender: string;
  departmentId?: number;
  departmentName?: string;
}


// ============================
// DEPARTMENT OPTION
// ============================

export interface DepartmentOption {
  id: number;
  name: string;
}


// ============================
// REGISTRATION OPTIONS
// ============================

export interface RegistrationOptions {

  genders: string[];

  employeeProfiles: string[];

  departments: DepartmentOption[];
}


// ============================
// AUTH SERVICE
// ============================

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private http = inject(HttpClient);

  private platformId = inject(PLATFORM_ID);


  // ============================
  // API URL
  // ============================

  private readonly API_URL =
    'http://localhost:3000/api/auth';


  // ============================
  // LOCAL STORAGE KEYS
  // ============================

  private readonly TOKEN_KEY =
    'knowflow_token';

  private readonly USER_KEY =
    'knowflow_user';


  // ============================
  // CHECK IF BROWSER
  // ============================

  private isBrowser(): boolean {

    return isPlatformBrowser(
      this.platformId
    );

  }


  // ============================
  // REGISTER
  // ============================

  register(
    request: RegisterRequest
  ): Observable<AuthResponse> {

    return this.http.post<AuthResponse>(
      `${this.API_URL}/register`,
      request
    );

  }


  // ============================
  // LOGIN
  // ============================

  login(
    request: LoginRequest
  ): Observable<AuthResponse> {

    return this.http
      .post<AuthResponse>(
        `${this.API_URL}/login`,
        request
      )
      .pipe(

        tap(response => {

          // localStorage exists only
          // in the browser

          if (!this.isBrowser()) {
            return;
          }

          const normalizedRole = this.normalizeRole(response.user?.role);
          const normalizedUser = {
            ...response.user,
            role: normalizedRole ?? response.user?.role ?? 'EMPLOYEE'
          };

          // Save JWT token

          localStorage.setItem(
            this.TOKEN_KEY,
            response.accessToken
          );


          // Save current user

          localStorage.setItem(
            this.USER_KEY,
            JSON.stringify(normalizedUser)
          );

        })

      );

  }


  // ============================
  // FORGOT PASSWORD
  // ============================

  forgotPassword(
    request: ForgotPasswordRequest
  ): Observable<any> {

    return this.http.post(
      `${this.API_URL}/forgot-password`,
      request
    );

  }


  // ============================
  // RESET PASSWORD
  // ============================

  resetPassword(
    request: ResetPasswordRequest
  ): Observable<any> {

    return this.http.post(
      `${this.API_URL}/reset-password`,
      request
    );

  }


  // ============================
  // GET TOKEN
  // ============================

  getToken(): string | null {

    // During SSR there is no localStorage

    if (!this.isBrowser()) {
      return null;
    }


    return localStorage.getItem(
      this.TOKEN_KEY
    );

  }


  // ============================
  // GET CURRENT USER
  // ============================

  getCurrentUser(): UserResponse | null {

    // During SSR there is no localStorage

    if (!this.isBrowser()) {
      return null;
    }


    const user =
      localStorage.getItem(
        this.USER_KEY
      );


    if (!user) {
      return null;
    }


    try {

      const parsedUser = JSON.parse(user) as UserResponse;
      if (parsedUser?.role) {
        parsedUser.role = this.normalizeRole(parsedUser.role) ?? parsedUser.role;
      }

      return parsedUser;

    } catch (error) {

      console.error(
        'Invalid user data in localStorage:',
        error
      );

      return null;
    }

  }

  updateStoredUser(user: UserResponse): void {
    if (this.isBrowser()) {
      localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    }
  }


  // ============================
  // IS AUTHENTICATED
  // ============================

  isAuthenticated(): boolean {

    return !!this.getToken();

  }


  // ============================
  // GET CURRENT USER ROLE
  // ============================

  normalizeRole(value: string | null | undefined): string | null {
    if (!value) {
      return null;
    }

    const role = value.trim();
    const normalized = role.toUpperCase();
    const compact = normalized.replace(/[^A-Z]/g, '');

    if (compact.includes('ADMIN')) {
      return 'ADMIN';
    }

    if (compact.includes('MANAGER')) {
      return 'MANAGER';
    }

    if (compact.includes('EMPLOYEE')) {
      return 'EMPLOYEE';
    }

    return role;
  }

  getRole(): string | null {

    const user =
      this.getCurrentUser();

    if (!user) {
      return null;
    }

    return this.normalizeRole(user.role);

  }


  // ============================
  // CHECK ADMIN
  // ============================

  isAdmin(): boolean {

    return this.getRole() === 'ADMIN';

  }


  // ============================
  // CHECK MANAGER
  // ============================

  isManager(): boolean {

    return this.getRole() === 'MANAGER';

  }


  // ============================
  // CHECK EMPLOYEE
  // ============================

  isEmployee(): boolean {

    return this.getRole() === 'EMPLOYEE';

  }


  // ============================
  // LOGOUT
  // ============================

  logout(): void {

    if (!this.isBrowser()) {
      return;
    }


    // Remove JWT

    localStorage.removeItem(
      this.TOKEN_KEY
    );


    // Remove user

    localStorage.removeItem(
      this.USER_KEY
    );

  }


  // ============================
  // REGISTRATION OPTIONS
  // ============================

  getRegistrationOptions():
    Observable<RegistrationOptions> {

    return this.http.get<RegistrationOptions>(
      `${this.API_URL}/registration-options`
    );

  }

}