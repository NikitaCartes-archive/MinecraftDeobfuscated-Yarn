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
    public InfinityEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.BOW, slotTypes);
    }

    @Override
    public int getMinimumPower(int level) {
        return 20;
    }

    @Override
    public int getMaximumPower(int level) {
        return 50;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        if (other instanceof MendingEnchantment) {
            return false;
        }
        return super.canAccept(other);
    }
}

