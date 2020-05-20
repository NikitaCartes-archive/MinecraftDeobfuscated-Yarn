/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EmptyMapItem
extends NetworkSyncedItem {
    public EmptyMapItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = FilledMapItem.createMap(world, MathHelper.floor(user.getX()), MathHelper.floor(user.getZ()), (byte)0, true, false);
        ItemStack itemStack2 = user.getStackInHand(hand);
        if (!user.abilities.creativeMode) {
            itemStack2.decrement(1);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        user.playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0f, 1.0f);
        if (itemStack2.isEmpty()) {
            return TypedActionResult.success(itemStack);
        }
        if (!user.inventory.insertStack(itemStack.copy())) {
            user.dropItem(itemStack, false);
        }
        return TypedActionResult.success(itemStack2);
    }
}

