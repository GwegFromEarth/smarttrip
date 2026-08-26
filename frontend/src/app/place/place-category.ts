export const PLACE_CATEGORIES = {
  ATTRACTION: 'tourism.attraction',
  MUSEUM: 'entertainment.museum',
  RESTAURANT: 'catering.restaurant'
} as const;

export type PlaceCategory =
  typeof PLACE_CATEGORIES[keyof typeof PLACE_CATEGORIES];