import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { PlaceService } from './place.service';
import { Place } from './place.models';
import { PLACE_CATEGORIES } from './place-category';

describe('PlaceService', () => {
  let service: PlaceService;
  let httpTesting: HttpTestingController;

  const places: Place[] = [
    {
      placeId: 'colosseum-test-id',
      name: 'Colosseum',
      description: 'Ancient Roman amphitheatre',
      latitude: 41.8902,
      longitude: 12.4922,
      category: PLACE_CATEGORIES.TOURIST_ATTRACTION,
      address: 'Piazza del Colosseo, 1, 00184 Roma RM, Italy',
      distance: 500
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PlaceService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(PlaceService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should search places by destination', () => {
    service.searchByDestination(
      'Rome',
      PLACE_CATEGORIES.TOURIST_ATTRACTION,
      1000,
      10
    ).subscribe(result => {
      expect(result).toEqual(places);
    });

    const request = httpTesting.expectOne(
      request =>
        request.url ===
          'http://localhost:8080/api/places/by-destination' &&
        request.method === 'GET'
    );

    expect(request.request.params.get('destination'))
      .toBe('Rome');

    expect(request.request.params.get('category'))
      .toBe('TOURIST_ATTRACTION');

    expect(request.request.params.get('radius'))
      .toBe('1000');

    expect(request.request.params.get('limit'))
      .toBe('10');

    request.flush(places);
  });

  it('should use default radius and limit', () => {
    service.searchByDestination(
      'Rome',
      PLACE_CATEGORIES.TOURIST_ATTRACTION
    ).subscribe(result => {
      expect(result).toEqual([]);
    });

    const request = httpTesting.expectOne(
      request =>
        request.url ===
          'http://localhost:8080/api/places/by-destination' &&
        request.method === 'GET'
    );

    expect(request.request.params.get('destination'))
      .toBe('Rome');

    expect(request.request.params.get('category'))
      .toBe('TOURIST_ATTRACTION');

    expect(request.request.params.get('radius'))
      .toBe('1000');

    expect(request.request.params.get('limit'))
      .toBe('10');

    request.flush([]);
  });

  it('should propagate HTTP errors', () => {
    service.searchByDestination(
      'Rome',
      PLACE_CATEGORIES.TOURIST_ATTRACTION
    ).subscribe({
      next: () => expect.fail('Expected an HTTP error'),
      error: error => {
        expect(error.status).toBe(400);
        expect(error.error.detail)
          .toBe('Category must not be blank');
      }
    });

    const request = httpTesting.expectOne(
      request =>
        request.url ===
          'http://localhost:8080/api/places/by-destination' &&
        request.method === 'GET'
    );

    request.flush(
      {
        detail: 'Category must not be blank',
        instance: '/api/places/by-destination',
        status: 400,
        title: 'Invalid request'
      },
      {
        status: 400,
        statusText: 'Bad Request'
      }
    );
  });
});