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
    public PiercingEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.CROSSBOW, slotTypes);
    }

    @Override
    public int getMinimumPower(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaximumPower(int level) {
        return 50;
    }

    @Override
    public int getMaximumLevel() {
        return 4;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.MULTISHOT;
    }
}

