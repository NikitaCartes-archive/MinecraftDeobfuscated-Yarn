/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class LoyaltyEnchantment
extends Enchantment {
    public LoyaltyEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.TRIDENT, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 5 + level * 7;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }
}

