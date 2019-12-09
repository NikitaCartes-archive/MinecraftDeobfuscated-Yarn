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

    public ProtectionEnchantment(Enchantment.Weight weight, Type type, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.ARMOR, slotTypes);
        this.type = type;
        if (type == Type.FALL) {
            this.type = EnchantmentTarget.ARMOR_FEET;
        }
    }

    @Override
    public int getMinimumPower(int level) {
        return this.type.getBasePower() + (level - 1) * this.type.getPowerPerLevel();
    }

    @Override
    public int getMaximumPower(int level) {
        return this.getMinimumPower(level) + this.type.getPowerPerLevel();
    }

    @Override
    public int getMaximumLevel() {
        return 4;
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        if (source.isOutOfWorld()) {
            return 0;
        }
        if (this.type == Type.ALL) {
            return level;
        }
        if (this.type == Type.FIRE && source.isFire()) {
            return level * 2;
        }
        if (this.type == Type.FALL && source == DamageSource.FALL) {
            return level * 3;
        }
        if (this.type == Type.EXPLOSION && source.isExplosive()) {
            return level * 2;
        }
        if (this.type == Type.PROJECTILE && source.isProjectile()) {
            return level * 2;
        }
        return 0;
    }

    @Override
    public boolean differs(Enchantment other) {
        if (other instanceof ProtectionEnchantment) {
            ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment)other;
            if (this.type == protectionEnchantment.type) {
                return false;
            }
            return this.type == Type.FALL || protectionEnchantment.type == Type.FALL;
        }
        return super.differs(other);
    }

    public static int transformFireDuration(LivingEntity entity, int duration) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_PROTECTION, entity);
        if (i > 0) {
            duration -= MathHelper.floor((float)duration * ((float)i * 0.15f));
        }
        return duration;
    }

    public static double transformExplosionKnockback(LivingEntity entity, double velocity) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.BLAST_PROTECTION, entity);
        if (i > 0) {
            velocity -= (double)MathHelper.floor(velocity * (double)((float)i * 0.15f));
        }
        return velocity;
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

        private Type(String name, int basePower, int powerPerLevel) {
            this.name = name;
            this.basePower = basePower;
            this.powerPerLevel = powerPerLevel;
        }

        public int getBasePower() {
            return this.basePower;
        }

        public int getPowerPerLevel() {
            return this.powerPerLevel;
        }
    }
}

