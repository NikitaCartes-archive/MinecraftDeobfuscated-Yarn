/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import java.util.Map;
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
import net.minecraft.util.math.random.AbstractRandom;

public class ThornsEnchantment
extends Enchantment {
    private static final float ATTACK_CHANCE_PER_LEVEL = 0.15f;

    public ThornsEnchantment(Enchantment.Rarity weight, EquipmentSlot ... slotTypes) {
        super(weight, EnchantmentTarget.ARMOR_CHEST, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 10 + 20 * (level - 1);
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
        if (stack.getItem() instanceof ArmorItem) {
            return true;
        }
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
        AbstractRandom abstractRandom = user.getRandom();
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.THORNS, user);
        if (ThornsEnchantment.shouldDamageAttacker(level, abstractRandom)) {
            if (attacker != null) {
                attacker.damage(DamageSource.thorns(user), ThornsEnchantment.getDamageAmount(level, abstractRandom));
            }
            if (entry != null) {
                entry.getValue().damage(2, user, entity -> entity.sendEquipmentBreakStatus((EquipmentSlot)((Object)((Object)entry.getKey()))));
            }
        }
    }

    public static boolean shouldDamageAttacker(int level, AbstractRandom random) {
        if (level <= 0) {
            return false;
        }
        return random.nextFloat() < 0.15f * (float)level;
    }

    public static int getDamageAmount(int level, AbstractRandom random) {
        if (level > 10) {
            return level - 10;
        }
        return 1 + random.nextInt(4);
    }
}

