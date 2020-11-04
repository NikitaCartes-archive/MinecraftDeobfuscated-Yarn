/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemUsage {
    public static TypedActionResult<ItemStack> consumeHeldItem(World world, PlayerEntity player, Hand hand) {
        player.setCurrentHand(hand);
        return TypedActionResult.consume(player.getStackInHand(hand));
    }

    public static ItemStack method_30270(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2, boolean bl) {
        boolean bl2 = playerEntity.getAbilities().creativeMode;
        if (bl && bl2) {
            if (!playerEntity.getInventory().contains(itemStack2)) {
                playerEntity.getInventory().insertStack(itemStack2);
            }
            return itemStack;
        }
        if (!bl2) {
            itemStack.decrement(1);
        }
        if (itemStack.isEmpty()) {
            return itemStack2;
        }
        if (!playerEntity.getInventory().insertStack(itemStack2)) {
            playerEntity.dropItem(itemStack2, false);
        }
        return itemStack;
    }

    public static ItemStack method_30012(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2) {
        return ItemUsage.method_30270(itemStack, playerEntity, itemStack2, true);
    }
}

