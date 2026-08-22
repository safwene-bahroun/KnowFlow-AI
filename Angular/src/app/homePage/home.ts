import { Component, HostListener, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {

  // ─── Navbar scroll ───
  isScrolled = false;

  // ─── Mobile menu ───
  mobileMenuOpen = false;

  // ─── Modal ───
  modalOpen = false;
  activeTab: 'login' | 'register' = 'login';

  // ─── Login form ───
  loginEmail = '';
  loginPassword = '';

  // ─── Register form ───
  regFirst = '';
  regLast = '';
  regEmail = '';
  regDept = '';
  regPassword = '';

  // ─── Toast ───
  toastVisible = false;
  toastMessage = '';
  private toastTimer: any;

  @HostListener('window:scroll')
  onScroll(): void {
    this.isScrolled = window.scrollY > 20;
  }

  @HostListener('document:keydown.escape')
  onEsc(): void {
    this.closeModal();
  }

  toggleMobile(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobile(): void {
    this.mobileMenuOpen = false;
  }

  // ─── Modal methods ───
  openModal(type: 'login' | 'register'): void {
    this.modalOpen = true;
    this.activeTab = type;
    document.body.style.overflow = 'hidden';
  }

  closeModal(event?: Event): void {
    if (event && event.target !== event.currentTarget) return;
    this.modalOpen = false;
    document.body.style.overflow = '';
  }

  switchTab(type: 'login' | 'register'): void {
    this.activeTab = type;
  }

  // ─── Form handlers ───
  handleLogin(): void {
    if (!this.loginEmail || !this.loginPassword) {
      this.showToast('Please fill in all fields');
      return;
    }

    if (!this.loginEmail.includes('@capgemini.com')) {
      this.showToast('Please use your Capgemini email address');
      return;
    }

    this.modalOpen = false;
    document.body.style.overflow = '';
    this.showToast('Welcome back! Redirecting to chat...');
  }

  handleRegister(): void {
    if (!this.regFirst || !this.regLast || !this.regEmail || !this.regDept || !this.regPassword) {
      this.showToast('Please fill in all fields');
      return;
    }

    if (!this.regEmail.includes('@capgemini.com')) {
      this.showToast('Registration is limited to @capgemini.com emails');
      return;
    }

    if (this.regPassword.length < 8) {
      this.showToast('Password must be at least 8 characters');
      return;
    }

    this.modalOpen = false;
    document.body.style.overflow = '';
    this.showToast('Account created! Check your email to verify.');
  }

  // ─── Toast ───
  showToast(message: string): void {
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
    }

    this.toastMessage = message;
    this.toastVisible = true;

    this.toastTimer = setTimeout(() => {
      this.toastVisible = false;
    }, 3500);
  }
}




