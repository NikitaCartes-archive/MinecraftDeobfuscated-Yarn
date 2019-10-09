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
    private Path field_7047;
    private Vec3d field_7048;

    public LandingApproachPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    public PhaseType<LandingApproachPhase> getType() {
        return PhaseType.LANDING_APPROACH;
    }

    @Override
    public void beginPhase() {
        this.field_7047 = null;
        this.field_7048 = null;
    }

    @Override
    public void serverTick() {
        double d;
        double d2 = d = this.field_7048 == null ? 0.0 : this.field_7048.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.method_6844();
        }
    }

    @Override
    @Nullable
    public Vec3d getTarget() {
        return this.field_7048;
    }

    private void method_6844() {
        if (this.field_7047 == null || this.field_7047.isFinished()) {
            int j;
            int i = this.dragon.method_6818();
            BlockPos blockPos = this.dragon.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
            PlayerEntity playerEntity = this.dragon.world.getClosestPlayer(PLAYERS_IN_RANGE_PREDICATE, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (playerEntity != null) {
                Vec3d vec3d = new Vec3d(playerEntity.getX(), 0.0, playerEntity.getZ()).normalize();
                j = this.dragon.method_6822(-vec3d.x * 40.0, 105.0, -vec3d.z * 40.0);
            } else {
                j = this.dragon.method_6822(40.0, blockPos.getY(), 0.0);
            }
            PathNode pathNode = new PathNode(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            this.field_7047 = this.dragon.method_6833(i, j, pathNode);
            if (this.field_7047 != null) {
                this.field_7047.next();
            }
        }
        this.method_6845();
        if (this.field_7047 != null && this.field_7047.isFinished()) {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING);
        }
    }

    private void method_6845() {
        if (this.field_7047 != null && !this.field_7047.isFinished()) {
            double f;
            Vec3d vec3d = this.field_7047.getCurrentPosition();
            this.field_7047.next();
            double d = vec3d.x;
            double e = vec3d.z;
            while ((f = vec3d.y + (double)(this.dragon.getRandom().nextFloat() * 20.0f)) < vec3d.y) {
            }
            this.field_7048 = new Vec3d(d, f, e);
        }
    }
}

