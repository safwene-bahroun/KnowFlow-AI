import {
  Component,
  ChangeDetectorRef,
  OnInit,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  UserService,
  type User as ApiUser,
  type Role,
  type EmployeeProfile,
  type Department as ApiDepartment
} from '../../../Services/user-service';

type UserRecord = ApiUser & {
  enabled?: boolean;
  createdAt?: string | null;
  modifiedAt?: string | null;
  department?: ApiDepartment & { id?: number };
  imageUrl?: string | null;
  photo?: string | null;
  profileImage?: string | null;
  departmentName?: string | null;
};

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class Users implements OnInit {

  private userService = inject(UserService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private changeDetector = inject(ChangeDetectorRef);

  mode: 'list' | 'add' | 'edit' = 'list';
  userId?: number;

  users: UserRecord[] = [];
  departments: { id: number; name: string }[] = [];

  loading = false;
  loadingUser = false;
  errorMessage = '';
  searchKeyword = '';
  selectedRole = '';

  imageFile: File | null = null;
  imagePreview: string | null = null;
  imageBase64: string | null = null;
  isDragging = false;
  passwordVisible = false;
  confirmPasswordVisible = false;

  userForm = this.fb.group({
    name: ['', Validators.required],
    familyName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: [''],
    confirmPassword: [''],
    cin: [''],
    phoneNumber: [''],
    address: [''],
    age: [null as number | null],
    gender: [''],
    role: ['EMPLOYEE' as Role, Validators.required],
    employeeProfile: [''],
    departmentId: [null as number | null],
    enabled: [true],
    createdAt: [{ value: null as string | null, disabled: true }],
    modifiedAt: [{ value: null as string | null, disabled: true }]
  });

  togglePasswordVisibility(field: 'password' | 'confirmPassword'): void {
    if (field === 'password') this.passwordVisible = !this.passwordVisible;
    else this.confirmPasswordVisible = !this.confirmPasswordVisible;
  }

  ngOnInit(): void {
    const routeMode = this.route.snapshot.data['mode'] as 'list' | 'add' | 'edit' | undefined;
    const path = this.route.snapshot.url.map(segment => segment.path);

    this.mode = routeMode || (path[0] === 'add' ? 'add' : path[0] === 'edit' ? 'edit' : 'list');

    if (this.mode === 'list') {
      this.loadUsers();
      return;
    }

    if (this.mode === 'add') {
      this.prepareAddForm();
      this.loadDepartments();
      return;
    }

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.userId = Number(id);
      this.loadUser(this.userId);
    }
    this.loadDepartments();
  }

  loadDepartments(): void {
    this.userService.getAllDepartments().subscribe({
      next: (deps) => {
        this.departments = deps || [];
        this.changeDetector.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load departments from database', err);
      }
    });
  }

  getUserImage(user: UserRecord): string | null {
    const image = user?.urlImage ?? user?.imageUrl ?? user?.photo ?? user?.profileImage ?? null;
    if (!image || !image.trim()) return null;
    if (image.startsWith('data:image')) return image;
    return /^https?:\/\//i.test(image)
      ? image
      : `http://localhost:3000${image.startsWith('/') ? '' : '/'}${image}`;
  }

  onImageError(event: Event): void {
    const image = event.target as HTMLImageElement;
    image.style.display = 'none';
    image.nextElementSibling?.removeAttribute('hidden');
  }

  getDepartmentName(user: UserRecord): string {
    return user?.department?.name ?? user?.departmentName ?? '-';
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    if (event.dataTransfer && event.dataTransfer.files.length > 0) {
      this.processFile(event.dataTransfer.files[0]);
    }
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    this.processFile(input.files[0]);
  }

  private processFile(file: File): void {
    if (!file.type.startsWith('image/')) {
      alert('Please upload a valid image file (PNG, JPG, WEBP).');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      alert('Image file size must be less than 5MB.');
      return;
    }

    this.imageFile = file;
    const reader = new FileReader();
    reader.onload = () => {
      this.imageBase64 = reader.result as string;
      this.imagePreview = this.imageBase64;
      this.changeDetector.markForCheck();
    };
    reader.readAsDataURL(file);
  }

  removeImage(): void {
    this.imageFile = null;
    this.imagePreview = null;
    this.imageBase64 = '';
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';
    this.userService.getAll().subscribe({
      next: (users: ApiUser[]) => {
        this.users = (users || []) as UserRecord[];
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: err => {
        console.error(err);
        if (err.status === 403) {
          this.errorMessage = 'Access denied. Your account is not allowed to view admin users.';
          this.users = [];
        } else {
          this.errorMessage = 'Unable to load users.';
        }
        this.loading = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  search(): void {
    const keyword = this.searchKeyword.trim();
    if (!keyword) {
      this.loadUsers();
      return;
    }
    this.loading = true;
    this.userService.search(keyword).subscribe({
      next: (users: ApiUser[]) => {
        this.users = (users || []) as UserRecord[];
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: err => {
        console.error(err);
        this.errorMessage = 'Unable to search users.';
        this.loading = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  filterByRole(): void {
    if (!this.selectedRole) {
      this.loadUsers();
      return;
    }
    this.loading = true;
    this.userService.getByRole(this.selectedRole as Role).subscribe({
      next: (users: ApiUser[]) => {
        this.users = (users || []) as UserRecord[];
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: err => {
        console.error(err);
        this.errorMessage = 'Unable to filter users.';
        this.loading = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  prepareAddForm(): void {
    this.userForm.reset({
      name: '',
      familyName: '',
      email: '',
      password: '',
      cin: '',
      phoneNumber: '',
      address: '',
      age: null,
      gender: '',
      role: 'EMPLOYEE',
      employeeProfile: '',
      departmentId: null,
      enabled: true,
      createdAt: null,
      modifiedAt: null
    });
    this.imageFile = null;
    this.imagePreview = null;
    this.imageBase64 = null;

    this.userForm.get('password')?.setValidators([
      Validators.required,
      Validators.minLength(6),
      Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).+$/)
    ]);
    this.userForm.get('confirmPassword')?.setValidators([Validators.required]);
    this.userForm.get('password')?.updateValueAndValidity();
  }

  loadUser(id: number): void {
    this.loadingUser = true;
    this.imageFile = null;
    this.imagePreview = null;
    this.imageBase64 = null;

    this.userService.getById(id).subscribe({
      next: (user: ApiUser) => {
        const userRecord = user as UserRecord;

        this.userForm.patchValue({
          name: userRecord.name,
          familyName: userRecord.familyName,
          email: userRecord.email,
          cin: userRecord.cin || '',
          phoneNumber: userRecord.phoneNumber || '',
          address: userRecord.address || '',
          age: userRecord.age ?? null,
          gender: userRecord.gender || '',
          role: userRecord.role,
          employeeProfile: userRecord.employeeProfile || '',
          departmentId: userRecord.department?.id ?? null,
          enabled: userRecord.enabled ?? true,
          createdAt: userRecord.createdAt || null,
          modifiedAt: userRecord.modifiedAt || null
        });

        if (userRecord.urlImage) {
          this.imagePreview = this.getUserImage(userRecord);
        }

        this.userForm.get('password')?.clearValidators();
        this.userForm.get('confirmPassword')?.clearValidators();
        this.userForm.get('password')?.updateValueAndValidity();
          this.userForm.get('confirmPassword')?.updateValueAndValidity();
        this.loadingUser = false;
        this.changeDetector.markForCheck();
      },
      error: err => {
        console.error(err);
        this.errorMessage = 'Unable to load user.';
        this.loadingUser = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  saveUser(): void {
    const password = this.userForm.get('password')?.value || '';
    const confirmation = this.userForm.get('confirmPassword')?.value || '';
    if (password && password !== confirmation) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const value = this.userForm.getRawValue();

    const userPayload: ApiUser & { enabled?: boolean } = {
      name: value.name!,
      familyName: value.familyName!,
      email: value.email!,
      cin: value.cin || '',
      phoneNumber: value.phoneNumber || '',
      address: value.address || '',
      age: value.age != null ? Number(value.age) : undefined,
      gender: value.gender || undefined,
      role: value.role as Role,
      employeeProfile: value.employeeProfile ? (value.employeeProfile as EmployeeProfile) : undefined,
      department: value.departmentId ? { id: Number(value.departmentId), name: '' } : undefined,
      enabled: value.enabled ?? true
    };

    if (this.imageBase64 !== null) {
      userPayload.urlImage = this.imageBase64;
    }

    if (this.mode === 'add' || (value.password && value.password.trim())) {
      userPayload.password = value.password!;
    }

    if (this.mode === 'add') {
      this.userService.create(userPayload as ApiUser).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/admin/users']);
        },
        error: err => {
          console.error(err);
          this.errorMessage = err?.error?.message || err?.error || 'Unable to create user.';
          this.loading = false;
          this.changeDetector.markForCheck();
        }
      });
      return;
    }

    if (this.mode === 'edit' && this.userId) {
      this.userService.update(this.userId, userPayload as ApiUser).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/admin/users']);
        },
        error: err => {
          console.error(err);
          this.errorMessage = err?.error?.message || err?.error || 'Unable to update user.';
          this.loading = false;
          this.changeDetector.markForCheck();
        }
      });
    }
  }

  addUser(): void {
    this.router.navigate(['/admin/users/add']);
  }

  editUser(id: number): void {
    this.router.navigate(['/admin/users/edit', id]);
  }

  deleteUser(id: number): void {
    if (!confirm('Are you sure you want to delete this user?')) return;

    this.userService.delete(id).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== id);
        this.changeDetector.markForCheck();
      },
      error: err => {
        console.error(err);
        alert('Unable to delete user.');
      }
    });
  }

  backToUsers(): void {
    this.router.navigate(['/admin/users']);
  }

  resetFilters(): void {
    this.searchKeyword = '';
    this.selectedRole = '';
    this.loadUsers();
  }
}
