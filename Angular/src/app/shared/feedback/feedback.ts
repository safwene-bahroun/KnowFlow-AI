import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FeedbackService, FeedbackType, FeedbackResponse } from '../../Services/feedback-service';
import { AuthService } from '../../Services/auth-service';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feedback.html',
  styleUrl: './feedback.css'
})
export class Feedback implements OnInit {
  private readonly feedbackService = inject(FeedbackService);
  private readonly authService = inject(AuthService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  type: FeedbackType = 'POSITIVE';
  comment = '';
  loading = false;
  submitted = false;
  errorMessage = '';
  feedbacks: FeedbackResponse[] = [];

  get isAdmin(): boolean {
    return this.authService.getCurrentUser()?.role?.toUpperCase() === 'ADMIN';
  }

  ngOnInit(): void {
    if (this.isAdmin) {
      this.loadFeedbacks();
    }
  }

  submit(): void {
    if (!this.comment.trim() || this.loading) return;
    this.loading = true;
    this.errorMessage = '';
    this.feedbackService.submit({ type: this.type, comment: this.comment.trim() }).subscribe({
      next: () => {
        this.comment = '';
        this.submitted = true;
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Unable to send feedback.';
        this.loading = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  loadFeedbacks(): void {
    this.loading = true;
    this.feedbackService.getAll().subscribe({
      next: feedbacks => { this.feedbacks = feedbacks; this.loading = false; this.changeDetector.markForCheck(); },
      error: () => { this.errorMessage = 'Unable to load feedback.'; this.loading = false; this.changeDetector.markForCheck(); }
    });
  }

  deleteFeedback(feedback: FeedbackResponse): void {
    if (!confirm('Delete this feedback?')) return;
    this.feedbackService.delete(feedback.id).subscribe({
      next: () => {
        this.feedbacks = this.feedbacks.filter(item => item.id !== feedback.id);
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Unable to delete feedback.';
        this.changeDetector.markForCheck();
      }
    });
  }
}
