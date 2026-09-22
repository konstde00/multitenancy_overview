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
stack is one common way to build the pattern. A multi-module Maven project.

| Module | Contents |
|---|---|
| `tenant-management` | routing, provisioning, migration, both data-access strategies |
| `lab` | measurement harnesses |
| `auth` | authentication and tenant resolution |
| `application` | entry point, HTTP layer, wiring |
| `commons` | shared types |

A request enters `TenantsRoutingFilter`, which resolves the tenant key and writes it to
`DataSourceContextHolder`. `DataSourceRoutingService` and `DataSourceConfigService` hold and
resolve the routing table. `ConnectionService` manages per-tenant pools. `LiquibaseService`
applies schema migrations during provisioning.

Provisioning writes tenant connection metadata to a control-plane database. Replica
reconciliation through broker events was added later, in
[`runtime-tenant-onboarding`](https://github.com/konstde00/runtime-tenant-onboarding).

Two data-access strategies are implemented over the same domain objects so they can be
compared: routed repositories, and per-tenant data-access objects. They differ in what each
exposes to a concurrent test, which is why both are retained.

Tenant identifiers carrying a statement terminator or a `DROP DATABASE` are rejected before
any database operation is issued. The tenant role holds neither create-database, nor
create-role, nor superuser, and the `public` role has no connect permission.

A design walkthrough is published at
[Spring Boot multi-tenant architecture overview](https://medium.com/@konstde00/spring-boot-multi-tenant-architecture-overview-88198ea3991f).

## What was measured

The measurements were taken against the current implementation, which lives at
[`runtime-tenant-onboarding`](https://github.com/konstde00/runtime-tenant-onboarding): the
isolation result under concurrent routing-table replacement, convergence across 3 and 10
replicas, provisioning and routed-request latency, and per-tenant connection and heap cost.
Raw results and reproduction instructions are in that repository's `benchmark/`.

This repository is the October 2022 implementation. It routes, provisions and migrates on a
single instance. It has no broker and no replica reconciliation, so the replicated results do
not apply to it.

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

## History

The problem has been the same since 2022: give every tenant its own database, and add a
tenant while the system is running. Each repository below took that further.

| | |
|---|---|
| June 2022 | [`demo-uni`](https://github.com/konstde00/demo-uni), the first decomposition: an application module, an authentication module, one module per faculty |
| October 2022 | this repository. Per-faculty modules replaced by a general tenant-management module, runtime provisioning added |
| February 2023 | [`ty_yak_be`](https://github.com/konstde00/ty_yak_be) carries the same decomposition into a deployed service |
| 2024 to 2026 | [`runtime-tenant-onboarding`](https://github.com/konstde00/runtime-tenant-onboarding), the artefact behind the papers: replica reconciliation, the benchmark harness, Kubernetes deployment, and the measurements |
