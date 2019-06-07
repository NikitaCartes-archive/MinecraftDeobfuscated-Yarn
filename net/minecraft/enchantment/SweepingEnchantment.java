/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class SweepingEnchantment
extends Enchantment {
    public SweepingEnchantment(Enchantment.Weight weight, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.WEAPON, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int i) {
        return 5 + (i - 1) * 9;
    }

    @Override
    public int method_20742(int i) {
        return this.getMinimumPower(i) + 15;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }

    public static float getMultiplier(int i) {
        return 1.0f - 1.0f / (float)(i + 1);
    }
}

