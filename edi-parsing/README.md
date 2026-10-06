# edi-parsing

A small `EdiParser.java` that reads X12 850 and EDIFACT ORDERS purchase orders into one `Order` record, with `EdiParserTest.java`.

## Goal

Show that two very different EDI dialects can map to the same internal Orders model, with the dialect chosen from the interchange header.

## Run it

```
mvn -q test
```

Expected: Maven exits with status 0 and prints nothing in quiet mode; `EdiParserTest` runs three tests, all passing.

## What it proves

- `EdiParser.parse` picks X12 when the text starts with `ISA` and EDIFACT when it starts with `UNB` or `UNA`.
- The sample X12 (`BEG03`, `PO1` lines) and the sample EDIFACT (`BGM`, `LIN`/`QTY` pairs) both produce order `O-1001` with WIDGET x 2 and GADGET x 5.
- Unknown text, or a document without an order number and lines, throws `IllegalArgumentException`.

## Trade-offs

- It reads only the few segments needed for an order; envelopes, acknowledgements (997, CONTRL) and validation are not handled.
- Delimiters are fixed (`~` `*` for X12, `'` `+` `:` for EDIFACT); a `UNA` header that redefines them is not honoured.
- Indexes are hard-coded, so a malformed segment can throw an `ArrayIndexOutOfBoundsException` instead of a clear message.

## When not to use it

- For real partner traffic with many transaction sets or strict validation; use an EDI library or the platform's EDI module.
