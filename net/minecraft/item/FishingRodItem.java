/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FishingRodItem
extends Item {
    public FishingRodItem(Item.Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier("cast"), (itemStack, world, livingEntity) -> {
            boolean bl2;
            if (livingEntity == null) {
                return 0.0f;
            }
            boolean bl = livingEntity.getMainHandStack() == itemStack;
            boolean bl3 = bl2 = livingEntity.getOffHandStack() == itemStack;
            if (livingEntity.getMainHandStack().getItem() instanceof FishingRodItem) {
                bl2 = false;
            }
            return (bl || bl2) && livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).fishHook != null ? 1.0f : 0.0f;
        });
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity2, Hand hand) {
        ItemStack itemStack = playerEntity2.getStackInHand(hand);
        if (playerEntity2.fishHook != null) {
            if (!world.isClient) {
                int i = playerEntity2.fishHook.method_6957(itemStack);
                itemStack.damage(i, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(hand));
            }
            world.playSound(null, playerEntity2.x, playerEntity2.y, playerEntity2.z, SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
        } else {
            world.playSound(null, playerEntity2.x, playerEntity2.y, playerEntity2.z, SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
            if (!world.isClient) {
                int i = EnchantmentHelper.getLure(itemStack);
                int j = EnchantmentHelper.getLuckOfTheSea(itemStack);
                world.spawnEntity(new FishingBobberEntity(playerEntity2, world, j, i));
            }
            playerEntity2.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return TypedActionResult.method_22427(itemStack);
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}

