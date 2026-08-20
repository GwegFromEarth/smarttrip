import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  provideHttpClientTesting,
  HttpTestingController
} from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { TripService } from './trip.service';
import { ItineraryDto } from './trip.models';

describe('TripService', () => {

  let service: TripService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(TripService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should generate an itinerary', () => {

    const mockItinerary: ItineraryDto = {
      tripId: 1,
      destination: 'Rome',
      days: []
    };

    service.generateItinerary(1).subscribe(itinerary => {
      expect(itinerary).toEqual(mockItinerary);
    });

    const request = httpTesting.expectOne(
      'http://localhost:8080/api/trips/1/itinerary'
    );

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});

    request.flush(mockItinerary);
  });

  it('should get an itinerary', () => {

    const mockItinerary: ItineraryDto = {
      tripId: 1,
      destination: 'Rome',
      days: []
    };

    service.getItinerary(1).subscribe(itinerary => {
      expect(itinerary).toEqual(mockItinerary);
    });

    const request = httpTesting.expectOne(
      'http://localhost:8080/api/trips/1/itinerary'
    );

    expect(request.request.method).toBe('GET');

    request.flush(mockItinerary);
  });
});