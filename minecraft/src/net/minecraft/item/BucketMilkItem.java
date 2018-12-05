package net.minecraft.item;

import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BucketMilkItem extends Item {
	public BucketMilkItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack onItemFinishedUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		if (livingEntity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
			CriterionCriterions.CONSUME_ITEM.handle(serverPlayerEntity, itemStack);
			serverPlayerEntity.incrementStat(Stats.field_15372.method_14956(this));
		}

		if (livingEntity instanceof PlayerEntity && !((PlayerEntity)livingEntity).abilities.creativeMode) {
			itemStack.subtractAmount(1);
		}

		if (!world.isRemote) {
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
		return new TypedActionResult<>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
	}
}
