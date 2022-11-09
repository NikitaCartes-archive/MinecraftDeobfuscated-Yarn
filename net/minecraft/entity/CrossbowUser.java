/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface CrossbowUser
extends RangedAttackMob {
    public void setCharging(boolean var1);

    public void shoot(LivingEntity var1, ItemStack var2, ProjectileEntity var3, float var4);

    @Nullable
    public LivingEntity getTarget();

    public void postShoot();

    default public void shoot(LivingEntity entity, float speed) {
        Hand hand = ProjectileUtil.getHandPossiblyHolding(entity, Items.CROSSBOW);
        ItemStack itemStack = entity.getStackInHand(hand);
        if (entity.isHolding(Items.CROSSBOW)) {
            CrossbowItem.shootAll(entity.world, entity, hand, itemStack, speed, 14 - entity.world.getDifficulty().getId() * 4);
        }
        this.postShoot();
    }

    default public void shoot(LivingEntity entity, LivingEntity target, ProjectileEntity projectile, float multishotSpray, float speed) {
        double d = target.getX() - entity.getX();
        double e = target.getZ() - entity.getZ();
        double f = Math.sqrt(d * d + e * e);
        double g = target.getBodyY(0.3333333333333333) - projectile.getY() + f * (double)0.2f;
        Vector3f vector3f = this.getProjectileLaunchVelocity(entity, new Vec3d(d, g, e), multishotSpray);
        projectile.setVelocity(vector3f.x(), vector3f.y(), vector3f.z(), speed, 14 - entity.world.getDifficulty().getId() * 4);
        entity.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0f, 1.0f / (entity.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    default public Vector3f getProjectileLaunchVelocity(LivingEntity entity, Vec3d positionDelta, float multishotSpray) {
        Vector3f vector3f = positionDelta.toVector3f().normalize();
        Vector3f vector3f2 = new Vector3f(vector3f).cross(new Vector3f(0.0f, 1.0f, 0.0f));
        if ((double)vector3f2.lengthSquared() <= 1.0E-7) {
            Vec3d vec3d = entity.getOppositeRotationVector(1.0f);
            vector3f2 = new Vector3f(vector3f).cross(vec3d.toVector3f());
        }
        Vector3f vector3f3 = new Vector3f(vector3f).rotateAxis(1.5707964f, vector3f2.x, vector3f2.y, vector3f2.z);
        return new Vector3f(vector3f).rotateAxis(multishotSpray * ((float)Math.PI / 180), vector3f3.x, vector3f3.y, vector3f3.z);
    }
}

