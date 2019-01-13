import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ThresholdEvent} from "../api/threshold-event.model";
import {Threshold} from "../api/threshold.model";

@Component({
  selector: 'app-threshold-label',
  templateUrl: './threshold-event.component.html',
  styleUrls: ['./threshold-event.component.scss']
})
export class ThresholdEventComponent {

  threshold: Threshold;
  deleted: boolean;

  @Output()
  cancel = new EventEmitter<Threshold>();

  @Input()
  set thresholdEvent(thresholdEvent: ThresholdEvent) {
    this.threshold = thresholdEvent.threshold;
    this.deleted = thresholdEvent.deleted;
  }

  cancelThreshold() {
    this.cancel.emit(this.threshold);
  }

}
