export interface TripResponse {
  id: number;
  destination: string;
  startDate: string;
  endDate: string;
  travelers: number;
  preferences: string;
}

export interface ActivityDto {
  time: string;
  title: string;
  description: string;
  location: string;
}

export interface ItineraryDayDto {
  dayNumber: number;
  date: string;
  activities: ActivityDto[];
}

export interface ItineraryDto {
  tripId: number;
  destination: string;
  days: ItineraryDayDto[];
}