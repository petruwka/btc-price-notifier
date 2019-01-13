import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MyThresholdsComponent } from './my-thresholds.component';

describe('MyThresholdsComponent', () => {
  let component: MyThresholdsComponent;
  let fixture: ComponentFixture<MyThresholdsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MyThresholdsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MyThresholdsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
