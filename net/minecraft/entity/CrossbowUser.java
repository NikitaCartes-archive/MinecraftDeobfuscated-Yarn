/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public interface CrossbowUser
extends RangedAttackMob {
    public void setCharging(boolean var1);

    public void shoot(LivingEntity var1, ItemStack var2, Projectile var3, float var4);

    @Nullable
    public LivingEntity getTarget();

    public void method_24651();

    default public void method_24654(LivingEntity livingEntity, float f) {
        Hand hand = ProjectileUtil.getHandPossiblyHolding(livingEntity, Items.CROSSBOW);
        ItemStack itemStack = livingEntity.getStackInHand(hand);
        if (livingEntity.method_24518(Items.CROSSBOW)) {
            CrossbowItem.shootAll(livingEntity.world, livingEntity, hand, itemStack, f, 14 - livingEntity.world.getDifficulty().getId() * 4);
        }
        this.method_24651();
    }

    default public void method_24652(LivingEntity livingEntity, LivingEntity livingEntity2, Projectile projectile, float f, float g) {
        Entity entity = (Entity)((Object)projectile);
        double d = livingEntity2.getX() - livingEntity.getX();
        double e = livingEntity2.getZ() - livingEntity.getZ();
        double h = MathHelper.sqrt(d * d + e * e);
        double i = livingEntity2.getBodyY(0.3333333333333333) - entity.getY() + h * (double)0.2f;
        Vector3f vector3f = this.method_24653(livingEntity, new Vec3d(d, i, e), f);
        projectile.setVelocity(vector3f.getX(), vector3f.getY(), vector3f.getZ(), g, 14 - livingEntity.world.getDifficulty().getId() * 4);
        livingEntity.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0f, 1.0f / (livingEntity.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    default public Vector3f method_24653(LivingEntity livingEntity, Vec3d vec3d, float f) {
        Vec3d vec3d2 = vec3d.normalize();
        Vec3d vec3d3 = vec3d2.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        if (vec3d3.lengthSquared() <= 1.0E-7) {
            vec3d3 = vec3d2.crossProduct(livingEntity.getOppositeRotationVector(1.0f));
        }
        Quaternion quaternion = new Quaternion(new Vector3f(vec3d3), 90.0f, true);
        Vector3f vector3f = new Vector3f(vec3d2);
        vector3f.rotate(quaternion);
        Quaternion quaternion2 = new Quaternion(vector3f, f, true);
        Vector3f vector3f2 = new Vector3f(vec3d2);
        vector3f2.rotate(quaternion2);
        return vector3f2;
    }
}

