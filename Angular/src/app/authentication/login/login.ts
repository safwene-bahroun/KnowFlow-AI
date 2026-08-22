
import { Component, inject } from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import {Router, RouterLink } from '@angular/router';

import { CommonModule } from '@angular/common';

import { AuthService } from '../../Services/auth-service';



@Component({
  selector: 'app-login',

  standalone: true,

  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
    styleUrl: './login.css',
  templateUrl: './login.html'
})


export class Login {

  private fb = inject(FormBuilder);

  private authService = inject(AuthService);

  private router = inject(Router);

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      return;
    }

    const role = this.authService.getRole();

    switch (role) {
      case 'ADMIN':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'MANAGER':
        this.router.navigate(['/manager/dashboard']);
        break;
      case 'EMPLOYEE':
        this.router.navigate(['/employee/dashboard']);
        break;
      default:
        this.authService.logout();
        break;
    }
  }

  loading = false;

  errorMessage = '';


  // ==========================================
  // LOGIN FORM
  // ==========================================

  loginForm = this.fb.nonNullable.group({

    email: [
      '',
      [
        Validators.required,
        Validators.email
      ]
    ],

    password: [
      '',
      [
        Validators.required
      ]
    ]

  });


  // ==========================================
  // LOGIN
  // ==========================================

  login(): void {

    // ----------------------------------------
    // VALIDATE FORM
    // ----------------------------------------

    if (this.loginForm.invalid) {

      this.loginForm.markAllAsTouched();

      return;
    }


    this.loading = true;

    this.errorMessage = '';


    // ========================================
    // CALL BACKEND
    // ========================================

    this.authService
      .login(this.loginForm.getRawValue())
      .subscribe({

        // ====================================
        // SUCCESS
        // ====================================

        next: (response) => {

          console.log('Login successful');

          console.log(
            'User:',
            response.user
          );


          this.loading = false;


          // ==================================
          // GET ROLE
          // ==================================

          const role =
            this.authService.getRole() ?? response.user.role;


          console.log(
            'User role:',
            role
          );


          // ==================================
          // REDIRECT BY ROLE
          // ==================================

          switch (role) {

            case 'ADMIN':

              this.router.navigate([
                '/admin/dashboard'
              ]);

              break;


            case 'MANAGER':

              this.router.navigate([
                '/manager/dashboard'
              ]);

              break;


            case 'EMPLOYEE':

              this.router.navigate([
                '/employee/dashboard'
              ]);

              break;


            default:

              console.error(
                'Unknown user role:',
                role
              );

              this.errorMessage =
                'Your account has an invalid role.';

              break;
          }

        },


        // ====================================
        // ERROR
        // ====================================

        error: (error) => {

          console.error(
            'Login error:',
            error
          );


          this.loading = false;


          if (error.status === 401) {

            this.errorMessage =
              'Invalid email or password.';

          }

          else if (error.status === 403) {

            this.errorMessage =
              'You are not authorized to access the system.';

          }

          else {

            this.errorMessage =
              'Unable to connect to the server.';

          }

        }

      });

  }

}