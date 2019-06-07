/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class PunchEnchantment
extends Enchantment {
    public PunchEnchantment(Enchantment.Weight weight, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.BOW, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int i) {
        return 12 + (i - 1) * 20;
    }

    @Override
    public int method_20742(int i) {
        return this.getMinimumPower(i) + 25;
    }

    @Override
    public int getMaximumLevel() {
        return 2;
    }
}

