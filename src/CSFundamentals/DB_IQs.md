## Database Interview Questions and Sample Answers (SDE-2)

### 1. SQL Querying & Optimization

* **How would you optimize a slow SQL query?**

    * Use `EXPLAIN` to analyze query plan.
    * Add appropriate indexes.
    * Avoid `SELECT *`; fetch only needed columns.
    * Use joins efficiently, avoid nested subqueries where possible.
    * Use LIMIT and pagination for large data sets.

* **Explain JOIN types — INNER, LEFT, RIGHT, FULL OUTER.**

    * INNER JOIN: Only matching records from both tables.
    * LEFT JOIN: All records from left, and matched from right.
    * RIGHT JOIN: All records from right, and matched from left.
    * FULL OUTER JOIN: All records from both tables, matched or not.

* **What is the difference between WHERE and HAVING?**

    * WHERE filters rows before aggregation.
    * HAVING filters rows after aggregation.

* **How do window functions like ROW\_NUMBER() work?**

    * Provide row-wise calculations like rankings without collapsing rows.
    * Example: `ROW_NUMBER() OVER (PARTITION BY dept ORDER BY salary DESC)`

* **Write a query to get the 2nd highest salary per department.**

  ```sql
  SELECT * FROM (
    SELECT *, ROW_NUMBER() OVER (PARTITION BY dept ORDER BY salary DESC) as rn
    FROM employees
  ) t WHERE rn = 2;
  ```

### 2. Indexes & Performance

* **What are indexes and how do they improve performance?**

    * Data structures (e.g., B-trees) that speed up searches by avoiding full table scans.

* **Primary Index vs Secondary Index.**

    * Primary: Based on primary key, unique and clustered.
    * Secondary: On other columns, may or may not be unique.

* **When can indexes hurt performance?**

    * On write-heavy tables (due to overhead).
    * Too many indexes slow down inserts/updates.

* **How does a B-tree index work?**

    * Maintains a balanced tree with sorted keys for O(log n) search, insert, delete.

* **How to decide which columns to index?**

    * Frequently queried columns in WHERE, JOIN, ORDER BY clauses.
    * Columns with high cardinality.

### 3. Database Design

* **What is normalization? Explain 1NF to 3NF.**

    * 1NF: Atomic values.
    * 2NF: No partial dependency.
    * 3NF: No transitive dependency.

* **When would you denormalize a schema?**

    * For performance improvements in read-heavy systems.
    * To reduce join complexity.

* **How would you design a restaurant reservation system?**

    * Tables: Restaurants, Tables, Reservations, Users.
    * Key constraints: capacity, time slot overlap prevention.

* **OLTP vs OLAP databases.**

    * OLTP: Online Transaction Processing – for real-time CRUD.
    * OLAP: Online Analytical Processing – for analytics, aggregation.

* **What is a surrogate key vs a natural key?**

    * Surrogate: Auto-generated (e.g., UUID, ID).
    * Natural: From real-world data (e.g., email, SSN).

### 4. Transactions & Concurrency

* **Explain ACID properties.**

    * Atomicity, Consistency, Isolation, Durability.

* **Different isolation levels: Read Committed vs Serializable.**

    * Read Committed: Prevents dirty reads.
    * Serializable: Highest isolation, avoids all anomalies.

* **What is a deadlock and how do you resolve it?**

    * Cyclic resource wait. Resolved via timeout, retry, or resource ordering.

* **Optimistic vs Pessimistic locking.**

    * Optimistic: Assume no conflict, check at commit.
    * Pessimistic: Lock data during transaction.

* **What are phantom reads?**

    * New rows added by others during your transaction.

### 5. Scalability & High Volume Systems

* **How would you scale a database with large data?**

    * Partitioning, sharding, archiving, read replicas.

* **What is partitioning and when would you use it?**

    * Dividing table into logical parts. Use when data grows large.

* **Horizontal vs vertical scaling.**

    * Horizontal: Add more machines.
    * Vertical: Add more resources to one machine.

* **What are read replicas and how do they help?**

    * Secondary nodes for read-only queries. Reduce load on primary.

* **Handling hotspotting in high-write tables.**

    * Use hash-based partitioning, write buffering, or queue-based ingestion.

### 6. NoSQL & Polyglot Persistence

* **When to use NoSQL over RDBMS?**

    * When schema flexibility, scalability, or unstructured data is needed.

* **Document DB vs Key-Value DB vs Graph DB.**

    * Document: JSON docs (e.g., MongoDB).
    * Key-Value: Fast, simple lookup (e.g., Redis).
    * Graph: Relationships (e.g., Neo4j).

* **What is CAP theorem?**

    * Trade-off between Consistency, Availability, Partition Tolerance.

* **How does MongoDB’s flexible schema work?**

    * Documents can have different fields; validated optionally.

* **Choosing between MongoDB and PostgreSQL.**

    * MongoDB: Fast dev, unstructured data.
    * PostgreSQL: Strong consistency, complex queries.

### 7. Real-World Troubleshooting

* **How to troubleshoot a slow query in production?**

    * Use EXPLAIN, analyze query plan, check indexes, data size, stats.

* **A table is growing too fast, what would you do?**

    * Archive old data, partitioning, review indexes and logging.

* **Handling slow JOINs and optimizing them.**

    * Ensure join columns are indexed. Avoid joining large datasets blindly.

* **Safe ways to apply schema changes in production.**

    * Zero-downtime deployment: add columns, backfill, switch.

* **Monitoring long-running queries.**

    * Use `pg_stat_activity`, Oracle AWR reports, query profiler.

### 8. Data Modeling for Microservices

* **Should microservices share the same DB?**

    * Prefer not to. Each service should own its data.

* **How to maintain data consistency across services?**

    * Use event-driven architecture or sagas for eventual consistency.

* **What is Change Data Capture (CDC)?**

    * Track DB changes in real-time for sync or events.

* **How do you model soft deletes?**

    * Add `is_deleted` flag or `deleted_at` timestamp.

### 9. Data Integrity & Auditing

* **How do you enforce data validation rules in DB?**

    * Use CHECK constraints, foreign keys, triggers.

* **Tracking changes — who changed what and when.**

    * Use audit tables, triggers, or built-in features (PostgreSQL `table_version`).

* **What are CHECK constraints and their usage?**

    * Ensure column values match certain conditions (e.g., age > 0).
