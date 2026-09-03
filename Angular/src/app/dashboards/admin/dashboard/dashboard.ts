import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { DepartmentService } from '../../../Services/department-service';
import { DocumentService } from '../../../Services/document-service';
import { NotificationService } from '../../../Services/notification-service';
import { UserService, type User } from '../../../Services/user-service';
import { DocumentStatus, DocumentVisibility, type Document } from '../Models/Document';

interface MetricCard {
  label: string;
  value: number;
  detail: string;
  icon: string;
  tone: string;
  link: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  private readonly userService = inject(UserService);
  private readonly departmentService = inject(DepartmentService);
  private readonly documentService = inject(DocumentService);
  private readonly notificationService = inject(NotificationService);
  private readonly changeDetector = inject(ChangeDetectorRef);

  users: User[] = [];
  documents: Document[] = [];
  departmentCount = 0;
  unreadNotifications = 0;
  isLoading = true;
  hasLoadError = false;

  readonly statusItems = [
    { label: 'Processed', key: DocumentStatus.PROCESSED, tone: 'processed' },
    { label: 'Processing', key: DocumentStatus.PROCESSING, tone: 'processing' },
    { label: 'Uploaded', key: DocumentStatus.UPLOADED, tone: 'uploaded' },
    { label: 'Failed', key: DocumentStatus.FAILED, tone: 'failed' }
  ];

  readonly visibilityItems = [
    { label: 'Company-wide', key: DocumentVisibility.COMPANY },
    { label: 'Public', key: DocumentVisibility.PUBLIC },
    { label: 'Department', key: DocumentVisibility.DEPARTMENT },
    { label: 'Managers only', key: DocumentVisibility.MANAGERS_ONLY },
    { label: 'Private', key: DocumentVisibility.PRIVATE }
  ];

  get metrics(): MetricCard[] {
    const staffCount = this.users.filter(user => user.role !== 'ADMIN').length;
    return [
      { label: 'Total users', value: this.users.length, detail: `${staffCount} staff members`, icon: '◎', tone: 'blue', link: '/admin/users' },
      { label: 'Documents', value: this.documents.length, detail: `${this.countStatus(DocumentStatus.PROCESSED)} processed`, icon: '▤', tone: 'green', link: '/admin/documents' },
      { label: 'Departments', value: this.departmentCount, detail: 'Organizational units', icon: '⌂', tone: 'orange', link: '/admin/departments' },
      { label: 'Needs attention', value: this.countStatus(DocumentStatus.FAILED), detail: `${this.unreadNotifications} unread notifications`, icon: '!', tone: 'red', link: '/admin/notifications' }
    ];
  }

  ngOnInit(): void {
    forkJoin({
      users: this.userService.getAll(),
      departments: this.departmentService.getAll(),
      documents: this.documentService.getAll(),
      unreadNotifications: this.notificationService.getUnreadCount()
    }).subscribe({
      next: result => {
        this.users = result.users || [];
        this.departmentCount = (result.departments || []).length;
        this.documents = result.documents || [];
        this.unreadNotifications = result.unreadNotifications || 0;
        this.isLoading = false;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.hasLoadError = true;
        this.isLoading = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  countStatus(status: DocumentStatus): number {
    return this.documents.filter(document => document.status === status).length;
  }

  countVisibility(visibility: DocumentVisibility): number {
    return this.documents.filter(document => document.visibility === visibility).length;
  }

  get recentDocuments(): Document[] {
    return [...this.documents].sort((first, second) => this.dateValue(second.createdAt) - this.dateValue(first.createdAt)).slice(0, 5);
  }

  getDocumentAuthor(document: Document): string {
    if (document.createdBy) {
      return [document.createdBy.name || document.createdBy.firstName, document.createdBy.familyName || document.createdBy.lastName]
        .filter(Boolean).join(' ') || document.createdBy.email || 'Unknown user';
    }
    return document.author || 'Unknown user';
  }

  private dateValue(date: string | Date | undefined): number {
    return date ? new Date(date).getTime() : 0;
  }

}
