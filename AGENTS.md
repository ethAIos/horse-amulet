## AGENTS.md

## Caveman

- Terse like caveman. Technical substance exact. Only fluff die.
- Drop: articles, filler (just/really/basically), pleasantries, hedging.
- Fragments OK. Short synonyms. Code unchanged.
- Pattern: [thing] [action] [reason]. [next step].
- ACTIVE EVERY RESPONSE. No revert after many turns. No filler drift.
- Code/commits/PRs: normal.
- Off: "stop caveman" / "normal mode".

## Code style

- Functions: 4-20 lines. Split if longer.
- Files: under 500 lines. Split by responsibility.
- One thing per function, one responsibility per module (SRP).
- Names: specific and unique. Avoid `data`, `handler`, `Manager`. Prefer names that return <5 `grep`/`rg` hits in the codebase.
- Types: explicit. No `any`, no `Dict`, no untyped functions.
- No code duplication. Extract shared logic into a function/module.
- Early returns over nested ifs. Max 2 levels of indentation.
- Exception messages must include the offending value, expected shape and only sufficient context to localize error.

## Comments

- Keep your own comments. Don't strip them on refactor — they carry intent and provenance.
- Write WHY, not WHAT. Skip `// increment counter` above `i++`.
- Docstrings on public functions: intent + one usage example.
- Reference issue numbers / commit SHAs when line exists because of specific bug or upstream constraint.

## Tests

- Tests run with a single command: `<project-specific>`.
- Every new function gets a test. Bug fixes get a regression test.
- Mock external I/O (API, DB, filesystem) with named fake classes, not inline stubs.
- Tests must be F.I.R.S.T: fast, independent, repeatable, self-validating, timely.

## Dependencies

- Inject dependencies through constructor/parameter, not global/import.
- Wrap third-party libs behind thin interface owned by project.

## Structure

- Follow framework's convention (Rails, Django, Next.js, etc.).
- Prefer small focused modules over god files.
- Predictable paths: controller/model/view, src/lib/test, etc.
- When no clear convention, search web, document convention, follow.

## Formatting

- Use language default formatter or tested popular options (`cargo fmt`, `gofmt`, `prettier`, `black`, `ruff`, `rubocop -A`). Don't discuss style beyond that.

## Logging

- Structured JSON when logging for debugging / observability.
- Plain text only for user-facing CLI output.
