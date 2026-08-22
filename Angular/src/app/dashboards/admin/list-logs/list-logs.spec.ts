import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListLogs } from './list-logs';

describe('ListLogs', () => {
  let component: ListLogs;
  let fixture: ComponentFixture<ListLogs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListLogs]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListLogs);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
