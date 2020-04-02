/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.Attributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;

public interface Hoglin {
    @Environment(value=EnvType.CLIENT)
    public int getMovementCooldownTicks();

    public static boolean tryAttack(LivingEntity livingEntity, LivingEntity livingEntity2) {
        float f = (float)livingEntity.method_26825(Attributes.GENERIC_ATTACK_DAMAGE);
        float g = !livingEntity.isBaby() && (int)f > 0 ? f / 2.0f + (float)livingEntity.world.random.nextInt((int)f) : f;
        boolean bl = livingEntity2.damage(DamageSource.mob(livingEntity), g);
        if (bl) {
            livingEntity.dealDamage(livingEntity, livingEntity2);
            if (!livingEntity.isBaby()) {
                Hoglin.knockback(livingEntity, livingEntity2);
            }
        }
        return bl;
    }

    public static void knockback(LivingEntity livingEntity, LivingEntity livingEntity2) {
        double e;
        double d = livingEntity.method_26825(Attributes.GENERIC_ATTACK_KNOCKBACK);
        double f = d - (e = livingEntity2.method_26825(Attributes.GENERIC_KNOCKBACK_RESISTANCE));
        if (f <= 0.0) {
            return;
        }
        double g = livingEntity2.getX() - livingEntity.getX();
        double h = livingEntity2.getZ() - livingEntity.getZ();
        float i = livingEntity.world.random.nextInt(21) - 10;
        double j = f * (double)(livingEntity.world.random.nextFloat() * 0.5f + 0.2f);
        Vec3d vec3d = new Vec3d(g, 0.0, h).normalize().multiply(j).rotateY(i);
        double k = f * (double)livingEntity.world.random.nextFloat() * 0.5;
        livingEntity2.addVelocity(vec3d.x, k, vec3d.z);
        livingEntity2.velocityModified = true;
    }
}

