# notification-channels-email-sms-push

A `NotificationService.java` that tries delivery channels in priority order, with in-memory stub channels and `NotificationServiceTest.java`.

## Goal

Show channel fallback: send through the first channel that works and fall through to the next one when a channel throws.

## Run it

```
mvn -q test
```

Expected: Maven exits with status 0 and prints nothing in quiet mode; `NotificationServiceTest` runs three tests, all passing.

## What it proves

- With email and SMS both up, only email receives the message and `failed()` is empty.
- With email and SMS down and push up, the result is channel `push` with `failed()` equal to `[email, sms]`.
- When every channel fails, `Result.delivered()` is false and all channel names are listed as failed.

## Trade-offs

- Channels are stubs (`StubChannel`); no real email, SMS or push provider is called.
- Fallback is sequential with no timeout, retry or backoff, so a slow channel delays the next one.
- Exceptions are only recorded by channel name; the cause is dropped.

## When not to use it

- When a notification must go out on several channels at once (for example a security alert); this stops at the first success.
- When you need delivery tracking, retries or rate limits; use a queue or a notification platform.
