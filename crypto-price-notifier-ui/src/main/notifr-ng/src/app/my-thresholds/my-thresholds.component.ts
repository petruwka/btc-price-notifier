import {Component, OnDestroy, OnInit} from '@angular/core';
import {ThresholdService} from "../api/threshold.service";
import {ThresholdEvent} from "../api/threshold-event.model";
import {Subscription} from "rxjs";
import {Threshold} from "../api/threshold.model";

@Component({
  selector: 'app-my-thresholds',
  templateUrl: './my-thresholds.component.html',
  styleUrls: ['./my-thresholds.component.scss']
})
export class MyThresholdsComponent implements OnInit, OnDestroy {

  thresholds: Array<ThresholdEvent> = [];

  private subscription: Subscription;

  constructor(private thresholdService: ThresholdService) {
  }

  ngOnInit() {
    this.subscription = this.thresholdService.getThresholdsStream().subscribe(thresholdEvent => {
      console.log(`threshold: ${JSON.stringify(thresholdEvent)}`);
      const {threshold, deleted} = thresholdEvent;
      const index = this.thresholds.findIndex(this.thresholdPredicate(threshold));
      const existing = index >= 0 ? this.thresholds[index] : null;
      if (deleted && existing == null || existing != null && deleted === existing.deleted) {
        return;
      }

      if (!deleted && existing == null) {
        this.thresholds.push(thresholdEvent);
      }

      if (deleted) {
        this.thresholds[index] = thresholdEvent;
        setTimeout(() => this.removeThreshold(thresholdEvent), 3000)
      }
    });
  }

  ngOnDestroy(): void {
    if (this.subscription != null) {
      this.subscription.unsubscribe()
    }
  }

  private thresholdPredicate(search: Threshold): (ThresholdEvent) => boolean {
    return (v: ThresholdEvent) => {
      const t = v.threshold;
      return t.limit === search.limit
        && t.pair.counter === search.pair.counter
        && t.pair.base === search.pair.base;
    };
  }

  private removeThreshold(thresholdEvent: ThresholdEvent) {
    const predicate = this.thresholdPredicate(thresholdEvent.threshold);
    const index = this.thresholds.findIndex(t => t.deleted === thresholdEvent.deleted && predicate(t));
    if (index >= 0) {
      this.thresholds.splice(index, 1)
    }
  }

  cancelSubscription(threshold: Threshold) {
    this.thresholdService.removeThreshold(threshold).subscribe();
  }
}
