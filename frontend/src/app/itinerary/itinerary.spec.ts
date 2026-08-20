import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';
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

  it('should load itinerary from route parameter', async () => {

    const tripService = {
      getItinerary: vi.fn().mockReturnValue(of(itinerary))
    };

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
});