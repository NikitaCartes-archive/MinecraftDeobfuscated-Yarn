/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Represents a type of item that is wearable in an armor equipment slot.
 * 
 * <p>This type of item can be targeted by the {@code minecraft:binding_curse} enchantment.
 */
public interface Wearable
extends Vanishable {
    default public TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user, Hand hand) {
        EquipmentSlot equipmentSlot;
        ItemStack itemStack2;
        ItemStack itemStack = user.getStackInHand(hand);
        if (ItemStack.areEqual(itemStack, itemStack2 = user.getEquippedStack(equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack)))) {
            return TypedActionResult.fail(itemStack);
        }
        user.equipStack(equipmentSlot, itemStack.copy());
        if (!world.isClient()) {
            user.incrementStat(Stats.USED.getOrCreateStat(item));
        }
        if (itemStack2.isEmpty()) {
            itemStack.setCount(0);
        } else {
            user.setStackInHand(hand, itemStack2.copy());
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }
}

