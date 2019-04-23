/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class MobEntityWithAi
extends MobEntity {
    protected MobEntityWithAi(EntityType<? extends MobEntityWithAi> entityType, World world) {
        super((EntityType<? extends MobEntity>)entityType, world);
    }

    public float getPathfindingFavor(BlockPos blockPos) {
        return this.getPathfindingFavor(blockPos, this.world);
    }

    public float getPathfindingFavor(BlockPos blockPos, ViewableWorld viewableWorld) {
        return 0.0f;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
        if (!super.canSpawn(iWorld, spawnType)) return false;
        BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
        if (!(this.getPathfindingFavor(blockPos, iWorld) >= 0.0f)) return false;
        return true;
    }

    public boolean isNavigating() {
        return !this.getNavigation().isIdle();
    }

    @Override
    protected void updateLeash() {
        super.updateLeash();
        Entity entity = this.getHoldingEntity();
        if (entity != null && entity.world == this.world) {
            this.setWalkTarget(new BlockPos((int)entity.x, (int)entity.y, (int)entity.z), 5);
            float f = this.distanceTo(entity);
            if (this instanceof TameableEntity && ((TameableEntity)this).isSitting()) {
                if (f > 10.0f) {
                    this.detachLeash(true, true);
                }
                return;
            }
            this.updateForLeashLength(f);
            if (f > 10.0f) {
                this.detachLeash(true, true);
                this.goalSelector.disableControl(Goal.Control.MOVE);
            } else if (f > 6.0f) {
                double d = (entity.x - this.x) / (double)f;
                double e = (entity.y - this.y) / (double)f;
                double g = (entity.z - this.z) / (double)f;
                this.setVelocity(this.getVelocity().add(Math.copySign(d * d * 0.4, d), Math.copySign(e * e * 0.4, e), Math.copySign(g * g * 0.4, g)));
            } else {
                this.goalSelector.enableControl(Goal.Control.MOVE);
                float h = 2.0f;
                Vec3d vec3d = new Vec3d(entity.x - this.x, entity.y - this.y, entity.z - this.z).normalize().multiply(Math.max(f - 2.0f, 0.0f));
                this.getNavigation().startMovingTo(this.x + vec3d.x, this.y + vec3d.y, this.z + vec3d.z, this.getRunFromLeashSpeed());
            }
        }
    }

    protected double getRunFromLeashSpeed() {
        return 1.0;
    }

    protected void updateForLeashLength(float f) {
    }
}

