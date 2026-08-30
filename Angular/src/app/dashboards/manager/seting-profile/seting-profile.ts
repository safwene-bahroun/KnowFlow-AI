import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { AuthService, UserResponse } from '../../../Services/auth-service';
import { ProfileRequest, UserService } from '../../../Services/user-service';

@Component({
  selector: 'app-setting-profile-manager',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './seting-profile.html',
  styleUrl: './seting-profile.css',
})
export class SetingProfile implements OnInit {

  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);

  getProfileImageUrl(url: string | null | undefined): string | null {
    if (!url || !url.trim()) return null;
    if (url.startsWith('data:image')) return url;
    return /^https?:\/\//i.test(url)
      ? url
      : `http://localhost:3000${url.startsWith('/') ? '' : '/'}${url}`;
  }

  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  imagePreview: string | null = null;
  isDragging = false;
  userDepartment = '-';
  userRole = '';
  userProfileTitle = '';
  private imageData: string | undefined;

  profileForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    familyName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: [''],
    address: ['']
  });

  ngOnInit(): void {
    this.loading = true;
    this.userService.getProfile().subscribe({
      next: user => {
        this.setUser(user);
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Unable to load your profile.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
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
    const file = input.files?.[0];
    if (!file) return;
    this.processFile(file);
  }

  private processFile(file: File): void {
    if (!file.type.startsWith('image/') || file.size > 5 * 1024 * 1024) {
      this.errorMessage = 'Please choose a valid image (PNG, JPG, WEBP) smaller than 5 MB.';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      this.imageData = reader.result as string;
      this.imagePreview = this.imageData;
      this.errorMessage = '';
      this.cdr.markForCheck();
    };
    reader.readAsDataURL(file);
  }

  removeImage(): void {
    this.imageData = '';
    this.imagePreview = null;
  }

  save(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';
    const value = this.profileForm.getRawValue();
    const request: ProfileRequest = { ...value, urlImage: this.imageData };

    this.userService.updateProfile(request).subscribe({
      next: user => {
        this.setUser(user);
        this.authService.updateStoredUser(user);
        this.successMessage = 'Profile updated successfully.';
        this.saving = false;
        this.cdr.markForCheck();
      },
      error: err => {
        this.errorMessage = err?.error?.message || 'Unable to update your profile.';
        this.saving = false;
        this.cdr.markForCheck();
      }
    });
  }

  private setUser(user: UserResponse): void {
    this.profileForm.patchValue({
      name: user.name || '',
      familyName: user.familyName || '',
      email: user.email || '',
      phoneNumber: user.phoneNumber || '',
      address: user.address || ''
    });
    this.imagePreview = this.getProfileImageUrl(user.urlImage);
    this.userDepartment = user.departmentName || 'General';
    this.userRole = user.role || '';
    this.userProfileTitle = user.employeeProfile || '';
    this.imageData = undefined;
  }

}

