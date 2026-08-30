import {
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet
} from '@angular/router';
import { AuthService } from '../../Services/auth-service';
import { NotificationService } from '../../Services/notification-service';

@Component({
  selector: 'app-manager',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive,
    RouterOutlet
  ],
  templateUrl: './manager.html',
  styleUrl: './manager.css',
})
export class Manager  implements OnInit{
   sidebarOpen = true;

  currentUser: any;
  unreadNotifications = 0;
  profileMenuOpen = false;

  getProfileImageUrl(url: string | null | undefined): string | null {
    if (!url || !url.trim()) return null;
    return /^https?:\/\//i.test(url)
      ? url
      : `http://localhost:3000${url.startsWith('/') ? '' : '/'}${url}`;
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.currentUser = this.authService.getCurrentUser();
    this.loadUnreadNotifications();
  }

  loadUnreadNotifications(): void {
    this.notificationService.getUnreadCount().subscribe({
      next: count => this.unreadNotifications = count,
      error: () => this.unreadNotifications = 0
    });
  }

  toggleProfileMenu(): void {
    this.profileMenuOpen = !this.profileMenuOpen;
  }

  closeProfileMenu(): void {
    this.profileMenuOpen = false;
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  navigateTo(path: string, event: Event): void {
    event.preventDefault();
    this.router.navigateByUrl(path);
  }

  isActive(path: string): boolean {
    return this.router.url === path || this.router.url.startsWith(`${path}/`);
  }

  logout(): void {
    this.closeProfileMenu();
    this.authService.logout();
    this.router.navigate(['/login']);
  }

}
