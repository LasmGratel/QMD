# Agent Guide

## Project goal

Finish migrating QMD to Minecraft 1.21.1 on NeoForge, using the compatible Nuclearcraft Neohaul release. Keep the mod ID `qmd` and display name `Quantum Minecraft Dynamics`.

## Start here

- Use the `minecraft-modding` skill when the task is mod code, registries, datagen, or networking.
- Read [docs/DEVELOPMENT_HANDOFF.md](docs/DEVELOPMENT_HANDOFF.md) when the task depends on verified status, test results, or known gaps. Do not read the whole handoff, README, and every migration note before an edit that does not need them.
- Inspect `git status --short` before editing. Preserve existing work and avoid destructive resets or checkouts.

## GPT-6.1 Sol: act, do not spin

These instructions are for GPT-6.1 Sol and the rest of the GPT-6 family. They follow OpenAI's prompting guidance for this model family: [prompt guidance](https://developers.openai.com/api/docs/guides/prompt-guidance), [rethinking skills and prompts](https://developers.openai.com/blog/rethinking-skills-and-prompts-for-gpt-6-astra), and [maintaining software with the GPT-6 family](https://developers.openai.com/blog/maintaining-software-with-the-gpt-6-family). Strong "stop and ask" rules make GPT-6.1 Sol pause and plan extra work. State the finished result instead.

Infer the task from the request and the code in front of you. Bias toward action and carry a single-agent change through to the Java edit. A request to change, build, or fix authorizes the in-scope edit. Do not stop after a plan, a capability acknowledgement, or an offer to continue. Concurrent work follows [docs/PARALLEL_WORK.md](docs/PARALLEL_WORK.md): the slice writes code only, and compile plus GameTest happen after the merge.

Do not read the repository, dependency jars, or legacy tree broadly before editing. Open the files that control the behavior being changed, write the change, then check the result. If a lookup fails, retry a transient failure once and then implement from the evidence already in hand. Stop exploring when the next read would not change the edit.

Do not add defensive programming for hypothetical failure. No extra null checks, fallback branches, catch-all handlers, wrapper types, feature flags, or validation layers unless the called API or a failing test shows they are required. Match the surrounding code. Do not introduce unsolicited warnings, disclaimers, approval steps, or compliance checklists for hypothetical risk.

Ask only when the next action is destructive, irreversible, or would change an external or shared system. Finish the reversible work first so a question, if one is still required, refers to a concrete diff.

Run the one check that covers the change. For a single agent that is `.\gradlew.bat compileJava --console=plain` after Java edits, then the GameTest that covers the behavior just changed. A concurrent slice does not compile and does not run GameTests; those two steps run once on `G:\QMD` after its Java is merged, in that order. Do not write tests that restate the implementation, and do not repeat or widen a passing check.

## Concurrent work

Follow [docs/PARALLEL_WORK.md](docs/PARALLEL_WORK.md).

When several agents write at once, each agent gets its own clone and a disjoint part of `src/main/java`. During that phase it only writes those Java files. It does not compile, run GameTests, generate data, or edit shared entry points, resources, Gradle files, or docs.

After the slices stop, merge their Java into the main tree. Then run `.\gradlew.bat compileJava --console=plain` on `G:\QMD` and fix the compile there, including registration the slices did not own. After that compile succeeds, run the GameTests for the behavior that was just merged. Do not start those tests while compile is failing.

## Project conventions

- The active implementation is under `src/main/java/`; `legacy-src/` is a reference copy of the old implementation and is not compiled.
- Hand-authored runtime assets (textures, translations and exceptional model geometry) live under `src/main/resources/`. Generated blockstates, models, recipes, tags, loot tables and related data live under `src/generated/resources/`; Gradle includes both in the main resource source set.
- Target Minecraft 1.21.1, NeoForge 21.1.x, and Java 21. Nuclearcraft-Neohaul is required at runtime; use the version recorded in `build.properties`.
- Keep `README.md` user-facing. Do not put development status, workflow, branch state, build/test results, or internal handoff notes there; put development documentation in `docs/*.md`.
- Update the relevant `docs/*.md` files when implementation or verification status changes. Update README only for player-facing installation, feature, and usage information.
- While the Nuclearcraft-Neohaul implements CraftTweaker support, DO NOT implement CraftTweaker in this project and migrate CraftTweaker and GroovyScript compat to KubeJS support instead.

## Data generation

- Use NeoForge [1.21.1 data generation](https://docs.neoforged.net/docs/1.21.1/resources/#data-generation) for repetitive models, blockstates, recipes, tags and loot tables. Providers live in the dedicated `src/data/java/lach_01298/qmd/datagen/` source set and register through `GatherDataEvent` with the appropriate client/server flag.
- Define families through registries, shared builders and compact domain tables. Do not duplicate generated JSON as handwritten files or embed copies of complete output JSON in a provider. Generation must work without reading previous generated output or the original handwritten JSON.
- For recipes, use direct Minecraft/Neohaul builders and typed QMD recipe factories. Do not encode recipe definitions as delimited column/row strings or assemble recipe JSON manually.
- Edit provider code or its domain tables, then run `.\gradlew.bat runData --console=plain`. Include the resulting `src/generated/resources/` changes in the same change. Do not hand-edit generated files or commit the generated `.cache/` directory.
- Each resource path must have a single owner across `src/main/resources/` and `src/generated/resources/`. When migrating a resource, compare its generated content before removing the handwritten original. Preserve recipe IDs, conditions, amounts, exact particle energies, tag semantics and model variants.
- Keep exceptional authored geometry, binary assets and translations in the main tree when generation would add no useful abstraction. Record ownership and verification in `docs/DATA_GENERATION.md`.

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
