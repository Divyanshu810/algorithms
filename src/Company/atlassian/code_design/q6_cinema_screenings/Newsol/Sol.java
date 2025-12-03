package Company.atlassian.code_design.q6_cinema_screenings.Newsol;

/*
┌─────────────────────────────────────────────────────────────────┐
│                       Movie                                      │
├─────────────────────────────────────────────────────────────────┤
│ - id: String                                                    │
│ - name: String                                                  │
│ - duration: int (minutes)                                       │
│ - revenue: double (Scale-Up 1)                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     Screening                                    │
├─────────────────────────────────────────────────────────────────┤
│ - movie: Movie                                                  │
│ - startTime: int (minutes from midnight)                        │
│ - endTime: int                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       Room                                       │
├─────────────────────────────────────────────────────────────────┤
│ - id: String                                                    │
│ - screenings: List<Screening>                                   │
├─────────────────────────────────────────────────────────────────┤
│ + addScreening(screening): boolean                              │
│ + canFitScreening(movie): boolean                               │
│ + findAvailableSlot(movie): int                                 │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                   CinemaService                                  │
├─────────────────────────────────────────────────────────────────┤
│ - rooms: Map<String, Room>                                      │
│ - OPENING_TIME: int = 600                                       │
│ - CLOSING_TIME: int = 1380                                      │
├─────────────────────────────────────────────────────────────────┤
│ + addRoom(roomId): void                                         │
│ + addScreening(roomId, movie, startTime): boolean               │
│ + canAddMovie(roomId, movie): boolean                           │
│ + printSchedule(roomId): void                                   │
│ + findBestScreeningToReplace(roomId, movie): Screening          │
│ + addMovieToMaximizeRevenue(movie): String                      │
└─────────────────────────────────────────────────────────────────┘
 */

import java.util.*;
public class Sol {
    public class Movie {
        private final String id;
        private final String name;
        private final int duration;  // in minutes
        private final double revenue;  // Scale-Up 1

        public Movie(String id, String name, int duration) {
            this(id, name, duration, 0.0);
        }

        public Movie(String id, String name, int duration, double revenue) {
            this.id = id;
            this.name = name;
            this.duration = duration;
            this.revenue = revenue;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getDuration() {
            return duration;
        }

        public double getRevenue() {
            return revenue;
        }
    }
    public class Screening {
        private final Movie movie;
        private final int startTime;  // minutes from midnight
        private final int endTime;

        public Screening(Movie movie, int startTime) {
            this.movie = movie;
            this.startTime = startTime;
            this.endTime = startTime + movie.getDuration();
        }

        public Movie getMovie() {
            return movie;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public double getRevenue() {
            return movie.getRevenue();
        }

        // Check if this screening overlaps with another time slot
        public boolean overlapsWith(int otherStart, int otherEnd) {
            return !(endTime <= otherStart || startTime >= otherEnd);
        }

        public boolean overlapsWith(Screening other) {
            return overlapsWith(other.getStartTime(), other.getEndTime());
        }

        @Override
        public String toString() {
            return movie.getName() + " [" + formatTime(startTime) + " - " + formatTime(endTime) + "]";
        }

        private String formatTime(int minutes) {
            int hours = minutes / 60;
            int mins = minutes % 60;
            return String.format("%02d:%02d", hours, mins);
        }
    }


    public class Room {

        private static final int OPENING_TIME = 600;   // 10:00
        private static final int CLOSING_TIME = 1380;  // 23:00

        private final String id;
        private final List<Screening> screenings;

        public Room(String id) {
            this.id = id;
            this.screenings = new ArrayList<>();
        }

        public String getId() {
            return id;
        }

        public List<Screening> getScreenings() {
            return new ArrayList<>(screenings);
        }

        // Base: Add screening if valid
        public boolean addScreening(Screening screening) {
            if (!isValidScreening(screening)) {
                return false;
            }

            if (hasConflict(screening)) {
                return false;
            }

            screenings.add(screening);
            sortScreenings();
            return true;
        }

        // Base: Check if movie can fit anywhere
        public boolean canFitMovie(Movie movie) {
            int slot = findAvailableSlot(movie);
            return slot != -1;
        }

        // Find first available slot for movie
        public int findAvailableSlot(Movie movie) {
            int duration = movie.getDuration();

            // Sort to ensure order
            sortScreenings();

            // Check before first screening
            if (screenings.isEmpty()) {
                if (OPENING_TIME + duration <= CLOSING_TIME) {
                    return OPENING_TIME;
                }
                return -1;
            }

            // Check gap before first screening
            int firstStart = screenings.get(0).getStartTime();
            if (OPENING_TIME + duration <= firstStart) {
                return OPENING_TIME;
            }

            // Check gaps between screenings
            for (int i = 0; i < screenings.size() - 1; i++) {
                int gapStart = screenings.get(i).getEndTime();
                int gapEnd = screenings.get(i + 1).getStartTime();

                if (gapStart + duration <= gapEnd) {
                    return gapStart;
                }
            }

            // Check gap after last screening
            int lastEnd = screenings.get(screenings.size() - 1).getEndTime();
            if (lastEnd + duration <= CLOSING_TIME) {
                return lastEnd;
            }

            return -1;  // No slot found
        }

        // Calculate total revenue
        public double getTotalRevenue() {
            double total = 0;
            for (Screening s : screenings) {
                total += s.getRevenue();
            }
            return total;
        }

        // Remove a screening
        public boolean removeScreening(Screening screening) {
            return screenings.remove(screening);
        }

