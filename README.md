# int-file-b2b-notifications

File-based B2B integration in miniature: poll a partner SFTP inbox exactly once per file, parse X12 and EDIFACT purchase orders into one Orders model, and send notifications with channel fallback.

## What is inside

| Folder | What it shows | Run |
| --- | --- | --- |
| [`sftp-file-integration`](./sftp-file-integration) | Camel SFTP consumer with an idempotent consumer, tested against an SFTP container | `mvn -q test -Dtest=SftpRoutesIT` (needs Docker) |
| [`edi-parsing`](./edi-parsing) | X12 850 and EDIFACT ORDERS parsed into the same Order record | `mvn -q test` |
| [`notification-channels-email-sms-push`](./notification-channels-email-sms-push) | Ordered channel fallback over email, SMS and push stubs | `mvn -q test` |
| [`mulesoft-wso2-awareness`](./mulesoft-wso2-awareness) | The same four patterns mapped to MuleSoft and WSO2, with a completeness check | `python3 check.py` |
| [`boomi-logic-apps-awareness`](./boomi-logic-apps-awareness) | The same four patterns mapped to Boomi and Azure Logic Apps, with a completeness check | `python3 check.py` |

The examples share a tiny Orders domain (order id, SKU, quantity).

## Prerequisites

- Java 21 and Maven 3.9+ for the three Java folders
- Docker, only for `sftp-file-integration` (Testcontainers pulls `atmoz/sftp:alpine-3.7`)
- Python 3 for the two mapping folders

## How to read it

Start with `edi-parsing` (pure code, no infrastructure), then `sftp-file-integration` for the polling side, then `notification-channels-email-sms-push`. The two awareness folders show how the same patterns look in other integration platforms.
