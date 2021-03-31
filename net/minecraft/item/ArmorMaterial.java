/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public interface ArmorMaterial {
    public int getDurability(EquipmentSlot var1);

    public int getProtectionAmount(EquipmentSlot var1);

    public int getEnchantability();

    public SoundEvent getEquipSound();

    public Ingredient getRepairIngredient();

    public String getName();

    public float getToughness();

    public float getKnockbackResistance();
}

