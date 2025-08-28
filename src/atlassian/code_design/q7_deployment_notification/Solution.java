package practice.atlassian.code_design.q7_deployment_notification;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Solution for Deployment Notification problem.
 * Implements a notification service for keeping developers informed about when their changes are deployed.
 */
public class Solution {
    
    public static void main(String[] args) {
        // Example usage of the base implementation
        DeploymentNotificationService service = new DeploymentNotificationService();
        
        // Register a notification listener
        service.addNotificationListener(notification -> 
            System.out.println("[Notification] " + notification.getMessage()));
        
        // Create some deployment events
        List<String> authors1 = Arrays.asList("alice", "bob");
        List<String> authors2 = Arrays.asList("bob", "charlie");
        
        // Started events
        service.receiveEvent(new DeploymentEvent("1.0.0", authors1, DeploymentStatus.STARTED));
        service.receiveEvent(new DeploymentEvent("1.0.1", authors2, DeploymentStatus.STARTED));
        
        // Completed events
        service.receiveEvent(new DeploymentEvent("1.0.0", authors1, DeploymentStatus.COMPLETED));
        service.receiveEvent(new DeploymentEvent("1.0.1", authors2, DeploymentStatus.COMPLETED));
        
        // Failed event
        List<String> authors3 = Arrays.asList("david", "emma");
        service.receiveEvent(new DeploymentEvent("1.0.2", authors3, DeploymentStatus.STARTED));
        service.receiveEvent(new DeploymentEvent("1.0.2", authors3, DeploymentStatus.FAILED));
        
        // Process notifications
        service.sendPendingNotifications();
        
        // Scale-up: Revert handling
        System.out.println("\n--- Scale-up: Revert Handling ---");
        DeploymentNotificationServiceWithRevert serviceWithRevert = new DeploymentNotificationServiceWithRevert();
        
        // Register a notification listener
        serviceWithRevert.addNotificationListener(notification -> 
            System.out.println("[Notification] " + notification.getMessage()));
        
        // Create deployment events
        serviceWithRevert.receiveEvent(new DeploymentEvent("2.0.0", 
                Arrays.asList("frank", "grace"), DeploymentStatus.STARTED));
        
        // Revert event before deployment completes
        serviceWithRevert.receiveEvent(new RevertDeploymentEvent("2.0.0", 
                Collections.singletonList("grace"), "henry"));
        
        // Complete the deployment
        serviceWithRevert.receiveEvent(new DeploymentEvent("2.0.0", 
                Arrays.asList("frank", "grace"), DeploymentStatus.COMPLETED));
        
        // Process notifications - should only notify frank, not grace
        serviceWithRevert.sendPendingNotifications();
        
        // Scale-down: Simplified implementation
        System.out.println("\n--- Scale-down: Simplified Implementation ---");
        SimpleDeploymentNotificationService simpleService = new SimpleDeploymentNotificationService();
        
        // Register a notification listener
        simpleService.addNotificationListener(notification -> 
            System.out.println("[Notification] " + notification.getMessage()));
        
        // Create deployment events (only STARTED and COMPLETED)
        simpleService.receiveEvent(new DeploymentEvent("3.0.0", 
                Arrays.asList("ian", "julia"), DeploymentStatus.STARTED));
        simpleService.receiveEvent(new DeploymentEvent("3.0.0", 
                Arrays.asList("ian", "julia"), DeploymentStatus.COMPLETED));
        
        // Process notifications
        simpleService.sendPendingNotifications();
    }
}

/**
 * Enum representing the status of a deployment.
 */
enum DeploymentStatus {
    STARTED,
    COMPLETED,
    FAILED
}

/**
 * Represents a deployment event with version, authors, and status.
 */
class DeploymentEvent {
    private String version;
    private List<String> authors;
    private DeploymentStatus status;
    
    public DeploymentEvent(String version, List<String> authors, DeploymentStatus status) {
        this.version = version;
        this.authors = new ArrayList<>(authors);
        this.status = status;
    }
    
    public String getVersion() {
        return version;
    }
    
    public List<String> getAuthors() {
        return new ArrayList<>(authors);
    }
    
    public DeploymentStatus getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return "DeploymentEvent{" +
                "version='" + version + '\'' +
                ", authors=" + authors +
                ", status=" + status +
                '}';
    }
}

/**
 * Represents a revert deployment event.
 */
class RevertDeploymentEvent extends DeploymentEvent {
    private String revertAuthor;
    
    public RevertDeploymentEvent(String version, List<String> revertedAuthors, String revertAuthor) {
        super(version, revertedAuthors, DeploymentStatus.STARTED);  // Status doesn't matter for reverts
        this.revertAuthor = revertAuthor;
    }
    
    public String getRevertAuthor() {
        return revertAuthor;
    }
    
    @Override
    public String toString() {
        return "RevertDeploymentEvent{" +
                "version='" + getVersion() + '\'' +
                ", revertedAuthors=" + getAuthors() +
                ", revertAuthor='" + revertAuthor + '\'' +
                '}';
    }
}

/**
 * Represents a notification to be sent to an author.
 */
class Notification {
    private String recipient;
    private String version;
    private String message;
    
