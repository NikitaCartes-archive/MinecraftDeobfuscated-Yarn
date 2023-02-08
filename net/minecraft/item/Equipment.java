/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a type of item that is wearable in an armor equipment slot, or a shield.
 * 
 * <p>This type of item can be targeted by the {@code minecraft:binding_curse} enchantment.
 */
public interface Equipment
extends Vanishable {
    public EquipmentSlot getSlotType();

    default public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
    }

    default public TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
        ItemStack itemStack2 = user.getEquippedStack(equipmentSlot);
        if (EnchantmentHelper.hasBindingCurse(itemStack2) || ItemStack.areEqual(itemStack, itemStack2)) {
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

    @Nullable
    public static Equipment fromStack(ItemStack stack) {
        BlockItem blockItem;
        Item item = stack.getItem();
        if (item instanceof Equipment) {
            Equipment equipment = (Equipment)((Object)item);
            return equipment;
        }
        ItemConvertible itemConvertible = stack.getItem();
        if (itemConvertible instanceof BlockItem && (itemConvertible = (blockItem = (BlockItem)itemConvertible).getBlock()) instanceof Equipment) {
            Equipment equipment2 = (Equipment)((Object)itemConvertible);
            return equipment2;
        }
        return null;
    }
}

