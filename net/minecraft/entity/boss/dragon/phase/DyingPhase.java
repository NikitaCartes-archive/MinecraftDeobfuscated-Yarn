/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.jetbrains.annotations.Nullable;

public class DyingPhase
extends AbstractPhase {
    private Vec3d target;
    private int ticks;

    public DyingPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Override
    public void clientTick() {
        if (this.ticks++ % 10 == 0) {
            float f = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            float g = (this.dragon.getRandom().nextFloat() - 0.5f) * 4.0f;
            float h = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            this.dragon.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.dragon.getX() + (double)f, this.dragon.getY() + 2.0 + (double)g, this.dragon.getZ() + (double)h, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void serverTick() {
        double d;
        ++this.ticks;
        if (this.target == null) {
            BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN);
            this.target = Vec3d.ofBottomCenter(blockPos);
        }
        if ((d = this.target.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ())) < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.dragon.setHealth(0.0f);
        } else {
            this.dragon.setHealth(1.0f);
        }
    }

    @Override
    public void beginPhase() {
        this.target = null;
        this.ticks = 0;
    }

    @Override
    public float getMaxYAcceleration() {
        return 3.0f;
    }

    @Override
    @Nullable
    public Vec3d getPathTarget() {
        return this.target;
    }

    public PhaseType<DyingPhase> getType() {
        return PhaseType.DYING;
    }
}

