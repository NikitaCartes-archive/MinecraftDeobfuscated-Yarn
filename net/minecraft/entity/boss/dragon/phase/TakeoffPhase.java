/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.jetbrains.annotations.Nullable;

public class TakeoffPhase
extends AbstractPhase {
    private boolean shouldFindNewPath;
    private Path path;
    private Vec3d pathTarget;

    public TakeoffPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Override
    public void serverTick() {
        if (this.shouldFindNewPath || this.path == null) {
            this.shouldFindNewPath = false;
            this.updatePath();
        } else {
            BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            if (!blockPos.isWithinDistance(this.dragon.getPos(), 10.0)) {
                this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            }
        }
    }

    @Override
    public void beginPhase() {
        this.shouldFindNewPath = true;
        this.path = null;
        this.pathTarget = null;
    }

    private void updatePath() {
        int i = this.dragon.getNearestPathNodeIndex();
        Vec3d vec3d = this.dragon.getRotationVectorFromPhase(1.0f);
        int j = this.dragon.getNearestPathNodeIndex(-vec3d.x * 40.0, 105.0, -vec3d.z * 40.0);
        if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() <= 0) {
            j -= 12;
            j &= 7;
            j += 12;
        } else if ((j %= 12) < 0) {
            j += 12;
        }
        this.path = this.dragon.findPath(i, j, null);
        this.followPath();
    }

    private void followPath() {
        if (this.path != null) {
            this.path.next();
            if (!this.path.isFinished()) {
                double d;
                BlockPos vec3i = this.path.getCurrentNodePos();
                this.path.next();
                while ((d = (double)((float)vec3i.getY() + this.dragon.getRandom().nextFloat() * 20.0f)) < (double)vec3i.getY()) {
                }
                this.pathTarget = new Vec3d(vec3i.getX(), d, vec3i.getZ());
            }
        }
    }

    @Override
    @Nullable
    public Vec3d getPathTarget() {
        return this.pathTarget;
    }

    public PhaseType<TakeoffPhase> getType() {
        return PhaseType.TAKEOFF;
    }
}

