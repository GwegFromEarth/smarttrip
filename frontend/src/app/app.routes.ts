import { Routes } from '@angular/router';

import { Itinerary } from './itinerary/itinerary';
import { Chat } from './chat/chat';
import { Trips } from './trip/trips/trips';

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
    path: 'trips/:id/itinerary',
    component: Itinerary,
    title: 'SmartTrip - Itinéraire'
  }
];
