/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FishingRodItem
extends Item
implements Vanishable {
    public FishingRodItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.fishHook != null) {
            if (!world.isClient) {
                int i = user.fishHook.use(itemStack);
                itemStack.damage(i, user, p -> p.sendToolBreakStatus(hand));
            }
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            world.emitGameEvent((Entity)user, GameEvent.FISHING_ROD_CAST, user);
        } else {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
            world.emitGameEvent((Entity)user, GameEvent.FISHING_ROD_REEL_IN, user);
            if (!world.isClient) {
                int i = EnchantmentHelper.getLure(itemStack);
                int j = EnchantmentHelper.getLuckOfTheSea(itemStack);
                world.spawnEntity(new FishingBobberEntity(user, world, j, i));
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}

