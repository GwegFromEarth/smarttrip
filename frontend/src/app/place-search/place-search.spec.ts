import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NEVER, Observable, of, Subject, throwError } from 'rxjs';
import { vi } from 'vitest';

import { PlaceSearch } from './place-search';
import { PlaceService } from '../place/place.service';
import {
  PLACE_CATEGORIES
} from '../place/place-category';
import { Place } from '../place/place.models';

describe('PlaceSearch', () => {

  let fixture: ComponentFixture<PlaceSearch>;
  let component: PlaceSearch;
  let placeService: {
    searchByDestination: ReturnType<typeof vi.fn>;
  };

  const places: Place[] = [
    {
      placeId: 'colosseum-test-id',
      name: 'Colosseum',
      description: 'Ancient Roman amphitheatre',
      latitude: 41.8902,
      longitude: 12.4922,
      category: PLACE_CATEGORIES.TOURIST_ATTRACTION,
      address: 'Piazza del Colosseo, 1, 00184 Roma RM, Italy',
      distance: 500,
      rating: 9.5,
      popularity: 0.95
    },
    {
      placeId: 'capitoline-test-id',
      name: 'Capitoline Museums',
      description: 'Museums on Capitoline Hill',
      latitude: 41.8931,
      longitude: 12.4828,
      category: PLACE_CATEGORIES.MUSEUM,
      address: 'Piazza del Campidoglio, 1, 00186 Roma RM, Italy',
      distance: 800,
      rating: 8.7,
      popularity: 0.9
    }
  ];

  beforeEach(async () => {

    placeService = {
      searchByDestination: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [PlaceSearch],
      providers: [
        {
          provide: PlaceService,
          useValue: placeService
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PlaceSearch);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should use tourist attractions as default category', () => {
    expect(component.selectedCategory)
      .toBe(PLACE_CATEGORIES.TOURIST_ATTRACTION);
  });

  it('should initialize with an empty destination', () => {
    expect(component.destination).toBe('');
  });

  it('should initialize with no places', () => {
    expect(component.places).toEqual([]);
  });

  it('should initialize without loading', () => {
    expect(component.loading).toBe(false);
  });

  it('should initialize without an error', () => {
    expect(component.errorMessage).toBeNull();
  });

  it('should contain all supported categories', () => {
    expect(component.categories).toHaveLength(5);

    expect(component.categories.map(category => category.value))
      .toEqual([
        PLACE_CATEGORIES.TOURIST_ATTRACTION,
        PLACE_CATEGORIES.MUSEUM,
        PLACE_CATEGORIES.RESTAURANT,
        PLACE_CATEGORIES.CAFE,
        PLACE_CATEGORIES.PARK
      ]);
  });

  it('should display the search form', () => {

    const element: HTMLElement = fixture.nativeElement;

    expect(
      element.querySelector('#destination')
    ).not.toBeNull();

    expect(
      element.querySelector('#category')
    ).not.toBeNull();

    expect(
      element.querySelector('button')
    ).not.toBeNull();
  });

  it('should display the default category', () => {

    const select = fixture.nativeElement
      .querySelector('#category') as HTMLSelectElement;

    expect(select.value)
      .toBe(PLACE_CATEGORIES.TOURIST_ATTRACTION);
  });

  it('should show an error when destination is blank', () => {

    component.destination = '   ';

    component.search();

    expect(placeService.searchByDestination)
      .not.toHaveBeenCalled();

    expect(component.errorMessage)
      .toBe('Veuillez saisir une destination.');

    expect(component.places)
      .toEqual([]);

    expect(component.loading)
      .toBe(false);
  });

  it('should trim destination before searching', () => {

    placeService.searchByDestination
      .mockReturnValue(of(places));

    component.destination = '  Rome  ';
    component.selectedCategory =
      PLACE_CATEGORIES.TOURIST_ATTRACTION;

    component.search();

    expect(placeService.searchByDestination)
      .toHaveBeenCalledExactlyOnceWith(
        'Rome',
        PLACE_CATEGORIES.TOURIST_ATTRACTION
      );
  });

  it('should search places by destination', () => {

    placeService.searchByDestination
      .mockReturnValue(of(places));

    component.destination = 'Rome';
    component.selectedCategory =
      PLACE_CATEGORIES.TOURIST_ATTRACTION;

    component.search();

    expect(placeService.searchByDestination)
      .toHaveBeenCalledExactlyOnceWith(
        'Rome',
        PLACE_CATEGORIES.TOURIST_ATTRACTION
      );

    expect(component.places)
      .toEqual(places);

    expect(component.loading)
      .toBe(false);

    expect(component.errorMessage)
      .toBeNull();
  });

  it('should search using the selected category', () => {

    placeService.searchByDestination
      .mockReturnValue(of([places[1]]));

    component.destination = 'Rome';
    component.selectedCategory =
      PLACE_CATEGORIES.MUSEUM;

    component.search();

    expect(placeService.searchByDestination)
      .toHaveBeenCalledExactlyOnceWith(
        'Rome',
        PLACE_CATEGORIES.MUSEUM
      );

    expect(component.places)
      .toEqual([places[1]]);
  });

  it('should display places returned by the service', () => {

    placeService.searchByDestination
      .mockReturnValue(of(places));

    component.destination = 'Rome';

    component.search();

    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;

    expect(element.textContent)
      .toContain('Colosseum');

    expect(element.textContent)
      .toContain('Capitoline Museums');

    expect(element.textContent)
      .toContain('Piazza del Colosseo');

    expect(element.textContent)
      .toContain('9.5/10');
  });

  it('should display the empty message when no places are returned', () => {

    placeService.searchByDestination
      .mockReturnValue(of([]));

    component.destination = 'Rome';

    component.search();

    fixture.detectChanges();

    expect(component.places)
      .toEqual([]);

    expect(component.loading)
      .toBe(false);

    const element: HTMLElement = fixture.nativeElement;

    expect(element.textContent)
      .toContain('Aucun lieu à afficher.');
  });

  it('should clear previous places before a new search', () => {

    placeService.searchByDestination
      .mockReturnValue(of(places));

    component.destination = 'Rome';
    component.search();

    expect(component.places)
      .toEqual(places);

    placeService.searchByDestination
      .mockReturnValue(of([]));

    component.destination = 'Paris';
    component.search();

    expect(component.places)
      .toEqual([]);
  });

  it('should clear previous error before a new search', () => {

    component.destination = '   ';
    component.search();

    expect(component.errorMessage)
      .toBe('Veuillez saisir une destination.');

    placeService.searchByDestination
      .mockReturnValue(of(places));

    component.destination = 'Rome';
    component.search();

    expect(component.errorMessage)
      .toBeNull();
  });

  it('should set loading to false after a successful search', () => {

    placeService.searchByDestination
      .mockReturnValue(of(places));

    component.destination = 'Rome';

    component.search();

    expect(component.loading)
      .toBe(false);
  });

  it('should handle a 400 error', () => {

    placeService.searchByDestination
      .mockReturnValue(
        throwError(() => ({
          status: 400,
          error: {
            detail: 'Category must not be blank'
          }
        }))
      );

    component.destination = 'Rome';

    component.search();

    expect(component.loading)
      .toBe(false);

    expect(component.errorMessage)
      .toBe('Category must not be blank');

    expect(component.places)
      .toEqual([]);
  });

  it('should handle a 429 rate limit error', () => {

    placeService.searchByDestination
      .mockReturnValue(
        throwError(() => ({
          status: 429,
          error: {
            detail: 'Foursquare rate limit exceeded'
          }
        }))
      );

    component.destination = 'Rome';

    component.search();

    expect(component.loading)
      .toBe(false);

    expect(component.errorMessage)
      .toBe(
        'Le service est temporairement limité. '
        + 'Veuillez réessayer dans quelques instants.'
      );

    expect(component.places)
      .toEqual([]);
  });

  it('should handle an error without a detail message', () => {

    placeService.searchByDestination
      .mockReturnValue(
        throwError(() => ({
          status: 500,
          error: {}
        }))
      );

    component.destination = 'Rome';

    component.search();

    expect(component.loading)
      .toBe(false);

    expect(component.errorMessage)
      .toBe(
        'Une erreur est survenue lors de la recherche.'
      );
  });

  it('should display the 429 error in the template', () => {

    placeService.searchByDestination
      .mockReturnValue(
        throwError(() => ({
          status: 429,
          error: {
            detail: 'Foursquare rate limit exceeded'
          }
        }))
      );

    component.destination = 'Rome';
    component.search();

    fixture.detectChanges();

    const errorElement = fixture.nativeElement
      .querySelector('.error-message');

    expect(errorElement)
      .not.toBeNull();

    expect(errorElement.textContent)
      .toContain('Le service est temporairement limité.');

    expect(errorElement.textContent)
      .toContain('Veuillez réessayer dans quelques instants.');
  });

  it('should set loading while the search is pending', () => {

    const subject = new Subject<Place[]>();

    placeService.searchByDestination
      .mockReturnValue(subject.asObservable());

    component.destination = 'Rome';

    component.search();

    expect(component.loading)
      .toBe(true);

    subject.next(places);

    expect(component.places)
      .toEqual(places);

    expect(component.loading)
      .toBe(false);
  });

  it('should disable the search button while loading', () => {
    placeService.searchByDestination =
      vi.fn().mockReturnValue(NEVER);

    component.destination = 'Rome';

    component.search();

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector(
      'button[type="button"]'
    ) as HTMLButtonElement;

    expect(component.loading).toBe(true);
    expect(button.disabled).toBe(true);
    expect(button.textContent).toContain('Recherche...');
  });

  it('should not display the empty message while loading', () => {

    const observable = new Observable<Place[]>(() => {
      // Keep the observable pending.
    });

    placeService.searchByDestination
      .mockReturnValue(observable);

    component.destination = 'Rome';

    component.search();

    fixture.detectChanges();

    const emptyMessage = fixture.nativeElement
      .querySelector('.empty-message');

    expect(emptyMessage)
      .toBeNull();
  });

  it('should display rating when available', () => {

    placeService.searchByDestination
      .mockReturnValue(of([places[0]]));

    component.destination = 'Rome';
    component.search();

    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;

    expect(element.textContent)
      .toContain('9.5/10');
  });

  it('should display popularity when available', () => {

    placeService.searchByDestination
      .mockReturnValue(of([places[0]]));

    component.destination = 'Rome';
    component.search();

    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;

    expect(element.textContent)
      .toContain('Popularité : 0.95');
  });

  it('should not display optional place information when null', () => {

    const placeWithoutOptionalData: Place = {
      ...places[0],
      address: null,
      description: null,
      distance: null,
      rating: null,
      popularity: null
    };

    placeService.searchByDestination
      .mockReturnValue(of([placeWithoutOptionalData]));

    component.destination = 'Rome';
    component.search();

    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;

    expect(element.textContent)
      .not.toContain('Piazza del Colosseo');

    expect(element.textContent)
      .not.toContain('Ancient Roman amphitheatre');

    expect(element.textContent)
      .not.toContain('⭐');

    expect(element.textContent)
      .not.toContain('Popularité');
  });

  it('should update destination from the input', async () => {

    const input = fixture.nativeElement
      .querySelector('#destination') as HTMLInputElement;

    input.value = 'Paris';
    input.dispatchEvent(new Event('input'));

    await fixture.whenStable();

    expect(component.destination)
      .toBe('Paris');
  });

  it('should update the selected category', async () => {

    const select = fixture.nativeElement
      .querySelector('#category') as HTMLSelectElement;

    select.value = PLACE_CATEGORIES.MUSEUM;
    select.dispatchEvent(new Event('change'));

    await fixture.whenStable();

    expect(component.selectedCategory)
      .toBe(PLACE_CATEGORIES.MUSEUM);
  });

  it('should search when clicking the search button', () => {

    const searchSpy = vi.spyOn(component, 'search');

    const button = fixture.nativeElement
      .querySelector('button[type="button"]') as HTMLButtonElement;

    button.click();

    expect(searchSpy)
      .toHaveBeenCalledOnce();
  });

  it('should search when pressing Enter in the destination input', () => {

    const searchSpy = vi.spyOn(component, 'search');

    const input = fixture.nativeElement
      .querySelector('#destination') as HTMLInputElement;

    input.dispatchEvent(
      new KeyboardEvent('keyup', {
        key: 'Enter'
      })
    );

    expect(searchSpy)
      .toHaveBeenCalledOnce();
  });

  it('should display the place search screen', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('h2')?.textContent)
      .toContain('Découvrir des lieux');

    expect(element.querySelector('label[for="destination"]')?.textContent)
      .toContain('Destination');

    expect(element.querySelector('label[for="category"]')?.textContent)
      .toContain('Catégorie');

    expect(element.querySelector('button')?.textContent)
      .toContain('Rechercher');
  });
});