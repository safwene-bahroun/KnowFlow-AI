import { Component, inject } from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { RouterLink } from '@angular/router';

import { CommonModule } from '@angular/common';

import { AuthService } from '../../Services/auth-service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,

  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  styleUrl: './forgot-password.css',
  templateUrl: './forgot-password.html'
})
export class ForgotPassword {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  loading = false;

  errorMessage = '';
  successMessage = '';

  forgotForm = this.fb.nonNullable.group({

    email: [
      '',
      [
        Validators.required,
        Validators.email
      ]
    ]

  });


  forgotPassword(): void {

    if (this.forgotForm.invalid) {

      this.forgotForm.markAllAsTouched();

      return;
    }

    this.loading = true;

    this.errorMessage = '';
    this.successMessage = '';

    this.authService
      .forgotPassword(
        this.forgotForm.getRawValue()
      )
      .subscribe({

        next: () => {

          this.loading = false;

          this.successMessage =
            'If this email exists, a password reset link has been sent.';

        },

        error: (error) => {

          console.error(error);

          this.loading = false;

          this.errorMessage =
            'Unable to process your request.';

        }

      });
  }
}