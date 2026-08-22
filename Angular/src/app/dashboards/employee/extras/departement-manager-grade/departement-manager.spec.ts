import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepartementManager } from './departement-manager';

describe('DepartementManager', () => {
  let component: DepartementManager;
  let fixture: ComponentFixture<DepartementManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepartementManager]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DepartementManager);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
