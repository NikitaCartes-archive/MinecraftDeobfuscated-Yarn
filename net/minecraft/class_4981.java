/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_4980;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface class_4981 {
    public boolean method_6577();

    public void setSaddled(boolean var1);

    public boolean isSaddled();

    public void method_26315(Vec3d var1);

    public float method_26316();

    default public boolean method_26313(MobEntity mobEntity, class_4980 arg, Vec3d vec3d) {
        Entity entity;
        if (!mobEntity.isAlive()) {
            return false;
        }
        Entity entity2 = entity = mobEntity.getPassengerList().isEmpty() ? null : mobEntity.getPassengerList().get(0);
        if (!(mobEntity.hasPassengers() && mobEntity.canBeControlledByRider() && entity instanceof PlayerEntity)) {
            mobEntity.stepHeight = 0.5f;
            mobEntity.flyingSpeed = 0.02f;
            this.method_26315(vec3d);
            return false;
        }
        mobEntity.prevYaw = mobEntity.yaw = entity.yaw;
        mobEntity.pitch = entity.pitch * 0.5f;
        mobEntity.setRotation(mobEntity.yaw, mobEntity.pitch);
        mobEntity.bodyYaw = mobEntity.yaw;
        mobEntity.headYaw = mobEntity.yaw;
        mobEntity.stepHeight = 1.0f;
        mobEntity.flyingSpeed = mobEntity.getMovementSpeed() * 0.1f;
        if (arg.field_23215 && arg.field_23216++ > arg.field_23217) {
            arg.field_23215 = false;
        }
        if (mobEntity.isLogicalSideForUpdatingMovement()) {
            float f = this.method_26316();
            if (arg.field_23215) {
                f += f * 1.15f * MathHelper.sin((float)arg.field_23216 / (float)arg.field_23217 * (float)Math.PI);
            }
            mobEntity.setMovementSpeed(f);
            this.method_26315(new Vec3d(0.0, 0.0, 1.0));
            mobEntity.bodyTrackingIncrements = 0;
        } else {
            mobEntity.setVelocity(Vec3d.ZERO);
        }
        return true;
    }

    default public boolean method_26314(MobEntity mobEntity, PlayerEntity playerEntity, Hand hand, boolean bl) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (itemStack.getItem() == Items.NAME_TAG) {
            itemStack.useOnEntity(playerEntity, mobEntity, hand);
            return true;
        }
        if (this.isSaddled() && !mobEntity.hasPassengers() && (bl || !mobEntity.isBaby())) {
            if (!mobEntity.world.isClient) {
                playerEntity.startRiding(mobEntity);
            }
            return true;
        }
        return itemStack.getItem() == Items.SADDLE && itemStack.useOnEntity(playerEntity, mobEntity, hand);
    }
}

