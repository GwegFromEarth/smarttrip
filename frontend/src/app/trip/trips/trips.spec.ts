import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Trips } from './trips';

describe('Trips', () => {

  let component: Trips;
  let fixture: ComponentFixture<Trips>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [Trips],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Trips);
    component = fixture.componentInstance;

    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should create', async () => {
    await fixture.whenStable();

    const request = httpTesting.expectOne(
      'http://localhost:8080/api/trips'
    );

    expect(request.request.method).toBe('GET');

    request.flush([]);

    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should load and display trips', async () => {

    const trips = [
      {
        id: 1,
        destination: 'Rome',
        startDate: '2026-09-12',
        endDate: '2026-09-17',
        travelers: 2,
        preferences: 'culture, histoire'
      }
    ];

    fixture.detectChanges();

    const request = httpTesting.expectOne(
      'http://localhost:8080/api/trips'
    );

    expect(request.request.method).toBe('GET');

    request.flush(trips);

    await fixture.whenStable();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Rome');
    expect(compiled.textContent).toContain('2026-09-12');
    expect(compiled.textContent).toContain('2026-09-17');
    expect(compiled.textContent).toContain('2');
    expect(compiled.textContent).toContain('culture, histoire');
  });

  it('should display create trip link', async () => {

    fixture.detectChanges();

    const request = httpTesting.expectOne(
      'http://localhost:8080/api/trips'
    );

    expect(request.request.method).toBe('GET');

    request.flush([]);

    await fixture.whenStable();

    const link = fixture.nativeElement.querySelector(
      'a[routerLink="/trips/create"]'
    ) as HTMLAnchorElement | null;

    expect(link).not.toBeNull();
    expect(link?.textContent).toContain('Créer un voyage');
    expect(link?.getAttribute('href')).toBe('/trips/create');
  });
});