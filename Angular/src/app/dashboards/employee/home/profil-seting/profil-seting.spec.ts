import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfilSeting } from './profil-seting';

describe('ProfilSeting', () => {
  let component: ProfilSeting;
  let fixture: ComponentFixture<ProfilSeting>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfilSeting]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfilSeting);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
