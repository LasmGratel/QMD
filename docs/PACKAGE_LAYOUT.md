# Active package layout

The Minecraft 1.21.1 implementation uses feature-independent packages. The preserved `legacy-src/` tree is reference material and is not compiled. Registry IDs remain under `qmd`; Java package moves do not change saved registry IDs.

| Package under `lach_01298.qmd` | Responsibility |
| --- | --- |
| `block` | World blocks and block interactions |
| `block.entity` | Ticking machines, stored inventories and capabilities; liquefier parts have a dedicated subpackage |
| `block.menu` | Every QMD `AbstractContainerMenu`, server slot contracts and menu actions |
| `client` | Client initialization, fluid rendering and multiblock renderers |
| `client.screen` | Main machine screens and configuration dialogs |
| `client.particle` | Particle ingredient and gamma-flash rendering |
| `registry` | Items, fluids, blocks, entities, data components, menus, recipe types and damage-type keys |
| `config` | Common configuration and immutable viewer settings |
| `network` | Validated menu actions and immutable state payloads |
| `capability` | Particle block capability and cell fluid adapter |
| `multiblock` | Structure validation and operating logic; accelerator and liquefier subpackages |
| `particle`, `physics` | Particle definitions, values, storage and beam physics |
| `recipe` | Recipe data, matching, codecs and ingredient contracts |
| `compat.jei`, `compat.kubejs` | Optional integrations discovered by their respective mods |
| `datagen` (in `src/data/java/`) | Mod-bus data generation; client, recipe and server providers produce checked-in resources without shipping generator classes |

The refactor removes the active `collector`, `machine`, `registration`, `capabilities` and root client class organization. Common classes do not import QMD screens or JEI. The external Neohaul fluid API itself holds its client extension, so its internals remain tied to the pinned upstream release.

GameTest packages follow their subject's active package. Main resources, tests and scripts reference the modern types. No forwarding classes preserve the old package hierarchy.

Generated resources belong to `src/generated/resources/`; authored textures, translations and exceptional model geometry remain in `src/main/resources/`. Both roots form the main resource source set, with duplicate paths rejected by Gradle. See [DATA_GENERATION.md](DATA_GENERATION.md) for provider ownership and regeneration.

The registry package has twelve shared owners: `QMDBlocks`, `QMDItems`, `QMDFluids`, `QMDEntities`, `QMDBlockEntities`, `QMDMenus`, `QMDRecipes`, `QMDArmorMaterials`, `QMDDataComponents`, `QMDSounds`, `QMDDamageTypes` and `QMDCreativeTabs`. Feature-specific deferred registers and forwarding classes are removed. See [REGISTRY_REFACTOR.md](REGISTRY_REFACTOR.md) for ownership and initialization order.
