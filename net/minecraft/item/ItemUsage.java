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
    public static TypedActionResult<ItemStack> consumeHeldItem(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.setCurrentHand(hand);
        return TypedActionResult.consume(playerEntity.getStackInHand(hand));
    }
}

