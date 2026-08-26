import { describe, expect, it } from 'vitest';

import {
  PLACE_CATEGORIES
} from './place-category';

describe('PLACE_CATEGORIES', () => {

  it('should define the attraction category', () => {
    expect(PLACE_CATEGORIES.ATTRACTION)
      .toBe('tourism.attraction');
  });

  it('should define the museum category', () => {
    expect(PLACE_CATEGORIES.MUSEUM)
      .toBe('entertainment.museum');
  });

  it('should define the restaurant category', () => {
    expect(PLACE_CATEGORIES.RESTAURANT)
      .toBe('catering.restaurant');
  });

});