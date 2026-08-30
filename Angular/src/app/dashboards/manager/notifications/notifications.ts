import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppNotification, NotificationService } from '../../../Services/notification-service';

@Component({
  selector: 'app-notifications-employee',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css',
})
export class Notifications implements OnInit {
  private readonly notificationService = inject(NotificationService);
  private readonly cdr = inject(ChangeDetectorRef);

  notifications: AppNotification[] = [];
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading = true;
    this.notificationService.getAll().subscribe({
      next: notifications => {
        this.notifications = notifications;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Unable to load notifications.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  markAsRead(notification: AppNotification): void {
    if (notification.read) return;
    this.notificationService.markAsRead(notification.id).subscribe(() => {
      notification.read = true;
      this.cdr.markForCheck();
    });
  }

  deleteNotification(notification: AppNotification): void {
    this.notificationService.delete(notification.id).subscribe(() => {
      this.notifications = this.notifications.filter(item => item.id !== notification.id);
      this.cdr.markForCheck();
    });
  }

}
