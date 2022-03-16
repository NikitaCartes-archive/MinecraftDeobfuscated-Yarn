/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

public class SuspiciousStewItem
extends Item {
    public static final String EFFECTS_KEY = "Effects";
    public static final String EFFECT_ID_KEY = "EffectId";
    public static final String EFFECT_DURATION_KEY = "EffectDuration";

    public SuspiciousStewItem(Item.Settings settings) {
        super(settings);
    }

    public static void addEffectToStew(ItemStack stew, StatusEffect effect, int duration) {
        NbtCompound nbtCompound = stew.getOrCreateNbt();
        NbtList nbtList = nbtCompound.getList(EFFECTS_KEY, 9);
        NbtCompound nbtCompound2 = new NbtCompound();
        nbtCompound2.putInt(EFFECT_ID_KEY, StatusEffect.getRawId(effect));
        nbtCompound2.putInt(EFFECT_DURATION_KEY, duration);
        nbtList.add(nbtCompound2);
        nbtCompound.put(EFFECTS_KEY, nbtList);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemStack = super.finishUsing(stack, world, user);
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null && nbtCompound.contains(EFFECTS_KEY, 9)) {
            NbtList nbtList = nbtCompound.getList(EFFECTS_KEY, 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                StatusEffect statusEffect;
                int j = 160;
                NbtCompound nbtCompound2 = nbtList.getCompound(i);
                if (nbtCompound2.contains(EFFECT_DURATION_KEY, 3)) {
                    j = nbtCompound2.getInt(EFFECT_DURATION_KEY);
                }
                if ((statusEffect = StatusEffect.byRawId(nbtCompound2.getInt(EFFECT_ID_KEY))) == null) continue;
                user.addStatusEffect(new StatusEffectInstance(statusEffect, j));
            }
        }
        if (user instanceof PlayerEntity && ((PlayerEntity)user).getAbilities().creativeMode) {
            return itemStack;
        }
        return new ItemStack(Items.BOWL);
    }
}

