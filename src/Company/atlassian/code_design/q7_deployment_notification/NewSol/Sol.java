package Company.atlassian.code_design.q7_deployment_notification.NewSol;

/*
┌─────────────────────────────────────────────────────────────────┐
│                  <<enum>>                                        │
│               DeploymentStatus                                   │
├─────────────────────────────────────────────────────────────────┤
│ STARTED, COMPLETED, FAILED, REVERTED                            │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                   DeploymentEvent                                │
├─────────────────────────────────────────────────────────────────┤
│ - version: String                                               │
│ - authors: List<String>                                         │
│ - status: DeploymentStatus                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    Notification                                  │
├─────────────────────────────────────────────────────────────────┤
│ - author: String                                                │
│ - version: String                                               │
└─────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────┐
│                 DeploymentService                                │
├─────────────────────────────────────────────────────────────────┤
│ - versionAuthors: Map<String, Set<String>>  ← Per version!      │
│ - pendingNotifications: List<Notification>                      │
├─────────────────────────────────────────────────────────────────┤
│ + receiveEvent(event): void                                     │
│ + sendNotifications(): List<Notification>                       │
└─────────────────────────────────────────────────────────────────┘
 */

import java.util.*;
public class Sol {

    public enum DeploymentStatus {
        STARTED,
        COMPLETED,
        FAILED,
        REVERTED  // Scale-Up
    }


    public class DeploymentEvent {
        private final String version;
        private final List<String> authors;
        private final DeploymentStatus status;

        public DeploymentEvent(String version, List<String> authors, DeploymentStatus status) {
            this.version = version;
            this.authors = new ArrayList<>(authors);
            this.status = status;
        }

        // Convenience constructor for single author
        public DeploymentEvent(String version, String author, DeploymentStatus status) {
            this.version = version;
            this.authors = new ArrayList<>();
            this.authors.add(author);
            this.status = status;
        }

        public String getVersion() {
            return version;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public DeploymentStatus getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "DeploymentEvent{version='" + version + "', authors=" + authors +
                    ", status=" + status + "}";
        }
    }

    public class Notification {
        private final String author;
        private final String version;

        public Notification(String author, String version) {
            this.author = author;
            this.version = version;
        }

        public String getAuthor() {
            return author;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "Notification{author='" + author + "', version='" + version + "'}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Notification that = (Notification) o;
            return author.equals(that.author) && version.equals(that.version);
        }
    }


    public class DeploymentService {

        // Track authors per version
        private final Map<String, Set<String>> versionAuthors;
        private final List<Notification> pendingNotifications;

        public DeploymentService() {
            this.versionAuthors = new HashMap<>();
            this.pendingNotifications = new ArrayList<>();
        }

        public void receiveEvent(DeploymentEvent event) {
            DeploymentStatus status = event.getStatus();

            switch (status) {
                case STARTED:
                    handleStarted(event);
                    break;
                case COMPLETED:
                    handleCompleted(event);
                    break;
                case FAILED:
                    handleFailed(event);
                    break;
                case REVERTED:
                    handleReverted(event);
                    break;
            }
        }

        public List<Notification> sendNotifications() {
            List<Notification> toSend = new ArrayList<>(pendingNotifications);
            pendingNotifications.clear();
            return toSend;
        }

        // STARTED: Add authors to specific version
        private void handleStarted(DeploymentEvent event) {
            String version = event.getVersion();

            if (!versionAuthors.containsKey(version)) {
                versionAuthors.put(version, new HashSet<>());
            }

            for (String author : event.getAuthors()) {
                versionAuthors.get(version).add(author);
            }
        }

        // COMPLETED: Only notify authors for THIS version
        private void handleCompleted(DeploymentEvent event) {
            String version = event.getVersion();

            Set<String> authors = versionAuthors.get(version);
            if (authors == null || authors.isEmpty()) {
                return;
            }

            // Create notifications only for this version's authors
            for (String author : authors) {
                Notification notification = new Notification(author, version);
                pendingNotifications.add(notification);
            }

            // Clear only this version's authors
            versionAuthors.remove(version);
        }

        // FAILED: Keep authors pending for retry
        private void handleFailed(DeploymentEvent event) {
            // Do nothing - authors remain pending for this version
        }

        // REVERTED: Remove authors from specific version
        private void handleReverted(DeploymentEvent event) {
            String version = event.getVersion();

            Set<String> authors = versionAuthors.get(version);
            if (authors == null) {
                return;
            }

            for (String author : event.getAuthors()) {
                authors.remove(author);
            }
        }

        // Helper: Get pending authors for a version
        public Set<String> getPendingAuthorsForVersion(String version) {
            Set<String> authors = versionAuthors.get(version);
            return authors != null ? new HashSet<>(authors) : new HashSet<>();
        }

        // Helper: Get all pending versions
        public Set<String> getPendingVersions() {
            return new HashSet<>(versionAuthors.keySet());
        }

        // Helper: Get total pending authors count
        public int getTotalPendingAuthorsCount() {
            int count = 0;
            for (Set<String> authors : versionAuthors.values()) {
                count += authors.size();
            }
            return count;
        }
    }
}
