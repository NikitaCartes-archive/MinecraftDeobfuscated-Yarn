/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class AquaAffinityEnchantment
extends Enchantment {
    public AquaAffinityEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.ARMOR_HEAD, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 40;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}

