import { Component, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { PlaceService } from '../place/place.service';
import {
  PLACE_CATEGORIES,
  PlaceCategory
} from '../place/place-category';
import { Place } from '../place/place.models';

@Component({
  selector: 'app-place-search',
  imports: [
    FormsModule,
    DecimalPipe
  ],
  templateUrl: './place-search.html',
  styleUrl: './place-search.css'
})
export class PlaceSearch {

  private readonly placeService = inject(PlaceService);

  destination = '';
  selectedCategory: PlaceCategory =
    PLACE_CATEGORIES.TOURIST_ATTRACTION;

  places = signal<Place[]>([]);

  loading = signal(false);
  errorMessage = signal<string | null>(null);

  readonly categories = [
    {
      value: PLACE_CATEGORIES.TOURIST_ATTRACTION,
      label: 'Lieux touristiques'
    },
    {
      value: PLACE_CATEGORIES.MUSEUM,
      label: 'Musées'
    },
    {
      value: PLACE_CATEGORIES.RESTAURANT,
      label: 'Restaurants'
    },
    {
      value: PLACE_CATEGORIES.CAFE,
      label: 'Cafés'
    },
    {
      value: PLACE_CATEGORIES.PARK,
      label: 'Parcs'
    }
  ];

  search(): void {

    const destination = this.destination.trim();

    if (!destination) {
      this.errorMessage.set(
        'Veuillez saisir une destination.'
      );
      this.places.set([]);
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);
    this.places.set([]);

    this.placeService.searchByDestination(
      destination,
      this.selectedCategory
    ).subscribe({
      next: places => {
        this.places.set(places);
        this.loading.set(false);
      },

      error: error => {
        this.loading.set(false);

        if (error.status === 429) {
          this.errorMessage.set(
            'Le service est temporairement limité. '
            + 'Veuillez réessayer dans quelques instants.'
          );
        } else {
          this.errorMessage.set(
            error.error?.detail ??
            'Une erreur est survenue lors de la recherche.'
          );
        }
      }
    });
  }
}