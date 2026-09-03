import {
  ChangeDetectorRef,
  Component,
  OnInit,
  OnDestroy,
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
import { Subscription } from 'rxjs';

import { DocumentRequest, DocumentService } from '../../../Services/document-service';
import { DepartmentService }         from '../../../Services/department-service';
import { AuthService, UserResponse } from '../../../Services/auth-service';
import { UserService, User as ApiUser } from '../../../Services/user-service';

import {
  Document,
  DocumentStatus,
  DocumentVisibility
} from '../Models/Document';
import { Department } from '../Models/Departments';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './documents.html',
  styleUrl:    './documents.css'
})
export class Documents implements OnInit, OnDestroy {

  // ── DI ──────────────────────────────────────────────────────────────────────
  private readonly documentService   = inject(DocumentService);
  private readonly departmentService = inject(DepartmentService);
  private readonly authService       = inject(AuthService);
  private readonly userService       = inject(UserService);
  private readonly fb                = inject(FormBuilder);
  private readonly cdr               = inject(ChangeDetectorRef);
  private readonly route             = inject(ActivatedRoute);
  private readonly router            = inject(Router);

  // ── State ────────────────────────────────────────────────────────────────────
  documents:        Document[]   = [];
  departments:      Department[] = [];
  users:             ApiUser[] = [];
  selectedDocument: Document | null = null;

  /**
   * Read from localStorage (set by AuthService on login).
   * Used for display only — authInterceptor sends the JWT on every request
   * so the backend can resolve createdBy automatically.
   */
  currentUser: UserResponse | null = null;

  readonly documentStatuses     = Object.values(DocumentStatus);
  readonly documentVisibilities = Object.values(DocumentVisibility);

  isEditing      = false;
  showForm       = false;
  isLoading      = false;
  errorMessage   = '';
  successMessage = '';
  searchKeyword  = '';

  isDragOver   = false;
  selectedFile: File | null = null;

  private routeSub?: Subscription;

  // ── Form ─────────────────────────────────────────────────────────────────────
  documentForm = this.fb.nonNullable.group({
    name:         ['', [Validators.required, Validators.maxLength(255)]],
    description:  ['', Validators.maxLength(500)],
    author:       ['', Validators.maxLength(255)],
    status:       [DocumentStatus.UPLOADED,    Validators.required],
    visibility:   [DocumentVisibility.PRIVATE, Validators.required],
    departmentId: [null as number | null]
  });

  // ── Computed ─────────────────────────────────────────────────────────────────
  get isDepartmentRequired(): boolean {
    return this.documentForm.get('visibility')?.value === DocumentVisibility.DEPARTMENT;
  }

  get nameControl() { return this.documentForm.controls.name; }

  get currentUserLabel(): string {
    if (!this.currentUser) return '-';
    const full = `${this.currentUser.name ?? ''} ${this.currentUser.familyName ?? ''}`.trim();
    return full || this.currentUser.email || '-';
  }

  getDocumentUrl(url: string | undefined): string {
    if (!url) return '';
    return /^https?:\/\//i.test(url)
      ? url
      : `http://localhost:3000${url.startsWith('/') ? '' : '/'}${url}`;
  }

  getCreatedByLabel(document: Document): string {
    const createdBy = document.createdBy;
    const user = createdBy?.id
      ? this.users.find(item => item.id === createdBy.id)
      : undefined;
    const source = user ?? createdBy;

    if (!source) return '-';

    const name = 'name' in source ? source.name : source.firstName;
    const familyName = 'familyName' in source ? source.familyName : source.lastName;
    return `${name ?? ''} ${familyName ?? ''}`.trim() || source.email || '-';
  }

  // ── Lifecycle ────────────────────────────────────────────────────────────────
  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadDepartments();
    this.loadUsers();
    this.loadDocuments();
    this.listenToRouteParams();

