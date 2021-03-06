import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable, Subscriber, throwError} from "rxjs";
import {Injectable} from "@angular/core";
import {ThresholdEvent} from "./threshold-event.model";
import {WebSocketSubject} from "rxjs/webSocket";
import {Threshold} from "./threshold.model";
import {catchError} from "rxjs/operators";
import {MatSnackBar} from "@angular/material";

@Injectable()
export class ThresholdService {

  constructor(private http: HttpClient, private snackBar: MatSnackBar) {
  }

  defineThreshold(pair: string, limit: number): Observable<void> {
    return this.http.put<void>('/alert', null,
      {
        params: new HttpParams().set('pair', pair).set('limit', limit + ''),
        observe: 'body'
      }).pipe(catchError(err => this.showError(err)));
  }

  getThresholdsStream(): Observable<ThresholdEvent> {
    const host = document.location.hostname;
    const port = document.location.port;
    const url = `ws://${host}:${port}/thresholds`;
    return new WebSocketSubject<any>(url);
  }

  removeThreshold(threshold: Threshold): Observable<void> {
    const pair = `${threshold.pair.base}-${threshold.pair.counter}`;
    return this.http.delete<void>('/alert', {
      params: new HttpParams().set('pair', pair).set('limit', threshold.limit + ''),
      observe: 'body'
    }).pipe(catchError(err => this.showError(err)));
  }

  //TODO: should be in another place
  showError(err: any): Observable<never> {
    console.error(err);
    this.snackBar.open('Something went wrong...');
    return throwError(err);

  }

}
