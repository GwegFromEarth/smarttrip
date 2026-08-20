import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { Itinerary } from './itinerary';
import { TripService } from '../trip/trip.service';
import { ItineraryDto } from '../trip/trip.models';

describe('Itinerary', () => {

  const itinerary: ItineraryDto = {
    tripId: 4,
    destination: 'Rome',
    days: [
      {
        dayNumber: 1,
        date: '2026-09-12',
        activities: [
          {
            time: '09:00 - 17:00',
            title: 'Basilique Saint-Pierre et Vatican',
            description: 'Visite du Vatican.',
            location: 'Vatican City, Rome'
          }
        ]
      }
    ]
  };

  function configureTestBed(tripService: {
    getItinerary: ReturnType<typeof vi.fn>;
    generateItinerary: ReturnType<typeof vi.fn>;
  }) {
    TestBed.configureTestingModule({
      imports: [Itinerary],
      providers: [
        provideRouter([
          {
            path: 'trips/:id/itinerary',
            component: Itinerary
          }
        ]),
        {
          provide: TripService,
          useValue: tripService
        }
      ]
    });
  }

  it('should load itinerary from route parameter', async () => {

    const tripService = {
      getItinerary: vi.fn().mockReturnValue(of(itinerary)),
      generateItinerary: vi.fn()
    };

    configureTestBed(tripService);

    const harness = await RouterTestingHarness.create();

    const component = await harness.navigateByUrl(
      '/trips/4/itinerary',
      Itinerary
    );

    expect(component).toBeTruthy();

    expect(tripService.getItinerary)
      .toHaveBeenCalledWith(4);

    expect(component.itinerary())
      .toEqual(itinerary);
  });

  it('should offer itinerary generation when itinerary does not exist', async () => {

    const tripService = {
      getItinerary: vi.fn().mockReturnValue(
        throwError(() => ({
          status: 404
        }))
      ),
      generateItinerary: vi.fn()
    };

    configureTestBed(tripService);

    const harness = await RouterTestingHarness.create();

    const component = await harness.navigateByUrl(
      '/trips/4/itinerary',
      Itinerary
    );

    expect(component.needsGeneration())
      .toBe(true);

    expect(harness.routeNativeElement?.textContent)
      .toContain('Générer mon itinéraire');
  });

  it('should generate itinerary', async () => {

    const tripService = {
      getItinerary: vi.fn().mockReturnValue(
        throwError(() => ({
          status: 404
        }))
      ),
      generateItinerary: vi.fn().mockReturnValue(
        of(itinerary)
      )
    };

    configureTestBed(tripService);

    const harness = await RouterTestingHarness.create();

    const component = await harness.navigateByUrl(
      '/trips/4/itinerary',
      Itinerary
    );

    const button = harness.routeNativeElement
      ?.querySelector('button');

    expect(button).not.toBeNull();
    expect(button?.textContent)
      .toContain('Générer mon itinéraire');

    button?.click();

    expect(tripService.generateItinerary)
      .toHaveBeenCalledWith(4);

    expect(component.itinerary())
      .toEqual(itinerary);

    expect(component.needsGeneration())
      .toBe(false);

    expect(component.generating())
      .toBe(false);
  });
});