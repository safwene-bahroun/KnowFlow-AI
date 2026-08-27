import { Injectable, inject } from '@angular/core';

import {
  HttpClient,
  HttpParams
} from '@angular/common/http';

import { Observable } from 'rxjs';

import {
  Document,
  DocumentStatus,
  DocumentVisibility
} from '../dashboards/admin/Models/Document';

export interface DocumentRequest {
  name: string;
  description: string;
  author: string;
  status: DocumentStatus;
  visibility: DocumentVisibility;
  departmentId: number | null;
  fileName?: string;
  mimeType?: string;
  fileSize?: number;
  fileData?: string;
}


@Injectable({
  providedIn: 'root'
})
export class DocumentService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    'http://localhost:3000/api/admin/documents';


  // ==========================================
  // CREATE
  // ==========================================

  create(
    document: DocumentRequest
  ): Observable<Document> {

    return this.http.post<Document>(
      this.apiUrl,
      document
    );
  }


  // ==========================================
  // GET ALL
  // ==========================================

  getAll(): Observable<Document[]> {

    return this.http.get<Document[]>(
      this.apiUrl
    );
  }


  // ==========================================
  // GET BY ID
  // ==========================================

  getById(
    id: number
  ): Observable<Document> {

    return this.http.get<Document>(
      `${this.apiUrl}/${id}`
    );
  }


  // ==========================================
  // SEARCH
  // ==========================================

  search(
    keyword: string
  ): Observable<Document[]> {

    const params = new HttpParams()
      .set('keyword', keyword);

    return this.http.get<Document[]>(
      `${this.apiUrl}/search`,
      { params }
    );
  }


  // ==========================================
  // GET BY NAME
  // ==========================================

  getByName(
    name: string
  ): Observable<Document[]> {

    return this.http.get<Document[]>(
      `${this.apiUrl}/name/${encodeURIComponent(name)}`
    );
  }


  // ==========================================
  // GET BY DEPARTMENT
  // ==========================================

  getByDepartment(
    departmentId: number
  ): Observable<Document[]> {

    return this.http.get<Document[]>(
      `${this.apiUrl}/department/${departmentId}`
    );
  }


  // ==========================================
  // GET BY STATUS
  // ==========================================

  getByStatus(
    status: DocumentStatus
  ): Observable<Document[]> {

    return this.http.get<Document[]>(
      `${this.apiUrl}/status/${status}`
    );
  }


  // ==========================================
  // GET BY VISIBILITY
  // ==========================================

  getByVisibility(
    visibility: DocumentVisibility
  ): Observable<Document[]> {

    return this.http.get<Document[]>(
      `${this.apiUrl}/visibility/${visibility}`
    );
  }


  // ==========================================
  // UPDATE
  // ==========================================

  update(
    id: number,
    document: DocumentRequest
  ): Observable<Document> {

    return this.http.put<Document>(
      `${this.apiUrl}/${id}`,
      document
    );
  }


  // ==========================================
  // UPDATE STATUS
  // ==========================================

  updateStatus(
    id: number,
    status: DocumentStatus
  ): Observable<Document> {

    const params = new HttpParams()
      .set('status', status);

    return this.http.patch<Document>(
      `${this.apiUrl}/${id}/status`,
      null,
      { params }
    );
  }


  // ==========================================
  // UPDATE VISIBILITY
  // ==========================================

  updateVisibility(
    id: number,
    visibility: DocumentVisibility
  ): Observable<Document> {

    const params = new HttpParams()
      .set('visibility', visibility);

    return this.http.patch<Document>(
      `${this.apiUrl}/${id}/visibility`,
      null,
      { params }
    );
  }


  // ==========================================
  // DELETE
  // ==========================================

  delete(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}