    this.documentForm.get('visibility')?.valueChanges.subscribe(() => {
      this.onVisibilityChange();
    });
    this.onVisibilityChange();
  }

  ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
  }

  // ── Route (observable — survives component reuse) ────────────────────────────
  private listenToRouteParams(): void {
    this.routeSub = this.route.data.subscribe(data => {
      const mode = data['mode'];
      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        if (mode === 'edit' && id) {
          this.isEditing = true;
          this.showForm = true;
          this.loadDocument(+id);
        } else {
          this.isEditing = false;
          this.resetForm();
          this.showForm = mode === 'add';
        }
      });
    });
  }

  // ── Visibility → Department validator ────────────────────────────────────────
  onVisibilityChange(): void {
    const visibility = this.documentForm.get('visibility')?.value;
    const deptCtrl   = this.documentForm.get('departmentId');

    if (visibility === DocumentVisibility.DEPARTMENT) {
      deptCtrl?.setValidators([Validators.required]);
    } else {
      deptCtrl?.clearValidators();
      deptCtrl?.setValue(null);
    }
    deptCtrl?.updateValueAndValidity();
  }

  // ── Load ─────────────────────────────────────────────────────────────────────
  loadDocuments(): void {
    this.isLoading = true;
    this.clearMessages();

    this.documentService.getAll().subscribe({
      next: (data) => {
        this.documents = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage =
          err?.error?.message ?? err?.message ?? 'Unable to load documents.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadDepartments(): void {
    this.departmentService.getAll().subscribe({
      next: (data) => {
        this.departments = data;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Unable to load departments.';
        this.cdr.markForCheck();
      }
    });
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: users => {
        this.users = users;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Unable to load users.';
        this.cdr.markForCheck();
      }
    });
  }

  loadDocument(id: number): void {
    this.isLoading = true;
    this.documentService.getById(id).subscribe({
      next: (doc) => {
        this.selectedDocument = doc;
        this.documentForm.patchValue({
          name:         doc.name,
          description:  doc.description  ?? '',
          author:       doc.author       ?? '',
          status:       doc.status,
          visibility:   doc.visibility,
          departmentId: doc.department?.id ?? null
        });
        this.onVisibilityChange();
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage =
          err?.error?.message ?? 'Unable to load document.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  // ── CRUD ─────────────────────────────────────────────────────────────────────

  /** Sends the selected file and document fields as multipart/form-data. */
  async createDocument(): Promise<void> {
    if (this.documentForm.invalid || !this.selectedFile) {
      this.documentForm.markAllAsTouched();
      return;
    }

    this.documentService.create(await this.buildJsonPayload()).subscribe({
      next: (created) => {
        this.successMessage = 'Document created successfully.';
        this.documents.push(created);
        this.resetForm();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage =
          err?.error?.message ?? 'Unable to create document.';
        this.cdr.markForCheck();
      }
    });
  }

  editDocument(doc: Document): void {
    if (!doc.id) return;
    this.router.navigate(['/admin/documents/edit', doc.id]);
  }

  async updateDocument(): Promise<void> {
    if (!this.selectedDocument?.id || this.documentForm.invalid) {
      this.documentForm.markAllAsTouched();
      return;
    }

    this.documentService.update(this.selectedDocument.id, await this.buildJsonPayload()).subscribe({
      next: (updated) => {
        const idx = this.documents.findIndex(d => d.id === updated.id);
        if (idx !== -1) this.documents[idx] = updated;
        this.successMessage = 'Document updated successfully.';
        this.resetForm();
        this.router.navigate(['/admin/documents']);
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage =
          err?.error?.message ?? 'Unable to update document.';
        this.cdr.markForCheck();
      }
    });
  }

  deleteDocument(id: number | undefined): void {
    if (!id) return;
    if (!confirm('Are you sure you want to delete this document?')) return;

    this.documentService.delete(id).subscribe({
      next: () => {
        this.documents = this.documents.filter(d => d.id !== id);
        this.successMessage = 'Document deleted successfully.';
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage =
          err?.error?.message ?? 'Unable to delete document.';
        this.cdr.markForCheck();
      }
    });
  }

  // ── Filters ──────────────────────────────────────────────────────────────────
  searchDocuments(): void {
    const keyword = this.searchKeyword.trim();
    if (!keyword) { this.loadDocuments(); return; }

    this.isLoading = true;
    this.clearMessages();
    this.documentService.search(keyword).subscribe({
      next: (data) => {
        this.documents = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.documents = [];
        this.isLoading = false;
        this.errorMessage = 'Unable to search documents.';
        this.cdr.markForCheck();
      }
    });
  }

  filterByDepartment(departmentId: number | null): void {
    if (!departmentId) { this.loadDocuments(); return; }

    this.isLoading = true;
    this.clearMessages();
    this.documentService.getByDepartment(departmentId).subscribe({
      next: (data) => {
        this.documents = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.documents = [];
        this.isLoading = false;
        this.errorMessage = 'Unable to filter documents.';
        this.cdr.markForCheck();
      }
    });
  }

  filterByStatus(status: DocumentStatus | ''): void {
    if (!status) { this.loadDocuments(); return; }

    this.isLoading = true;
    this.clearMessages();
    this.documentService.getByStatus(status).subscribe({
      next: (data) => {
        this.documents = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.documents = [];
        this.isLoading = false;
        this.errorMessage = 'Unable to filter documents.';
        this.cdr.markForCheck();
      }
    });
  }

  filterByVisibility(visibility: DocumentVisibility | ''): void {
    if (!visibility) { this.loadDocuments(); return; }

    this.isLoading = true;
    this.clearMessages();
    this.documentService.getByVisibility(visibility).subscribe({
      next: (data) => {
        this.documents = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.documents = [];
        this.isLoading = false;
        this.errorMessage = 'Unable to filter documents.';
        this.cdr.markForCheck();
      }
    });
  }

  // ── Helpers ──────────────────────────────────────────────────────────────────

  private async buildJsonPayload(): Promise<DocumentRequest> {
    const v = this.documentForm.getRawValue();
    const payload: DocumentRequest = {
      name: v.name,
      description: v.description,
      author: v.author,
      status: v.status,
      visibility: v.visibility,
      departmentId: v.departmentId
    };

    if (this.selectedFile) {
      payload.fileName = this.selectedFile.name;
      payload.mimeType = this.selectedFile.type || 'application/octet-stream';
      payload.fileSize = this.selectedFile.size;
      payload.fileData = await this.fileToBase64(this.selectedFile);
    }

    return payload;
  }

  private fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve((reader.result as string).split(',')[1]);
      reader.onerror = () => reject(reader.error);
      reader.readAsDataURL(file);
    });
  }

  resetForm(): void {
    this.documentForm.reset({
      name:         '',
      description:  '',
      author:       '',
      status:       DocumentStatus.UPLOADED,
      visibility:   DocumentVisibility.PRIVATE,
      departmentId: null
    });
    this.selectedDocument = null;
    this.isEditing        = false;
    this.showForm         = false;
    this.clearFile();
    this.onVisibilityChange();
  }

  openCreateForm(): void {
    this.resetForm();
    this.showForm = true;
  }

  private clearMessages(): void {
    this.errorMessage   = '';
    this.successMessage = '';
  }

  // ── Drag & Drop ─────────────────────────────────────────────────────────────
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
    const files = event.dataTransfer?.files;
    if (files?.length) this.handleFile(files[0]);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) this.handleFile(input.files[0]);
  }

  private handleFile(file: File): void {
    const allowedTypes = [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'text/plain',
      'application/rtf',
      'application/vnd.oasis.opendocument.text'
    ];
    const allowedExt = ['.pdf', '.doc', '.docx', '.txt', '.rtf', '.odt'];

    const ok =
      allowedTypes.includes(file.type) ||
      allowedExt.some(ext => file.name.toLowerCase().endsWith(ext));

    if (!ok) {
      this.errorMessage = 'Only PDF, Word, TXT, RTF or ODT files are allowed.';
      return;
    }

    this.selectedFile = file;
    this.documentForm.patchValue({ name: file.name.replace(/\.[^/.]+$/, '') });

    this.cdr.markForCheck();
  }

  clearFile(): void {
    this.selectedFile = null;
    this.documentForm.patchValue({ name: '' });
    this.cdr.markForCheck();
  }
}