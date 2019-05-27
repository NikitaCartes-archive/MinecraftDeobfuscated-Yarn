/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.World;

public class SuspiciousStewItem
extends Item {
    public SuspiciousStewItem(Item.Settings settings) {
        super(settings);
    }

    public static void addEffectToStew(ItemStack itemStack, StatusEffect statusEffect, int i) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        ListTag listTag = compoundTag.getList("Effects", 9);
        CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putByte("EffectId", (byte)StatusEffect.getRawId(statusEffect));
        compoundTag2.putInt("EffectDuration", i);
        listTag.add(compoundTag2);
        compoundTag.put("Effects", listTag);
    }

    @Override
    public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
        super.finishUsing(itemStack, world, livingEntity);
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null && compoundTag.containsKey("Effects", 9)) {
            ListTag listTag = compoundTag.getList("Effects", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                StatusEffect statusEffect;
                int j = 160;
                CompoundTag compoundTag2 = listTag.getCompoundTag(i);
                if (compoundTag2.containsKey("EffectDuration", 3)) {
                    j = compoundTag2.getInt("EffectDuration");
                }
                if ((statusEffect = StatusEffect.byRawId(compoundTag2.getByte("EffectId"))) == null) continue;
                livingEntity.addPotionEffect(new StatusEffectInstance(statusEffect, j));
            }
        }
        return new ItemStack(Items.BOWL);
    }
}

