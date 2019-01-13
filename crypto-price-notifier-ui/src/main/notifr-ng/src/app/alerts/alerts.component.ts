import {Component, OnInit} from '@angular/core';
import {WebSocketSubject} from "rxjs/webSocket";

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.scss']
})
export class AlertsComponent implements OnInit {

  private wsSubject: WebSocketSubject<any>;
  alerts = [];

  constructor() {

  }

  ngOnInit(): void {
    const host = document.location.hostname;
    const port = document.location.port;
    const url = `ws://${host}:${port}/alerts`;
    this.wsSubject = new WebSocketSubject<any>(url);
    // this.wsSubject = new WebSocketSubject<any>('ws://localhost:9090/alerts');
    this.wsSubject
      .subscribe(
        (message) => {
          console.log(message);
          this.alerts.push(message);
        },
        (err) => console.error(err),
        () => console.warn('Completed!')
      );

  }
}
