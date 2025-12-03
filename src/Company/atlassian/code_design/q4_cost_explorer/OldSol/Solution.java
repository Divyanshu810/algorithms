package Company.atlassian.code_design.q4_cost_explorer.OldSol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

enum PlanType {
    MONTHLY,
    YEARLY,
    USAGE_BASED,
    TIERED
}

enum SubscriptionStatus {
    ACTIVE,
    PAUSED,
    CANCELLED,
    PENDING
}

class Product {
    private String productId;
    private String name;
    private List<PricingPlan> availablePlans;
    
    public Product(String productId, String name) {
        this.productId = productId;
        this.name = name;
        this.availablePlans = new ArrayList<>();
    }
    
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public List<PricingPlan> getAvailablePlans() { return availablePlans; }
    
    public void addPlan(PricingPlan plan) {
        availablePlans.add(plan);
    }
}

class PricingPlan {
    private String planId;
    private String name;
    private PlanType type;
    private BigDecimal basePrice;
    private String currency;
    private Map<String, Object> planDetails;
    
    public PricingPlan(String planId, String name, PlanType type, BigDecimal basePrice, String currency) {
        this.planId = planId;
        this.name = name;
        this.type = type;
        this.basePrice = basePrice;
        this.currency = currency;
        this.planDetails = new HashMap<>();
    }
    
    public String getPlanId() { return planId; }
    public String getName() { return name; }
    public PlanType getType() { return type; }
    public BigDecimal getBasePrice() { return basePrice; }
    public String getCurrency() { return currency; }
    public Map<String, Object> getPlanDetails() { return planDetails; }
    
    public BigDecimal getDailyRate() {
        switch (type) {
            case MONTHLY:
                return basePrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP);
            case YEARLY:
                return basePrice.divide(BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);
            default:
                return basePrice;
        }
    }
}

class Subscription {
    private String subscriptionId;
    private String customerId;
    private Product product;
    private PricingPlan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private int quantity;
    private Map<String, Object> metadata;
    
    public Subscription(String subscriptionId, String customerId, Product product, 
                       PricingPlan plan, LocalDate startDate) {
        this.subscriptionId = subscriptionId;
        this.customerId = customerId;
        this.product = product;
        this.plan = plan;
        this.startDate = startDate;
        this.status = SubscriptionStatus.ACTIVE;
        this.quantity = 1;
        this.metadata = new HashMap<>();
    }
    
    public String getSubscriptionId() { return subscriptionId; }
    public String getCustomerId() { return customerId; }
    public Product getProduct() { return product; }
    public PricingPlan getPlan() { return plan; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public SubscriptionStatus getStatus() { return status; }
    public int getQuantity() { return quantity; }
    
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void changePlan(PricingPlan newPlan) { this.plan = newPlan; }
    
    public boolean isActiveInMonth(YearMonth month) {
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();
        
        return status == SubscriptionStatus.ACTIVE &&
               !startDate.isAfter(monthEnd) &&
               (endDate == null || !endDate.isBefore(monthStart));
    }
    
    public int getActiveDaysInMonth(YearMonth month) {
        if (!isActiveInMonth(month)) {
            return 0;
        }
        
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();
        LocalDate effectiveStart = startDate.isAfter(monthStart) ? startDate : monthStart;
        LocalDate effectiveEnd = (endDate != null && endDate.isBefore(monthEnd)) ? endDate : monthEnd;
        
        return (int) ChronoUnit.DAYS.between(effectiveStart, effectiveEnd.plusDays(1));
    }
}

class MonthlyCost {
    private YearMonth month;
    private BigDecimal totalCost;
    private String currency;
    private List<SubscriptionCost> subscriptionCosts;
    private Map<String, BigDecimal> costByProduct;
    
    public MonthlyCost(YearMonth month, String currency) {
        this.month = month;
        this.currency = currency;
        this.totalCost = BigDecimal.ZERO;
        this.subscriptionCosts = new ArrayList<>();
        this.costByProduct = new HashMap<>();
    }
    
    public YearMonth getMonth() { return month; }
    public BigDecimal getTotalCost() { return totalCost; }
    public String getCurrency() { return currency; }
    public List<SubscriptionCost> getSubscriptionCosts() { return subscriptionCosts; }
    public Map<String, BigDecimal> getCostByProduct() { return costByProduct; }
    
    public void addSubscriptionCost(SubscriptionCost cost) {
        subscriptionCosts.add(cost);
        totalCost = totalCost.add(cost.getCost());
        
        String productId = cost.getSubscription().getProduct().getProductId();
        costByProduct.merge(productId, cost.getCost(), BigDecimal::add);
    }
}

class SubscriptionCost {
    private Subscription subscription;
    private YearMonth month;
    private BigDecimal cost;
    private int activeDays;
    private String description;
    
    public SubscriptionCost(Subscription subscription, YearMonth month, BigDecimal cost, int activeDays) {
        this.subscription = subscription;
        this.month = month;
        this.cost = cost;
        this.activeDays = activeDays;
        this.description = generateDescription();
    }
    
