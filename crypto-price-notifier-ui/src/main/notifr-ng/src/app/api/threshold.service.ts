import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable, Subscriber} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable()
export class ThresholdService {

  constructor(private http: HttpClient) {
  }

  defineThreshold(pair: string, limit: number): Observable<void> {
    return this.http.put<void>('/alert', null,
      {
        params: new HttpParams().set('pair', pair).set('limit', limit + ''),
        observe: 'body'
      });
  }

}
