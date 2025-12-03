package Company.atlassian.code_design.q1_customer_satisfaction.OldSol;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

enum TieBreakingStrategy {
    AGENT_ID_ASC,
    AGENT_NAME_ASC,
    TOTAL_RATINGS_DESC,
    HIRE_DATE_ASC
}

class Rating {
    private int score;
    private String customerId;
    private LocalDate date;
    
    public Rating(int score, String customerId, LocalDate date) {
        this.score = score;
        this.customerId = customerId;
        this.date = date;
    }
    
    public int getScore() { return score; }
    public String getCustomerId() { return customerId; }
    public LocalDate getDate() { return date; }
    public YearMonth getYearMonth() { return YearMonth.from(date); }
}

class Agent {
    private String agentId;
    private String name;
    private LocalDate hireDate;
    private List<Rating> ratings;
    private Map<YearMonth, List<Rating>> monthlyRatings;
    
    public Agent(String agentId, String name, LocalDate hireDate) {
        this.agentId = agentId;
        this.name = name;
        this.hireDate = hireDate;
        this.ratings = new ArrayList<>();
        this.monthlyRatings = new HashMap<>();
    }
    
    public void addRating(Rating rating) {
        ratings.add(rating);
        monthlyRatings.computeIfAbsent(rating.getYearMonth(), k -> new ArrayList<>()).add(rating);
    }
    
    public String getAgentId() { return agentId; }
    public String getName() { return name; }
    public LocalDate getHireDate() { return hireDate; }
    
    public double getAverageRating() {
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
    }
    
    public int getTotalRatings() {
        return ratings.size();
    }
    
    public double getTotalRatingSum() {
        return ratings.stream().mapToInt(Rating::getScore).sum();
    }
    
    public double getAverageRatingForMonth(YearMonth month) {
        List<Rating> monthRatings = monthlyRatings.getOrDefault(month, new ArrayList<>());
        if (monthRatings.isEmpty()) return 0.0;
        return monthRatings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
    }
    
    public int getTotalRatingsForMonth(YearMonth month) {
        return monthlyRatings.getOrDefault(month, new ArrayList<>()).size();
    }
    
    public double getTotalRatingSumForMonth(YearMonth month) {
        return monthlyRatings.getOrDefault(month, new ArrayList<>()).stream()
            .mapToInt(Rating::getScore).sum();
    }
    
    public Set<YearMonth> getActiveMonths() {
        return new HashSet<>(monthlyRatings.keySet());
    }
}

interface ExportFormat {
    String export(List<Agent> agents, YearMonth month);
}

class CSVExportFormat implements ExportFormat {
    @Override
    public String export(List<Agent> agents, YearMonth month) {
        StringBuilder csv = new StringBuilder();
        if (month != null) {
            csv.append("AgentId,Name,MonthlyAverageRating,MonthlyTotalRatings\n");
            for (Agent agent : agents) {
                csv.append(agent.getAgentId()).append(",")
                   .append(agent.getName()).append(",")
                   .append(String.format("%.2f", agent.getAverageRatingForMonth(month))).append(",")
                   .append(agent.getTotalRatingsForMonth(month)).append("\n");
            }
        } else {
            csv.append("AgentId,Name,OverallAverageRating,TotalRatings\n");
            for (Agent agent : agents) {
                csv.append(agent.getAgentId()).append(",")
                   .append(agent.getName()).append(",")
                   .append(String.format("%.2f", agent.getAverageRating())).append(",")
                   .append(agent.getTotalRatings()).append("\n");
            }
        }
        return csv.toString();
    }
}

class JSONExportFormat implements ExportFormat {
    @Override
    public String export(List<Agent> agents, YearMonth month) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"agents\": [\n");
        
        for (int i = 0; i < agents.size(); i++) {
            Agent agent = agents.get(i);
            json.append("    {\n");
            json.append("      \"agentId\": \"").append(agent.getAgentId()).append("\",\n");
            json.append("      \"name\": \"").append(agent.getName()).append("\",\n");
            
            if (month != null) {
                json.append("      \"averageRating\": ").append(String.format("%.2f", agent.getAverageRatingForMonth(month))).append(",\n");
                json.append("      \"totalRatings\": ").append(agent.getTotalRatingsForMonth(month)).append("\n");
            } else {
                json.append("      \"averageRating\": ").append(String.format("%.2f", agent.getAverageRating())).append(",\n");
                json.append("      \"totalRatings\": ").append(agent.getTotalRatings()).append("\n");
            }
            
            if (i < agents.size() - 1) {
                json.append("    },\n");
            } else {
                json.append("    }\n");
            }
        }
        
        json.append("  ]\n");
        json.append("}");
        return json.toString();
    }
}

class XMLExportFormat implements ExportFormat {
    @Override
    public String export(List<Agent> agents, YearMonth month) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<agents");
        if (month != null) {
            xml.append(" month=\"").append(month).append("\"");
        }
        xml.append(">\n");
        
        for (Agent agent : agents) {
            xml.append("  <agent>\n");
            xml.append("    <agentId>").append(agent.getAgentId()).append("</agentId>\n");
            xml.append("    <name>").append(agent.getName()).append("</name>\n");
            
            if (month != null) {
                xml.append("    <averageRating>").append(String.format("%.2f", agent.getAverageRatingForMonth(month))).append("</averageRating>\n");
                xml.append("    <totalRatings>").append(agent.getTotalRatingsForMonth(month)).append("</totalRatings>\n");
            } else {
                xml.append("    <averageRating>").append(String.format("%.2f", agent.getAverageRating())).append("</averageRating>\n");
                xml.append("    <totalRatings>").append(agent.getTotalRatings()).append("</totalRatings>\n");
            }
            
            xml.append("  </agent>\n");
        }
        
