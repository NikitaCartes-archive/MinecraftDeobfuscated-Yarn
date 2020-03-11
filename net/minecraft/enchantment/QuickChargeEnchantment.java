/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class QuickChargeEnchantment
extends Enchantment {
    public QuickChargeEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slot) {
        super(weight, EnchantmentTarget.CROSSBOW, slot);
    }

    @Override
    public int getMinimumPower(int level) {
        return 12 + (level - 1) * 20;
    }

    @Override
    public int getMaximumPower(int level) {
        return 50;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }
}

