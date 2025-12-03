package Company.atlassian.code_design.q6_cinema_screenings.Newsol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class Test {


    public class CinemaServiceTest {

        private Sol.CinemaService cinema;

        @BeforeEach
        void setUp() {
            cinema = new CinemaService();
            cinema.addRoom("Room1");
        }

        @Nested
        @DisplayName("Base: Add Screening Tests")
        class AddScreeningTests {

            @Test
            @DisplayName("Should add screening within cinema hours")
            void testAddValidScreening() {
                Movie movie = new Movie("M1", "Inception", 150);

                boolean result = cinema.addScreening("Room1", movie, 600);  // 10:00

                assertTrue(result);
            }

            @Test
            @DisplayName("Should reject screening before opening")
            void testRejectBeforeOpening() {
                Movie movie = new Movie("M1", "Inception", 150);

                boolean result = cinema.addScreening("Room1", movie, 500);  // 8:20

                assertFalse(result);
            }

            @Test
            @DisplayName("Should reject screening ending after closing")
            void testRejectAfterClosing() {
                Movie movie = new Movie("M1", "Inception", 150);

                boolean result = cinema.addScreening("Room1", movie, 1300);  // 21:40 + 150 = 24:10

                assertFalse(result);
            }

            @Test
            @DisplayName("Should reject overlapping screenings")
            void testRejectOverlapping() {
                Movie movie1 = new Movie("M1", "Inception", 150);
                Movie movie2 = new Movie("M2", "Avatar", 180);

                cinema.addScreening("Room1", movie1, 600);  // 10:00 - 12:30
                boolean result = cinema.addScreening("Room1", movie2, 700);  // 11:40 - overlaps

                assertFalse(result);
            }

            @Test
            @DisplayName("Should allow back-to-back screenings")
            void testBackToBackScreenings() {
                Movie movie1 = new Movie("M1", "Inception", 150);
                Movie movie2 = new Movie("M2", "Avatar", 180);

                cinema.addScreening("Room1", movie1, 600);  // 10:00 - 12:30
                boolean result = cinema.addScreening("Room1", movie2, 750);  // 12:30 - 15:30

                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("Base: Can Add Movie Tests")
        class CanAddMovieTests {

            @Test
            @DisplayName("Should find slot in empty schedule")
            void testEmptySchedule() {
                Movie movie = new Movie("M1", "Inception", 150);

                assertTrue(cinema.canAddMovie("Room1", movie));
            }

            @Test
            @DisplayName("Should find slot between screenings")
            void testSlotBetweenScreenings() {
                Movie movie1 = new Movie("M1", "Short1", 60);
                Movie movie2 = new Movie("M2", "Short2", 60);
                Movie movie3 = new Movie("M3", "NewMovie", 120);

                cinema.addScreening("Room1", movie1, 600);   // 10:00 - 11:00
                cinema.addScreening("Room1", movie2, 900);   // 15:00 - 16:00

                // Gap: 11:00 - 15:00 (240 minutes)
                assertTrue(cinema.canAddMovie("Room1", movie3));
            }

            @Test
            @DisplayName("Should reject if no slot available")
            void testNoSlotAvailable() {
                Movie movie1 = new Movie("M1", "Long1", 390);  // 6.5 hours
                Movie movie2 = new Movie("M2", "Long2", 390);
                Movie movie3 = new Movie("M3", "NewMovie", 120);

                cinema.addScreening("Room1", movie1, 600);   // 10:00 - 16:30
                cinema.addScreening("Room1", movie2, 990);   // 16:30 - 23:00

                assertFalse(cinema.canAddMovie("Room1", movie3));
            }
        }

        @Nested
        @DisplayName("Scale-Up 1: Revenue Tests")
        class RevenueTests {

            @Test
            @DisplayName("Should track revenue per screening")
            void testScreeningRevenue() {
                Movie movie = new Movie("M1", "Inception", 150, 500.0);

                cinema.addScreening("Room1", movie, 600);

                // Check through print or direct access
                assertEquals(500.0, cinema.getTotalRevenue());
            }

            @Test
            @DisplayName("Should calculate total revenue")
            void testTotalRevenue() {
                Movie movie1 = new Movie("M1", "Inception", 150, 500.0);
                Movie movie2 = new Movie("M2", "Avatar", 180, 700.0);

                cinema.addScreening("Room1", movie1, 600);
                cinema.addScreening("Room1", movie2, 750);

                assertEquals(1200.0, cinema.getTotalRevenue());
            }
        }

        @Nested
        @DisplayName("Scale-Up 2: Replace Screening Tests")
        class ReplaceScreeningTests {

            @Test
            @DisplayName("Should find screening to replace with positive gain")
            void testFindScreeningToReplace() {
                Movie lowRevenue = new Movie("M1", "LowMovie", 120, 200.0);
                Movie highRevenue = new Movie("M2", "HighMovie", 120, 500.0);

                cinema.addScreening("Room1", lowRevenue, 600);

                Screening toReplace = cinema.findBestScreeningToReplace("Room1", highRevenue);

                assertNotNull(toReplace);
                assertEquals("LowMovie", toReplace.getMovie().getName());
            }

            @Test
            @DisplayName("Should replace screening successfully")
            void testReplaceScreening() {
                Movie lowRevenue = new Movie("M1", "LowMovie", 120, 200.0);
                Movie highRevenue = new Movie("M2", "HighMovie", 120, 500.0);

                cinema.addScreening("Room1", lowRevenue, 600);

                boolean result = cinema.replaceScreening("Room1", highRevenue);

                assertTrue(result);
                assertEquals(500.0, cinema.getTotalRevenue());
            }
        }

        @Nested
        @DisplayName("Scale-Up 3: Multiple Rooms Tests")
        class MultipleRoomsTests {

            @BeforeEach
            void setUpMultipleRooms() {
                cinema.addRoom("Room2");
                cinema.addRoom("Room3");
            }

            @Test
            @DisplayName("Should find best room for movie")
            void testFindBestRoom() {
                Movie movie1 = new Movie("M1", "Movie1", 300);
                Movie movie2 = new Movie("M2", "Movie2", 300);
                Movie newMovie = new Movie("M3", "NewMovie", 120);

                // Fill Room1
                cinema.addScreening("Room1", movie1, 600);
                cinema.addScreening("Room1", movie2, 900);

                String bestRoom = cinema.findBestRoomForMovie(newMovie);

                assertNotNull(bestRoom);
                // Room2 or Room3 should be available
                assertTrue(bestRoom.equals("Room2") || bestRoom.equals("Room3"));
            }

            @Test
            @DisplayName("Should add movie to best available room")
            void testAddToBestRoom() {
                Movie newMovie = new Movie("M1", "NewMovie", 120, 300.0);

                boolean result = cinema.addMovieToBestRoom(newMovie);

                assertTrue(result);
                assertEquals(300.0, cinema.getTotalRevenue());
            }
        }

        @Nested
        @DisplayName("Scale-Down: Print Schedule Tests")
        class PrintScheduleTests {

            @Test
            @DisplayName("Should print schedule without errors")
            void testPrintSchedule() {
                Movie movie1 = new Movie("M1", "Inception", 150, 500.0);
                Movie movie2 = new Movie("M2", "Avatar", 180, 700.0);

                cinema.addScreening("Room1", movie1, 600);
                cinema.addScreening("Room1", movie2, 750);

                // Should not throw
                assertDoesNotThrow(() -> cinema.printSchedule("Room1"));
            }

            @Test
            @DisplayName("Should handle empty schedule")
            void testPrintEmptySchedule() {
                assertDoesNotThrow(() -> cinema.printSchedule("Room1"));
            }
        }

        @Nested
        @DisplayName("Edge Cases")
        class EdgeCaseTests {

            @Test
            @DisplayName("Should handle movie exactly filling available time")
            void testExactFit() {
                // Cinema: 10:00 - 23:00 = 780 minutes
                Movie movie = new Movie("M1", "Marathon", 780);

                boolean result = cinema.addScreening("Room1", movie, 600);

                assertTrue(result);
            }

            @Test
            @DisplayName("Should reject movie too long for cinema hours")
            void testTooLong() {
                Movie movie = new Movie("M1", "TooLong", 800);

                boolean result = cinema.addScreening("Room1", movie, 600);

                assertFalse(result);
            }

            @Test
            @DisplayName("Should handle unknown room")
            void testUnknownRoom() {
                Movie movie = new Movie("M1", "Inception", 150);

                assertThrows(IllegalArgumentException.class,
                        () -> cinema.addScreening("UnknownRoom", movie, 600));
            }
        }
    }
```

        ---

        ## Project Structure
```
    src/
            ├── main/java/
            │   ├── Movie.java
│   ├── Screening.java
│   ├── Room.java
│   └── CinemaService.java
└── test/java/
            └── CinemaServiceTest.java
}
