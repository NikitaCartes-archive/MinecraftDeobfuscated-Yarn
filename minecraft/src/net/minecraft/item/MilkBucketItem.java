package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
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
			Criterions.CONSUME_ITEM.handle(serverPlayerEntity, itemStack);
			serverPlayerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
		}

		if (livingEntity instanceof PlayerEntity && !((PlayerEntity)livingEntity).abilities.creativeMode) {
			itemStack.decrement(1);
		}

		if (!world.isClient) {
			livingEntity.clearPotionEffects();
		}

		return itemStack.isEmpty() ? new ItemStack(Items.field_8550) : itemStack;
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.field_8946;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.setCurrentHand(hand);
		return new TypedActionResult<>(ActionResult.field_5812, playerEntity.getStackInHand(hand));
	}
}
