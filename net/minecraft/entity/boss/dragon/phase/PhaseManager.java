/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PhaseManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final EnderDragonEntity dragon;
    private final Phase[] phases = new Phase[PhaseType.count()];
    @Nullable
    private Phase current;

    public PhaseManager(EnderDragonEntity dragon) {
        this.dragon = dragon;
        this.setPhase(PhaseType.HOVER);
    }

    public void setPhase(PhaseType<?> type) {
        if (this.current != null && type == this.current.getType()) {
            return;
        }
        if (this.current != null) {
            this.current.endPhase();
        }
        this.current = this.create(type);
        if (!this.dragon.world.isClient) {
            this.dragon.getDataTracker().set(EnderDragonEntity.PHASE_TYPE, type.getTypeId());
        }
        LOGGER.debug("Dragon is now in phase {} on the {}", (Object)type, (Object)(this.dragon.world.isClient ? "client" : "server"));
        this.current.beginPhase();
    }

    public Phase getCurrent() {
        return this.current;
    }

    public <T extends Phase> T create(PhaseType<T> type) {
        int i = type.getTypeId();
        if (this.phases[i] == null) {
            this.phases[i] = type.create(this.dragon);
        }
        return (T)this.phases[i];
    }
}

