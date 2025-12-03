package Company.atlassian.code_design.q4_cost_explorer.NewSol;

import java.time.Month;
import java.util.*;

import java.time.LocalDate;
import java.time.YearMonth;

/*
┌─────────────────────────────────────────────────────────────────┐
│                       Plan                                       │
├─────────────────────────────────────────────────────────────────┤
│ - id: String                                                    │
│ - name: String                                                  │
│ - monthlyPrice: double                                          │
├─────────────────────────────────────────────────────────────────┤
│ + getMonthlyPrice(): double                                     │
│ + getYearlyPrice(): double                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      Product                                     │
├─────────────────────────────────────────────────────────────────┤
│ - id: String                                                    │
│ - name: String                                                  │
│ - plans: Map<String, Plan>                                      │
├─────────────────────────────────────────────────────────────────┤
│ + addPlan(plan: Plan): void                                     │
│ + getPlan(planId: String): Plan                                 │
│ + getPlans(): List<Plan>                                        │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    Subscription                                  │
├─────────────────────────────────────────────────────────────────┤
│ - product: Product                                              │
│ - plan: Plan                                                    │
│ - startDate: LocalDate                                          │
│ - endDate: LocalDate                                            │
├─────────────────────────────────────────────────────────────────┤
│ + isActiveInMonth(year, month): boolean                         │
│ + getMonthlyCost(): double                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      Customer                                    │
├─────────────────────────────────────────────────────────────────┤
│ - id: String                                                    │
│ - name: String                                                  │
│ - subscriptions: List<Subscription>                             │
├─────────────────────────────────────────────────────────────────┤
│ + addSubscription(subscription): void                           │
│ + getSubscriptions(): List<Subscription>                        │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    MonthlyBill                                   │
├─────────────────────────────────────────────────────────────────┤
│ - year: int                                                     │
│ - month: int                                                    │
│ - lineItems: List<BillLineItem>                                 │
│ - totalAmount: double                                           │
├─────────────────────────────────────────────────────────────────┤
│ + addLineItem(item: BillLineItem): void                         │
│ + getTotalAmount(): double                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                   BillLineItem                                   │
├─────────────────────────────────────────────────────────────────┤
│ - productName: String                                           │
│ - planName: String                                              │
│ - amount: double                                                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    CostReport                                    │
├─────────────────────────────────────────────────────────────────┤
│ - year: int                                                     │
│ - monthlyBills: List<MonthlyBill>                               │
│ - yearlyTotal: double                                           │
├─────────────────────────────────────────────────────────────────┤
│ + getMonthlyBills(): List<MonthlyBill>                          │
│ + getYearlyTotal(): double                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                <<interface>>                                     │
│                  CostExplorer                                    │
├─────────────────────────────────────────────────────────────────┤
│ + generateReport(customer, year): CostReport                    │
│ + getMonthlyCost(customer, year, month): MonthlyBill            │
│ + getYearlyCost(customer, year): double                         │
└─────────────────────────────────────────────────────────────────┘
                        ▲
                        │ implements
┌─────────────────────────────────────────────────────────────────┐
│                CostExplorerImpl                                  │
├─────────────────────────────────────────────────────────────────┤
│ + generateReport(customer, year): CostReport                    │
│ + getMonthlyCost(customer, year, month): MonthlyBill            │
│ + getYearlyCost(customer, year): double                         │
└─────────────────────────────────────────────────────────────────┘
 */

public class Sol {

    public class Plan {
        private final String id;
        private final String name;
        private final double monthlyPrice;

        public Plan(String id, String name, double monthlyPrice) {
            this.id = id;
            this.name = name;
            this.monthlyPrice = monthlyPrice;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getMonthlyPrice() {
            return monthlyPrice;
        }

        public double getYearlyPrice() {
            return monthlyPrice * 12;
        }
    }

    public class Product {
        private final String id;
        private final String name;
        private final Map<String, Plan> plans;

        public Product(String id, String name) {
            this.id = id;
            this.name = name;
            this.plans = new HashMap<>();
        }

        public void addPlan(Plan plan) {
            plans.put(plan.getId(), plan);
        }

        public Plan getPlan(String planId) {
            return plans.get(planId);
        }

