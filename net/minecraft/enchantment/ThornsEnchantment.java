/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class ThornsEnchantment
extends Enchantment {
    public ThornsEnchantment(Enchantment.Weight weight, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.ARMOR_CHEST, equipmentSlots);
    }

    @Override
    public int getMinimumPower(int i) {
        return 10 + 20 * (i - 1);
    }

    @Override
    public int getMaximumPower(int i) {
        return super.getMinimumPower(i) + 50;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }

    @Override
    public boolean isAcceptableItem(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ArmorItem) {
            return true;
        }
        return super.isAcceptableItem(itemStack);
    }

    @Override
    public void onUserDamaged(LivingEntity livingEntity2, Entity entity, int i) {
        Random random = livingEntity2.getRand();
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomEnchantedEquipment(Enchantments.THORNS, livingEntity2);
        if (ThornsEnchantment.shouldDamageAttacker(i, random)) {
            if (entity != null) {
                entity.damage(DamageSource.thorns(livingEntity2), ThornsEnchantment.getDamageAmount(i, random));
            }
            if (entry != null) {
                entry.getValue().damage(3, livingEntity2, livingEntity -> livingEntity.sendEquipmentBreakStatus((EquipmentSlot)((Object)((Object)entry.getKey()))));
            }
        } else if (entry != null) {
            entry.getValue().damage(1, livingEntity2, livingEntity -> livingEntity.sendEquipmentBreakStatus((EquipmentSlot)((Object)((Object)entry.getKey()))));
        }
    }

    public static boolean shouldDamageAttacker(int i, Random random) {
        if (i <= 0) {
            return false;
        }
        return random.nextFloat() < 0.15f * (float)i;
    }

    public static int getDamageAmount(int i, Random random) {
        if (i > 10) {
            return i - 10;
        }
        return 1 + random.nextInt(4);
    }
}

