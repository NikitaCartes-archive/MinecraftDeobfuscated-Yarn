/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class StrafePlayerPhase
extends AbstractPhase {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_30440 = 5;
    private int seenTargetTimes;
    private Path path;
    private Vec3d pathTarget;
    private LivingEntity target;
    private boolean shouldFindNewPath;

    public StrafePlayerPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Override
    public void serverTick() {
        double h;
        double e;
        double d;
        if (this.target == null) {
            LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
            return;
        }
        if (this.path != null && this.path.isFinished()) {
            d = this.target.getX();
            e = this.target.getZ();
            double f = d - this.dragon.getX();
            double g = e - this.dragon.getZ();
            h = MathHelper.sqrt(f * f + g * g);
            double i = Math.min((double)0.4f + h / 80.0 - 1.0, 10.0);
            this.pathTarget = new Vec3d(d, this.target.getY() + i, e);
        }
        double d2 = d = this.pathTarget == null ? 0.0 : this.pathTarget.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (d < 100.0 || d > 22500.0) {
            this.updatePath();
        }
        e = 64.0;
        if (this.target.squaredDistanceTo(this.dragon) < 4096.0) {
            if (this.dragon.canSee(this.target)) {
                ++this.seenTargetTimes;
                Vec3d vec3d = new Vec3d(this.target.getX() - this.dragon.getX(), 0.0, this.target.getZ() - this.dragon.getZ()).normalize();
                Vec3d vec3d2 = new Vec3d(MathHelper.sin(this.dragon.yaw * ((float)Math.PI / 180)), 0.0, -MathHelper.cos(this.dragon.yaw * ((float)Math.PI / 180))).normalize();
                float j = (float)vec3d2.dotProduct(vec3d);
                float k = (float)(Math.acos(j) * 57.2957763671875);
                k += 0.5f;
                if (this.seenTargetTimes >= 5 && k >= 0.0f && k < 10.0f) {
                    h = 1.0;
                    Vec3d vec3d3 = this.dragon.getRotationVec(1.0f);
                    double l = this.dragon.partHead.getX() - vec3d3.x * 1.0;
                    double m = this.dragon.partHead.getBodyY(0.5) + 0.5;
                    double n = this.dragon.partHead.getZ() - vec3d3.z * 1.0;
                    double o = this.target.getX() - l;
                    double p = this.target.getBodyY(0.5) - m;
                    double q = this.target.getZ() - n;
                    if (!this.dragon.isSilent()) {
                        this.dragon.world.syncWorldEvent(null, WorldEvents.ENDER_DRAGON_SHOOTS, this.dragon.getBlockPos(), 0);
                    }
                    DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(this.dragon.world, this.dragon, o, p, q);
                    dragonFireballEntity.refreshPositionAndAngles(l, m, n, 0.0f, 0.0f);
                    this.dragon.world.spawnEntity(dragonFireballEntity);
                    this.seenTargetTimes = 0;
                    if (this.path != null) {
                        while (!this.path.isFinished()) {
                            this.path.next();
                        }
                    }
                    this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
                }
            } else if (this.seenTargetTimes > 0) {
                --this.seenTargetTimes;
            }
        } else if (this.seenTargetTimes > 0) {
            --this.seenTargetTimes;
        }
    }

    private void updatePath() {
        if (this.path == null || this.path.isFinished()) {
            int i;
            int j = i = this.dragon.getNearestPathNodeIndex();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.shouldFindNewPath = !this.shouldFindNewPath;
                j += 6;
            }
            j = this.shouldFindNewPath ? ++j : --j;
            if (this.dragon.getFight() == null || this.dragon.getFight().getAliveEndCrystals() <= 0) {
                j -= 12;
                j &= 7;
                j += 12;
            } else if ((j %= 12) < 0) {
                j += 12;
            }
            this.path = this.dragon.findPath(i, j, null);
            if (this.path != null) {
                this.path.next();
            }
        }
        this.followPath();
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

    @Override
    public void beginPhase() {
        this.seenTargetTimes = 0;
        this.pathTarget = null;
        this.path = null;
        this.target = null;
    }

    public void setTargetEntity(LivingEntity targetEntity) {
        this.target = targetEntity;
        int i = this.dragon.getNearestPathNodeIndex();
        int j = this.dragon.getNearestPathNodeIndex(this.target.getX(), this.target.getY(), this.target.getZ());
        int k = this.target.getBlockX();
        int l = this.target.getBlockZ();
        double d = (double)k - this.dragon.getX();
        double e = (double)l - this.dragon.getZ();
        double f = MathHelper.sqrt(d * d + e * e);
        double g = Math.min((double)0.4f + f / 80.0 - 1.0, 10.0);
        int m = MathHelper.floor(this.target.getY() + g);
        PathNode pathNode = new PathNode(k, m, l);
        this.path = this.dragon.findPath(i, j, pathNode);
        if (this.path != null) {
            this.path.next();
            this.followPath();
        }
    }

    @Override
    @Nullable
    public Vec3d getPathTarget() {
        return this.pathTarget;
    }

    public PhaseType<StrafePlayerPhase> getType() {
        return PhaseType.STRAFE_PLAYER;
    }
}

