/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class FlyingEntity
extends MobEntity {
    protected FlyingEntity(EntityType<? extends FlyingEntity> entityType, World world) {
        super((EntityType<? extends MobEntity>)entityType, world);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isTouchingWater()) {
            this.updateVelocity(0.02f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.8f));
        } else if (this.isInLava()) {
            this.updateVelocity(0.02f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.5));
        } else {
            float f = 0.91f;
            if (this.onGround) {
                f = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.91f;
            }
            float g = 0.16277137f / (f * f * f);
            f = 0.91f;
            if (this.onGround) {
                f = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.91f;
            }
            this.updateVelocity(this.onGround ? 0.1f * g : 0.02f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(f));
        }
        this.updateLimbs(this, false);
    }

    @Override
    public boolean isClimbing() {
        return false;
    }
}

