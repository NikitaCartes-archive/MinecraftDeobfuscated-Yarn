/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class HoverPhase
extends AbstractPhase {
    private Vec3d field_7042;

    public HoverPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Override
    public void serverTick() {
        if (this.field_7042 == null) {
            this.field_7042 = this.dragon.getPos();
        }
    }

    @Override
    public boolean method_6848() {
        return true;
    }

    @Override
    public void beginPhase() {
        this.field_7042 = null;
    }

    @Override
    public float method_6846() {
        return 1.0f;
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7042;
    }

    public PhaseType<HoverPhase> getType() {
        return PhaseType.HOVER;
    }
}