    public Subscription getSubscription() { return subscription; }
    public YearMonth getMonth() { return month; }
    public BigDecimal getCost() { return cost; }
    public int getActiveDays() { return activeDays; }
    public String getDescription() { return description; }
    
    private String generateDescription() {
        return String.format("%s - %s (%d days)", 
                           subscription.getProduct().getName(),
                           subscription.getPlan().getName(),
                           activeDays);
    }
}

class YearlyCostReport {
    private int year;
    private BigDecimal totalCost;
    private String currency;
    private List<MonthlyCost> monthlyCosts;
    private Map<String, BigDecimal> costByProduct;
    private LocalDate reportGeneratedOn;
    
    public YearlyCostReport(int year, String currency) {
        this.year = year;
        this.currency = currency;
        this.totalCost = BigDecimal.ZERO;
        this.monthlyCosts = new ArrayList<>();
        this.costByProduct = new HashMap<>();
        this.reportGeneratedOn = LocalDate.now();
    }
    
    public int getYear() { return year; }
    public BigDecimal getTotalCost() { return totalCost; }
    public String getCurrency() { return currency; }
    public List<MonthlyCost> getMonthlyCosts() { return monthlyCosts; }
    public Map<String, BigDecimal> getCostByProduct() { return costByProduct; }
    public LocalDate getReportGeneratedOn() { return reportGeneratedOn; }
    
    public void addMonthlyCost(MonthlyCost monthlyCost) {
        monthlyCosts.add(monthlyCost);
        totalCost = totalCost.add(monthlyCost.getTotalCost());
        
        monthlyCost.getCostByProduct().forEach((product, cost) -> 
            costByProduct.merge(product, cost, BigDecimal::add));
    }
    
    public BigDecimal getAverageMonthlyCost() {
        if (monthlyCosts.isEmpty()) return BigDecimal.ZERO;
        return totalCost.divide(BigDecimal.valueOf(monthlyCosts.size()), 2, RoundingMode.HALF_UP);
    }
}

public class Solution {
    
    public static class CostExplorer {
        private List<Subscription> subscriptions;
        private Map<YearMonth, MonthlyCost> costCache;
        private String defaultCurrency;
        
        public CostExplorer(String defaultCurrency) {
            this.subscriptions = new ArrayList<>();
            this.costCache = new HashMap<>();
            this.defaultCurrency = defaultCurrency;
        }
        
        public void addSubscription(Subscription subscription) {
            subscriptions.add(subscription);
            clearCache(); // Clear cache when subscriptions change
        }
        
        public void removeSubscription(String subscriptionId) {
            subscriptions.removeIf(sub -> sub.getSubscriptionId().equals(subscriptionId));
            clearCache();
        }
        
        public MonthlyCost calculateMonthlyCost(YearMonth month) {
            if (costCache.containsKey(month)) {
                return costCache.get(month);
            }
            
            MonthlyCost monthlyCost = new MonthlyCost(month, defaultCurrency);
            
            for (Subscription subscription : subscriptions) {
                if (subscription.isActiveInMonth(month)) {
                    SubscriptionCost subCost = calculateSubscriptionCost(subscription, month);
                    monthlyCost.addSubscriptionCost(subCost);
                }
            }
            
            costCache.put(month, monthlyCost);
            return monthlyCost;
        }
        
        private SubscriptionCost calculateSubscriptionCost(Subscription subscription, YearMonth month) {
            int activeDays = subscription.getActiveDaysInMonth(month);
            
            BigDecimal dailyRate = subscription.getPlan().getDailyRate();
            BigDecimal cost = dailyRate.multiply(BigDecimal.valueOf(activeDays))
                                     .multiply(BigDecimal.valueOf(subscription.getQuantity()))
                                     .setScale(2, RoundingMode.HALF_UP);
            
            return new SubscriptionCost(subscription, month, cost, activeDays);
        }
        
        public YearlyCostReport generateYearlyReport(int year) {
            YearlyCostReport report = new YearlyCostReport(year, defaultCurrency);
            
            for (int monthValue = 1; monthValue <= 12; monthValue++) {
                YearMonth month = YearMonth.of(year, monthValue);
                MonthlyCost monthlyCost = calculateMonthlyCost(month);
                report.addMonthlyCost(monthlyCost);
            }
            
            return report;
        }
        
        public List<Subscription> getActiveSubscriptions(YearMonth month) {
            return subscriptions.stream()
                .filter(sub -> sub.isActiveInMonth(month))
                .collect(Collectors.toList());
        }
        
        public BigDecimal getTotalCostBetween(LocalDate startDate, LocalDate endDate) {
            BigDecimal totalCost = BigDecimal.ZERO;
            YearMonth start = YearMonth.from(startDate);
            YearMonth end = YearMonth.from(endDate);
            
            for (YearMonth month = start; !month.isAfter(end); month = month.plusMonths(1)) {
                MonthlyCost monthlyCost = calculateMonthlyCost(month);
                totalCost = totalCost.add(monthlyCost.getTotalCost());
            }
            
            return totalCost;
        }
        
