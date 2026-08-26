import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Place } from './place.models';

@Injectable({
  providedIn: 'root'
})
export class PlaceService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/places';

  searchByDestination(
    destination: string,
    category: string,
    radius = 1000,
    limit = 10
  ): Observable<Place[]> {

    const params = new HttpParams()
      .set('destination', destination)
      .set('category', category)
      .set('radius', radius)
      .set('limit', limit);

    return this.http.get<Place[]>(
      `${this.apiUrl}/by-destination`,
      { params }
    );
  }
}