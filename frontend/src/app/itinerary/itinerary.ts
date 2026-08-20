import {
  Component,
  inject,
  signal
} from '@angular/core';

import { ActivatedRoute } from '@angular/router';

import { TripService } from '../trip/trip.service';

import { ItineraryDto } from '../trip/trip.models';

@Component({
  selector: 'app-itinerary',
  imports: [],
  templateUrl: './itinerary.html',
  styleUrl: './itinerary.css'
})
export class Itinerary {

  private readonly route = inject(ActivatedRoute);
  private readonly tripService = inject(TripService);

  itinerary = signal<ItineraryDto | null>(null);
  loading = signal(true);
  error = signal(false);

  constructor() {

    const tripId = Number(
      this.route.snapshot.paramMap.get('id')
    );

    this.tripService
      .getItinerary(tripId)
      .subscribe({
        next: itinerary => {
          this.itinerary.set(itinerary);
          this.loading.set(false);
        },
        error: error => {
          console.error(
            'Erreur lors du chargement de l’itinéraire :',
            error
          );

          this.error.set(true);
          this.loading.set(false);
        }
      });
  }
}