/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment
extends Enchantment {
    public final Type type;

    public ProtectionEnchantment(Enchantment.Weight weight, Type type, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.ARMOR, equipmentSlots);
        this.type = type;
        if (type == Type.FALL) {
            this.type = EnchantmentTarget.ARMOR_FEET;
        }
    }

    @Override
    public int getMinimumPower(int i) {
        return this.type.getBasePower() + (i - 1) * this.type.getPowerPerLevel();
    }

    @Override
    public int getMaximumPower(int i) {
        return this.getMinimumPower(i) + this.type.getPowerPerLevel();
    }

    @Override
    public int getMaximumLevel() {
        return 4;
    }

    @Override
    public int getProtectionAmount(int i, DamageSource damageSource) {
        if (damageSource.doesDamageToCreative()) {
            return 0;
        }
        if (this.type == Type.ALL) {
            return i;
        }
        if (this.type == Type.FIRE && damageSource.isFire()) {
            return i * 2;
        }
        if (this.type == Type.FALL && damageSource == DamageSource.FALL) {
            return i * 3;
        }
        if (this.type == Type.EXPLOSION && damageSource.isExplosive()) {
            return i * 2;
        }
        if (this.type == Type.PROJECTILE && damageSource.isProjectile()) {
            return i * 2;
        }
        return 0;
    }

    @Override
    public boolean differs(Enchantment enchantment) {
        if (enchantment instanceof ProtectionEnchantment) {
            ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment)enchantment;
            if (this.type == protectionEnchantment.type) {
                return false;
            }
            return this.type == Type.FALL || protectionEnchantment.type == Type.FALL;
        }
        return super.differs(enchantment);
    }

    public static int transformFireDuration(LivingEntity livingEntity, int i) {
        int j = EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_PROTECTION, livingEntity);
        if (j > 0) {
            i -= MathHelper.floor((float)i * ((float)j * 0.15f));
        }
        return i;
    }

    public static double transformExplosionKnockback(LivingEntity livingEntity, double d) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.BLAST_PROTECTION, livingEntity);
        if (i > 0) {
            d -= (double)MathHelper.floor(d * (double)((float)i * 0.15f));
        }
        return d;
    }

    public static enum Type {
        ALL("all", 1, 11),
        FIRE("fire", 10, 8),
        FALL("fall", 5, 6),
        EXPLOSION("explosion", 5, 8),
        PROJECTILE("projectile", 3, 6);

        private final String name;
        private final int basePower;
        private final int powerPerLevel;

        private Type(String string2, int j, int k) {
            this.name = string2;
            this.basePower = j;
            this.powerPerLevel = k;
        }

        public int getBasePower() {
            return this.basePower;
        }

        public int getPowerPerLevel() {
            return this.powerPerLevel;
        }
    }
}

