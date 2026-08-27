// ==========================================
// DEPARTMENT
// ==========================================
export interface Department {
  id: number;
  name: string;
}

// ==========================================
// DOCUMENT
// ==========================================
export interface Document {
  id?: number;

  name: string;
  url: string;
  mimeType?: string;
  fileSize?: number;
  description?: string;
  author?: string;

  status: DocumentStatus;
  visibility: DocumentVisibility;

  department?: Department;

  // From BaseEntity
  createdAt?: string | Date;
  modifiedAt?: string | Date;

  createdBy?: {
    id: number;
    name?: string;
    familyName?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
  }
}

// ==========================================
// DOCUMENT STATUS
// ==========================================
export enum DocumentStatus {
  UPLOADED = 'UPLOADED',
  PROCESSING = 'PROCESSING',
  PROCESSED = 'PROCESSED',
  FAILED = 'FAILED'
}

// ==========================================
// DOCUMENT VISIBILITY
// ==========================================
export enum DocumentVisibility {
  PUBLIC = 'PUBLIC',
  COMPANY = 'COMPANY',
  DEPARTMENT = 'DEPARTMENT',
  MANAGERS_ONLY = 'MANAGERS_ONLY',
  PRIVATE = 'PRIVATE'
}

