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

  userForm = this.fb.group({
    name: ['', Validators.required],
    familyName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: [''],
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
        this.departments = deps;
      },
      error: (err) => {
        console.error('Failed to load departments', err);
      }
    });
  }

  getUserImage(user: UserRecord): string | null {
    const image = user?.urlImage ?? user?.imageUrl ?? user?.photo ?? user?.profileImage ?? null;
    return image && image.trim() ? image : null;
  }

  getDepartmentName(user: UserRecord): string {
    return user?.department?.name ?? user?.departmentName ?? '-';
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    if (!file.type.startsWith('image/')) {
      alert('Please select an image file.');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      alert('Image must be less than 5MB.');
      return;
    }

    this.imageFile = file;
    this.imagePreview = URL.createObjectURL(file);
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';
    this.userService.getAll().subscribe({
      next: (users: ApiUser[]) => {
        this.users = users as UserRecord[];
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
        this.users = users as UserRecord[];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.errorMessage = 'Unable to search users.';
        this.loading = false;
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
        this.users = users as UserRecord[];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.errorMessage = 'Unable to filter users.';
        this.loading = false;
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

    this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(8)]);
    this.userForm.get('password')?.updateValueAndValidity();
  }

  loadUser(id: number): void {
    this.loadingUser = true;
    this.imageFile = null;
    this.imagePreview = null;

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
          this.imagePreview = userRecord.urlImage;
        }

        this.userForm.get('password')?.clearValidators();
        this.userForm.get('password')?.updateValueAndValidity();
        this.loadingUser = false;
      },
      error: err => {
        console.error(err);
        this.errorMessage = 'Unable to load user.';
        this.loadingUser = false;
      }
    });
  }

  saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const value = this.userForm.getRawValue();
    const formData = new FormData();

    formData.append('name', value.name!);
    formData.append('familyName', value.familyName!);
    formData.append('email', value.email!);
    formData.append('cin', value.cin || '');
    formData.append('role', value.role!);
    formData.append('enabled', String(value.enabled ?? true));

    if (value.password) formData.append('password', value.password);
    if (value.phoneNumber) formData.append('phoneNumber', value.phoneNumber);
    if (value.address) formData.append('address', value.address);
    if (value.age != null) formData.append('age', String(value.age));
    if (value.gender) formData.append('gender', value.gender);
    if (value.employeeProfile) formData.append('employeeProfile', value.employeeProfile);
    if (value.departmentId != null) formData.append('departmentId', String(value.departmentId));
    if (this.imageFile) formData.append('image', this.imageFile);

    if (this.mode === 'add') {
      this.userService.createWithImage(formData).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/admin/users']);
        },
        error: err => {
          console.error(err);
          this.errorMessage = err?.error?.message || 'Unable to create user.';
          this.loading = false;
        }
      });
      return;
    }

    if (this.mode === 'edit' && this.userId) {
      this.userService.updateWithImage(this.userId, formData).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/admin/users']);
        },
        error: err => {
          console.error(err);
          this.errorMessage = err?.error?.message || 'Unable to update user.';
          this.loading = false;
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