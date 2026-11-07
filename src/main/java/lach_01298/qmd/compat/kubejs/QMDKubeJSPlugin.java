package lach_01298.qmd.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

/** Loaded by KubeJS through kubejs.plugins.txt when KubeJS is installed. */
public final class QMDKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("QMDParticles", QMDParticles.class);
    }
}
