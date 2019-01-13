import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ThresholdRequestPanelComponent } from './threshold-request-panel.component';

describe('ThresholdRequestPanelComponent', () => {
  let component: ThresholdRequestPanelComponent;
  let fixture: ComponentFixture<ThresholdRequestPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ThresholdRequestPanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ThresholdRequestPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
