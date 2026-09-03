import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type FeedbackType = 'POSITIVE' | 'NEGATIVE';

export interface FeedbackRequest {
  type: FeedbackType;
  comment: string;
  documentId?: number;
}

export interface FeedbackResponse {
  id: number;
  type: FeedbackType;
  comment: string;
  userName: string;
  userEmail: string;
  documentName?: string | null;
  messageContent?: string | null;
  createdAt?: string | null;
}

@Injectable({ providedIn: 'root' })
export class FeedbackService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:3000/api/feedback';

  submit(request: FeedbackRequest): Observable<FeedbackResponse> {
    return this.http.post<FeedbackResponse>(this.apiUrl, request);
  }

  getAll(): Observable<FeedbackResponse[]> {
    return this.http.get<FeedbackResponse[]>(this.apiUrl);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
