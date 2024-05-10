package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MilkBucketItem extends Item {
	private static final int MAX_USE_TIME = 32;

	public MilkBucketItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof ServerPlayerEntity serverPlayerEntity) {
			Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		}

		if (!world.isClient) {
			user.clearStatusEffects();
		}

		if (user instanceof PlayerEntity playerEntity) {
			return ItemUsage.exchangeStack(stack, playerEntity, new ItemStack(Items.BUCKET), false);
		} else {
			stack.decrementUnlessCreative(1, user);
			return stack;
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return ItemUsage.consumeHeldItem(world, user, hand);
	}
}
