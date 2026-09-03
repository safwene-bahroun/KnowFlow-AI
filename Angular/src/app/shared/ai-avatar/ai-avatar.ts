import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

export type AiAvatarState = 'idle' | 'listening' | 'processing' | 'success' | 'error';

@Component({
  selector: 'app-ai-avatar',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './ai-avatar.html',
  styleUrl: './ai-avatar.css'
})
export class AiAvatar {
  @Input() state: AiAvatarState = 'idle';
  @Input() size: 'tiny' | 'small' | 'medium' | 'large' = 'medium';
}
