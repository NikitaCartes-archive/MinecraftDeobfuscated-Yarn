package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class HoneyBottleItem extends Item {
	public HoneyBottleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		super.finishUsing(itemStack, world, livingEntity);
		if (livingEntity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
			Criterions.CONSUME_ITEM.trigger(serverPlayerEntity, itemStack);
			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
		}

		if (!world.isClient) {
			livingEntity.tryRemoveStatusEffect(StatusEffects.POISON);
		}

		return itemStack.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : itemStack;
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return 40;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.DRINK;
	}

	@Override
	public SoundEvent getDrinkSound() {
		return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
	}

	@Override
	public SoundEvent getEatSound() {
		return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		playerEntity.setCurrentHand(hand);
		return TypedActionResult.successWithSwing(playerEntity.getStackInHand(hand));
	}
}
