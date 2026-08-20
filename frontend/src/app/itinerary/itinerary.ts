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

  private readonly tripId = Number(
    this.route.snapshot.paramMap.get('id')
  );

  itinerary = signal<ItineraryDto | null>(null);
  loading = signal(true);
  generating = signal(false);
  needsGeneration = signal(false);
  error = signal(false);

  constructor() {
    this.loadItinerary();
  }

  private loadItinerary(): void {
    this.loading.set(true);
    this.error.set(false);
    this.needsGeneration.set(false);

    this.tripService
      .getItinerary(this.tripId)
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

          this.loading.set(false);

          if (error.status === 404) {
            this.needsGeneration.set(true);
          } else {
            this.error.set(true);
          }
        }
      });
  }

  generateItinerary(): void {
    this.generating.set(true);
    this.error.set(false);

    this.tripService
      .generateItinerary(this.tripId)
      .subscribe({
        next: itinerary => {
          this.itinerary.set(itinerary);
          this.needsGeneration.set(false);
          this.generating.set(false);
        },
        error: error => {
          console.error(
            'Erreur lors de la génération de l’itinéraire :',
            error
          );

          this.generating.set(false);
          this.error.set(true);
        }
      });
  }
}