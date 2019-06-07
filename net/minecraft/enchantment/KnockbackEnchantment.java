/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class KnockbackEnchantment
extends Enchantment {
    protected KnockbackEnchantment(Enchantment.Weight weight, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.WEAPON, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int i) {
        return 5 + 20 * (i - 1);
    }

    @Override
    public int method_20742(int i) {
        return super.getMinimumPower(i) + 50;
    }

    @Override
    public int getMaximumLevel() {
        return 2;
    }
}

