# Supply Chain Management System — Deployment Guide

This guide covers a full deployment of the Supply Chain Management System from a clean environment, including the performance-monitoring setup used for testing. It assumes the source package layout `Supply_Chain_Management_System`, a Maven reactor of four modules — `supply_chain_core` (JAR), `supply_chain_ejb` (EJB JAR), `supply_chain_web` (WAR), `supply_chain_ear` (EAR) — plus the static `index.jsp` test console bundled inside the WAR.

## Prerequisites

- Java 17
- Apache Maven
- WildFly (Jakarta EE 11-compliant; WildFly 27+ recommended)
- PostgreSQL, running and reachable
- Apache JMeter and VisualVM, for the performance monitoring steps at the end of this guide

## Step 1. Install the PostgreSQL JDBC Driver as a WildFly Module and Create the Datasource

Use driver version 42.7.9 or later — older drivers fail SCRAM-SHA-256 authentication against a modern PostgreSQL server. Connect to the CLI first.

```
jboss-cli.bat --connect
```

Add the PostgreSQL JDBC driver.

```
/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql, driver-module-name=org.postgresql, driver-class-name=org.postgresql.Driver, driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)
```

Create the datasource. The JNDI name below must match `<jta-data-source>` in `persistence.xml` exactly (`java:/jdbc/Supply_ChainDS`).

```
data-source add --name=Supply_ChainDS --jndi-name=java:/jdbc/Supply_ChainDS --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5432/supply_chain_db --user-name=postgres --password=<your_db_password> --jta=true --use-ccm=true --min-pool-size=5 --max-pool-size=20 --statistics-enabled=true --enabled=true
```

Verify the datasource configuration.

```
/subsystem=datasources/data-source=Supply_ChainDS:test-connection-in-pool
```

List all data sources.

```
/subsystem=datasources:read-children-names(child-type=data-source)
```

## Step 2. Configure the Security Domain

`supply_chain_web/src/main/webapp/WEB-INF/jboss-web.xml` binds the WAR to a security domain named `ApplicationDomain`. Application-level authorization in this project is actually enforced in portable code (`AuthFilter`, a `ContainerRequestFilter` that reads `@RolesAllowed` and validates the JWT directly.), so `ApplicationDomain` does not need a fully populated Elytron identity store for the API to function correctly. It is still declared because `jboss-web.xml` requires a bound security domain to be present; the default `ApplicationDomain` shipped with WildFly is sufficient. No further Elytron configuration is required for a standard deployment.

## Step 3. Set the JWT Signing Secret

`JWTUtil` reads its signing secret from MicroProfile Config (`jwt.secret=${JWT_SECRET}` in `microprofile-config.properties`), so the secret must be provided as an environment variable before starting WildFly — it is never hardcoded in source or shipped inside the EAR.

```
set JWT_SECRET=<a long, random, secret string>
standalone.bat
```

On Linux/macOS:

```
export JWT_SECRET=<a long, random, secret string>
./standalone.sh
```

If `JWT_SECRET` is not set, `JWTUtil` throws `IllegalStateException` on class initialization and login/authenticated requests will fail — check this first if the server starts but every authenticated call errors out.

## Step 4. Build and Deploy the EAR

```
cd Supply_Chain_Management_System
mvn clean package
copy supply_chain_ear\target\supply_chain_ear.ear <WILDFLY_HOME>\standalone\deployments\
```

Watch the server log for a successful deployment message and confirm the datasource binds without error, and that `[AuthDiscovery] total mechanisms found: 1` is logged (confirming `JWTAuthMechanism` was picked up via the `META-INF/services` entry) before moving on.

## Step 5. Verify the Deployment End to End

The WAR ships a bundled test console (`index.jsp`) at the context root, so the fastest verification path does not require a separate frontend:

```
http://localhost:8080/supply_chain_web/
```

Or verify directly with `curl`:

```
curl -X POST http://localhost:8080/supply_chain_web/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin1\",\"password\":\"secret123\",\"roles\":[\"ADMIN\"]}"

curl -X POST http://localhost:8080/supply_chain_web/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin1\",\"password\":\"secret123\"}"
```

Copy the returned `accessToken`, then confirm a role-protected endpoint accepts it:

```
curl http://localhost:8080/supply_chain_web/api/vendor ^
  -H "Authorization: Bearer <accessToken>"
```

And confirm the low-stock timer is running by watching the server log for `InventoryMonitorBean` output roughly every five minutes, or by triggering it early via the WildFly admin console's Timer Service management panel.

## Step 6. CORS Configuration (If Adding a Separate Frontend)

`CORSFilter` currently allows only `http://localhost:3000`. If a separate frontend is introduced later, update the allowed origin in `CORSFilter.java` (or externalize it via MicroProfile Config) before deploying — a mismatched origin will silently fail every browser-based request with a CORS error that does not appear in the server log.

## Performance Monitoring Setup

**VisualVM.** Launch VisualVM on the same machine as WildFly and attach directly to the running `standalone` process under Local Applications. For remote monitoring, enable the JMX remote connector in `standalone.xml`/`standalone-full.xml` and connect VisualVM to that host and port instead. Install the JMX MBeans plugin to browse WildFly's own datasource and thread pool statistics alongside heap and GC activity.

**Datasource pool statistics.** Because `--statistics-enabled=true` was set on the datasource in Step 1, live pool figures are available directly through the CLI, which is more reliable than reading pool size out of the Hibernate startup log.

```
/subsystem=datasources/data-source=Supply_ChainDS/statistics=pool:read-resource(include-runtime=true)
```

**Apache JMeter.** Build a test plan covering the three request-heavy endpoints — `POST /api/order/{vendorEmail}` (order placement, the most write-intensive path), `GET /api/inventory` (read path), and `POST /api/auth/login` (authentication path) — then run it in headless mode for a repeatable, scriptable measurement.

```
jmeter -n -t Supply_Chain_Test_Plan.jmx -l results.jtl -e -o report/
```

**Timer Service.** WildFly's admin console (`Runtime → EJB Timer Service` under the deployed EAR) shows the next scheduled fire time for `InventoryMonitorBean`, which is the most direct way to confirm the `@Schedule(hour = "*", minute = "*/5", persistent = false)` timer is actually registered and firing on the expected cadence, without waiting for a log line.

## Optimization Notes for Production

- Set `hibernate.show_sql` to `false` in `persistence.xml` before measuring or deploying — it adds per-statement logging overhead that skews response time results.
- Apply the index statements in `DATABASE_SCHEMA.md`; Hibernate's automatic schema generation does not create them.
- Re-tune `min-pool-size` and `max-pool-size` against the actual expected concurrent user count rather than the values used for local JMeter testing.
- The low-stock timer runs with `persistent = false` (see the Technical Implementation Documentation for the reasoning); if a future requirement needs the check to survive a server restart without fail, switch to `persistent = true` and configure a persistent timer store first.
- Cross-service or asynchronous processing (e.g. decoupling order confirmation from downstream notification) currently runs through synchronous CDI events (`Event<T>.fire()`), which execute on the same thread and transaction as the firer. If that coupling becomes a bottleneck, evaluate `Event<T>.fireAsync()` (CDI 2.0+) or introducing a JMS queue via WildFly's messaging-activemq subsystem as a documented next step, rather than as an implemented feature of this prototype.
- If horizontal scaling is introduced later, the JWT-based, stateless authentication in this project (no server-side session state, no `@Stateful` beans) means no sticky-session configuration is required at the load balancer — this was a deliberate benefit of the token-based approach over a container-managed session, and is worth naming explicitly in any scaling discussion.
