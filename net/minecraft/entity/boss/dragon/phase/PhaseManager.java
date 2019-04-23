/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final EnderDragonEntity dragon;
    private final Phase[] phases = new Phase[PhaseType.count()];
    private Phase current;

    public PhaseManager(EnderDragonEntity enderDragonEntity) {
        this.dragon = enderDragonEntity;
        this.setPhase(PhaseType.HOVER);
    }

    public void setPhase(PhaseType<?> phaseType) {
        if (this.current != null && phaseType == this.current.getType()) {
            return;
        }
        if (this.current != null) {
            this.current.endPhase();
        }
        this.current = this.create(phaseType);
        if (!this.dragon.world.isClient) {
            this.dragon.getDataTracker().set(EnderDragonEntity.PHASE_TYPE, phaseType.getTypeId());
        }
        LOGGER.debug("Dragon is now in phase {} on the {}", (Object)phaseType, (Object)(this.dragon.world.isClient ? "client" : "server"));
        this.current.beginPhase();
    }

    public Phase getCurrent() {
        return this.current;
    }

    public <T extends Phase> T create(PhaseType<T> phaseType) {
        int i = phaseType.getTypeId();
        if (this.phases[i] == null) {
            this.phases[i] = phaseType.create(this.dragon);
        }
        return (T)this.phases[i];
    }
}

