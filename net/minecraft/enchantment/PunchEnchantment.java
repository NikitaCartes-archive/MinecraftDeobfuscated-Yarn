/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class PunchEnchantment
extends Enchantment {
    public PunchEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.BOW, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 12 + (level - 1) * 20;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 25;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}

