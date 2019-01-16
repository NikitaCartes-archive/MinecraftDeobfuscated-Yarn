package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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
			this.onConsumed(itemStack, world, playerEntity);
			playerEntity.incrementStat(Stats.field_15372.method_14956(this));
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.CONSUME_ITEM.handle((ServerPlayerEntity)playerEntity, itemStack);
			}
		}

		itemStack.subtractAmount(1);
		return itemStack;
	}

	protected void onConsumed(ItemStack itemStack, World world, PlayerEntity playerEntity) {
		if (!world.isClient && this.statusEffect != null && world.random.nextFloat() < this.statusEffectChance) {
			playerEntity.addPotionEffect(new StatusEffectInstance(this.statusEffect));
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
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
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
