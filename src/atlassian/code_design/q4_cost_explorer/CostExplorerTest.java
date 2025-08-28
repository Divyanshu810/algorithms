package practice.atlassian.code_design.q4_cost_explorer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CostExplorer implementation.
 */
public class CostExplorerTest {

    private Solution.CostExplorer costExplorer;
    private Product jiraProduct;
    private Product confluenceProduct;
    private Subscription jiraSubscription;
    private Subscription confluenceSubscription;
    private LocalDate today;
    private YearMonth currentMonth;

    @BeforeEach
    public void setUp() {
        // Create products and plans
        jiraProduct = Solution.PricingPlanFactory.createJiraProduct();
        confluenceProduct = Solution.PricingPlanFactory.createConfluenceProduct();
        
        // Create cost explorer
        costExplorer = new Solution.CostExplorer("USD");
        
        // Setup test data
        today = LocalDate.now();
        currentMonth = YearMonth.now();
        
        // Create subscriptions
        jiraSubscription = new Subscription(
            "sub-1", "customer-123", jiraProduct, 
            jiraProduct.getAvailablePlans().get(0), // Standard Monthly
            today.minusDays(15)
        );
        jiraSubscription.setQuantity(10); // 10 users
        
        confluenceSubscription = new Subscription(
            "sub-2", "customer-123", confluenceProduct, 
            confluenceProduct.getAvailablePlans().get(0), // Standard Monthly
            today.minusDays(30)
        );
        confluenceSubscription.setQuantity(5); // 5 users
        
        costExplorer.addSubscription(jiraSubscription);
        costExplorer.addSubscription(confluenceSubscription);
    }

    @Test
    @DisplayName("Test monthly cost calculation")
    public void testMonthlyCostCalculation() {
        MonthlyCost monthlyCost = costExplorer.calculateMonthlyCost(currentMonth);
        
        // Verify total cost
        assertNotNull(monthlyCost);
        assertTrue(monthlyCost.getTotalCost().compareTo(BigDecimal.ZERO) > 0);
        assertEquals("USD", monthlyCost.getCurrency());
        assertEquals(currentMonth, monthlyCost.getMonth());
        
        // Verify subscription costs
        assertEquals(2, monthlyCost.getSubscriptionCosts().size());
        
        // Verify cost by product
        assertEquals(2, monthlyCost.getCostByProduct().size());
        assertTrue(monthlyCost.getCostByProduct().containsKey("jira"));
        assertTrue(monthlyCost.getCostByProduct().containsKey("confluence"));
    }

    @Test
    @DisplayName("Test subscription cost for full month")
    public void testSubscriptionCostForFullMonth() {
        // Create a subscription active for the full month
        YearMonth lastMonth = currentMonth.minusMonths(1);
        LocalDate startOfLastMonth = lastMonth.atDay(1);
        
        Subscription fullMonthSubscription = new Subscription(
            "sub-full", "customer-123", jiraProduct,
            jiraProduct.getAvailablePlans().get(0), // Standard Monthly
            startOfLastMonth
        );
        fullMonthSubscription.setQuantity(1);
        
        Solution.CostExplorer explorer = new Solution.CostExplorer("USD");
        explorer.addSubscription(fullMonthSubscription);
        
        MonthlyCost monthlyCost = explorer.calculateMonthlyCost(lastMonth);
        
        // Verify that cost equals the monthly plan price
        PricingPlan plan = jiraProduct.getAvailablePlans().get(0);
        assertEquals(plan.getBasePrice(), monthlyCost.getTotalCost());
    }

    @Test
    @DisplayName("Test subscription cost for partial month")
    public void testSubscriptionCostForPartialMonth() {
        // Create a subscription active for only part of the month
        YearMonth thisMonth = currentMonth;
        LocalDate middleOfMonth = thisMonth.atDay(15);
        
        Subscription partialMonthSubscription = new Subscription(
            "sub-partial", "customer-123", jiraProduct,
            jiraProduct.getAvailablePlans().get(0), // Standard Monthly
            middleOfMonth
        );
        partialMonthSubscription.setQuantity(1);
        
        Solution.CostExplorer explorer = new Solution.CostExplorer("USD");
        explorer.addSubscription(partialMonthSubscription);
        
        MonthlyCost monthlyCost = explorer.calculateMonthlyCost(thisMonth);
        
        // Calculate expected cost (daily rate * remaining days in month)
        PricingPlan plan = jiraProduct.getAvailablePlans().get(0);
        BigDecimal dailyRate = plan.getDailyRate();
        int daysInMonth = thisMonth.lengthOfMonth();
        int remainingDays = daysInMonth - middleOfMonth.getDayOfMonth() + 1;
        BigDecimal expectedCost = dailyRate.multiply(BigDecimal.valueOf(remainingDays))
                                        .setScale(2, RoundingMode.HALF_UP);
        
        // Allow for minor rounding differences
        BigDecimal difference = expectedCost.subtract(monthlyCost.getTotalCost()).abs();
        assertTrue(difference.compareTo(new BigDecimal("0.02")) < 0);
    }

