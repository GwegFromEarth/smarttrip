import { TestBed } from '@angular/core/testing';
import { RouterTestingHarness } from '@angular/router/testing';
import { provideRouter } from '@angular/router';

import { App } from './app';
import { Chat } from './chat/chat';
import { Itinerary } from './itinerary/itinerary';

describe('App routing', () => {

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([
          {
            path: '',
            component: Chat
          },
          {
            path: 'trips/:id/itinerary',
            component: Itinerary
          }
        ])
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);

    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display Chat for the default route', async () => {
    const harness = await RouterTestingHarness.create();

    await harness.navigateByUrl('/', Chat);

    expect(harness.routeNativeElement)
      .not.toBeNull();
  });

  it('should display Itinerary for an itinerary route', async () => {
    const harness = await RouterTestingHarness.create();

    await harness.navigateByUrl(
      '/trips/1/itinerary',
      Itinerary
    );

    expect(harness.routeNativeElement)
      .not.toBeNull();
  });
});