package Company.atlassian.code_design.q1_customer_satisfaction.NewSol;

import java.time.LocalDateTime;
import java.util.*;

/*
┌─────────────────────────────────────────────────────────────────┐
│                       Agent                                      │
├─────────────────────────────────────────────────────────────────┤
│ - id: String                                                    │
│ - name: String                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       Rating                                     │
├─────────────────────────────────────────────────────────────────┤
│ - agentId: String                                               │
│ - score: int (1-5)                                              │
│ - timestamp: LocalDateTime                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    AgentStats                                    │
├─────────────────────────────────────────────────────────────────┤
│ - agentId: String                                               │
│ - agentName: String                                             │
│ - averageRating: double                                         │
│ - totalRatings: int                                             │
│ - totalScore: int                                               │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│               <<interface>>                                      │
│              TieBreakStrategy                                    │
├─────────────────────────────────────────────────────────────────┤
│ + compare(a1: AgentStats, a2: AgentStats): int                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│               <<interface>>                                      │
│                 Exporter                                         │
├─────────────────────────────────────────────────────────────────┤
│ + export(data: Map<String, List<AgentStats>>): String           │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                 RatingService                                    │
├─────────────────────────────────────────────────────────────────┤
│ - agents: Map<String, Agent>                                    │
│ - ratings: List<Rating>                                         │
│ - tieBreaker: TieBreakStrategy                                  │
├─────────────────────────────────────────────────────────────────┤
│ + addAgent(id, name): void                                      │
│ + addRating(agentId, score): void                               │
│ + addRating(agentId, score, timestamp): void                    │
│ + getAgentsSortedByRating(): List<AgentStats>                   │
│ + getAgentsUnsorted(): List<AgentStats>                         │
│ + getAgentTotalRatings(): List<AgentStats>                      │
│ + getBestAgentByMonth(year, month): AgentStats                  │
│ + getMonthlyRatings(): Map<String, List<AgentStats>>            │
│ + exportMonthlyRatings(exporter): String                        │
└─────────────────────────────────────────────────────────────────┘
 */

public class RatingService {

    public class Agent {
        private final String id;
        private final String name;

