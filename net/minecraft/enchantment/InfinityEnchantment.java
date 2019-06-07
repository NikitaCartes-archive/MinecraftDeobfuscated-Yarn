/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.entity.EquipmentSlot;

public class InfinityEnchantment
extends Enchantment {
    public InfinityEnchantment(Enchantment.Weight weight, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.BOW, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int i) {
        return 20;
    }

    @Override
    public int method_20742(int i) {
        return 50;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }

    @Override
    public boolean differs(Enchantment enchantment) {
        if (enchantment instanceof MendingEnchantment) {
            return false;
        }
        return super.differs(enchantment);
    }
}

