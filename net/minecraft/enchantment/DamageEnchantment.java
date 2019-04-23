/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class DamageEnchantment
extends Enchantment {
    private static final String[] typeNames = new String[]{"all", "undead", "arthropods"};
    private static final int[] field_9063 = new int[]{1, 5, 5};
    private static final int[] field_9066 = new int[]{11, 8, 8};
    private static final int[] field_9064 = new int[]{20, 20, 20};
    public final int typeIndex;

    public DamageEnchantment(Enchantment.Weight weight, int i, EquipmentSlot ... equipmentSlots) {
        super(weight, EnchantmentTarget.WEAPON, equipmentSlots);
        this.typeIndex = i;
    }

    @Override
    public int getMinimumPower(int i) {
        return field_9063[this.typeIndex] + (i - 1) * field_9066[this.typeIndex];
    }

    @Override
    public int getMaximumLevel() {
        return 5;
    }

    @Override
    public float getAttackDamage(int i, EntityGroup entityGroup) {
        if (this.typeIndex == 0) {
            return 1.0f + (float)Math.max(0, i - 1) * 0.5f;
        }
        if (this.typeIndex == 1 && entityGroup == EntityGroup.UNDEAD) {
            return (float)i * 2.5f;
        }
        if (this.typeIndex == 2 && entityGroup == EntityGroup.ARTHROPOD) {
            return (float)i * 2.5f;
        }
        return 0.0f;
    }

    @Override
    public boolean differs(Enchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment);
    }

    @Override
    public boolean isAcceptableItem(ItemStack itemStack) {
        if (itemStack.getItem() instanceof AxeItem) {
            return true;
        }
        return super.isAcceptableItem(itemStack);
    }

    @Override
    public void onTargetDamaged(LivingEntity livingEntity, Entity entity, int i) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            if (this.typeIndex == 2 && livingEntity2.getGroup() == EntityGroup.ARTHROPOD) {
                int j = 20 + livingEntity.getRand().nextInt(10 * i);
                livingEntity2.addPotionEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, j, 3));
            }
        }
    }
}

