# Tenant isolation under dynamic routing

A system serving many tenants from one process holds a routing table: a map from a tenant key
to that tenant's backing resources. If tenants can be added while the system runs, that table
is shared mutable state, written by provisioning and read concurrently by every request in
flight.

How the table is updated decides whether isolation holds. Rebuilding the map in place exposes a
partially written structure to concurrent readers. A reader that misses its entry does not
raise: it takes the fallback path and returns a well-formed response assembled from another
tenant's data. Replacing the map atomically, with the fallback path removed, closes the
window.

Two consequences follow. The failure is silent, so it does not appear in logs or error rates.
And it cannot be reproduced by a test that exercises one tenant at a time, because it requires
a reader and a writer to interleave. Tenant isolation under dynamic routing is a concurrency
property, and only a concurrent test can observe it.

This repository implements both, in-place rebuild and atomic replacement, and measures the difference.

## What was implemented

The measured instance is a Spring Boot application over PostgreSQL with a message broker for
replica reconciliation. The result concerns how the table is updated rather than this stack; the
stack is one common way to build the pattern. A multi-module Maven project, 64 classes across
five modules.

| Module | Classes | Contents |
|---|---|---|
| `tenant-management` | 28 | routing, provisioning, migration, both data-access strategies |
| `lab` | 11 | measurement harnesses |
| `auth` | 10 | authentication and tenant resolution |
| `application` | 8 | entry point, HTTP layer, wiring |
| `commons` | 7 | shared types |

A request enters `TenantsRoutingFilter`, which resolves the tenant key and writes it to
`DataSourceContextHolder`. `DataSourceRoutingService` and `DataSourceConfigService` hold and
resolve the routing table. `ConnectionService` manages per-tenant pools. `LiquibaseService`
applies schema migrations during provisioning.

Provisioning is idempotent and writes tenant connection metadata to a control-plane database
with explicit tenant states. On completion it publishes an event; every replica consumes it
and reconciles its routing table against the control-plane database rather than applying the
event payload directly, so a replica that missed an event recovers on the next one. Replicas
starting later bootstrap from the same database.

Two data-access strategies are implemented over the same domain objects so they can be
compared: routed repositories, and per-tenant data-access objects. They differ in what each
exposes to a concurrent test, which is why both are retained.

Tenant identifiers carrying a statement terminator or a `DROP DATABASE` are rejected before
any database operation is issued. The tenant role holds neither create-database, nor
create-role, nor superuser, and the `public` role has no connect permission.

A design walkthrough is published at
[Spring Boot multi-tenant architecture overview](https://medium.com/@konstde00/spring-boot-multi-tenant-architecture-overview-88198ea3991f).

## What was measured

### Isolation under contention

Sixteen threads resolved connections for two tenants while a seventeenth thread replaced the
routing table continuously. Each configuration ran 4,000 requests. The quantity counted is
requests served from the control-plane database rather than from the requesting tenant's own
database.

| Routing table update | Wrong-database responses |
|---|---|
| Rebuilt in place | 202 of 4,000 (5.1%) |
| Replaced atomically | 0 of 4,000 |

No exception was thrown and nothing was written to the log in either run. Every one of the 202
responses was well formed.

### Convergence across replicas

Convergence is measured as the time from the provisioning response returning to the tenant
being served correctly by every replica, which is the interval during which replicas disagree.

| Replicas | Tenants at start | Median | 95th percentile | Maximum | Converged |
|---|---|---|---|---|---|
| 3 | 0 | 419 ms | 745 ms | 1,168 ms | 20 of 20 |
| 10 | 0 | 595 ms | 2,653 ms | 3,055 ms | 20 of 20 |
| 3 | 0, over 100 provisionings | 313 ms | 438 ms | 1,014 ms | 100 of 100 |
| 10 | 100 | 4,897 ms | 7,584 ms | 7,675 ms | 20 of 20 |

Every replica converged in all 160 provisionings.

Raising the replica count from 3 to 10 at a fixed tenant count costs little, 419 ms to 595 ms
at the median. Raising the tenant count from 20 to 120 at 10 replicas costs a factor of eight.
That cost is structural rather than incidental: each replica reconciles by re-reading every
tenant in the CREATED state, so the work per event is proportional to tenant count and is
performed on every replica. The same re-read is what makes the refresh idempotent under
duplicate and reordered delivery, so this measurement prices a property the design relies on.
Applying the event payload incrementally and reconciling fully only on a timer would flatten
the curve and would have to argue convergence on different grounds.

Provisioning latency, measured separately, did not grow with the number of tenants already
present. A routed request cost a median of 3.90 ms.

Integration tests run against real infrastructure rather than mocks: PostgreSQL and the
message broker are started by Testcontainers and their addresses injected into the application
context, so the measurements include real connection establishment, real migrations and real
event delivery.

### Resource cost

Per-tenant isolation is commonly rejected on the grounds that connections and memory grow
linearly with tenant count. The connection half of that claim is a property of one
connection-pool parameter.

When the minimum idle pool size exceeds zero, which is the default unless configured
otherwise, provisioned tenants each hold connections and the ceiling is linear in tenant
count. When it is zero, the tenant count cancels out of the expression and the ceiling is set
by concurrently active tenants alone: an idle tenant holds no connection, because pools
release connections when idle.

Live heap grew by roughly 10 KiB per additional idle tenant beyond the first 25. Neither cost
grew linearly with tenant count in this deployment.

## The same fault in unrelated infrastructure

The general fault is addressing shared derived state by a mutable identifier instead of by
something derived from the state itself. A companion study of large-language-model serving
finds the same shape in a different stack: a prefix cache keyed by an adapter's
client-supplied name, so two adapters that share a name and differ in weights collide, and the
blocks served to the second tenant are those computed for the first.

## Reproducing

```bash
docker compose up -d postgres
./mvnw spring-boot:run -pl application
```

Request examples are in `test.http`.

## Earlier work in this repository set

The module decomposition follows [`demo-uni`](https://github.com/konstde00/demo-uni)
(June 2022), which separated an application module, an authentication module and one module
per faculty. This repository replaces the per-faculty modules with a general
tenant-management module and adds runtime provisioning. `ty_yak_be` (February 2023) carries
the same decomposition into a deployed service.