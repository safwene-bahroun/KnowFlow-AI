import {
  Component,
  OnInit,
  inject
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import { CommonModule } from '@angular/common';

import { AuthService } from '../../Services/auth-service';


// =====================================================
// DEPARTMENT
// =====================================================

export interface DepartmentOption {
  id: number;
  name: string;
}

// =====================================================
// REGISTRATION OPTIONS
// =====================================================

export interface RegistrationOptions {
  genders: string[];
  employeeProfiles: string[];
  departments: DepartmentOption[];
}

// =====================================================
// REGISTRATION COMPONENT
// =====================================================

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  styleUrl: './registration.css',
  templateUrl: './registration.html'
})

export class Registration implements OnInit {


  // ===================================================
  // SERVICES
  // ===================================================

  private fb = inject(FormBuilder);

  private authService = inject(AuthService);

  private router = inject(Router);


  // ===================================================
  // STATES
  // ===================================================

  loading = false;

  loadingOptions = false;

  errorMessage = '';

  successMessage = '';


  // ==========================================
  // STEP PAGINATION
  // ==========================================

  currentStep = 1;


  // ==========================================
  // IMAGE PREVIEW
  // ==========================================

  imagePreview: string | null = null;


  // ===================================================
  // OPTIONS FROM BACKEND
  // ===================================================

  genders: string[] = [];

  employeeProfiles: string[] = [];

  departments: DepartmentOption[] = [];


  // ===================================================
  // REGISTRATION FORM
  // ===================================================

  registerForm = this.fb.nonNullable.group({

    name: [
      '',
      [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        Validators.pattern(/^[\p{L} .'-]+$/u)
      ]
    ],

    familyName: [
      '',
      [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        Validators.pattern(/^[\p{L} .'-]+$/u)
      ]
    ],

    email: [
      '',
      [
        Validators.required,
        Validators.email,
        Validators.maxLength(150)
      ]
    ],

    password: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(100),
        Validators.pattern(
          /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).+$/
        )
      ]
    ],

    cin: [
      '',
      [
        Validators.required,
        Validators.pattern(/^\d{8}$/)
      ]
    ],

    phoneNumber: [
      '',
      [
        Validators.pattern(/^[+]?[0-9]{8,15}$/)
      ]
    ],

    urlImage: [
      ''
    ],

    address: [
      '',
      [
        Validators.maxLength(255)
      ]
    ],

    age: [
      18,
      [
        Validators.required,
        Validators.min(18),
        Validators.max(100)
      ]
    ],

    gender: [
      '',
      [
        Validators.required
      ]
    ],

    employeeProfile: [
      '',
      [
        Validators.required
      ]
    ],

    departmentName: [
      '',
      [
        Validators.required
      ]
    ]

  });


  // ===================================================
  // COMPONENT INITIALIZATION
  // ===================================================

  ngOnInit(): void {
    this.loadRegistrationOptions();
  }


  // ===================================================
  // LOAD REGISTRATION OPTIONS
  // ===================================================

  loadRegistrationOptions(): void {

    this.loadingOptions = true;

    this.errorMessage = '';


    this.authService
      .getRegistrationOptions()
      .subscribe({

        next: (options: RegistrationOptions) => {

          this.genders = options.genders;
          this.employeeProfiles = options.employeeProfiles;
          this.departments = options.departments;

          this.loadingOptions = false;

        },

        error: (error) => {

          console.error(
            'Failed to load registration options:',
            error
          );

          this.loadingOptions = false;

          this.errorMessage =
            'Unable to load registration options.';

        }

      });

  }


  // ==========================================
  // STEP 1 VALIDATION CHECK
  // ==========================================

  isStep1Valid(): boolean {

    const controls = [
      'name',
      'familyName',
      'email',
      'cin',
      'age',
      'gender'
    ];

    return controls.every(key => {

      const control = this.registerForm.get(key);

      return control && control.valid;

    });

  }


  // ==========================================
  // STEP NAVIGATION
  // ==========================================

  nextStep(): void {

    // Mark step 1 fields as touched to show errors
    const step1Fields = [
      'name',
      'familyName',
      'email',
      'cin',
      'age',
      'gender'
    ];

    step1Fields.forEach(key => {

      this.registerForm.get(key)?.markAsTouched();

    });


    if (this.isStep1Valid()) {

      this.currentStep = 2;

    }

  }

  prevStep(): void {

    this.currentStep = 1;

  }


  // ==========================================
  // FILE HANDLING
  // ==========================================

  onFileSelected(event: Event): void {

    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {

      return;

    }

    const file: File = input.files[0];


    // ----------------------------------------
    // SIZE CHECK (5MB max)
    // ----------------------------------------

    if (file.size > 5 * 1024 * 1024) {

      this.errorMessage = 'Image must be smaller than 5 MB.';

      input.value = '';

      return;

    }


    // ----------------------------------------
    // READ AS BASE64 DATA URL
    // ----------------------------------------

    const reader = new FileReader();

    reader.onload = () => {

      const dataUrl = reader.result as string;

      this.imagePreview = dataUrl;

      this.registerForm.patchValue({
        urlImage: dataUrl
      });

      this.errorMessage = '';

    };

    reader.onerror = () => {

      this.errorMessage = 'Failed to read the image file.';

    };

    reader.readAsDataURL(file);

  }

  removeImage(event: Event): void {

    event.stopPropagation();

    this.imagePreview = null;

    this.registerForm.patchValue({
      urlImage: ''
    });

  }


  // ===================================================
  // REGISTER
  // ===================================================

  register(): void {

    if (this.registerForm.invalid) {

      this.registerForm.markAllAsTouched();

      return;

    }


    this.loading = true;

    this.errorMessage = '';

    this.successMessage = '';


    const request = this.registerForm.getRawValue();


    this.authService
      .register(request)
      .subscribe({

        next: () => {

          this.loading = false;

          this.successMessage =
            'Account created successfully.';

          setTimeout(() => {

            this.router.navigate(['/login']);

          }, 1500);

        },

        error: (error) => {

          console.error('Registration error:', error);

          this.loading = false;

          if (error.status === 400) {

            this.errorMessage =
              error.error?.message ||
              'Invalid registration data.';

          } else if (error.status === 409) {

            this.errorMessage =
              error.error?.message ||
              'Email or CIN already exists.';

          } else {

            this.errorMessage =
              'Unable to create the account.';

          }

        }

      });

  }

}