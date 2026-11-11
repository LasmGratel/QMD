package lach_01298.qmd.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/** Startup-only QMD extension event. */
public interface QMDStartupEvents {

    EventGroup GROUP = EventGroup.of("QMDStartupEvents");
    EventHandler REGISTRY = GROUP.startup("registry", () -> QMDStartupRegistrationEvent.class);
}
