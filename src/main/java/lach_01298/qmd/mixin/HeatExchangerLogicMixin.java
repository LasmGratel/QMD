package lach_01298.qmd.mixin;

import com.nred.nuclearcraft.multiblock.hx.HeatExchanger;
import com.nred.nuclearcraft.multiblock.hx.HeatExchangerLogic;
import lach_01298.qmd.multiblock.liquefier.LiquefierLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Neohaul hardcodes its two HX logic IDs. Extend only that dispatch point for QMD's HX variant. */
@Mixin(value = HeatExchanger.class, remap = false)
public abstract class HeatExchangerLogicMixin {

    @Shadow
    protected HeatExchangerLogic logic;

    @Inject(method = "setLogic(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void qmd$installLiquefier(String id, CallbackInfo callback) {
        if (id.equals("liquefier")) {
            if (!(logic instanceof LiquefierLogic)) logic = new LiquefierLogic(logic);
            callback.cancel();
        }
    }
}
