/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EggItem
extends Item {
    public EggItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        world.playSound(null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
        if (!world.isClient) {
            ThrownEggEntity thrownEggEntity = new ThrownEggEntity(world, playerEntity);
            thrownEggEntity.setItem(itemStack);
            thrownEggEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0f, 1.5f, 1.0f);
            world.spawnEntity(thrownEggEntity);
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!playerEntity.abilities.creativeMode) {
            itemStack.decrement(1);
        }
        return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
    }
}