        xml.append("</agents>");
        return xml.toString();
    }
}

public class Solution {
    
    public static class CustomerSatisfactionService {
        private Map<String, Agent> agents;
        private TieBreakingStrategy tieBreakingStrategy;
        private Set<YearMonth> allActiveMonths;
        
        public CustomerSatisfactionService() {
            this.agents = new HashMap<>();
            this.tieBreakingStrategy = TieBreakingStrategy.AGENT_ID_ASC;
            this.allActiveMonths = new HashSet<>();
        }
        
        public void addAgent(String agentId, String name, LocalDate hireDate) {
            if (agents.containsKey(agentId)) {
                throw new IllegalArgumentException("Agent with ID " + agentId + " already exists");
            }
            agents.put(agentId, new Agent(agentId, name, hireDate));
        }
        
        public void submitRating(String agentId, int rating, String customerId) {
            submitRating(agentId, rating, customerId, LocalDate.now());
        }
        
        public void submitRating(String agentId, int rating, String customerId, LocalDate date) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
            
            Agent agent = agents.get(agentId);
            if (agent == null) {
                throw new IllegalArgumentException("Agent not found: " + agentId);
            }
            
            Rating ratingObj = new Rating(rating, customerId, date);
            agent.addRating(ratingObj);
            allActiveMonths.add(ratingObj.getYearMonth());
        }
        
        public Agent getAgent(String agentId) {
            return agents.get(agentId);
        }
        
        public List<Agent> getAllAgentsOrderedByRating() {
            return getAllAgentsOrderedByRating(false);
        }
        
        public List<Agent> getAllAgentsOrderedByRating(boolean unsorted) {
            List<Agent> agentList = new ArrayList<>(agents.values());
            
            if (unsorted) {
                return agentList;
            }
            
            agentList.sort((a, b) -> {
                int ratingCompare = Double.compare(b.getAverageRating(), a.getAverageRating());
                if (ratingCompare != 0) {
                    return ratingCompare;
                }
                
                switch (tieBreakingStrategy) {
                    case AGENT_ID_ASC:
                        return a.getAgentId().compareTo(b.getAgentId());
                    case AGENT_NAME_ASC:
                        return a.getName().compareTo(b.getName());
                    case TOTAL_RATINGS_DESC:
                        return Integer.compare(b.getTotalRatings(), a.getTotalRatings());
                    case HIRE_DATE_ASC:
                        return a.getHireDate().compareTo(b.getHireDate());
                    default:
                        return a.getAgentId().compareTo(b.getAgentId());
                }
            });
            
            return agentList;
        }
        
        public List<Agent> getBestAgentsForMonth(YearMonth month) {
            List<Agent> agentList = new ArrayList<>(agents.values());
            agentList.sort((a, b) -> Double.compare(b.getAverageRatingForMonth(month), a.getAverageRatingForMonth(month)));
            return agentList;
        }
        
        public List<Agent> getAgentsWithTotalRating() {
            List<Agent> agentList = new ArrayList<>(agents.values());
            agentList.sort((a, b) -> Double.compare(b.getTotalRatingSum(), a.getTotalRatingSum()));
            return agentList;
        }
        
        public String exportAgentRatings(ExportFormat format) {
            List<Agent> orderedAgents = getAllAgentsOrderedByRating();
            return format.export(orderedAgents, null);
        }
        
        public String exportMonthlyAgentRatings(YearMonth month, ExportFormat format) {
            List<Agent> orderedAgents = getBestAgentsForMonth(month);
            return format.export(orderedAgents, month);
        }
        
        public Set<String> getAllAgentIds() {
            return new HashSet<>(agents.keySet());
        }
        
        public Set<YearMonth> getAllActiveMonths() {
            return new HashSet<>(allActiveMonths);
        }
        
        public Map<YearMonth, Double> getAverageRatingTrendForAgent(String agentId) {
            Agent agent = agents.get(agentId);
            if (agent == null) {
                return new HashMap<>();
            }
            
            Map<YearMonth, Double> trend = new HashMap<>();
            for (YearMonth month : agent.getActiveMonths()) {
                trend.put(month, agent.getAverageRatingForMonth(month));
            }
            return trend;
        }
        
        public void setTieBreakingStrategy(TieBreakingStrategy strategy) {
            this.tieBreakingStrategy = strategy;
        }
    }
    
    public static void main(String[] args) {
        CustomerSatisfactionService service = new CustomerSatisfactionService();
        
        service.addAgent("agent1", "Alice Smith", LocalDate.of(2023, 1, 15));
        service.addAgent("agent2", "Bob Johnson", LocalDate.of(2023, 2, 1));
        service.addAgent("agent3", "Carol Davis", LocalDate.of(2023, 1, 10));
        
        service.submitRating("agent1", 5, "customer1");
        service.submitRating("agent1", 4, "customer2");
        service.submitRating("agent2", 3, "customer3");
        service.submitRating("agent3", 4, "customer4");
        
        System.out.println("=== Agent Rankings ===");
        List<Agent> rankings = service.getAllAgentsOrderedByRating();
        for (int i = 0; i < rankings.size(); i++) {
            Agent agent = rankings.get(i);
            System.out.println((i + 1) + ". " + agent.getName() + 
                " (ID: " + agent.getAgentId() + 
                ", Avg: " + String.format("%.2f", agent.getAverageRating()) + 
                ", Total: " + agent.getTotalRatings() + ")");
        }
        
        System.out.println("\n=== CSV Export ===");
        System.out.println(service.exportAgentRatings(new CSVExportFormat()));
        
        System.out.println("\n=== JSON Export ===");
        System.out.println(service.exportAgentRatings(new JSONExportFormat()));
    }
}