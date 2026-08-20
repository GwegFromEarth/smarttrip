import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';

import { TripService } from '../trip.service';

@Component({
  selector: 'app-create-trip',
  imports: [ReactiveFormsModule],
  templateUrl: './create-trip.html',
  styleUrl: './create-trip.css'
})
export class CreateTrip {

  private readonly fb = inject(FormBuilder);
  private readonly tripService = inject(TripService);
  private readonly router = inject(Router);

  isSubmitting = signal(false);
  error = signal<string | null>(null);

  tripForm = this.fb.group({
    destination: ['', Validators.required],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    travelers: [1, [
      Validators.required,
      Validators.min(1)
    ]],
    preferences: ['']
  });

  submit(): void {

    if (this.tripForm.invalid) {
      this.tripForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.error.set(null);

    const formValue = this.tripForm.getRawValue();

    this.tripService.createTrip({
      destination: formValue.destination!,
      startDate: formValue.startDate!,
      endDate: formValue.endDate!,
      travelers: formValue.travelers!,
      preferences: formValue.preferences ?? ''
    }).subscribe({

      next: () => {
        this.router.navigate(['/trips']);
      },

      error: error => {
        console.error(
          'Erreur lors de la création du voyage :',
          error
        );

        this.error.set(
          'Impossible de créer le voyage.'
        );

        this.isSubmitting.set(false);
      }
    });
  }
}