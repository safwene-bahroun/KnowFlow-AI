import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetingProfile } from './seting-profile';

describe('SetingProfile', () => {
  let component: SetingProfile;
  let fixture: ComponentFixture<SetingProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetingProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetingProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
