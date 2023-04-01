package net.minecraft.item;

import net.minecraft.class_8293;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MilkBucketItem extends PowderSnowBucketItem {
	private static final int MAX_USE_TIME = 32;

	public MilkBucketItem(Item.Settings settings) {
		super(Blocks.CHEESE, SoundEvents.BLOCK_FUNGUS_PLACE, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		return !class_8293.field_43656.method_50116() ? ActionResult.PASS : super.useOnBlock(context);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof ServerPlayerEntity serverPlayerEntity) {
			Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		}

		if (user instanceof PlayerEntity && !((PlayerEntity)user).getAbilities().creativeMode) {
			stack.decrement(1);
		}

		if (!world.isClient) {
			user.clearStatusEffects();
		}

		return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
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
