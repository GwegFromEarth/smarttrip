import { Routes } from '@angular/router';

import { Itinerary } from './itinerary/itinerary';
import { Chat } from './chat/chat';
import { Trips } from './trip/trips/trips';
import { CreateTrip } from './trip/create-trip/create-trip';
import { PlaceSearch } from './place-search/place-search';

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
  },
  {
    path: 'places',
    component: PlaceSearch,
    title: 'SmartTrip - Découvrir des lieux'
  },
];
