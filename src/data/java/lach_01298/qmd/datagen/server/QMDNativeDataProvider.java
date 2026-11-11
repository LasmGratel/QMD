package lach_01298.qmd.datagen.server;

import com.nred.nuclearcraft.datamap.FissionReflectorData;
import com.nred.nuclearcraft.registration.DataMapTypeRegistration;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

/** Typed entries in Neohaul's registered data maps. */
public final class QMDNativeDataProvider extends DataMapProvider {
    public QMDNativeDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void gather(HolderLookup.Provider registries) {
        builder(DataMapTypeRegistration.FISSION_REFLECTOR_DATA)
                .add(QMDBlocks.TUNGSTEN_CARBIDE_REFLECTOR.getId(), new FissionReflectorData(0.75, 0.75), false);
    }
}