    public Notification(String recipient, String version) {
        this.recipient = recipient;
        this.version = version;
        this.message = "Your changes have been deployed in version " + version;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getMessage() {
        return message;
    }
}

/**
 * Interface for notification listeners.
 */
interface NotificationListener {
    void onNotification(Notification notification);
}

/**
 * Base implementation of the deployment notification service.
 */
class DeploymentNotificationService {
    protected Map<String, Set<String>> versionToAuthors;
    protected Set<String> notifiedAuthors;
    protected List<Notification> pendingNotifications;
    protected List<NotificationListener> listeners;
    
    public DeploymentNotificationService() {
        this.versionToAuthors = new HashMap<>();
        this.notifiedAuthors = new HashSet<>();
        this.pendingNotifications = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }
    
    public void addNotificationListener(NotificationListener listener) {
        listeners.add(listener);
    }
    
    public void receiveEvent(DeploymentEvent event) {
        String version = event.getVersion();
        
        // Store the authors for this version
        if (event.getStatus() == DeploymentStatus.STARTED) {
            versionToAuthors.put(version, new HashSet<>(event.getAuthors()));
        } 
        // Generate notifications for completed deployments
        else if (event.getStatus() == DeploymentStatus.COMPLETED) {
            Set<String> authors = versionToAuthors.getOrDefault(version, new HashSet<>());
            
            for (String author : authors) {
                // Only notify if the author hasn't been notified before
                if (!notifiedAuthors.contains(author)) {
                    pendingNotifications.add(new Notification(author, version));
                    notifiedAuthors.add(author);
                }
            }
        }
        // No action needed for failed deployments
    }
    
    public void sendPendingNotifications() {
        List<Notification> notifications = new ArrayList<>(pendingNotifications);
        pendingNotifications.clear();
        
        for (Notification notification : notifications) {
            for (NotificationListener listener : listeners) {
                listener.onNotification(notification);
            }
        }
    }
    
    public Set<String> getNotifiedAuthors() {
        return new HashSet<>(notifiedAuthors);
    }
}

/**
 * Extended implementation with revert support.
 */
class DeploymentNotificationServiceWithRevert extends DeploymentNotificationService {
    private Map<String, Set<String>> versionToRevertedAuthors;
    
    public DeploymentNotificationServiceWithRevert() {
        super();
        this.versionToRevertedAuthors = new HashMap<>();
    }
    
    @Override
    public void receiveEvent(DeploymentEvent event) {
        // Handle revert events
        if (event instanceof RevertDeploymentEvent) {
            handleRevertEvent((RevertDeploymentEvent) event);
            return;
        }
        
        String version = event.getVersion();
        
        // Store the authors for this version
        if (event.getStatus() == DeploymentStatus.STARTED) {
            versionToAuthors.put(version, new HashSet<>(event.getAuthors()));
        } 
        // Generate notifications for completed deployments
        else if (event.getStatus() == DeploymentStatus.COMPLETED) {
            Set<String> authors = versionToAuthors.getOrDefault(version, new HashSet<>());
            Set<String> revertedAuthors = versionToRevertedAuthors.getOrDefault(version, new HashSet<>());
            
            for (String author : authors) {
                // Only notify if the author hasn't been notified before and their changes weren't reverted
                if (!notifiedAuthors.contains(author) && !revertedAuthors.contains(author)) {
                    pendingNotifications.add(new Notification(author, version));
                    notifiedAuthors.add(author);
                }
            }
        }
        // No action needed for failed deployments
    }
    
    private void handleRevertEvent(RevertDeploymentEvent event) {
        String version = event.getVersion();
        List<String> revertedAuthors = event.getAuthors();
        
        // Store reverted authors for this version
        Set<String> existingRevertedAuthors = versionToRevertedAuthors.getOrDefault(version, new HashSet<>());
        existingRevertedAuthors.addAll(revertedAuthors);
        versionToRevertedAuthors.put(version, existingRevertedAuthors);
    }
}

/**
 * Simplified implementation for the scale-down requirement.
 */
class SimpleDeploymentNotificationService {
    private Map<String, Set<String>> pendingDeployments;  // version -> authors
    private Set<String> notifiedAuthors;
    private List<NotificationListener> listeners;
    
    public SimpleDeploymentNotificationService() {
        this.pendingDeployments = new HashMap<>();
        this.notifiedAuthors = new HashSet<>();
        this.listeners = new ArrayList<>();
    }
    
    public void addNotificationListener(NotificationListener listener) {
        listeners.add(listener);
    }
    
    public void receiveEvent(DeploymentEvent event) {
        String version = event.getVersion();
        
        if (event.getStatus() == DeploymentStatus.STARTED) {
            // Record the deployment start
            pendingDeployments.put(version, new HashSet<>(event.getAuthors()));
        } 
        else if (event.getStatus() == DeploymentStatus.COMPLETED) {
            // Process completed deployment
            Set<String> authors = pendingDeployments.remove(version);
            if (authors != null) {
                // Notify authors who haven't been notified before
                List<Notification> notifications = authors.stream()
                    .filter(author -> !notifiedAuthors.contains(author))
                    .map(author -> {
                        notifiedAuthors.add(author);
                        return new Notification(author, version);
                    })
                    .collect(Collectors.toList());
                
                // Queue notifications
                for (Notification notification : notifications) {
                    for (NotificationListener listener : listeners) {
                        listener.onNotification(notification);
                    }
                }
            }
        }
    }
    
    public void sendPendingNotifications() {
        // All notifications are sent immediately in this simplified version
    }
}