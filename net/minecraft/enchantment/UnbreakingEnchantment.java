/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class UnbreakingEnchantment
extends Enchantment {
    protected UnbreakingEnchantment(Enchantment.Weight weight, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.BREAKABLE, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int i) {
        return 5 + (i - 1) * 8;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }

    @Override
    public boolean isAcceptableItem(ItemStack itemStack) {
        if (itemStack.isDamageable()) {
            return true;
        }
        return super.isAcceptableItem(itemStack);
    }

    public static boolean shouldPreventDamage(ItemStack itemStack, int i, Random random) {
        if (itemStack.getItem() instanceof ArmorItem && random.nextFloat() < 0.6f) {
            return false;
        }
        return random.nextInt(i + 1) > 0;
    }
}

