import {
  Component,
  inject
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  ActivatedRoute,
  Router
} from '@angular/router';

import { CommonModule } from '@angular/common';

import { AuthService } from '../../Services/auth-service';

@Component({
  selector: 'app-reset-password',
  standalone: true,

  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  styleUrl: './reset-password.css',
  templateUrl: './reset-password.html'
})
export class ResetPassword{

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  token = '';

  loading = false;

  errorMessage = '';
  successMessage = '';

  resetForm = this.fb.nonNullable.group({

    password: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(
          /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).+$/
        )
      ]
    ],

    confirmPassword: [
      '',
      Validators.required
    ]

  });


  constructor() {

    this.route.queryParams.subscribe(params => {

      this.token = params['token'] || '';

    });

  }


  resetPassword(): void {

    if (this.resetForm.invalid) {

      this.resetForm.markAllAsTouched();

      return;
    }

    const {
      password,
      confirmPassword
    } = this.resetForm.getRawValue();


    if (password !== confirmPassword) {

      this.errorMessage =
        'Passwords do not match.';

      return;
    }


    if (!this.token) {

      this.errorMessage =
        'Invalid or missing reset token.';

      return;
    }


    this.loading = true;

    this.errorMessage = '';
    this.successMessage = '';


    this.authService
      .resetPassword({

        token: this.token,

        password: password

      })
      .subscribe({

        next: () => {

          this.loading = false;

          this.successMessage =
            'Password reset successfully.';

          setTimeout(() => {

            this.router.navigate(['/login']);

          }, 1500);

        },

        error: (error) => {

          console.error(error);

          this.loading = false;

          this.errorMessage =
            error.error?.message ||
            'Invalid or expired reset token.';

        }

      });
  }
}