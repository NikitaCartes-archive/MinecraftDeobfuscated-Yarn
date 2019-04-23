/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class PiercingEnchantment
extends Enchantment {
    public PiercingEnchantment(Enchantment.Weight weight, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.CROSSBOW, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int i) {
        return 1 + (i - 1) * 10;
    }

    @Override
    public int getMaximumLevel() {
        return 4;
    }

    @Override
    public boolean differs(Enchantment enchantment) {
        return super.differs(enchantment) && enchantment != Enchantments.MULTISHOT;
    }
}