        public List<Plan> getPlans() {
            return new ArrayList<>(plans.values());
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Subscription {
        private final Product product;
        private final Plan plan;
        private final LocalDate startDate;
        private LocalDate endDate;  // null means ongoing

        public Subscription(Product product, Plan plan, LocalDate startDate) {
            this.product = product;
            this.plan = plan;
            this.startDate = startDate;
            this.endDate = null;
        }

        public Subscription(Product product, Plan plan, LocalDate startDate, LocalDate endDate) {
            this.product = product;
            this.plan = plan;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public boolean isActiveInMonth(int year, int month) {
            YearMonth targetMonth = YearMonth.of(year, month);
            YearMonth startMonth = YearMonth.from(startDate);

            if (targetMonth.isBefore(startMonth)) {
                return false;
            }

            if (endDate != null) {
                YearMonth endMonth = YearMonth.from(endDate);
                if (targetMonth.isAfter(endMonth)) {
                    return false;
                }
            }

            return true;
        }

        public double getMonthlyCost() {
            return plan.getMonthlyPrice();
        }

        public Product getProduct() {
            return product;
        }

        public Plan getPlan() {
            return plan;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }
    }

    public class Customer {
        private final String id;
        private final String name;
        private final List<Subscription> subscriptions;

        public Customer(String id, String name) {
            this.id = id;
            this.name = name;
            this.subscriptions = new ArrayList<>();
        }

        public void addSubscription(Subscription subscription) {
            subscriptions.add(subscription);
        }

        public List<Subscription> getSubscriptions() {
            return Collections.unmodifiableList(subscriptions);
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class BillLineItem {
        private final String productName;
        private final String planName;
        private final double amount;

        public BillLineItem(String productName, String planName, double amount) {
            this.productName = productName;
            this.planName = planName;
            this.amount = amount;
        }

        public String getProductName() {
            return productName;
        }

        public String getPlanName() {
            return planName;
        }

        public double getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return productName + " (" + planName + "): $" + amount;
        }
    }

    public class MonthlyBill {
        private final int year;
        private final int month;
        private final List<BillLineItem> lineItems;

        public MonthlyBill(int year, int month) {
            this.year = year;
            this.month = month;
            this.lineItems = new ArrayList<>();
        }

        public void addLineItem(BillLineItem item) {
            lineItems.add(item);
        }

        public double getTotalAmount() {
            return lineItems.stream()
                    .mapToDouble(BillLineItem::getAmount)
                    .sum();
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public String getMonthName() {
            return Month.of(month).name();
        }

        public List<BillLineItem> getLineItems() {
            return Collections.unmodifiableList(lineItems);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getMonthName()).append(" ").append(year).append(":\n");
            for (BillLineItem item : lineItems) {
                sb.append("  - ").append(item).append("\n");
            }
            sb.append("  Total: $").append(getTotalAmount());
            return sb.toString();
        }
    }

    public interface CostExplorer {
//        CostReport generateReport(Customer customer, int year);
        MonthlyBill getMonthlyCost(Customer customer, int year, int month);
        double getYearlyCost(Customer customer, int year);
    }

    public class CostExplorerImpl implements CostExplorer {

//        @Override
//        public CostReport generateReport(Customer customer, int year) {
//            CostReport report = new CostReport(year);
//
//            // Generate bill for each month (1 to 12)
//            for (int month = 1; month <= 12; month++) {
//                MonthlyBill bill = getMonthlyCost(customer, year, month);
//                report.addMonthlyBill(bill);
//            }
//
//            return report;
//        }

        @Override
        public MonthlyBill getMonthlyCost(Customer customer, int year, int month) {
            MonthlyBill bill = new MonthlyBill(year, month);

            for (Subscription subscription : customer.getSubscriptions()) {
                if (subscription.isActiveInMonth(year, month)) {
                    BillLineItem item = new BillLineItem(
                            subscription.getProduct().getName(),
                            subscription.getPlan().getName(),
                            subscription.getMonthlyCost()
                    );
                    bill.addLineItem(item);
                }
            }

            return bill;
        }

        @Override
        public double getYearlyCost(Customer customer, int year) {
            double total = 0;

            for (int month = 1; month <= 12; month++) {
                MonthlyBill bill = getMonthlyCost(customer, year, month);
                total += bill.getTotalAmount();
            }

            return total;
        }
    }


}
