# Automated game testing

Use NeoForge's [Minecraft 1.21.1 GameTest framework](https://docs.neoforged.net/docs/1.21.1/misc/gametest/) for in-game verification. The user requested this workflow instead of the computer-use skill. Desktop access is not required.

From `E:/QMD`, run:

```powershell
.\gradlew.bat build
.\gradlew.bat runGameTestServer
.\gradlew.bat runGameTestServer -PwithKubeJS
```

`build` runs the Java unit tests and builds the main mod jar. The two GameTest commands start real headless Minecraft servers with the pinned Neohaul release, respectively without and with optional KubeJS. A required GameTest failure produces a failing Gradle task. The task also clears the previous log and requires a positive required-test success summary: a script bootstrap abort that exits with code zero must still fail the build. Read the test count and failure summary; starting a server alone does not establish a passing test run.

GameTest sources are under `src/gameTest/java/`. Tests use `@GameTestHolder("qmd")`, `@PrefixGameTestTemplate(false)` and `@GameTest(template = "empty")`. The template resolves to `data/qmd/structure/empty.nbt` in the test-only resources; its readable source is `src/gameTest/structures/empty.snbt`. Only the `qmd` GameTest namespace is enabled. All tests must explicitly succeed or they fail at their timeout. Schedule delayed assertions without blocking the server thread, and isolate global resource reloads in separate named batches.

The server uses `run/gametest/` for its world, configuration and logs, separately from the development client. With `-PwithKubeJS`, Gradle syncs the tracked fixtures from `src/gameTest/kubejs/` into that server's `kubejs/startup_scripts/` and `kubejs/server_scripts/` directories before launch. These two directories belong to the test fixtures: untracked or renamed scripts there are removed by the next sync. KubeJS configuration and logs outside them are preserved. Keep these fixtures independent of player saves and main runtime resources. An alternate mod folder group includes test sources only for `gameTestServer`; normal client/server/data runs and unit tests use the main source set. GameTest classes, the template and the scripts are excluded from the distributed mod jar.

Unit test reports are in `build/reports/tests/test/`. GameTest output and failures are in `run/gametest/logs/latest.log`; KubeJS also writes logs under `run/gametest/logs/kubejs/`. The CI workflow runs the build and both GameTest configurations, saves separate command transcripts under `build/reports/migration/`, then uploads reports and logs.

The expanded base suite has 34 required tests. It retains the 14 ore-leacher processing/menu/synchronization tests and adds eight collector world/capability/persistence tests, four collector recipe-codec/world-predicate tests, three chemistry tests, one cooling-policy reload test and four GUI-action/packet tests. Collector tests include a real loaded/unloaded chunk boundary, checking that sampling and output push do not load its neighbor. Collector chemistry runs real ticking crystallizers, an enricher, an electrolyzer and a chemical reactor to produce hydrochloric acid from collected salt water. The GUI tests cover relative faces, output actions, actual output packet handling and delayed server acknowledgements; they do not execute a rendered client screen.

The cooling-policy test runs in its own `qmd.cooling.reload` batch. It changes `recipes.override_nc_recipes`, asynchronously reloads server resources, verifies the alternate cooling ratios and restores the original policy through another reload. Global resource reload tests must stay in separate batches to avoid changing another test's recipe manager mid-assertion.

With KubeJS present, a `@GameTestGenerator` adds a thirty-fifth required test that requires successful startup helpers, real removal/replacement of tracked ore-leacher and liquid-collector datapack recipes, a new recipe-manager instance after resource reload, generation-specific recipe values, and zero script errors. The collector smoke recipes require an intentionally absent biome tag, so they cannot change the collector world fixtures. The test-only Java bridge records script assertions and lifecycle generations, so log messages alone cannot satisfy the test. Resource reload runs asynchronously; its polling uses a wall-clock deadline because this headless server advances test ticks faster than real time.

GameTests verify processing, automation, menu interactions, persistence, synchronization data and recipe reload behavior. They do not verify the screen's rendered appearance. Record this boundary in the development handoff without requiring desktop automation.

Exact observed results and known upstream log issues are recorded in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md).
