import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {
  MatButtonModule,
  MatInputModule,
  MatNativeDateModule,
  MatSnackBarModule
} from "@angular/material";

import {AppComponent} from './app.component';
import {ThresholdRequestPanelComponent} from './threshold-request-panel/threshold-request-panel.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {ThresholdService} from "./api/threshold.service";
import { AlertsComponent } from './alerts/alerts.component';

@NgModule({
  declarations: [
    AppComponent,
    ThresholdRequestPanelComponent,
    AlertsComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    FormsModule,
    HttpClientModule,
    MatNativeDateModule,
    ReactiveFormsModule
  ],
  providers: [ThresholdService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
