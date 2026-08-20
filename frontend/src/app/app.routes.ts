import { Routes } from '@angular/router';

import { Itinerary } from './itinerary/itinerary';
import { Chat } from './chat/chat';

export const routes: Routes = [
  {
    path: '',
    component: Chat,
    title: 'SmartTrip - Chat'
  },
  {
    path: 'trips/:id/itinerary',
    component: Itinerary,
    title: 'SmartTrip - Itinéraire'
  }
];
