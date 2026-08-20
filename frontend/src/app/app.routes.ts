import { Routes } from '@angular/router';

import { Itinerary } from './itinerary/itinerary';
import { Chat } from './chat/chat';
import { Trips } from './trip/trips/trips';
import { CreateTrip } from './trip/create-trip/create-trip';

export const routes: Routes = [
  {
    path: '',
    component: Chat,
    title: 'SmartTrip - Chat'
  },
  {
    path: 'trips',
    component: Trips,
    title: 'SmartTrip - Mes voyages'
  },
  {
    path: 'trips/create',
    component: CreateTrip,
    title: 'SmartTrip - Créer un voyage'
  },
  {
    path: 'trips/:id/itinerary',
    component: Itinerary,
    title: 'SmartTrip - Itinéraire'
  }
];
