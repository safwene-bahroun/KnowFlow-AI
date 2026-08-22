import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { DepartmentService } from '../../../Services/department-service';
import { Department } from '../Models/Departments';

@Component({
  selector: 'app-department',
  standalone: true,
 imports: [
  CommonModule,
  ReactiveFormsModule,
  FormsModule
],
  styleUrl:'./departments.css',
  templateUrl: './departments.html'
})
export class Departments implements OnInit {

  private readonly departmentService =
    inject(DepartmentService);

  private readonly fb =
    inject(FormBuilder);
  private readonly changeDetector =
    inject(ChangeDetectorRef);

  departments: Department[] = [];

  selectedDepartment: Department | null = null;

  isEditing = false;
  isLoading = false;

  errorMessage = '';
  successMessage = '';

  searchKeyword = '';

  departmentForm = this.fb.nonNullable.group({
    name: [
      '',
      [
        Validators.required,
        Validators.maxLength(100)
      ]
    ],

    description: [
      '',
      [
        Validators.maxLength(500)
      ]
    ]
  });

  ngOnInit(): void {
    this.loadDepartments();
  }

  // =========================
  // GET ALL
  // =========================

  loadDepartments(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.departmentService
      .getAll()
      .pipe(
        catchError((error) => {
          console.error(error);
          this.errorMessage = 'Unable to load departments.';
          return of([] as Department[]);
        }),
        finalize(() => {
          this.isLoading = false;
          this.changeDetector.markForCheck();
        })
      )
      .subscribe((data) => {
        this.departments = data ?? [];
        this.changeDetector.markForCheck();
      });
  }

  // =========================
  // CREATE
  // =========================

  createDepartment(): void {

    if (this.departmentForm.invalid) {

      this.departmentForm.markAllAsTouched();

      return;
    }

    const department: Department = {
      name: this.departmentForm.controls.name.value,
      description:
        this.departmentForm.controls.description.value
    };

    this.departmentService
      .create(department)
      .subscribe({

        next: (createdDepartment) => {

          this.departments.push(createdDepartment);

          this.successMessage =
            'Department created successfully.';

          this.resetForm();
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            error?.error?.message ??
            'Unable to create department.';
        }
      });
  }

  // =========================
  // EDIT
  // =========================

  editDepartment(
    department: Department
  ): void {

    if (!department.id) {
      return;
    }

    this.selectedDepartment = department;
    this.isEditing = true;

    this.departmentForm.patchValue({
      name: department.name,
      description: department.description ?? ''
    });
  }

  // =========================
  // UPDATE
  // =========================

  updateDepartment(): void {

    if (!this.selectedDepartment?.id) {
      return;
    }

    if (this.departmentForm.invalid) {

      this.departmentForm.markAllAsTouched();

      return;
    }

    const updatedDepartment: Department = {
      name: this.departmentForm.controls.name.value,
      description:
        this.departmentForm.controls.description.value
    };

    this.departmentService
      .update(
        this.selectedDepartment.id,
        updatedDepartment
      )
      .subscribe({

        next: (updated) => {

          const index =
            this.departments.findIndex(
              department =>
                department.id === updated.id
            );

          if (index !== -1) {
            this.departments[index] = updated;
          }

          this.successMessage =
            'Department updated successfully.';

          this.resetForm();
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            error?.error?.message ??
            'Unable to update department.';
        }
      });
  }

  // =========================
  // DELETE
  // =========================

  deleteDepartment(
    id: number | undefined
  ): void {

    if (!id) {
      return;
    }

    const confirmed =
      confirm(
        'Are you sure you want to delete this department?'
      );

    if (!confirmed) {
      return;
    }

    this.departmentService
      .delete(id)
      .subscribe({

        next: () => {

          this.departments =
            this.departments.filter(
              department =>
                department.id !== id
            );

          this.successMessage =
            'Department deleted successfully.';
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            error?.error?.message ??
            'Unable to delete department.';
        }
      });
  }

  // =========================
  // SEARCH
  // =========================

  searchDepartments(): void {
    const keyword = this.searchKeyword.trim();

    if (!keyword) {
      this.loadDepartments();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.departmentService
      .search(keyword)
      .pipe(
        catchError((error) => {
          console.error(error);
          this.errorMessage = 'Unable to search departments.';
          return of([] as Department[]);
        }),
        finalize(() => {
          this.isLoading = false;
        })
      )
      .subscribe((data) => {
        this.departments = data ?? [];
      });
  }

  // =========================
  // RESET
  // =========================

  resetForm(): void {

    this.departmentForm.reset({
      name: '',
      description: ''
    });

    this.selectedDepartment = null;
    this.isEditing = false;
  }

  // =========================
  // FORM HELPERS
  // =========================

  get nameControl() {
    return this.departmentForm.controls.name;
  }

  get descriptionControl() {
    return this.departmentForm.controls.description;
  }
}