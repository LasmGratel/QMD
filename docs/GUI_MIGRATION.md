# Client and JEI GUI migration

The machine screens use the authored textures from `legacy-src/main/resources/assets/qmd/textures/gui/`. Twenty-five machine and JEI textures are copied byte-for-byte into `src/main/resources/assets/qmd/textures/gui/`; their hashes are recorded in the ignored `build/reports/migration/gui/assets.json`. The ore-leacher sprite copy remains for Neohaul's sprite-based processor screen; JEI uses the original raw texture.

## Container screens

| Screen | Artwork and interaction |
| --- | --- |
| Accelerator | Separate ion-source, spectrometer and accelerator-controller panels. Menu slot positions match the authored inventories. Live particle icons, textured FE/heat/progress instruments, tiled fluid sprites, actual tank capacities, length/radius and RF/magnet/thermal data. Existing redstone/percentage/routing buttons remain; Shift activates source-tank, individual-tank and controller material clearing. |
| Particle and vacuum chambers | Target, decay, collision, beam-dump, neutral-containment and nucleosynthesis controller panels retain their distinct beam, fluid and inventory coordinates. Active beam connections use the legacy overlay atlas. Real inventory slots, bucket exchange, per-channel progress, thermal/energy instruments, status/validation information, enable/routing and Shift-clear actions remain available. |
| Irradiator | Original machine panel and both progress overlays, configuration/redstone controls, reservoir information and all six faces of all three slot sorptions. The configuration screen uses native buttons and the original irradiator artwork. |
| Liquefier | Original six-line panel and Shift modifier view. Native `StringWidget` readouts retain the complete text in tooltips; material clearing retains the server ownership checks. |
| Creative particle source | Original four-field editor and preview. Native `EditBox`, `Button` and particle widgets retain validation, exact long energy values, Enter submission, text-field keyboard handling and unsent edits across resize. |
| Beam meter | Native readouts on QMD's authored liquefier instrument panel replace the drawn rectangle panel. Particle/amount/energy/focus/attenuation/travel/beamline measurements and port modes remain available. The class now lives in `client.screen`. |
| Ore leacher | Retains Neohaul's native processor and sorption widgets, original QMD sprite, two-stage progress, upgrades, redstone, relative-face tank controls and output settings. These already use the modern container/widget APIs. |

`client.screen.widget.MachineWidget` supplies focusable, narrated particle, fluid, gauge and readout widgets. Fluid drawing uses the registered still sprite and tint from `IClientFluidTypeExtensions`, clipped to the instrument's current fill. Gauges use the original atlas overlays, including top-down and right-to-left progress. Native tooltips retain complete numbers and validation information. Repeated frames do not reset unchanged instrument tooltips.

Controller screens that previously exposed player inventory retain it. For legacy panels without an inventory, the original source inventory section is appended; controls use a separate native button row. Machine inventories keep their existing slot indices for shift-click and JEI transfer.

## JEI

The sixteen concrete category implementations cover all 19 registered category families. Source/cooling/spectrometer, collectors, irradiator/fuel, liquefier/coolant and all chamber families use the original category textures or the exact controller texture crops. Slots retain all physical recipe channels, alternatives, chance outputs, amounts, energy ranges and server-configured scales. Recipe metadata and world predicates remain in native JEI text/scroll widgets.

Chamber menus/screens now have six concrete controller families under `block.menu.chamber` and `client.screen.chamber`; their abstract bases share widgets and validated interactions. The eight JEI chamber families have typed recipe/category implementations under `recipe.chamber` and `compat.jei.category.chamber`. Layouts, artwork, overlays and click areas remain owned by each concrete class. See [CHAMBER_REFACTOR.md](CHAMBER_REFACTOR.md) for ownership, compatibility and the 123-test verification checkpoint.

Accelerator menus/screens now have six concrete controller families, an ion-source view and a common component/port view under `block.menu.accelerator` and `client.screen.accelerator`. Three concrete JEI categories use typed source/cooling/spectrometer recipes. The extracted classes retain the original instruments, controls, hidden spectrometer slot, transfer indices and worktree JEI offsets. See [ACCELERATOR_REFACTOR.md](ACCELERATOR_REFACTOR.md) for the hierarchy and the 125-test checkpoint.

`JeiPanel` creates animated drawable widgets through the current `addDrawableWidget` API. The ore-leacher retains its synchronized two-stage animation. Other atlas animations cover cooling, spectrometer separation, target reactions, beam dumps, nucleosynthesis, liquefaction and irradiation. `MachineGuiHandler` obtains ingredient areas from the actual screen widgets, keeping fluid and particle lookup aligned with their rendered bounds. Recipe click areas and transfer handlers retain the corresponding real machine actions.

Missing legacy GUI translation keys were imported for English, Russian and Chinese without replacing existing translations. New clear-action hints and accelerator viewer labels have translations in all three locales.

## Verification

Compilation and GameTest results are recorded in [DEVELOPMENT_HANDOFF.md](DEVELOPMENT_HANDOFF.md). Two GUI contract GameTests exercise real source inventory transfer, carried fluid containers, ingredient filtering, tank/material clearing, player inventory conservation, owner checks and stale menu rejection. Existing accelerator persistence coverage also checks synchronized capacities and clearing through an assembled controller.

Headless tests do not render the client screens or JEI layouts, exercise keyboard navigation or establish actual client packet delivery. No desktop automation or rendered appearance verification was used.
