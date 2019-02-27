package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class FoodItem extends Item {
	private final int hungerRestored;
	private final float saturationModifier;
	private final boolean wolfFood;
	private boolean alwaysConsumable;
	private boolean consumeQuickly;
	private StatusEffectInstance statusEffect;
	private float statusEffectChance;

	public FoodItem(int i, float f, boolean bl, Item.Settings settings) {
		super(settings);
		this.hungerRestored = i;
		this.wolfFood = bl;
		this.saturationModifier = f;
	}

	@Override
	public ItemStack onItemFinishedUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		if (livingEntity instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)livingEntity;
			playerEntity.getHungerManager().eat(this, itemStack);
			world.playSound(
				null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_14990, SoundCategory.field_15248, 0.5F, world.random.nextFloat() * 0.1F + 0.9F
			);
			playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.CONSUME_ITEM.handle((ServerPlayerEntity)playerEntity, itemStack);
			}
		} else if (livingEntity instanceof FoxEntity) {
			world.playSound(
				null,
				livingEntity.x,
				livingEntity.y,
				livingEntity.z,
				SoundEvents.field_18060,
				SoundCategory.field_15254,
				1.0F,
				1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
			);
		}

		this.onConsumed(itemStack, world, livingEntity);
		itemStack.subtractAmount(1);
		return itemStack;
	}

	protected void onConsumed(ItemStack itemStack, World world, LivingEntity livingEntity) {
		if (!world.isClient && this.statusEffect != null && world.random.nextFloat() < this.statusEffectChance) {
			livingEntity.addPotionEffect(new StatusEffectInstance(this.statusEffect));
		}
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return this.consumeQuickly ? 16 : 32;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.field_8950;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (playerEntity.canConsume(this.alwaysConsumable)) {
			playerEntity.setCurrentHand(hand);
			return new TypedActionResult<>(ActionResult.field_5812, itemStack);
		} else {
			return new TypedActionResult<>(ActionResult.field_5814, itemStack);
		}
	}

	public int getHungerRestored(ItemStack itemStack) {
		return this.hungerRestored;
	}

	public float getSaturationModifier(ItemStack itemStack) {
		return this.saturationModifier;
	}

	public boolean isWolfFood() {
		return this.wolfFood;
	}

	public FoodItem setStatusEffect(StatusEffectInstance statusEffectInstance, float f) {
		this.statusEffect = statusEffectInstance;
		this.statusEffectChance = f;
		return this;
	}

	public FoodItem setAlwaysConsumable() {
		this.alwaysConsumable = true;
		return this;
	}

	public FoodItem setConsumeQuickly() {
		this.consumeQuickly = true;
		return this;
	}
}
