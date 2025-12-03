package Company.atlassian.code_design.q4_cost_explorer.NewSol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class TestSol {


    public class CostExplorerTest {

        @Nested
        @DisplayName("Plan Tests")
        class PlanTests {

            @Test
            @DisplayName("Should calculate monthly and yearly price")
            void testPlanPricing() {
                Plan plan = new Plan("basic", "Basic Plan", 10.0);

                assertEquals(10.0, plan.getMonthlyPrice());
                assertEquals(120.0, plan.getYearlyPrice());
            }
        }

        @Nested
        @DisplayName("Product Tests")
        class ProductTests {

            @Test
            @DisplayName("Should support multiple plans")
            void testMultiplePlans() {
                Sol.Product jira = new Product("jira", "Jira");

                jira.addPlan(new Plan("free", "Free", 0.0));
                jira.addPlan(new Plan("standard", "Standard", 7.75));
                jira.addPlan(new Plan("premium", "Premium", 15.25));

                assertEquals(3, jira.getPlans().size());
                assertEquals(7.75, jira.getPlan("standard").getMonthlyPrice());
            }
        }

        @Nested
        @DisplayName("Subscription Tests")
        class SubscriptionTests {

            private Product product;
            private Plan plan;

            @BeforeEach
            void setUp() {
                product = new Product("jira", "Jira");
                plan = new Plan("standard", "Standard", 10.0);
                product.addPlan(plan);
            }

            @Test
            @DisplayName("Should be active in months after start date")
            void testActiveAfterStart() {
                Subscription sub = new Subscription(product, plan, LocalDate.of(2024, 3, 15));

                assertFalse(sub.isActiveInMonth(2024, 1));
                assertFalse(sub.isActiveInMonth(2024, 2));
                assertTrue(sub.isActiveInMonth(2024, 3));
                assertTrue(sub.isActiveInMonth(2024, 4));
                assertTrue(sub.isActiveInMonth(2024, 12));
            }

            @Test
            @DisplayName("Should not be active after end date")
            void testInactiveAfterEnd() {
                Subscription sub = new Subscription(product, plan,
                        LocalDate.of(2024, 3, 1),
                        LocalDate.of(2024, 6, 30));

                assertFalse(sub.isActiveInMonth(2024, 2));
                assertTrue(sub.isActiveInMonth(2024, 3));
                assertTrue(sub.isActiveInMonth(2024, 6));
                assertFalse(sub.isActiveInMonth(2024, 7));
            }

            @Test
            @DisplayName("Should return correct monthly cost")
            void testMonthlyCost() {
                Subscription sub = new Subscription(product, plan, LocalDate.of(2024, 1, 1));

                assertEquals(10.0, sub.getMonthlyCost());
            }
        }

        @Nested
        @DisplayName("CostExplorer Tests")
        class CostExplorerTests {

            private CostExplorer costExplorer;
            private Customer customer;
            private Product jira;
            private Product confluence;

            @BeforeEach
            void setUp() {
                costExplorer = new CostExplorerImpl();
                customer = new Customer("C001", "Acme Corp");

                // Setup Jira with multiple plans
                jira = new Product("jira", "Jira");
                jira.addPlan(new Plan("free", "Free", 0.0));
                jira.addPlan(new Plan("standard", "Standard", 7.75));
                jira.addPlan(new Plan("premium", "Premium", 15.25));

                // Setup Confluence
                confluence = new Product("confluence", "Confluence");
                confluence.addPlan(new Plan("standard", "Standard", 5.50));
                confluence.addPlan(new Plan("premium", "Premium", 10.50));
            }

            @Test
            @DisplayName("Should calculate monthly cost for single subscription")
            void testSingleSubscriptionMonthlyCost() {
                customer.addSubscription(new Subscription(
                        jira,
                        jira.getPlan("standard"),
                        LocalDate.of(2024, 1, 1)
                ));

                MonthlyBill bill = costExplorer.getMonthlyCost(customer, 2024, 6);

                assertEquals(7.75, bill.getTotalAmount());
                assertEquals(1, bill.getLineItems().size());
            }

            @Test
            @DisplayName("Should calculate monthly cost for multiple subscriptions")
            void testMultipleSubscriptionsMonthlyCost() {
                customer.addSubscription(new Subscription(
                        jira,
                        jira.getPlan("premium"),
                        LocalDate.of(2024, 1, 1)
                ));
                customer.addSubscription(new Subscription(
                        confluence,
                        confluence.getPlan("standard"),
                        LocalDate.of(2024, 1, 1)
                ));

                MonthlyBill bill = costExplorer.getMonthlyCost(customer, 2024, 6);

                assertEquals(15.25 + 5.50, bill.getTotalAmount());
                assertEquals(2, bill.getLineItems().size());
            }

            @Test
            @DisplayName("Should calculate yearly cost")
            void testYearlyCost() {
                customer.addSubscription(new Subscription(
                        jira,
                        jira.getPlan("standard"),
                        LocalDate.of(2024, 1, 1)
                ));

                double yearlyCost = costExplorer.getYearlyCost(customer, 2024);

                assertEquals(7.75 * 12, yearlyCost);
            }

            @Test
            @DisplayName("Should generate full year report")
            void testGenerateReport() {
                customer.addSubscription(new Subscription(
                        jira,
                        jira.getPlan("standard"),
                        LocalDate.of(2024, 1, 1)
                ));

                CostReport report = costExplorer.generateReport(customer, 2024);

                assertEquals(12, report.getMonthlyBills().size());
                assertEquals(7.75 * 12, report.getYearlyTotal());
            }

            @Test
            @DisplayName("Should handle subscription starting mid-year")
            void testMidYearSubscription() {
                customer.addSubscription(new Subscription(
                        jira,
                        jira.getPlan("standard"),
                        LocalDate.of(2024, 6, 15)  // Starts in June
                ));

                CostReport report = costExplorer.generateReport(customer, 2024);

                // Jan-May should be 0, Jun-Dec should be 7.75 each
                assertEquals(0.0, report.getBillForMonth(1).getTotalAmount());
                assertEquals(0.0, report.getBillForMonth(5).getTotalAmount());
                assertEquals(7.75, report.getBillForMonth(6).getTotalAmount());
                assertEquals(7.75, report.getBillForMonth(12).getTotalAmount());

                // 7 months * 7.75 = 54.25
                assertEquals(7.75 * 7, report.getYearlyTotal());
            }

            @Test
            @DisplayName("Should handle subscription ending mid-year")
            void testEndingSubscription() {
                customer.addSubscription(new Subscription(
                        jira,
                        jira.getPlan("standard"),
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 6, 30)  // Ends in June
                ));

                CostReport report = costExplorer.generateReport(customer, 2024);

                assertEquals(7.75, report.getBillForMonth(1).getTotalAmount());
                assertEquals(7.75, report.getBillForMonth(6).getTotalAmount());
                assertEquals(0.0, report.getBillForMonth(7).getTotalAmount());
                assertEquals(0.0, report.getBillForMonth(12).getTotalAmount());

                // 6 months * 7.75 = 46.50
                assertEquals(7.75 * 6, report.getYearlyTotal());
            }

            @Test
            @DisplayName("Should handle free plan")
            void testFreePlan() {
                customer.addSubscription(new Subscription(
                        jira,
                        jira.getPlan("free"),
                        LocalDate.of(2024, 1, 1)
                ));

                double yearlyCost = costExplorer.getYearlyCost(customer, 2024);

                assertEquals(0.0, yearlyCost);
            }

            @Test
            @DisplayName("Should handle customer with no subscriptions")
            void testNoSubscriptions() {
                CostReport report = costExplorer.generateReport(customer, 2024);

                assertEquals(0.0, report.getYearlyTotal());
                assertEquals(12, report.getMonthlyBills().size());
            }
        }

        @Nested
        @DisplayName("MonthlyBill Tests")
        class MonthlyBillTests {

            @Test
            @DisplayName("Should calculate total from line items")
            void testTotalCalculation() {
                MonthlyBill bill = new MonthlyBill(2024, 6);
                bill.addLineItem(new BillLineItem("Jira", "Standard", 7.75));
                bill.addLineItem(new BillLineItem("Confluence", "Premium", 10.50));
                bill.addLineItem(new BillLineItem("Bitbucket", "Standard", 3.00));

                assertEquals(21.25, bill.getTotalAmount());
            }

            @Test
            @DisplayName("Should return correct month name")
            void testMonthName() {
                MonthlyBill bill = new MonthlyBill(2024, 6);

                assertEquals("JUNE", bill.getMonthName());
            }
        }

        @Nested
        @DisplayName("CostReport Tests")
        class CostReportTests {

            @Test
            @DisplayName("Should get bill for specific month")
            void testGetBillForMonth() {
                CostReport report = new CostReport(2024);

                MonthlyBill juneBill = new MonthlyBill(2024, 6);
                juneBill.addLineItem(new BillLineItem("Jira", "Standard", 7.75));
                report.addMonthlyBill(juneBill);

                MonthlyBill julyBill = new MonthlyBill(2024, 7);
                julyBill.addLineItem(new BillLineItem("Jira", "Standard", 7.75));
                report.addMonthlyBill(julyBill);

                assertEquals(juneBill, report.getBillForMonth(6));
                assertEquals(julyBill, report.getBillForMonth(7));
                assertNull(report.getBillForMonth(1));
            }
        }
    }
```

        ---

        ## Project Structure
```
    src/
            ├── main/java/
            │   ├── Plan.java
│   ├── Product.java
│   ├── Subscription.java
│   ├── Customer.java
│   ├── BillLineItem.java
│   ├── MonthlyBill.java
│   ├── CostReport.java
│   ├── CostExplorer.java
│   └── CostExplorerImpl.java
└── test/java/
            └── CostExplorerTest.java
}
