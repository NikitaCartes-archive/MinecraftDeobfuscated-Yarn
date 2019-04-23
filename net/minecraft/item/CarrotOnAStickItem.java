/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CarrotOnAStickItem
extends Item {
    public CarrotOnAStickItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity2, Hand hand) {
        ItemStack itemStack = playerEntity2.getStackInHand(hand);
        if (world.isClient) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        if (playerEntity2.hasVehicle() && playerEntity2.getVehicle() instanceof PigEntity) {
            PigEntity pigEntity = (PigEntity)playerEntity2.getVehicle();
            if (itemStack.getDurability() - itemStack.getDamage() >= 7 && pigEntity.method_6577()) {
                itemStack.applyDamage(7, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(hand));
                if (itemStack.isEmpty()) {
                    ItemStack itemStack2 = new ItemStack(Items.FISHING_ROD);
                    itemStack2.setTag(itemStack.getTag());
                    return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack2);
                }
                return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
            }
        }
        playerEntity2.incrementStat(Stats.USED.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
    }
}

