# boomi-logic-apps-awareness

A table in `mapping.json` that maps this repo's four patterns from Camel to Boomi and Azure Logic Apps, and `check.py` that validates it.

## Goal

Show how SFTP polling, idempotent handling, EDI parsing and channel fallback are expressed in Boomi and Azure Logic Apps, using the Camel implementation in this repo as the reference.

## Run it

```
python3 check.py
```

Expected: `ok: 4 patterns x 3 tools` and exit status 0. The script fails with a message if a pattern is missing or a cell is empty.

## What it proves

- `mapping.json` has an entry for each of `sftp polling`, `idempotent handling`, `edi parsing` and `channel fallback`.
- Each entry names the Camel, Boomi and Logic Apps construct, for example an Integration Account with X12/EDIFACT Decode actions in Logic Apps.
- The Logic Apps SFTP trigger only tracks last-modified, so the mapping adds a key lookup for idempotency.

## Trade-offs

- The check only tests that cells are filled in; it cannot tell whether a construct name is correct.
- The mapping is written from product documentation knowledge and was not tried in a Boomi or Logic Apps environment.
- One line per cell hides connector settings, pricing and runtime limits.

## When not to use it

- As a migration guide; real conversions need a proof of concept on the target platform.
