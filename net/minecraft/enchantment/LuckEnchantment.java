/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class LuckEnchantment
extends Enchantment {
    protected LuckEnchantment(Enchantment.Rarity rarity, EnchantmentTarget enchantmentTarget, EquipmentSlot ... equipmentSlots) {
        super(rarity, enchantmentTarget, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaximumPower(int level) {
        return super.getMinimumPower(level) + 50;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.SILK_TOUCH;
    }
}

