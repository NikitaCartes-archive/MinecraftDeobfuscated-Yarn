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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

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
        ProjectileEntity entity2 = projectile;
        double d = target.getX() - entity.getX();
        double e = target.getZ() - entity.getZ();
        double f = MathHelper.sqrt(d * d + e * e);
        double g = target.getBodyY(0.3333333333333333) - entity2.getY() + f * (double)0.2f;
        Vec3f vec3f = this.getProjectileLaunchVelocity(entity, new Vec3d(d, g, e), multishotSpray);
        projectile.setVelocity(vec3f.getX(), vec3f.getY(), vec3f.getZ(), speed, 14 - entity.world.getDifficulty().getId() * 4);
        entity.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0f, 1.0f / (entity.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    default public Vec3f getProjectileLaunchVelocity(LivingEntity entity, Vec3d positionDelta, float multishotSpray) {
        Vec3d vec3d = positionDelta.normalize();
        Vec3d vec3d2 = vec3d.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        if (vec3d2.lengthSquared() <= 1.0E-7) {
            vec3d2 = vec3d.crossProduct(entity.getOppositeRotationVector(1.0f));
        }
        Quaternion quaternion = new Quaternion(new Vec3f(vec3d2), 90.0f, true);
        Vec3f vec3f = new Vec3f(vec3d);
        vec3f.rotate(quaternion);
        Quaternion quaternion2 = new Quaternion(vec3f, multishotSpray, true);
        Vec3f vec3f2 = new Vec3f(vec3d);
        vec3f2.rotate(quaternion2);
        return vec3f2;
    }
}

