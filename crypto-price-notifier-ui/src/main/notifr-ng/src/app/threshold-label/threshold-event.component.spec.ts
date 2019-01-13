import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ThresholdEventComponent } from './threshold-event.component';

describe('ThresholdEventComponent', () => {
  let component: ThresholdEventComponent;
  let fixture: ComponentFixture<ThresholdEventComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ThresholdEventComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ThresholdEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
