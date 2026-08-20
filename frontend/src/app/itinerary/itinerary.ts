import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ItineraryDto } from '../trip/trip.models';
import { TripService } from '../trip/trip.service';

@Component({
  selector: 'app-itinerary',
  imports: [],
  templateUrl: './itinerary.html',
  styleUrl: './itinerary.css',
})
export class Itinerary {

  private readonly route = inject(ActivatedRoute);
  private readonly tripService = inject(TripService);

  itinerary = signal<ItineraryDto | null>(null);

  constructor() {
    this.route.paramMap.subscribe(params => {

      const id = params.get('id');

      if (!id) {
        return;
      }

      this.tripService
        .getItinerary(Number(id))
        .subscribe({
          next: itinerary => {
            this.itinerary.set(itinerary);
          },
          error: error => {
            console.error(
              'Erreur lors de la récupération de l’itinéraire :',
              error
            );
          }
        });
    });
  }
}