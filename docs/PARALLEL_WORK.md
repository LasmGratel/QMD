# Concurrent implementation

Use this sequence whenever more than one agent writes code at the same time. A concurrent slice writes Java only. Compilation, repair, and GameTests happen once, on the main tree, after the merge.

## 1. Clone and divide `src/main/java`

Clone the current main working tree, including uncommitted source, into one git worktree per agent. Do not edit `G:\QMD` while those agents are writing.

Divide ownership only inside `src/main/java`. Give each agent disjoint packages. An agent creates and edits Java files only inside its packages.

These paths stay on the main tree and are not part of a concurrent slice:

- `QuantumMinecraftDynamics`, `QMDClient`, and any other shared entry point
- `src/data/`, `src/gameTest/`, `src/test/`, `src/generated/`
- `src/main/resources/`, Gradle files, language files, and `docs/`

If two slices need the same new type, one slice owns that type. The other slice calls it. Do not let two agents edit one file.

## 2. Write code only

Each agent implements its packages and stops. It does not compile, run GameTests, run data generation, copy the worktree back, or write integration notes. It does not inspect dependency jars or walk the repository beyond the types its slice calls and the legacy classes that define that slice.

The slice is finished when its Java files exist in the worktree. A worktree compile is not part of this step.

## 3. Merge back into the main repository

After every slice has stopped, copy only the Java files from those owned packages into `G:\QMD`. Leave the main tree's other files as they are. Do not replace `G:\QMD` with a worktree.

## 4. Compile and fix on the main tree

On `G:\QMD`, run:

```text
.\gradlew.bat compileJava --console=plain
```

Fix compile errors in the main tree. This step is where shared registration, client hooks, and recipe-catalog calls are added, because those files were outside the concurrent split. Repeat the same compile until it succeeds. Do not start GameTests while `compileJava` fails.

## 5. GameTest the migrated behavior

After `compileJava` succeeds, add or run the GameTests that exercise the behavior just merged. Run them on the main tree and record the exact command and result. Headless GameTests do not verify rendered appearance. A successful compile is not a GameTest result.
