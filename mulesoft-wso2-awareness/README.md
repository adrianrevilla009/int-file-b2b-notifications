# mulesoft-wso2-awareness

A table in `mapping.json` that maps this repo's four patterns from Camel to MuleSoft and WSO2, and `check.py` that validates it.

## Goal

Show how SFTP polling, idempotent handling, EDI parsing and channel fallback are expressed in MuleSoft and WSO2 Micro Integrator, using the Camel implementation in this repo as the reference.

## Run it

```
python3 check.py
```

Expected: `ok: 4 patterns x 3 tools` and exit status 0. The script fails with a message if a pattern is missing or a cell is empty.

## What it proves

- `mapping.json` has an entry for each of `sftp polling`, `idempotent handling`, `edi parsing` and `channel fallback`.
- Each entry names the Camel, MuleSoft and WSO2 construct, for example an Idempotent Message Validator with an Object Store in MuleSoft.
- WSO2 has no built-in idempotent EIP, and the mapping says so.

## Trade-offs

- The check only tests that cells are filled in; it cannot tell whether a construct name is correct.
- The mapping is written from product documentation knowledge and was not tried in a MuleSoft or WSO2 runtime.
- One line per cell hides configuration details such as watermarks and failover settings.

## When not to use it

- As a migration guide; real conversions need a proof of concept on the target platform.
