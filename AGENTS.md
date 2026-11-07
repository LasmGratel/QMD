# Agent Guide

## Project goal

Finish migrating QMD to Minecraft 1.21.1 on NeoForge, using the compatible Nuclearcraft Neohaul release. Keep the mod ID `qmd` and display name `Quantum Minecraft Dynamics`.

## Start here

- Find and use `minecraft-modding` skill.
- Read [docs/DEVELOPMENT_HANDOFF.md](docs/DEVELOPMENT_HANDOFF.md) for the verified status, test results, known gaps, build commands, and next steps.
- Read [README.md](README.md) for the project overview.
- Inspect `git status --short` before editing. Preserve existing work and avoid destructive resets or checkouts.

## Project conventions

- The active implementation is under `src/main/java/`; `legacy-src/` is a reference copy of the old implementation and is not compiled.
- Runtime assets, block states, models, recipes, and loot tables live under `src/main/resources/`.
- Target Minecraft 1.21.1, NeoForge 21.1.x, and Java 21. Nuclearcraft-Neohaul is required at runtime; use the version recorded in `build.properties`.
- Keep `README.md` user-facing. Do not put development status, workflow, branch state, build/test results, or internal handoff notes there; put development documentation in `docs/*.md`.
- Update the relevant `docs/*.md` files when implementation or verification status changes. Update README only for player-facing installation, feature, and usage information.
- While the Nuclearcraft-Neohaul implements CraftTweaker support, DO NOT implement CraftTweaker in this project and migrate CraftTweaker and GroovyScript compat to KubeJS support instead.

## Implementation principles

- Fixes and feature work may include small, step-by-step overhauls of the affected systems. Keep each step reviewable and preserve the complete behavior while improving the design.
- Preserve feature parity with the legacy mod. Never omit or downgrade a feature as a migration shortcut. In particular, restore all GUI; a right-click text popup is not an acceptable replacement for it.
- When a legacy feature is difficult to port, implement a full equivalent for 1.21.1 and record real technical blockers. Do not silently replace it with a reduced interaction.

## Commits

- Include `Co-authored-by: Codex <codex@openai.com>` in every commit created for this project.

## Verification and completion

- Distinguish code that exists from behavior verified in game. Report the exact build or run command and its result; a successful `runClient` is not a full release build.
- Use NeoForge GameTests for automated in-game verification, following the [1.21.1 GameTest documentation](https://docs.neoforged.net/docs/1.21.1/misc/gametest/). Do not use the computer-use skill to test the game or request desktop access for testing.
- Cover machine behavior, menu/slot interactions, automation, persistence and resource reload through GameTests. Headless GameTests do not verify rendered appearance; document that limit without treating desktop access as a blocker for migration.
