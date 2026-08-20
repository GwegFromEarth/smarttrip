import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ItineraryDto } from './trip.models';

@Injectable({
  providedIn: 'root'
})
export class TripService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/trips';

  generateItinerary(tripId: number): Observable<ItineraryDto> {
    return this.http.post<ItineraryDto>(
      `${this.apiUrl}/${tripId}/itinerary`,
      {}
    );
  }

  getItinerary(tripId: number): Observable<ItineraryDto> {
    return this.http.get<ItineraryDto>(
      `${this.apiUrl}/${tripId}/itinerary`
    );
  }
}