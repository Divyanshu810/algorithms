package practice.atlassian.code_design.q7_deployment_notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Test class for the Deployment Notification solution.
 */
public class TestSolution {
    
    private DeploymentNotificationService baseService;
    private DeploymentNotificationServiceWithRevert revertService;
    private SimpleDeploymentNotificationService simpleService;
    
    @BeforeEach
    void setUp() {
        baseService = new DeploymentNotificationService();
        revertService = new DeploymentNotificationServiceWithRevert();
        simpleService = new SimpleDeploymentNotificationService();
    }
    
    @Test
    @DisplayName("Test single deployment with multiple authors")
    void testSingleDeploymentMultipleAuthors() {
        // Create a notification collector for testing
        List<Notification> notificationLog = new ArrayList<>();
        baseService.addNotificationListener(notification -> notificationLog.add(notification));
        
        // Single deployment with multiple authors
        List<String> authors = Arrays.asList("alice", "bob");
        baseService.receiveEvent(new DeploymentEvent("1.0.0", authors, DeploymentStatus.STARTED));
        baseService.receiveEvent(new DeploymentEvent("1.0.0", authors, DeploymentStatus.COMPLETED));
        
        baseService.sendPendingNotifications();
        assertEquals(2, notificationLog.size(), "Should generate 2 notifications");
        
        // Verify notification content
        for (Notification notification : notificationLog) {
            assertTrue(authors.contains(notification.getRecipient()), 
                    "Recipient should be one of the authors");
            assertEquals("1.0.0", notification.getVersion(), "Version should match");
            assertTrue(notification.getMessage().contains("1.0.0"), 
                    "Message should contain version");
        }
    }
    
    @Test
    @DisplayName("Test multiple deployments with overlapping authors")
    void testMultipleDeploymentsOverlappingAuthors() {
        // Create a notification collector for testing
        List<Notification> notificationLog = new ArrayList<>();
        baseService.addNotificationListener(notification -> notificationLog.add(notification));
        
        // First deployment
        List<String> authors1 = Arrays.asList("alice", "bob");
        baseService.receiveEvent(new DeploymentEvent("1.0.0", authors1, DeploymentStatus.STARTED));
        baseService.receiveEvent(new DeploymentEvent("1.0.0", authors1, DeploymentStatus.COMPLETED));
        
        baseService.sendPendingNotifications();
        
        // Second deployment with overlapping author
        notificationLog.clear();
        List<String> authors2 = Arrays.asList("bob", "charlie");
        baseService.receiveEvent(new DeploymentEvent("1.0.1", authors2, DeploymentStatus.STARTED));
        baseService.receiveEvent(new DeploymentEvent("1.0.1", authors2, DeploymentStatus.COMPLETED));
        
        baseService.sendPendingNotifications();
        assertEquals(1, notificationLog.size(), "Should generate 1 notification (only for charlie)");
        assertEquals("charlie", notificationLog.get(0).getRecipient(), "Should notify charlie");
    }
    
    @Test
    @DisplayName("Test failed deployment")
    void testFailedDeployment() {
        // Create a notification collector for testing
        List<Notification> notificationLog = new ArrayList<>();
        baseService.addNotificationListener(notification -> notificationLog.add(notification));
        
        // Failed deployment
        List<String> authors = Arrays.asList("david", "emma");
        baseService.receiveEvent(new DeploymentEvent("1.0.2", authors, DeploymentStatus.STARTED));
        baseService.receiveEvent(new DeploymentEvent("1.0.2", authors, DeploymentStatus.FAILED));
        
        baseService.sendPendingNotifications();
        assertTrue(notificationLog.isEmpty(), "Should not generate notifications for failed deployment");
    }
    
    @Test
    @DisplayName("Test multiple completed events")
    void testMultipleCompletedEvents() {
        // Create a notification collector for testing
        List<Notification> notificationLog = new ArrayList<>();
        baseService.addNotificationListener(notification -> notificationLog.add(notification));
        
        // Deployment with multiple completed events
        List<String> authors = Arrays.asList("frank", "grace");
        baseService.receiveEvent(new DeploymentEvent("1.0.3", authors, DeploymentStatus.STARTED));
        baseService.receiveEvent(new DeploymentEvent("1.0.3", authors, DeploymentStatus.COMPLETED));
        baseService.receiveEvent(new DeploymentEvent("1.0.3", authors, DeploymentStatus.COMPLETED));  // Duplicate
        
        baseService.sendPendingNotifications();
        assertEquals(2, notificationLog.size(), "Should generate notifications only once per author");
        
        // Check that authors were notified correctly
        Set<String> notifiedAuthors = new HashSet<>();
        for (Notification notification : notificationLog) {
            notifiedAuthors.add(notification.getRecipient());
        }
        assertEquals(authors.size(), notifiedAuthors.size(), "All authors should be notified once");
        assertTrue(notifiedAuthors.containsAll(authors), "All authors should be notified");
    }
    
