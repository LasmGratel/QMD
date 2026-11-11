# Weapons migration

`QMDWeapons`, `QMDEntities` and `QMDWeaponSounds` register all three weapons, their entities and legacy sounds. `qmd-weapons.toml` exposes the migrated tuning. Ammunition uses existing particle cells; the first matching inventory cell controls firing. Creative firing requires sufficient ammunition but does not consume it. Exhausted cells become `qmd:cell_empty`.

| Weapon | Default behavior |
| --- | --- |
| Lepton cannon | Positronium/muonium/tauonium: 500 units per shot, damage 7/14/28, radiation 10/20/40, range 30/60/90, cooldown 8 ticks. Entity-only tracing passes through blocks. A tinted beam ends at the actual hit and expires after four ticks. |
| Gluon gun | Glueballs: 10 units per active tick, damage/radiation 10, range 40. Sneak-use toggles persistent Fortune IV/Silk Touch mining. Mining respects unbreakable blocks, player restrictions and break-event cancellation while ignoring harvest tier. The beam follows its owner and hand, stopping when use or ammunition ends. |
| Antimatter launcher | Five antimatter isotopes: 5,000 units per shot, mass 1/2/3/3/4, damage 20 times mass, cooldown 30. Speed 3, inaccuracy 1, drag .99, gravity .05. Swept impacts respect projectile cancellation and PvP; water detonates. Explosion radius is 2.5 times mass and gamma radiation uses the cell-radiation setting times mass. |

Dedicated indirect damage types preserve armor bypass and shooter/projectile attribution without magic-specific damage semantics. Beam persistence retains owner, hand, length, color and lifetime; antimatter persistence retains shooter, mass, color and age. This restores state the legacy projectile saves omitted. The launcher radiation setting unused by the legacy implementation is not substituted for its actual cell-radiation behavior.

Client renderers implement fullbright tinted crossed planes, original beam widths/muzzle offsets, gluon rotation/scrolling, and a rotating antimatter projectile with end caps. The gluon sound follows the beam through start, loop and stop. Original textures and audio are retained. Recipe builders preserve the legacy crafting patterns and translation-key-derived IDs.

The shared gluon/lepton renderer uses negative local X for the right hand and positive local X for the left hand, correcting the reversed muzzle positions for main-hand and off-hand firing. The player's dominant arm still determines which physical hand each interaction hand represents. Rendered hand alignment has not been visually verified.

The three weapon item models are authored resources copied from the legacy Blockbench models, preserving their geometry, UVs, display transforms and credits. Texture references use the migrated `qmd:item/tools/` paths. The item-model provider skips these weapons, and `runData` removes their old generated flat models. Compilation/data generation and `processResources` passed for this ownership change; structural model and texture-reference checks passed. Rendered appearance has not been verified.

Eight required GameTests cover real target damage through a wall with armor and attribution, beam expiry, first-cell and creative rules, break cancellation, ammo exhaustion, silk drops and item persistence, continuous beam ticking/owner/hand persistence, loaded weapon crafting, launcher motion/persistence/water detonation, and canceled/uncanceled solid-block impacts. They passed in the 119-test base run; exact commands and evidence are in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md).

No desktop or client testing was used. Rendered beams, projectile geometry, GUI appearance, audio playback, client input and actual network delivery remain unverified by these headless tests.