        private void clearCache() {
            costCache.clear();
        }
        
        public List<Subscription> getAllSubscriptions() {
            return new ArrayList<>(subscriptions);
        }
    }
    
    // Factory for creating common pricing plans
    public static class PricingPlanFactory {
        public static PricingPlan createMonthlyPlan(String planId, String name, BigDecimal monthlyPrice, String currency) {
            return new PricingPlan(planId, name, PlanType.MONTHLY, monthlyPrice, currency);
        }
        
        public static PricingPlan createYearlyPlan(String planId, String name, BigDecimal yearlyPrice, String currency) {
            return new PricingPlan(planId, name, PlanType.YEARLY, yearlyPrice, currency);
        }
        
        public static Product createJiraProduct() {
            Product jira = new Product("jira", "Jira Software");
            jira.addPlan(createMonthlyPlan("jira-std-monthly", "Standard Monthly", new BigDecimal("10.00"), "USD"));
            jira.addPlan(createMonthlyPlan("jira-prem-monthly", "Premium Monthly", new BigDecimal("20.00"), "USD"));
            jira.addPlan(createYearlyPlan("jira-std-yearly", "Standard Yearly", new BigDecimal("100.00"), "USD"));
            jira.addPlan(createYearlyPlan("jira-prem-yearly", "Premium Yearly", new BigDecimal("200.00"), "USD"));
            return jira;
        }
        
        public static Product createConfluenceProduct() {
            Product confluence = new Product("confluence", "Confluence");
            confluence.addPlan(createMonthlyPlan("conf-std-monthly", "Standard Monthly", new BigDecimal("8.00"), "USD"));
            confluence.addPlan(createYearlyPlan("conf-std-yearly", "Standard Yearly", new BigDecimal("80.00"), "USD"));
            return confluence;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Cost Explorer Demo ===");
        
        // Create products and plans
        Product jira = PricingPlanFactory.createJiraProduct();
        Product confluence = PricingPlanFactory.createConfluenceProduct();
        
        // Create cost explorer
        CostExplorer explorer = new CostExplorer("USD");
        
        // Create subscriptions
        LocalDate today = LocalDate.now();
        
        Subscription jiraSubscription = new Subscription(
            "sub-1", "customer-123", jira, jira.getAvailablePlans().get(0), today.minusDays(15)
        );
        jiraSubscription.setQuantity(10); // 10 users
        
        Subscription confluenceSubscription = new Subscription(
            "sub-2", "customer-123", confluence, confluence.getAvailablePlans().get(0), today.minusDays(30)
        );
        confluenceSubscription.setQuantity(5); // 5 users
        
        explorer.addSubscription(jiraSubscription);
        explorer.addSubscription(confluenceSubscription);
        
        // Generate current month cost
        YearMonth currentMonth = YearMonth.now();
        System.out.println("\n=== Current Month Cost (" + currentMonth + ") ===");
        MonthlyCost currentCost = explorer.calculateMonthlyCost(currentMonth);
        System.out.println("Total Cost: " + currentCost.getCurrency() + " " + currentCost.getTotalCost());
        
        for (SubscriptionCost subCost : currentCost.getSubscriptionCosts()) {
            System.out.println("  " + subCost.getDescription() + ": " + 
                             currentCost.getCurrency() + " " + subCost.getCost());
        }
        
        // Generate yearly report
        System.out.println("\n=== Yearly Cost Report (" + currentMonth.getYear() + ") ===");
        YearlyCostReport yearlyReport = explorer.generateYearlyReport(currentMonth.getYear());
        System.out.println("Total Yearly Cost: " + yearlyReport.getCurrency() + " " + yearlyReport.getTotalCost());
        System.out.println("Average Monthly Cost: " + yearlyReport.getCurrency() + " " + yearlyReport.getAverageMonthlyCost());
        
        System.out.println("\nCost by Product:");
        yearlyReport.getCostByProduct().forEach((product, cost) -> 
            System.out.println("  " + product + ": " + yearlyReport.getCurrency() + " " + cost));
        
        System.out.println("\nMonthly Breakdown:");
        for (MonthlyCost monthlyCost : yearlyReport.getMonthlyCosts()) {
            System.out.println("  " + monthlyCost.getMonth() + ": " + 
                             monthlyCost.getCurrency() + " " + monthlyCost.getTotalCost());
        }
        
        // Test plan change scenario
        System.out.println("\n=== Plan Change Scenario ===");
        jiraSubscription.changePlan(jira.getAvailablePlans().get(1)); // Upgrade to premium
        explorer.clearCache(); // Clear cache to reflect changes
        
        YearMonth nextMonth = currentMonth.plusMonths(1);
        MonthlyCost nextMonthCost = explorer.calculateMonthlyCost(nextMonth);
        System.out.println("Next Month Cost (after upgrade): " + 
                         nextMonthCost.getCurrency() + " " + nextMonthCost.getTotalCost());
    }
}