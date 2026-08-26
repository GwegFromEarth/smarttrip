import {
  Component,
  inject,
  signal
} from '@angular/core';

import { ActivatedRoute } from '@angular/router';

import { TripService } from '../trip/trip.service';
import { ItineraryDto } from '../trip/trip.models';

import { PlaceService } from '../place/place.service';
import { Place } from '../place/place.models';

@Component({
  selector: 'app-itinerary',
  imports: [],
  templateUrl: './itinerary.html',
  styleUrl: './itinerary.css'
})
export class Itinerary {

  private readonly route = inject(ActivatedRoute);
  private readonly tripService = inject(TripService);
  private readonly placeService = inject(PlaceService);

  private readonly tripId = Number(
    this.route.snapshot.paramMap.get('id')
  );

  itinerary = signal<ItineraryDto | null>(null);
  places = signal<Place[]>([]);

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

          this.loadPlaces(itinerary.destination);
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

  private loadPlaces(destination: string): void {
    this.placeService
      .searchByDestination(
        destination,
        'tourism.attraction',
        1000,
        10
      )
      .subscribe({
        next: places => {
          this.places.set(places);
        },
        error: error => {
          console.error(
            'Erreur lors du chargement des lieux :',
            error
          );
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

          this.loadPlaces(itinerary.destination);
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