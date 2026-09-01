import { Component, inject } from '@angular/core';
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

  places: Place[] = [];

  loading = false;
  errorMessage: string | null = null;

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
      this.errorMessage = 'Veuillez saisir une destination.';
      this.places = [];
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.places = [];

    this.placeService.searchByDestination(
      destination,
      this.selectedCategory
    ).subscribe({
      next: places => {
        this.places = places;
        this.loading = false;
      },
      error: error => {
        this.loading = false;

        if (error.status === 429) {
          this.errorMessage =
            'Le service est temporairement limité. '
            + 'Veuillez réessayer dans quelques instants.';
        } else {
          this.errorMessage =
            error.error?.detail ??
            'Une erreur est survenue lors de la recherche.';
        }
      }
    });
  }
}