    @Test
    @DisplayName("Test yearly cost report")
    public void testYearlyCostReport() {
        YearlyCostReport yearlyReport = costExplorer.generateYearlyReport(currentMonth.getYear());
        
        // Verify yearly report
        assertNotNull(yearlyReport);
        assertEquals(currentMonth.getYear(), yearlyReport.getYear());
        assertEquals("USD", yearlyReport.getCurrency());
        
        // Should have 12 monthly costs
        assertEquals(12, yearlyReport.getMonthlyCosts().size());
        
        // Total cost should be sum of all monthly costs
        BigDecimal sumOfMonthly = BigDecimal.ZERO;
        for (MonthlyCost monthlyCost : yearlyReport.getMonthlyCosts()) {
            sumOfMonthly = sumOfMonthly.add(monthlyCost.getTotalCost());
        }
        assertEquals(0, yearlyReport.getTotalCost().compareTo(sumOfMonthly));
        
        // Average should be total divided by 12
        BigDecimal expectedAverage = yearlyReport.getTotalCost()
                                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        assertEquals(0, yearlyReport.getAverageMonthlyCost().compareTo(expectedAverage));
    }

    @Test
    @DisplayName("Test plan change affects cost")
    public void testPlanChangeAffectsCost() {
        // Calculate cost before plan change
        YearMonth nextMonth = currentMonth.plusMonths(1);
        MonthlyCost costBeforeChange = costExplorer.calculateMonthlyCost(nextMonth);
        
        // Change plan to premium (higher price)
        jiraSubscription.changePlan(jiraProduct.getAvailablePlans().get(1)); // Premium Monthly
        
        // Need to clear cache to reflect changes
//        costExplorer.clearCache();
        
        // Calculate cost after plan change
        MonthlyCost costAfterChange = costExplorer.calculateMonthlyCost(nextMonth);
        
        // Cost should be higher after upgrading to premium
        assertTrue(costAfterChange.getTotalCost().compareTo(costBeforeChange.getTotalCost()) > 0);
    }

    @Test
    @DisplayName("Test removing subscription affects cost")
    public void testRemovingSubscriptionAffectsCost() {
        // Calculate cost with both subscriptions
        MonthlyCost costBefore = costExplorer.calculateMonthlyCost(currentMonth);
        
        // Remove one subscription
        costExplorer.removeSubscription(confluenceSubscription.getSubscriptionId());
        
        // Calculate cost after removal
        MonthlyCost costAfter = costExplorer.calculateMonthlyCost(currentMonth);
        
        // Cost should be lower after removing subscription
        assertTrue(costAfter.getTotalCost().compareTo(costBefore.getTotalCost()) < 0);
        assertEquals(1, costAfter.getSubscriptionCosts().size());
    }

    @Test
    @DisplayName("Test get active subscriptions")
    public void testGetActiveSubscriptions() {
        // Both subscriptions are active in current month
        List<Subscription> activeSubscriptions = costExplorer.getActiveSubscriptions(currentMonth);
        assertEquals(2, activeSubscriptions.size());
        
        // Set end date for one subscription to make it inactive next month
        YearMonth nextMonth = currentMonth.plusMonths(1);
        confluenceSubscription.setEndDate(currentMonth.atEndOfMonth());
        
        // Only one subscription should be active next month
        activeSubscriptions = costExplorer.getActiveSubscriptions(nextMonth);
        assertEquals(1, activeSubscriptions.size());
        assertEquals(jiraSubscription.getSubscriptionId(), activeSubscriptions.get(0).getSubscriptionId());
    }

    @Test
    @DisplayName("Test get total cost between dates")
    public void testGetTotalCostBetweenDates() {
        LocalDate startDate = currentMonth.minusMonths(2).atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        
        BigDecimal totalCost = costExplorer.getTotalCostBetween(startDate, endDate);
        
        // Calculate expected cost manually
        BigDecimal expectedCost = BigDecimal.ZERO;
        for (YearMonth month = YearMonth.from(startDate); 
             !month.isAfter(YearMonth.from(endDate)); 
             month = month.plusMonths(1)) {
            MonthlyCost monthlyCost = costExplorer.calculateMonthlyCost(month);
            expectedCost = expectedCost.add(monthlyCost.getTotalCost());
        }
        
        assertEquals(0, totalCost.compareTo(expectedCost));
    }
}