    @Test
    @DisplayName("Test revert before deployment completes")
    void testRevertBeforeCompletion() {
        // Create a notification collector for testing
        List<Notification> notificationLog = new ArrayList<>();
        revertService.addNotificationListener(notification -> notificationLog.add(notification));
        
        // Test case: Revert before deployment completes
        List<String> authors = Arrays.asList("alice", "bob");
        revertService.receiveEvent(new DeploymentEvent("2.0.0", authors, DeploymentStatus.STARTED));
        
        // Revert bob's changes
        revertService.receiveEvent(new RevertDeploymentEvent("2.0.0", Collections.singletonList("bob"), "charlie"));
        
        // Complete the deployment
        revertService.receiveEvent(new DeploymentEvent("2.0.0", authors, DeploymentStatus.COMPLETED));
        
        revertService.sendPendingNotifications();
        assertEquals(1, notificationLog.size(), "Should generate 1 notification (only for alice)");
        assertEquals("alice", notificationLog.get(0).getRecipient(), "Should notify alice but not bob");
    }
    
    @Test
    @DisplayName("Test multiple reverts")
    void testMultipleReverts() {
        // Create a notification collector for testing
        List<Notification> notificationLog = new ArrayList<>();
        revertService.addNotificationListener(notification -> notificationLog.add(notification));
        
        // Test case: Multiple reverts
        List<String> authors = Arrays.asList("david", "emma", "frank");
        revertService.receiveEvent(new DeploymentEvent("2.0.1", authors, DeploymentStatus.STARTED));
        
        // Revert david and emma's changes
        revertService.receiveEvent(new RevertDeploymentEvent("2.0.1", Arrays.asList("david", "emma"), "grace"));
        
        // Complete the deployment
        revertService.receiveEvent(new DeploymentEvent("2.0.1", authors, DeploymentStatus.COMPLETED));
        
        revertService.sendPendingNotifications();
        assertEquals(1, notificationLog.size(), "Should generate 1 notification (only for frank)");
        assertEquals("frank", notificationLog.get(0).getRecipient(), 
                "Should notify frank but not david or emma");
    }
    
    @Test
    @DisplayName("Test revert after deployment completed")
    void testRevertAfterCompletion() {
        // Create a notification collector for testing
        List<Notification> notificationLog = new ArrayList<>();
        revertService.addNotificationListener(notification -> notificationLog.add(notification));
        
        // Test case: Revert after deployment completed
        List<String> authors = Arrays.asList("henry", "ian");
        revertService.receiveEvent(new DeploymentEvent("2.0.2", authors, DeploymentStatus.STARTED));
        revertService.receiveEvent(new DeploymentEvent("2.0.2", authors, DeploymentStatus.COMPLETED));
        
        // Notifications should be generated for both authors
        revertService.sendPendingNotifications();
        assertEquals(2, notificationLog.size(), "Should generate 2 notifications");
        
        // Revert after completion doesn't affect already sent notifications
        notificationLog.clear();
        revertService.receiveEvent(new RevertDeploymentEvent("2.0.2", Collections.singletonList("henry"), "julia"));
        
        revertService.sendPendingNotifications();
        assertTrue(notificationLog.isEmpty(), "Should not generate additional notifications");
    }
    
    @Test
    @DisplayName("Test simplified implementation")
    void testSimplifiedImplementation() {
        // Create a notification collector for testing
        List<Notification> notificationLog = new ArrayList<>();
        simpleService.addNotificationListener(notification -> notificationLog.add(notification));
        
        // Test case 1: Single deployment with multiple authors
        List<String> authors1 = Arrays.asList("alice", "bob");
        simpleService.receiveEvent(new DeploymentEvent("3.0.0", authors1, DeploymentStatus.STARTED));
        simpleService.receiveEvent(new DeploymentEvent("3.0.0", authors1, DeploymentStatus.COMPLETED));
        
        assertEquals(2, notificationLog.size(), "Should generate 2 notifications");
        
        // Test case 2: Multiple deployments with overlapping authors
        notificationLog.clear();
        
        List<String> authors2 = Arrays.asList("bob", "charlie");
        simpleService.receiveEvent(new DeploymentEvent("3.0.1", authors2, DeploymentStatus.STARTED));
        simpleService.receiveEvent(new DeploymentEvent("3.0.1", authors2, DeploymentStatus.COMPLETED));
        
        assertEquals(1, notificationLog.size(), "Should generate 1 notification (only for charlie)");
        assertEquals("charlie", notificationLog.get(0).getRecipient(), "Should notify charlie");
    }
    
    @Test
    @DisplayName("Test notification content")
    void testNotificationContent() {
        // Create a notification collector for testing
        List<Notification> notificationLog = new ArrayList<>();
        baseService.addNotificationListener(notification -> notificationLog.add(notification));
        
        // Create a deployment
        List<String> authors = Arrays.asList("alice", "bob");
        baseService.receiveEvent(new DeploymentEvent("1.0.0", authors, DeploymentStatus.STARTED));
        baseService.receiveEvent(new DeploymentEvent("1.0.0", authors, DeploymentStatus.COMPLETED));
        
        baseService.sendPendingNotifications();
        
        // Verify notification content
        for (Notification notification : notificationLog) {
            assertNotNull(notification.getRecipient(), "Recipient should not be null");
            assertNotNull(notification.getVersion(), "Version should not be null");
            assertNotNull(notification.getMessage(), "Message should not be null");
            assertTrue(notification.getMessage().contains(notification.getVersion()), 
                    "Message should contain version");
        }
    }
}