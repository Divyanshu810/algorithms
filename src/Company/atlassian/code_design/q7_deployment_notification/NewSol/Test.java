package Company.atlassian.code_design.q7_deployment_notification.NewSol;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class Test {

    public class DeploymentServiceTest {

        private Sol.DeploymentService service;

        @BeforeEach
        void setUp() {
            service = new Sol.DeploymentService();
        }

        @Nested
        @DisplayName("Base: Version-Specific Tracking")
        class VersionTrackingTests {

            @Test
            @DisplayName("Should track authors per version")
            void testAuthorsPerVersion() {
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice", "Bob"), DeploymentStatus.STARTED));
                service.receiveEvent(new DeploymentEvent("v2.0",
                        Arrays.asList("Charlie"), DeploymentStatus.STARTED));

                assertEquals(2, service.getPendingAuthorsForVersion("v1.0").size());
                assertEquals(1, service.getPendingAuthorsForVersion("v2.0").size());
            }

            @Test
            @DisplayName("Should only notify authors for completed version")
            void testNotifyOnlyCompletedVersion() {
                // Start two versions
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice"), DeploymentStatus.STARTED));
                service.receiveEvent(new DeploymentEvent("v2.0",
                        Arrays.asList("Bob"), DeploymentStatus.STARTED));

                // Complete only v1.0
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList(), DeploymentStatus.COMPLETED));

                List<Notification> notifications = service.sendNotifications();

                // Only Alice should be notified
                assertEquals(1, notifications.size());
                assertEquals("Alice", notifications.get(0).getAuthor());
                assertEquals("v1.0", notifications.get(0).getVersion());

                // Bob should still be pending
                assertTrue(service.getPendingAuthorsForVersion("v2.0").contains("Bob"));
            }

            @Test
            @DisplayName("Should clear only completed version authors")
            void testClearOnlyCompletedVersion() {
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice"), DeploymentStatus.STARTED));
                service.receiveEvent(new DeploymentEvent("v2.0",
                        Arrays.asList("Bob", "Charlie"), DeploymentStatus.STARTED));

                // Complete v1.0
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList(), DeploymentStatus.COMPLETED));

                // v1.0 should be removed, v2.0 should remain
                assertEquals(0, service.getPendingAuthorsForVersion("v1.0").size());
                assertEquals(2, service.getPendingAuthorsForVersion("v2.0").size());
            }
        }

        @Nested
        @DisplayName("Base: Multiple Authors Same Version")
        class MultipleAuthorsSameVersionTests {

            @Test
            @DisplayName("Should add multiple authors to same version")
            void testMultipleAuthors() {
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice"), DeploymentStatus.STARTED));
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Bob"), DeploymentStatus.STARTED));

                Set<String> authors = service.getPendingAuthorsForVersion("v1.0");

                assertEquals(2, authors.size());
                assertTrue(authors.contains("Alice"));
                assertTrue(authors.contains("Bob"));
            }

            @Test
            @DisplayName("Should deduplicate same author in same version")
            void testDeduplicateAuthor() {
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice"), DeploymentStatus.STARTED));
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice"), DeploymentStatus.STARTED));

                assertEquals(1, service.getPendingAuthorsForVersion("v1.0").size());
            }
        }

        @Nested
        @DisplayName("Scale-Up: Revert Per Version")
        class RevertPerVersionTests {

            @Test
            @DisplayName("Should revert author from specific version")
            void testRevertFromVersion() {
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice", "Bob"), DeploymentStatus.STARTED));
                service.receiveEvent(new DeploymentEvent("v2.0",
                        Arrays.asList("Alice"), DeploymentStatus.STARTED));

                // Revert Alice from v1.0 only
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice"), DeploymentStatus.REVERTED));

                // Alice should be removed from v1.0 but still in v2.0
                assertFalse(service.getPendingAuthorsForVersion("v1.0").contains("Alice"));
                assertTrue(service.getPendingAuthorsForVersion("v2.0").contains("Alice"));
            }
        }

        @Nested
        @DisplayName("Complex Scenarios")
        class ComplexScenarioTests {

            @Test
            @DisplayName("Should handle interleaved deployments")
            void testInterleavedDeployments() {
                // v1.0 starts
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice"), DeploymentStatus.STARTED));

                // v2.0 starts
                service.receiveEvent(new DeploymentEvent("v2.0",
                        Arrays.asList("Bob"), DeploymentStatus.STARTED));

                // v2.0 completes first
                service.receiveEvent(new DeploymentEvent("v2.0",
                        Arrays.asList(), DeploymentStatus.COMPLETED));

                List<Notification> batch1 = service.sendNotifications();
                assertEquals(1, batch1.size());
                assertEquals("Bob", batch1.get(0).getAuthor());

                // Alice still pending
                assertTrue(service.getPendingAuthorsForVersion("v1.0").contains("Alice"));

                // v1.0 completes later
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList(), DeploymentStatus.COMPLETED));

                List<Notification> batch2 = service.sendNotifications();
                assertEquals(1, batch2.size());
                assertEquals("Alice", batch2.get(0).getAuthor());
            }

            @Test
            @DisplayName("Should handle version failure without affecting others")
            void testVersionFailure() {
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList("Alice"), DeploymentStatus.STARTED));
                service.receiveEvent(new DeploymentEvent("v2.0",
                        Arrays.asList("Bob"), DeploymentStatus.STARTED));

                // v1.0 fails
                service.receiveEvent(new DeploymentEvent("v1.0",
                        Arrays.asList(), DeploymentStatus.FAILED));

                // v2.0 completes
                service.receiveEvent(new DeploymentEvent("v2.0",
                        Arrays.asList(), DeploymentStatus.COMPLETED));

                List<Notification> notifications = service.sendNotifications();

                // Only Bob notified, Alice still pending
                assertEquals(1, notifications.size());
                assertEquals("Bob", notifications.get(0).getAuthor());
                assertTrue(service.getPendingAuthorsForVersion("v1.0").contains("Alice"));
            }
        }
    }
}