        public Agent(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Rating {
        private final String agentId;
        private final int score;  // 1-5
        private final LocalDateTime timestamp;

        public Rating(String agentId, int score) {
            this(agentId, score, LocalDateTime.now());
        }

        public Rating(String agentId, int score, LocalDateTime timestamp) {
            if (score < 1 || score > 5) {
                throw new IllegalArgumentException("Score must be between 1 and 5");
            }
            this.agentId = agentId;
            this.score = score;
            this.timestamp = timestamp;
        }

        public String getAgentId() {
            return agentId;
        }

        public int getScore() {
            return score;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public int getYear() {
            return timestamp.getYear();
        }

        public int getMonth() {
            return timestamp.getMonthValue();
        }
    }

    public class AgentStats {
        private final String agentId;
        private final String agentName;
        private final double averageRating;
        private final int totalRatings;
        private final int totalScore;

        public AgentStats(String agentId, String agentName, int totalScore, int totalRatings) {
            this.agentId = agentId;
            this.agentName = agentName;
            this.totalScore = totalScore;
            this.totalRatings = totalRatings;
            this.averageRating = totalRatings > 0 ? (double) totalScore / totalRatings : 0.0;
        }

        public String getAgentId() {
            return agentId;
        }

        public String getAgentName() {
            return agentName;
        }

        public double getAverageRating() {
            return averageRating;
        }

        public int getTotalRatings() {
            return totalRatings;
        }

        public int getTotalScore() {
            return totalScore;
        }

        @Override
        public String toString() {
            return agentName + ": " + String.format("%.2f", averageRating) +
                    " (from " + totalRatings + " ratings)";
        }
    }

    public interface TieBreakStrategy extends Comparator<AgentStats> {
        int compare(AgentStats a1, AgentStats a2);
    }

    public class TieBreakStrategies {

        // More ratings = higher rank
        public static TieBreakStrategy BY_TOTAL_RATINGS = (a1, a2) -> {
            int avgCompare = Double.compare(a2.getAverageRating(), a1.getAverageRating());
            if (avgCompare != 0) return avgCompare;
            return Integer.compare(a2.getTotalRatings(), a1.getTotalRatings());
        };

        // Alphabetical by name
        public static TieBreakStrategy BY_NAME = (a1, a2) -> {
            int avgCompare = Double.compare(a2.getAverageRating(), a1.getAverageRating());
            if (avgCompare != 0) return avgCompare;
            return a1.getAgentName().compareTo(a2.getAgentName());
        };

    }

    public interface Exporter {
        String export(Map<Integer, List<AgentStats>> monthlyData);
    }
    public class CSVExporter implements Exporter {

        @Override
        public String export(Map<Integer, List<AgentStats>> monthlyData) {
            StringBuilder sb = new StringBuilder();
            sb.append("Month,AgentId,AgentName,AverageRating,TotalRatings\n");

            for (Map.Entry<Integer, List<AgentStats>> entry : monthlyData.entrySet()) {
                int monthKey = entry.getKey();
                int year = monthKey / 100;
                int month = monthKey % 100;

                for (AgentStats stats : entry.getValue()) {
                    sb.append(year).append("-").append(month).append(",")
                            .append(stats.getAgentId()).append(",")
                            .append(stats.getAgentName()).append(",")
                            .append(stats.getAverageRating()).append(",")
                            .append(stats.getTotalRatings()).append("\n");
                }
            }

            return sb.toString();
        }
    }

    private final Map<String, Agent> agents;
    private final List<Rating> ratings;
    private TieBreakStrategy tieBreaker;

    public RatingService() {
        this.agents = new HashMap<>();
        this.ratings = new ArrayList<>();
        this.tieBreaker = TieBreakStrategies.BY_TOTAL_RATINGS;
    }

    public void setTieBreaker(TieBreakStrategy tieBreaker) {
        this.tieBreaker = tieBreaker;
    }

    public void addAgent(String id, String name) {
        agents.put(id, new Agent(id, name));
    }

    public void addRating(String agentId, int score) {
        if (!agents.containsKey(agentId)) {
            throw new IllegalArgumentException("Agent not found: " + agentId);
        }
        ratings.add(new Rating(agentId, score));
    }

    public void addRating(String agentId, int score, LocalDateTime timestamp) {
        if (!agents.containsKey(agentId)) {
            throw new IllegalArgumentException("Agent not found: " + agentId);
        }
        ratings.add(new Rating(agentId, score, timestamp));
    }

    public List<AgentStats> getAgentsSortedByRating() {
        List<AgentStats> stats = getAgentStats(ratings);
        Collections.sort(stats, tieBreaker);
        return stats;
    }

    public List<AgentStats> getAgentsUnsorted() {
        return getAgentStats(ratings);
    }

    public List<AgentStats> getAgentTotalRatings() {
        return getAgentStats(ratings);
    }

    public AgentStats getBestAgentByMonth(int year, int month) {
        List<Rating> monthlyRatings = new ArrayList<>();
        for (Rating rating : ratings) {
            if (rating.getYear() == year && rating.getMonth() == month) {
                monthlyRatings.add(rating);
            }
        }

        if (monthlyRatings.isEmpty()) {
            return null;
        }

        List<AgentStats> stats = getAgentStats(monthlyRatings);
        Collections.sort(stats, tieBreaker);

        return stats.get(0);
    }

    public Map<Integer, List<AgentStats>> getMonthlyRatings() {
        // Group ratings by month using integer key: year * 100 + month
        // e.g., Jan 2024 = 202401, Dec 2024 = 202412
        Map<Integer, List<Rating>> ratingsByMonth = new HashMap<>();

        for (Rating rating : ratings) {
            int monthKey = rating.getYear() * 100 + rating.getMonth();

            if (!ratingsByMonth.containsKey(monthKey)) {
                ratingsByMonth.put(monthKey, new ArrayList<>());
            }
            ratingsByMonth.get(monthKey).add(rating);
        }

        // Calculate stats for each month
        Map<Integer, List<AgentStats>> result = new TreeMap<>();

        for (Map.Entry<Integer, List<Rating>> entry : ratingsByMonth.entrySet()) {
            List<AgentStats> stats = getAgentStats(entry.getValue());
            Collections.sort(stats, tieBreaker);
            result.put(entry.getKey(), stats);
        }

        return result;
    }

    public String exportMonthlyRatings(Exporter exporter) {
        return exporter.export(getMonthlyRatings());
    }

    private List<AgentStats> getAgentStats(List<Rating> ratingList) {
        Map<String, int[]> statsMap = new HashMap<>();

        for (Rating rating : ratingList) {
            String agentId = rating.getAgentId();

            if (!statsMap.containsKey(agentId)) {
                statsMap.put(agentId, new int[2]);
            }

            statsMap.get(agentId)[0] += rating.getScore();
            statsMap.get(agentId)[1]++;
        }

        List<AgentStats> result = new ArrayList<>();

        for (Map.Entry<String, int[]> entry : statsMap.entrySet()) {
            String agentId = entry.getKey();
            int totalScore = entry.getValue()[0];
            int count = entry.getValue()[1];

            Agent agent = agents.get(agentId);
            result.add(new AgentStats(agentId, agent.getName(), totalScore, count));
        }

        return result;
    }
}
