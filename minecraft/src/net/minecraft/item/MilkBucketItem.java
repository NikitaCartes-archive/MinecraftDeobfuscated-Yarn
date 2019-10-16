package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MilkBucketItem extends Item {
	public MilkBucketItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		if (livingEntity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
			Criterions.CONSUME_ITEM.trigger(serverPlayerEntity, itemStack);
			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		}

		if (livingEntity instanceof PlayerEntity && !((PlayerEntity)livingEntity).abilities.creativeMode) {
			itemStack.decrement(1);
		}

		if (!world.isClient) {
			livingEntity.clearStatusEffects();
		}

		return itemStack.isEmpty() ? new ItemStack(Items.BUCKET) : itemStack;
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.DRINK;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.setCurrentHand(hand);
		return TypedActionResult.successWithSwing(playerEntity.getStackInHand(hand));
	}
}
