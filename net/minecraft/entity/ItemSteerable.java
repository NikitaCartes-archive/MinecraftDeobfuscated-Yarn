/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface ItemSteerable {
    public boolean consumeOnAStickItem();

    public void setMovementInput(Vec3d var1);

    public float getSaddledSpeed();

    default public boolean travel(MobEntity entity, SaddledComponent saddledEntity, Vec3d movementInput) {
        if (!entity.isAlive()) {
            return false;
        }
        Entity entity2 = entity.getFirstPassenger();
        if (!(entity.hasPassengers() && entity.canBeControlledByRider() && entity2 instanceof PlayerEntity)) {
            entity.stepHeight = 0.5f;
            entity.airStrafingSpeed = 0.02f;
            this.setMovementInput(movementInput);
            return false;
        }
        entity.setYaw(entity2.getYaw());
        entity.prevYaw = entity.getYaw();
        entity.setPitch(entity2.getPitch() * 0.5f);
        entity.setRotation(entity.getYaw(), entity.getPitch());
        entity.bodyYaw = entity.getYaw();
        entity.headYaw = entity.getYaw();
        entity.stepHeight = 1.0f;
        entity.airStrafingSpeed = entity.getMovementSpeed() * 0.1f;
        if (saddledEntity.boosted && saddledEntity.boostedTime++ > saddledEntity.currentBoostTime) {
            saddledEntity.boosted = false;
        }
        if (entity.isLogicalSideForUpdatingMovement()) {
            float f = this.getSaddledSpeed();
            if (saddledEntity.boosted) {
                f += f * 1.15f * MathHelper.sin((float)saddledEntity.boostedTime / (float)saddledEntity.currentBoostTime * (float)Math.PI);
            }
            entity.setMovementSpeed(f);
            this.setMovementInput(new Vec3d(0.0, 0.0, 1.0));
            entity.bodyTrackingIncrements = 0;
        } else {
            entity.updateLimbs(entity, false);
            entity.setVelocity(Vec3d.ZERO);
        }
        entity.tryCheckBlockCollision();
        return true;
    }
}

