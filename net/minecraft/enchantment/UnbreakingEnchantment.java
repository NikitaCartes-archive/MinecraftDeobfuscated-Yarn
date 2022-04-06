/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.AbstractRandom;

public class UnbreakingEnchantment
extends Enchantment {
    protected UnbreakingEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.BREAKABLE, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if (stack.isDamageable()) {
            return true;
        }
        return super.isAcceptableItem(stack);
    }

    public static boolean shouldPreventDamage(ItemStack item, int level, AbstractRandom random) {
        if (item.getItem() instanceof ArmorItem && random.nextFloat() < 0.6f) {
            return false;
        }
        return random.nextInt(level + 1) > 0;
    }
}

