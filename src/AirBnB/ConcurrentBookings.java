package practice.airbnb;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * You are given an array of bookings for a hotel, where each booking includes:
 *
 * Check-in time (start date/time)
 *
 * Check-out time (end date/time)
 *
 * Your goal is to determine something like:
 *
 * The minimum number of rooms required to accommodate all bookings without conflicts.
 *
 * Whether a given booking can be accommodated given existing bookings.
 *
 * Find time intervals when the hotel is fully booked or has vacancies.
 *
 * Detect any overlaps/conflicts among bookings.
 */
public class ConcurrentBookings {
    // This class can be implemented using a data structure like a TreeMap or PriorityQueue
    // to efficiently manage and query bookings.

    // Example fields:
     private Map<LocalDateTime, Integer> bookings; // Maps check-in time to number of rooms booked

    // Example methods:
//     public void addBooking(LocalDateTime checkIn, LocalDateTime checkOut, int rooms);
//     public boolean canAccommodate(LocalDateTime checkIn, LocalDateTime checkOut, int rooms);
//     public List<LocalDateTime> getFullyBookedTimes();
//     public List<LocalDateTime> getVacantTimes();
}
