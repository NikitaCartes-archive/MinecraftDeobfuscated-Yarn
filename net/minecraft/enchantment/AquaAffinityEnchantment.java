/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class AquaAffinityEnchantment
extends Enchantment {
    public AquaAffinityEnchantment(Enchantment.Weight weight, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.ARMOR_HEAD, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int i) {
        return 1;
    }

    @Override
    public int method_20742(int i) {
        return this.getMinimumPower(i) + 40;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }
}

