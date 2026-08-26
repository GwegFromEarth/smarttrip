import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { Itinerary } from './itinerary';
import { TripService } from '../trip/trip.service';
import { ItineraryDto } from '../trip/trip.models';
import { PlaceService } from '../place/place.service';
import { Place } from '../place/place.models';

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

  const places: Place[] = [
    {
      placeId: 'colosseum-test-id',
      name: 'Colosseum',
      description: 'Ancient Roman amphitheatre',
      latitude: 41.8902,
      longitude: 12.4922,
      category: 'tourism.attraction',
      address: 'Piazza del Colosseo, 1, 00184 Roma RM, Italy'
    },
    {
      placeId: 'pantheon-test-id',
      name: 'Pantheon',
      description: 'Ancient Roman temple',
      latitude: 41.8986,
      longitude: 12.4769,
      category: 'tourism.attraction',
      address: 'Piazza della Rotonda, 00186 Roma RM, Italy'
    }
  ];

  function configureTestBed(
    tripService: {
      getItinerary: ReturnType<typeof vi.fn>;
      generateItinerary: ReturnType<typeof vi.fn>;
    },
    placeService: {
      searchByDestination: ReturnType<typeof vi.fn>;
    }
  ) {
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
        },
        {
          provide: PlaceService,
          useValue: placeService
        }
      ]
    });
  }

  it('should load itinerary from route parameter', async () => {

    const tripService = {
      getItinerary: vi.fn().mockReturnValue(of(itinerary)),
      generateItinerary: vi.fn()
    };

    const placeService = {
      searchByDestination: vi.fn().mockReturnValue(of([]))
    };

    configureTestBed(
      tripService,
      placeService
    );

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

    const placeService = {
      searchByDestination: vi.fn().mockReturnValue(of([]))
    };

    configureTestBed(
      tripService,
      placeService
    );

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

    const placeService = {
      searchByDestination: vi.fn().mockReturnValue(of([]))
    };

    configureTestBed(
      tripService,
      placeService
    );

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

  it('should load places for the itinerary destination', async () => {

    const tripService = {
      getItinerary: vi.fn().mockReturnValue(of(itinerary)),
      generateItinerary: vi.fn()
    };

    const placeService = {
      searchByDestination: vi.fn().mockReturnValue(
        of(places)
      )
    };

    configureTestBed(
      tripService,
      placeService
    );

    const harness = await RouterTestingHarness.create();

    const component = await harness.navigateByUrl(
      '/trips/4/itinerary',
      Itinerary
    );

    expect(placeService.searchByDestination)
      .toHaveBeenCalledWith(
        'Rome',
        'tourism.attraction',
        1000,
        10
      );
  });
});