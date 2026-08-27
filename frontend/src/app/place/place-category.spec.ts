import {
  PLACE_CATEGORIES,
  PlaceCategory
} from './place-category';

describe('PLACE_CATEGORIES', () => {

  it('should contain all supported place categories', () => {
    expect(PLACE_CATEGORIES.TOURIST_ATTRACTION)
      .toBe('TOURIST_ATTRACTION');

    expect(PLACE_CATEGORIES.MUSEUM)
      .toBe('MUSEUM');

    expect(PLACE_CATEGORIES.RESTAURANT)
      .toBe('RESTAURANT');

    expect(PLACE_CATEGORIES.CAFE)
      .toBe('CAFE');

    expect(PLACE_CATEGORIES.PARK)
      .toBe('PARK');
  });

  it('should expose valid PlaceCategory values', () => {
    const categories: PlaceCategory[] = [
      PLACE_CATEGORIES.TOURIST_ATTRACTION,
      PLACE_CATEGORIES.MUSEUM,
      PLACE_CATEGORIES.RESTAURANT,
      PLACE_CATEGORIES.CAFE,
      PLACE_CATEGORIES.PARK
    ];

    expect(categories.length).toBe(5);
  });
});