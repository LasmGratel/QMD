// Tracked GameTest fixture; failures reach both KubeJS logs and the Java GameTest bridge.
;(function () {
var QmdServerSmokeBridge = Java.loadClass('lach_01298.qmd.compat.kubejs.KubeJSMigrationSmokeBridge')
var QmdSmokeResourceLocation = Java.loadClass('net.minecraft.resources.ResourceLocation')
var QmdSmokeBuiltInRegistries = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries')
var qmdSmokeGeneration = 0
var qmdSmokeVerifyPending = true

function qmdSmokeGuard(qmdSmokeAction) {
  try { return qmdSmokeAction() } catch (qmdSmokeError) {
    QmdServerSmokeBridge.failure('server generation ' + qmdSmokeGeneration + ': ' + String(qmdSmokeError))
    throw qmdSmokeError
  }
}

qmdSmokeGuard(() => {
  if (!Item.exists('qmd:ingot_tungsten') || !Item.exists('qmd:part_empty_cooler')) {
    throw new Error('QMD material registry is incomplete')
  }
  if (QMDParticles.get('proton').getAntiParticle().getName() !== 'antiproton') {
    throw new Error('QMD particle relationships are missing')
  }
  qmdSmokeGeneration = QmdServerSmokeBridge.serverLoaded()
})

ServerEvents.recipes(event => qmdSmokeGuard(() => {
  // These IDs come from tracked test datapack fixtures, so removal is exercised on every reload.
  if (!event.containsRecipe({ id: 'qmd:smoke/ore_leacher' })
      || !event.containsRecipe({ id: 'qmd:smoke/removed_ore_leacher' })
      || !event.containsRecipe({ id: 'qmd:smoke/collector' })
      || !event.containsRecipe({ id: 'qmd:smoke/collector_removed' })) {
    throw new Error('Test datapack baseline recipes must exist before KubeJS filtering')
  }
  event.remove({ id: 'qmd:smoke/ore_leacher' })
  event.remove({ id: 'qmd:smoke/removed_ore_leacher' })
  event.remove({ id: 'qmd:smoke/collector' })
  event.remove({ id: 'qmd:smoke/collector_removed' })
  event.custom({
    type: 'qmd:ore_leacher',
    itemIngredients: [{ ingredient: { item: 'minecraft:cobblestone' }, count: 1 }],
    fluidIngredients: [
      { ingredient: { fluid: 'qmd:nitric_acid' }, amount: 16 },
      { ingredient: { fluid: 'qmd:hydrochloric_acid' }, amount: 16 },
      { ingredient: { fluid: 'nuclearcraftneohaul:sulfuric_acid' }, amount: 16 }
    ],
    itemProducts: [{ ingredient: { item: 'minecraft:brick' }, count: qmdSmokeGeneration }],
    timeModifier: 0.01 * qmdSmokeGeneration,
    powerModifier: 1,
    radiation: 0
  }).id('qmd:smoke/ore_leacher')
  event.custom({
    type: 'qmd:liquid_collector',
    world: {
      blocks: [{ block: 'minecraft:water', properties: { level: '0' } }],
      biomes: [],
      // This test-only missing tag prevents smoke recipes from affecting collector world tests.
      biome_tags: ['qmd:smoke/collector_only'],
      dimensions: ['minecraft:overworld']
    },
    output: { id: 'qmd:salt_water', amount: 137 * qmdSmokeGeneration },
    priority: -10000 - qmdSmokeGeneration
  }).id('qmd:smoke/collector')
  QmdServerSmokeBridge.recipesPassed(qmdSmokeGeneration)
}))

ServerEvents.tick(event => qmdSmokeGuard(() => {
  if (!qmdSmokeVerifyPending) return
  var qmdSmokeManager = event.server.getRecipeManager()
  var qmdSmokeRecipe = qmdSmokeManager.byKey(QmdSmokeResourceLocation.parse('qmd:smoke/ore_leacher'))
  var qmdSmokeCollector = qmdSmokeManager.byKey(QmdSmokeResourceLocation.parse('qmd:smoke/collector'))
  // While reload is in flight the previous manager is still visible; verify once swapped.
  if (!qmdSmokeRecipe.isPresent() || qmdSmokeRecipe.get().value().getProcessTimeMultiplier() !== 0.01 * qmdSmokeGeneration) return
  if (!qmdSmokeCollector.isPresent() || qmdSmokeCollector.get().value().output().getAmount() !== 137 * qmdSmokeGeneration) return
  var qmdSmokeValue = qmdSmokeRecipe.get().value()
  var qmdSmokeCollectorValue = qmdSmokeCollector.get().value()
  if (qmdSmokeValue.getItemProducts().size() !== 3
      || qmdSmokeValue.getItemProducts().get(0).count() !== qmdSmokeGeneration
      || qmdSmokeManager.byKey(QmdSmokeResourceLocation.parse('qmd:smoke/removed_ore_leacher')).isPresent()) {
    throw new Error('Loaded recipe manager does not contain the decoded replacement/removal')
  }
  if (String(QmdSmokeBuiltInRegistries.FLUID.getKey(qmdSmokeCollectorValue.output().getFluid())) !== 'qmd:salt_water'
      || qmdSmokeCollectorValue.priority() !== -10000 - qmdSmokeGeneration
      || qmdSmokeManager.byKey(QmdSmokeResourceLocation.parse('qmd:smoke/collector_removed')).isPresent()) {
    throw new Error('Loaded recipe manager does not contain the collector replacement/removal')
  }
  QmdServerSmokeBridge.managerPassed(qmdSmokeGeneration)
  qmdSmokeVerifyPending = false
  console.info('[QMD_SMOKE] Loaded manager verified generation ' + qmdSmokeGeneration)
}))
})()
