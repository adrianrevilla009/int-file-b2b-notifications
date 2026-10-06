# sftp-file-integration

A Camel route (`SftpRoutes.java`) that polls an SFTP inbox, plus an integration test (`SftpRoutesIT.java`) that runs it against an `atmoz/sftp` container.

## Goal

Show how to consume files from a partner-owned SFTP inbox that you must not delete from. The route leaves files in place and uses an idempotent consumer so each file is processed once.

## Run it

```
mvn -q test -Dtest=SftpRoutesIT
```

Expected: `SftpRoutesIT.processesEachFileOnce` passes and Maven prints nothing in quiet mode.

Not run end to end here: this session had no approved Docker access, so the test was not executed. The code compiles against pinned versions (Camel 4.8.0, Testcontainers 1.20.4, JUnit 5.11.3), but the pass result above is what the test asserts, not output I observed.

## What it proves

- The route in `SftpRoutes.java` polls `/upload/inbox` every 500 ms with `noop=true`, so files stay on the server.
- The idempotent consumer keys on `${file:name}-${file:size}` using an in-memory repository of 1000 entries.
- The test uploads `order-1.csv` and `order-2.csv`, waits for two messages on `mock:orders`, polls for two more seconds, and asserts the received count is still 2.

## Trade-offs

- `MemoryIdempotentRepository` is lost on restart, so every file in the inbox is processed again after a restart. A shared store (JDBC, Redis) fixes that.
- Name plus size misses a file replaced by different content of the same size.
- The test uses a password and disables host key checking; real partners need key authentication and a known-hosts file.

## When not to use it

- When you own the inbox and can move or delete files after processing; Camel's default `move` handling is simpler.
- When the partner can push events (webhook, queue) instead of files.
