/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;

public class ImpalingEnchantment
extends Enchantment {
    public ImpalingEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.TRIDENT, slotTypes);
    }

    @Override
    public int getMinimumPower(int level) {
        return 1 + (level - 1) * 8;
    }

    @Override
    public int getMaximumPower(int level) {
        return this.getMinimumPower(level) + 20;
    }

    @Override
    public int getMaximumLevel() {
        return 5;
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        if (group == EntityGroup.AQUATIC) {
            return (float)level * 2.5f;
        }
        return 0.0f;
    }
}

