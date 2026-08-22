import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  Validators
} from '@angular/forms';

import {
  ActivatedRoute,
  Router
} from '@angular/router';

import {
  DocumentService
} from '../../../Services/document-service';

import {
  DepartmentService
} from '../../../Services/department-service';

import {
  Document,
  DocumentStatus,
  DocumentVisibility
} from '../Models/Document';

import {
  Department
} from '../Models/Departments';


@Component({
  selector: 'app-document',

  standalone: true,

  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ],

  templateUrl: './documents.html',

  styleUrl: './documents.css'
})
export class Documents
  implements OnInit {


  private readonly documentService =
    inject(DocumentService);

  private readonly departmentService =
    inject(DepartmentService);

  private readonly fb =
    inject(FormBuilder);
  private readonly changeDetector =
    inject(ChangeDetectorRef);

  private readonly route =
    inject(ActivatedRoute);

  private readonly router =
    inject(Router);


  // ==========================================
  // DATA
  // ==========================================

  documents: Document[] = [];

  departments: Department[] = [];

  selectedDocument: Document | null = null;


  // ==========================================
  // ENUMS
  // ==========================================

  readonly documentStatuses =
    Object.values(DocumentStatus);

  readonly documentVisibilities =
    Object.values(DocumentVisibility);


  // ==========================================
  // STATE
  // ==========================================

  isEditing = false;

  isLoading = false;

  errorMessage = '';

  successMessage = '';

  searchKeyword = '';


  // ==========================================
  // FORM
  // ==========================================

  documentForm =
    this.fb.nonNullable.group({

      name: [
        '',
        [
          Validators.required,
          Validators.maxLength(255)
        ]
      ],

      url: [
        '',
        [
          Validators.required,
          Validators.maxLength(500)
        ]
      ],

      mimeType: [
        ''
      ],

      fileSize: [
        0
      ],

      description: [
        '',
        [
          Validators.maxLength(500)
        ]
      ],

      author: [
        '',
        [
          Validators.maxLength(255)
        ]
      ],

      status: [
        DocumentStatus.UPLOADED,
        Validators.required
      ],

      visibility: [
        DocumentVisibility.PRIVATE,
        Validators.required
      ],

      departmentId: [
        null as number | null
      ]
    });


  // ==========================================
  // INIT
  // ==========================================

  ngOnInit(): void {

    this.loadDepartments();

    this.loadDocuments();

    this.checkEditMode();
  }


  // ==========================================
  // CHECK ROUTE MODE
  // ==========================================

  private checkEditMode(): void {

    const mode =
      this.route.snapshot.data['mode'];

    const id =
      this.route.snapshot.paramMap.get('id');

    if (
      mode === 'edit' &&
      id
    ) {

      this.isEditing = true;

      this.loadDocument(
        Number(id)
      );
    }
  }


  // ==========================================
  // LOAD ALL DOCUMENTS
  // ==========================================

  loadDocuments(): void {

    this.isLoading = true;

    this.clearMessages();

    this.documentService
      .getAll()
      .subscribe({

        next: (data) => {

          this.documents = data;

          this.isLoading = false;
          this.changeDetector.markForCheck();
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            'Unable to load documents.';

          this.isLoading = false;
          this.changeDetector.markForCheck();
        }
      });
  }


  // ==========================================
  // LOAD DEPARTMENTS
  // ==========================================

  loadDepartments(): void {

    this.departmentService
      .getAll()
      .subscribe({

        next: (data) => {

          this.departments = data;
          this.changeDetector.markForCheck();
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            'Unable to load departments.';

          this.changeDetector.markForCheck();
        }
      });
  }


  // ==========================================
  // LOAD DOCUMENT BY ID
  // ==========================================

  loadDocument(
    id: number
  ): void {

    this.isLoading = true;

    this.documentService
      .getById(id)
      .subscribe({

        next: (document) => {

          this.selectedDocument =
            document;

          this.documentForm.patchValue({

            name: document.name,

            url: document.url,

            mimeType:
              document.mimeType ?? '',

            fileSize:
              document.fileSize ?? 0,

            description:
              document.description ?? '',

            author:
              document.author ?? '',

            status:
              document.status,

            visibility:
              document.visibility,

            departmentId:
              document.department?.id ?? null
          });

          this.isLoading = false;
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            'Unable to load document.';

          this.isLoading = false;
        }
      });
  }


  // ==========================================
  // CREATE
  // ==========================================

  createDocument(): void {

    if (
      this.documentForm.invalid
    ) {

      this.documentForm
        .markAllAsTouched();

      return;
    }

    const document =
      this.buildDocument();

    this.documentService
      .create(document)
      .subscribe({

        next: (created) => {

          this.successMessage =
            'Document created successfully.';

          this.documents.push(created);

          this.resetForm();
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            error?.error?.message ??
            'Unable to create document.';
        }
      });
  }


  // ==========================================
  // EDIT
  // ==========================================

  editDocument(
    document: Document
  ): void {

    if (!document.id) {
      return;
    }

    this.router.navigate([
      '/admin/documents/edit',
      document.id
    ]);
  }


  // ==========================================
  // UPDATE
  // ==========================================

  updateDocument(): void {

    if (
      !this.selectedDocument?.id
    ) {
      return;
    }

    if (
      this.documentForm.invalid
    ) {

      this.documentForm
        .markAllAsTouched();

      return;
    }

    const document =
      this.buildDocument();

    this.documentService
      .update(
        this.selectedDocument.id,
        document
      )
      .subscribe({

        next: (updated) => {

          const index =
            this.documents.findIndex(
              d =>
                d.id === updated.id
            );

          if (index !== -1) {

            this.documents[index] =
              updated;
          }

          this.successMessage =
            'Document updated successfully.';

          this.resetForm();
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            error?.error?.message ??
            'Unable to update document.';
        }
      });
  }


  // ==========================================
  // DELETE
  // ==========================================

  deleteDocument(
    id: number | undefined
  ): void {

    if (!id) {
      return;
    }

    const confirmed =
      confirm(
        'Are you sure you want to delete this document?'
      );

    if (!confirmed) {
      return;
    }

    this.documentService
      .delete(id)
      .subscribe({

        next: () => {

          this.documents =
            this.documents.filter(
              d =>
                d.id !== id
            );

          this.successMessage =
            'Document deleted successfully.';
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            error?.error?.message ??
            'Unable to delete document.';
        }
      });
  }


  // ==========================================
  // SEARCH
  // ==========================================

  searchDocuments(): void {

    const keyword =
      this.searchKeyword.trim();

    if (!keyword) {

      this.loadDocuments();

      return;
    }

    this.documentService
      .search(keyword)
      .subscribe({

        next: (data) => {

          this.documents = data;
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            'Unable to search documents.';
        }
      });
  }


  // ==========================================
  // FILTER BY DEPARTMENT
  // ==========================================

  filterByDepartment(
    departmentId: number | null
  ): void {

    if (!departmentId) {

      this.loadDocuments();

      return;
    }

    this.documentService
      .getByDepartment(departmentId)
      .subscribe({

        next: (data) => {

          this.documents = data;
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            'Unable to filter documents.';
        }
      });
  }


  // ==========================================
  // FILTER BY STATUS
  // ==========================================

  filterByStatus(
    status: DocumentStatus | ''
  ): void {

    if (!status) {

      this.loadDocuments();

      return;
    }

    this.documentService
      .getByStatus(status)
      .subscribe({

        next: (data) => {

          this.documents = data;
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            'Unable to filter documents.';
        }
      });
  }


  // ==========================================
  // FILTER BY VISIBILITY
  // ==========================================

  filterByVisibility(
    visibility: DocumentVisibility | ''
  ): void {

    if (!visibility) {

      this.loadDocuments();

      return;
    }

    this.documentService
      .getByVisibility(visibility)
      .subscribe({

        next: (data) => {

          this.documents = data;
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            'Unable to filter documents.';
        }
      });
  }


  // ==========================================
  // UPDATE STATUS
  // ==========================================

  changeStatus(
    document: Document,
    status: DocumentStatus
  ): void {

    if (!document.id) {
      return;
    }

    this.documentService
      .updateStatus(
        document.id,
        status
      )
      .subscribe({

        next: (updated) => {

          const index =
            this.documents.findIndex(
              d =>
                d.id === updated.id
            );

          if (index !== -1) {

            this.documents[index] =
              updated;
          }
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            'Unable to update status.';
        }
      });
  }


  // ==========================================
  // UPDATE VISIBILITY
  // ==========================================

  changeVisibility(
    document: Document,
    visibility: DocumentVisibility
  ): void {

    if (!document.id) {
      return;
    }

    this.documentService
      .updateVisibility(
        document.id,
        visibility
      )
      .subscribe({

        next: (updated) => {

          const index =
            this.documents.findIndex(
              d =>
                d.id === updated.id
            );

          if (index !== -1) {

            this.documents[index] =
              updated;
          }
        },

        error: (error) => {

          console.error(error);

          this.errorMessage =
            'Unable to update visibility.';
        }
      });
  }


  // ==========================================
  // BUILD DOCUMENT
  // ==========================================

  private buildDocument(): Document {

    const value =
      this.documentForm.getRawValue();

    const document: Document = {

      name: value.name,

      url: value.url,

      mimeType:
        value.mimeType || undefined,

      fileSize:
        value.fileSize || undefined,

      description:
        value.description || undefined,

      author:
        value.author || undefined,

      status:
        value.status,

      visibility:
        value.visibility
    };

    if (value.departmentId) {

      document.department = {

        id: value.departmentId,

        name: ''
      };
    }

    return document;
  }


  // ==========================================
  // RESET
  // ==========================================

  resetForm(): void {

    this.documentForm.reset({

      name: '',

      url: '',

      mimeType: '',

      fileSize: 0,

      description: '',

      author: '',

      status:
        DocumentStatus.UPLOADED,

      visibility:
        DocumentVisibility.PRIVATE,

      departmentId: null
    });

    this.selectedDocument = null;

    this.isEditing = false;
  }


  // ==========================================
  // CLEAR MESSAGES
  // ==========================================

  private clearMessages(): void {

    this.errorMessage = '';

    this.successMessage = '';
  }


  // ==========================================
  // FORM CONTROLS
  // ==========================================

  get nameControl() {

    return this.documentForm.controls.name;
  }

  get urlControl() {

    return this.documentForm.controls.url;
  }

  get descriptionControl() {

    return this.documentForm.controls.description;
  }
}