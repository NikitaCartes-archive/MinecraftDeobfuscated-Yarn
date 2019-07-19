/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class DepthStriderEnchantment
extends Enchantment {
    public DepthStriderEnchantment(Enchantment.Weight weight, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.ARMOR_FEET, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int i) {
        return i * 10;
    }

    @Override
    public int getMaximumPower(int i) {
        return this.getMinimumPower(i) + 15;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }

    @Override
    public boolean differs(Enchantment enchantment) {
        return super.differs(enchantment) && enchantment != Enchantments.FROST_WALKER;
    }
}

