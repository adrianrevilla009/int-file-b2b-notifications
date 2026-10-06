#!/usr/bin/env python3
"""Verify the mapping covers every pattern implemented in this repo and has no empty cells."""
import json, pathlib, sys

REQUIRED = {"sftp polling", "idempotent handling", "edi parsing", "channel fallback"}
TOOLS = {"camel", "boomi", "logic_apps"}

m = json.loads((pathlib.Path(__file__).parent / "mapping.json").read_text())
errors = [f"missing pattern: {p}" for p in sorted(REQUIRED - m.keys())]
for pattern, row in m.items():
    errors += [f"{pattern}: missing or empty '{t}'" for t in sorted(TOOLS) if not row.get(t, "").strip()]
print("\n".join(errors) if errors else f"ok: {len(m)} patterns x {len(TOOLS)} tools")
sys.exit(1 if errors else 0)
