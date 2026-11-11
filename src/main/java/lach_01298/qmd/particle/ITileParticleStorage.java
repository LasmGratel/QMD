package lach_01298.qmd.particle;

import java.util.List;

/** Common access for beam meters, synchronization and particle-processing block entities. */
public interface ITileParticleStorage {

    List<? extends ParticleStorage> getParticleBeams();
}
