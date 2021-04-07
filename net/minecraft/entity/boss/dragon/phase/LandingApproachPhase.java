/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.jetbrains.annotations.Nullable;

public class LandingApproachPhase
extends AbstractPhase {
    private static final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(128.0);
    private Path path;
    private Vec3d pathTarget;

    public LandingApproachPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    public PhaseType<LandingApproachPhase> getType() {
        return PhaseType.LANDING_APPROACH;
    }

    @Override
    public void beginPhase() {
        this.path = null;
        this.pathTarget = null;
    }

    @Override
    public void serverTick() {
        double d;
        double d2 = d = this.pathTarget == null ? 0.0 : this.pathTarget.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.updatePath();
        }
    }

    @Override
    @Nullable
    public Vec3d getPathTarget() {
        return this.pathTarget;
    }

    private void updatePath() {
        if (this.path == null || this.path.isFinished()) {
            int j;
            int i = this.dragon.getNearestPathNodeIndex();
            BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            PlayerEntity playerEntity = this.dragon.world.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (playerEntity != null) {
                Vec3d vec3d = new Vec3d(playerEntity.getX(), 0.0, playerEntity.getZ()).normalize();
                j = this.dragon.getNearestPathNodeIndex(-vec3d.x * 40.0, 105.0, -vec3d.z * 40.0);
            } else {
                j = this.dragon.getNearestPathNodeIndex(40.0, blockPos.getY(), 0.0);
            }
            PathNode pathNode = new PathNode(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            this.path = this.dragon.findPath(i, j, pathNode);
            if (this.path != null) {
                this.path.next();
            }
        }
        this.followPath();
        if (this.path != null && this.path.isFinished()) {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING);
        }
    }

    private void followPath() {
        if (this.path != null && !this.path.isFinished()) {
            double f;
            BlockPos vec3i = this.path.getCurrentNodePos();
            this.path.next();
            double d = vec3i.getX();
            double e = vec3i.getZ();
            while ((f = (double)((float)vec3i.getY() + this.dragon.getRandom().nextFloat() * 20.0f)) < (double)vec3i.getY()) {
            }
            this.pathTarget = new Vec3d(d, f, e);
        }
    }
}

