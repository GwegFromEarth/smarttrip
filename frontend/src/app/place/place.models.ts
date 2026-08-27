import { PlaceCategory } from './place-category';

export interface Place {
  placeId: string;
  name: string;
  description: string | null;
  latitude: number;
  longitude: number;
  category: PlaceCategory;
  address: string | null;
  distance: number | null;
}