package lach_01298.qmd.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

/** Loaded by KubeJS through kubejs.plugins.txt when KubeJS is installed. */
public final class QMDKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerRecipeComponents(
        dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry registry) {
        QMDRecipeSchemas.registerComponents(registry);
    }

    @Override
    public void registerRecipeSchemas(dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry registry) {
        QMDRecipeSchemas.registerSchemas(registry);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("QMDParticles", QMDParticles.class);
    }
}
