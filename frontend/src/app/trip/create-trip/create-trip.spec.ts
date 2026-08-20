import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { CreateTrip } from './create-trip';
import { Trips } from '../trips/trips';

describe('CreateTrip', () => {

  let component: CreateTrip;
  let fixture: ComponentFixture<CreateTrip>;
  let httpTesting: HttpTestingController;
  let router: Router;

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [CreateTrip],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([
          {
            path: 'trips',
            component: Trips
          }
        ])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateTrip);
    component = fixture.componentInstance;

    httpTesting = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have an invalid form initially', () => {
    expect(component.tripForm.invalid).toBe(true);
  });

  it('should reject an empty destination', () => {

    component.tripForm.patchValue({
      startDate: '2026-09-12',
      endDate: '2026-09-17',
      travelers: 2
    });

    expect(component.tripForm.invalid).toBe(true);
    expect(
      component.tripForm.controls.destination.invalid
    ).toBe(true);
  });

  it('should reject zero travelers', () => {

    component.tripForm.patchValue({
      destination: 'Rome',
      startDate: '2026-09-12',
      endDate: '2026-09-17',
      travelers: 0
    });

    expect(component.tripForm.invalid).toBe(true);
    expect(
      component.tripForm.controls.travelers.invalid
    ).toBe(true);
  });

  it('should create a trip and navigate to trips', async () => {

    component.tripForm.setValue({
      destination: 'Rome',
      startDate: '2026-09-12',
      endDate: '2026-09-17',
      travelers: 2,
      preferences: 'culture, histoire'
    });

    component.submit();

    const request = httpTesting.expectOne(
      'http://localhost:8080/api/trips'
    );

    expect(request.request.method).toBe('POST');

    expect(request.request.body).toEqual({
      destination: 'Rome',
      startDate: '2026-09-12',
      endDate: '2026-09-17',
      travelers: 2,
      preferences: 'culture, histoire'
    });

    request.flush({
      id: 1,
      destination: 'Rome',
      startDate: '2026-09-12',
      endDate: '2026-09-17',
      travelers: 2,
      preferences: 'culture, histoire'
    });

    await fixture.whenStable();

    expect(router.url).toBe('/trips');
  });

  it('should display an error when trip creation fails', () => {

    component.tripForm.setValue({
      destination: 'Rome',
      startDate: '2026-09-12',
      endDate: '2026-09-17',
      travelers: 2,
      preferences: 'culture'
    });

    component.submit();

    const request = httpTesting.expectOne(
      'http://localhost:8080/api/trips'
    );

    request.flush(
      'Erreur serveur',
      {
        status: 500,
        statusText: 'Internal Server Error'
      }
    );

    expect(component.error()).toBe(
      'Impossible de créer le voyage.'
    );

    expect(component.isSubmitting()).toBe(false);
  });
});