import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ThresholdService} from "../api/threshold.service";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-threshold-request-panel',
  templateUrl: './threshold-request-panel.component.html',
  styleUrls: ['./threshold-request-panel.component.scss']
})
export class ThresholdRequestPanelComponent {

  form: FormGroup;

  constructor(private alertService: ThresholdService,
              private snackBar: MatSnackBar,
              fb: FormBuilder) {
    this.form = fb.group({
      'pair': 'BTC-USD',
      'limit': 500
    });
  }

  requestAlerts() {
    const {pair, limit} = this.form.value;
    this.alertService.defineThreshold(pair, limit).subscribe(_ => this.openSnackBar('wait for alerts'));
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, '', {
      duration: 2000,
    });
  }

}
