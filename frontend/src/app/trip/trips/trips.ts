import { Component, inject, OnInit, signal } from '@angular/core';

import { TripResponse } from '../trip.models';
import { TripService } from '../trip.service';

@Component({
  selector: 'app-trips',
  imports: [],
  templateUrl: './trips.html',
  styleUrl: './trips.css'
})
export class Trips implements OnInit {

  private readonly tripService = inject(TripService);

  trips = signal<TripResponse[]>([]);
  isLoading = signal(false);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.loadTrips();
  }

  private loadTrips(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.tripService.getTrips().subscribe({
      next: trips => {
        this.trips.set(trips);
        this.isLoading.set(false);
      },
      error: error => {
        console.error(
          'Erreur lors du chargement des voyages :',
          error
        );

        this.error.set(
          'Impossible de charger les voyages.'
        );

        this.isLoading.set(false);
      }
    });
  }
}