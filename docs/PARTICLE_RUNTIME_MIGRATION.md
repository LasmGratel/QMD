# Particle runtime migration

Updated 2026-11-10. Target Minecraft 1.21.1, NeoForge 21.1.265, Neohaul 0.10.10 and Java 21. Exact verification results are recorded in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md).

## Creative particle source

`qmd:creative_particle_source` is a real, ticking block entity with the original block texture and 176×115 GUI texture. It is available in QMD's creative tab, cannot be broken in survival or destroyed by entities, and has no crafting recipe or survival loot. Creative players can open the configuration menu by right-clicking with the main hand; crouching suppresses opening. There are no inventory slots, matching the legacy menu.

The screen retains the four fields, Set control, particle image and complete particle tooltip. Fields accept built-in bare or `qmd:` particle names, integer amounts from 0 through 2147483647 pu, exact integer energies from 0 through 9223372036854775807 keV, and finite nonnegative focus. Invalid fields disable Set and identify the error. Clear removes the configured beam. A zero amount also clears the stored beam. Edits survive window resizing; typing does not trigger the inventory-close key binding. English, Russian and Chinese include source controls and all 64 particle names.

Each server tick emits a complete independent copy to every accepting adjacent particle capability, using the receiving face. Emission never consumes the configured beam. A rejecting neighbor does not stop other faces. Redstone halts active emission and retains configuration; capability pulls also halt while powered, preventing downstream transport from bypassing the control. Empty sources send nothing. Neighbor output and weak/strong redstone checks skip unloaded chunks.

The source saves the legacy `ParticleStorage0` / `particle_stack` NBT shape. Unknown particles and missing/empty saved storage clear the beam. The storage is attached to its block entity so changes mark it for saving.

## Capability, values and storage contracts

`QMDParticleCapabilities.BLOCK` exposes `IParticleStackHandler` under `qmd:particle_handler`, with a nullable receiving `Direction`. The source provides one stable handler on every face and for unsided queries. Other migrated consumers can use the same contract without depending on a concrete source class.

`ParticleBeam` is an immutable snapshot carrying species, amount, long mean energy and double focus. Its persistent and network codecs validate all fields, reject unknown species and nonfinite/negative values, and preserve exact long energies. An empty beam has an empty species and all-zero fields. This value contract is used by the source menu packets; source NBT retains its legacy storage shape. Source/cell item data components and native item-fluid exchange are implemented; see [FISSION_AND_CELLS_MIGRATION.md](FISSION_AND_CELLS_MIGRATION.md).

| Store | Insertion | Extraction |
| --- | --- | --- |
| `ParticleStorageSource` | Always rejected | Independent, non-consuming samples for full, species-filtered and count-limited requests |
| `ParticleStorageAccelerator` | Replaces the current beam within inclusive energy/capacity limits; null explicitly clears it | Same non-consuming snapshot behavior as the source |
| `ParticleStorageBeamline` | Replaces the current beam; requires positive focus and valid energy/capacity | Consumes the requested beam/count and applies legacy focus loss |

Beamline extraction retains the strict legacy gate `focus > length × beam_attenuation_rate`. Focus loss is `beam_attenuation_rate × length × (1 + abs(charge) × sqrt(amount / beam_scaling))`, clamped to zero by the particle value model. The charge term can exhaust focus even after the baseline gate passes; this is retained behavior. Partial extraction applies loss to the extracted portion. The remaining stored portion retains its incoming focus. Beamline length is nonnegative and persists in NBT. `other.beam_scaling` restores the legacy default 10000, with range 1 through 2147483647; `accelerator.beam_attenuation_rate` retains default 0.02.

Compared with legacy, insertion checks and actual insertion now agree, input/output objects do not alias internal source state, and specialized stores accept the full modern long-energy range. `ITileParticleStorage` exposes beam stores for later meters and machine integration.

## Menu and network authority

The server controls source edits. A settings packet carries a container ID, position and one validated beam. The handler requires the sender's current source menu, matching ID/position, creative mode, distance at most eight blocks, a loaded position and the exact original live block entity. Closing, removing or replacing the source invalidates the request. Clients cannot change storage capacity or energy limits through this packet.

Initial menu data includes position and a full beam snapshot. Subsequent server changes send immutable snapshots only to the player with that menu open. Client application checks menu ID and position and replaces the complete display, including clearing an old beam. Screen edits are submitted without changing the authoritative preview locally. This covers packet values and common handler application; actual client delivery is not established by headless tests.

## Verification and remaining scope

Focused unit tests cover specialized storage replacement, non-consuming and partial extraction, boundary checks, alias isolation, exact energy, beamline attenuation and length persistence. Source GameTests exercise the registered ticker, real adjacent receiving block entities, all six faces and unsided capability access, redstone stop/resume, empty/rejected output, source NBT, menu/network factories, player inventory conservation, authorization and stale menus, persistent/network codecs, independent snapshots, display clearing and unloaded neighbors. A separate batch checks source configuration and emission across an asynchronous server resource reload.

Test receivers exist only in the GameTest source set and are excluded from the main jar. See [TESTING.md](TESTING.md) for the repeatable headless workflow. Rendered appearance, actual screen clicks and client packet delivery remain unverified.

Operating beamlines and all six accelerator families now use this capability and storage model in real worlds. Target/decay/collision/beam-dump chambers, neutral containment, cell exchange and nucleosynthesis supply actual gameplay consumers. See [ACCELERATOR_MIGRATION.md](ACCELERATOR_MIGRATION.md) and [CHAMBERS_MIGRATION.md](CHAMBERS_MIGRATION.md). Source/cell items retain component-aware fluid exchange, dropped-cell hazards and the gamma-flash entity. The particle meter, equipment/projectile entities, beam rendering beyond controller previews, and remaining KubeJS content-registration APIs remain migration work. No CraftTweaker or GroovyScript integration was added.