        // Helper: Check if screening is within cinema hours
        private boolean isValidScreening(Screening screening) {
            return screening.getStartTime() >= OPENING_TIME &&
                    screening.getEndTime() <= CLOSING_TIME;
        }

        // Helper: Check for conflicts with existing screenings
        private boolean hasConflict(Screening newScreening) {
            for (Screening existing : screenings) {
                if (existing.overlapsWith(newScreening)) {
                    return true;
                }
            }
            return false;
        }

        // Helper: Sort screenings by start time
        private void sortScreenings() {
            Collections.sort(screenings, (a, b) -> a.getStartTime() - b.getStartTime());
        }
    }


    public class CinemaService {

        private static final int OPENING_TIME = 600;   // 10:00
        private static final int CLOSING_TIME = 1380;  // 23:00

        private final Map<String, Room> rooms;

        public CinemaService() {
            this.rooms = new HashMap<>();
        }

        public void addRoom(String roomId) {
            rooms.put(roomId, new Room(roomId));
        }

        // Base: Add screening to a room
        public boolean addScreening(String roomId, Movie movie, int startTime) {
            Room room = rooms.get(roomId);
            if (room == null) {
                throw new IllegalArgumentException("Room not found: " + roomId);
            }

            Screening screening = new Screening(movie, startTime);
            return room.addScreening(screening);
        }

        // Base: Check if movie can be added to room
        public boolean canAddMovie(String roomId, Movie movie) {
            Room room = rooms.get(roomId);
            if (room == null) {
                return false;
            }
            return room.canFitMovie(movie);
        }

        // Base: Find available slot for movie
        public int findAvailableSlot(String roomId, Movie movie) {
            Room room = rooms.get(roomId);
            if (room == null) {
                return -1;
            }
            return room.findAvailableSlot(movie);
        }

        // Scale-Down: Print schedule for a room
        public void printSchedule(String roomId) {
            Room room = rooms.get(roomId);
            if (room == null) {
                System.out.println("Room not found: " + roomId);
                return;
            }

            System.out.println("=== Schedule for Room " + roomId + " ===");
            List<Screening> screenings = room.getScreenings();

            if (screenings.isEmpty()) {
                System.out.println("No screenings scheduled");
                return;
            }

            for (Screening s : screenings) {
                System.out.println(s);
            }
            System.out.println("Total Revenue: $" + room.getTotalRevenue());
        }

        // Scale-Up 2: Find best screening to replace
        public Screening findBestScreeningToReplace(String roomId, Movie newMovie) {
            Room room = rooms.get(roomId);
            if (room == null) {
                return null;
            }

            List<Screening> screenings = room.getScreenings();
            Screening bestToRemove = null;
            double bestNetGain = Double.NEGATIVE_INFINITY;

            for (Screening candidate : screenings) {
                // Check if removing this screening allows new movie to fit
                int candidateStart = candidate.getStartTime();
                int candidateEnd = candidate.getEndTime();

                // Find available time if we remove this screening
                int availableStart = candidateStart;
                int availableEnd = candidateEnd;

                // Expand available time based on adjacent gaps
                int index = screenings.indexOf(candidate);

                // Check gap before
                if (index > 0) {
                    availableStart = screenings.get(index - 1).getEndTime();
                } else {
                    availableStart = OPENING_TIME;
                }

                // Check gap after
                if (index < screenings.size() - 1) {
                    availableEnd = screenings.get(index + 1).getStartTime();
                } else {
                    availableEnd = CLOSING_TIME;
                }

                // Check if new movie fits in this slot
                int slotDuration = availableEnd - availableStart;
                if (slotDuration >= newMovie.getDuration()) {
                    // Calculate net gain (new revenue - lost revenue)
                    double netGain = newMovie.getRevenue() - candidate.getRevenue();

                    if (netGain > bestNetGain) {
                        bestNetGain = netGain;
                        bestToRemove = candidate;
                    }
                }
            }

            return bestToRemove;
        }

        // Scale-Up 2: Replace screening with new movie
        public boolean replaceScreening(String roomId, Movie newMovie) {
            Room room = rooms.get(roomId);
            if (room == null) {
                return false;
            }

            Screening toRemove = findBestScreeningToReplace(roomId, newMovie);
            if (toRemove == null) {
                return false;
            }

            int newStartTime = toRemove.getStartTime();
            room.removeScreening(toRemove);

            Screening newScreening = new Screening(newMovie, newStartTime);
            return room.addScreening(newScreening);
        }

        // Scale-Up 3: Find best room to add movie (maximize revenue)
        public String findBestRoomForMovie(Movie movie) {
            String bestRoom = null;

            for (Map.Entry<String, Room> entry : rooms.entrySet()) {
                String roomId = entry.getKey();
                Room room = entry.getValue();

                if (room.canFitMovie(movie)) {
                    if (bestRoom == null) {
                        bestRoom = roomId;
                    }
                    // Could add more logic here for revenue optimization
                }
            }

            return bestRoom;
        }

        // Scale-Up 3: Add movie to best available room
        public boolean addMovieToBestRoom(Movie movie) {
            String bestRoom = findBestRoomForMovie(movie);

            if (bestRoom == null) {
                return false;
            }

            int slot = findAvailableSlot(bestRoom, movie);
            if (slot == -1) {
                return false;
            }

            return addScreening(bestRoom, movie, slot);
        }

        // Get total revenue across all rooms
        public double getTotalRevenue() {
            double total = 0;
            for (Room room : rooms.values()) {
                total += room.getTotalRevenue();
            }
            return total;
        }

        // Get all rooms
        public List<String> getRoomIds() {
            return new ArrayList<>(rooms.keySet());
        }
    }


}
