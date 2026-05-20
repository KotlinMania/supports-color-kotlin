# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 1/1 (100.0%)
- **Function parity:** 16/17 matched (target 63) — 94.1%
- **Class/type parity:** 2/2 matched (target 3) — 100.0%
- **Combined symbol parity:** 18/19 matched (target 66) — 94.7%
- **Average inline-code cosine:** 0.73 (function body across 1 matched files)
- **Average documentation cosine:** 0.00 (doc text across 1 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 0 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `supportscolor.OnTest`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 11902.7
- **Functions:** 16/17 matched (target 63)
- **Missing functions:** `set_up`
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Tests:** 4/5 matched

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../tmp/supports-color/src rust ../../src kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
