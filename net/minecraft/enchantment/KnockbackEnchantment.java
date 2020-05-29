/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class KnockbackEnchantment
extends Enchantment {
    protected KnockbackEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slot) {
        super(weight, EnchantmentTarget.WEAPON, slot);
    }

    @Override
    public int getMinPower(int level) {
        return 5 + 20 * (level - 1);
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}

