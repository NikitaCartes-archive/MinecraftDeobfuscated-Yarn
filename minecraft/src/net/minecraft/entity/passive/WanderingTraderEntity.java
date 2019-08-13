package net.minecraft.entity.passive;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.GoToEntityGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HoldInHandsGoal;
import net.minecraft.entity.ai.goal.LookAtCustomerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.StopFollowingCustomerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;

public class WanderingTraderEntity extends AbstractTraderEntity {
	@Nullable
	private BlockPos wanderTarget;
	private int despawnDelay;

	public WanderingTraderEntity(EntityType<? extends WanderingTraderEntity> entityType, World world) {
		super(entityType, world);
		this.teleporting = true;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector
			.add(
				0,
				new HoldInHandsGoal<>(
					this,
					PotionUtil.setPotion(new ItemStack(Items.field_8574), Potions.field_8997),
					SoundEvents.field_18315,
					wanderingTraderEntity -> !this.world.isDaylight() && !wanderingTraderEntity.isInvisible()
				)
			);
		this.goalSelector
			.add(
				0,
				new HoldInHandsGoal<>(
					this, new ItemStack(Items.field_8103), SoundEvents.field_18314, wanderingTraderEntity -> this.world.isDaylight() && wanderingTraderEntity.isInvisible()
				)
			);
		this.goalSelector.add(1, new StopFollowingCustomerGoal(this));
		this.goalSelector.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 0.5, 0.5));
		this.goalSelector.add(1, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 0.5, 0.5));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 0.5));
		this.goalSelector.add(1, new LookAtCustomerGoal(this));
		this.goalSelector.add(2, new WanderingTraderEntity.WanderToTargetGoal(this, 2.0, 0.35));
		this.goalSelector.add(4, new GoToWalkTargetGoal(this, 1.0));
		this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.35));
		this.goalSelector.add(9, new GoToEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return null;
	}

	@Override
	public boolean isLevelledTrader() {
		return false;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = itemStack.getItem() == Items.field_8448;
		if (bl) {
			itemStack.useOnEntity(playerEntity, this, hand);
			return true;
		} else if (itemStack.getItem() != Items.field_8086 && this.isAlive() && !this.hasCustomer() && !this.isBaby()) {
			if (hand == Hand.field_5808) {
				playerEntity.incrementStat(Stats.field_15384);
			}

			if (this.getOffers().isEmpty()) {
				return super.interactMob(playerEntity, hand);
			} else {
				if (!this.world.isClient) {
					this.setCurrentCustomer(playerEntity);
					this.sendOffers(playerEntity, this.getDisplayName(), 1);
				}

				return true;
			}
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	protected void fillRecipes() {
		TradeOffers.Factory[] factorys = (TradeOffers.Factory[])TradeOffers.WANDERING_TRADER_TRADES.get(1);
		TradeOffers.Factory[] factorys2 = (TradeOffers.Factory[])TradeOffers.WANDERING_TRADER_TRADES.get(2);
		if (factorys != null && factorys2 != null) {
			TraderOfferList traderOfferList = this.getOffers();
			this.fillRecipesFromPool(traderOfferList, factorys, 5);
			int i = this.random.nextInt(factorys2.length);
			TradeOffers.Factory factory = factorys2[i];
			TradeOffer tradeOffer = factory.create(this, this.random);
			if (tradeOffer != null) {
				traderOfferList.add(tradeOffer);
			}
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("DespawnDelay", this.despawnDelay);
		if (this.wanderTarget != null) {
			compoundTag.put("WanderTarget", TagHelper.serializeBlockPos(this.wanderTarget));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("DespawnDelay", 99)) {
			this.despawnDelay = compoundTag.getInt("DespawnDelay");
		}

		if (compoundTag.containsKey("WanderTarget")) {
			this.wanderTarget = TagHelper.deserializeBlockPos(compoundTag.getCompound("WanderTarget"));
		}

		this.setBreedingAge(Math.max(0, this.getBreedingAge()));
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return false;
	}

	@Override
	protected void afterUsing(TradeOffer tradeOffer) {
		if (tradeOffer.shouldRewardPlayerExperience()) {
			int i = 3 + this.random.nextInt(4);
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y + 0.5, this.z, i));
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.hasCustomer() ? SoundEvents.field_17751 : SoundEvents.field_17747;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_17749;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_17748;
	}

	@Override
	protected SoundEvent getDrinkSound(ItemStack itemStack) {
		Item item = itemStack.getItem();
		return item == Items.field_8103 ? SoundEvents.field_18316 : SoundEvents.field_18313;
	}

	@Override
	protected SoundEvent getTradingSound(boolean bl) {
		return bl ? SoundEvents.field_17752 : SoundEvents.field_17750;
	}

	@Override
	public SoundEvent method_18010() {
		return SoundEvents.field_17752;
	}

	public void setDespawnDelay(int i) {
		this.despawnDelay = i;
	}

	public int getDespawnDelay() {
		return this.despawnDelay;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.world.isClient) {
			this.tickDespawnDelay();
		}
	}

	private void tickDespawnDelay() {
		if (this.despawnDelay > 0 && !this.hasCustomer() && --this.despawnDelay == 0) {
			this.remove();
		}
	}

	public void setWanderTarget(@Nullable BlockPos blockPos) {
		this.wanderTarget = blockPos;
	}

	@Nullable
	private BlockPos getWanderTarget() {
		return this.wanderTarget;
	}

	class WanderToTargetGoal extends Goal {
		final WanderingTraderEntity trader;
		final double proximityDistance;
		final double speed;

		WanderToTargetGoal(WanderingTraderEntity wanderingTraderEntity2, double d, double e) {
			this.trader = wanderingTraderEntity2;
			this.proximityDistance = d;
			this.speed = e;
			this.setControls(EnumSet.of(Goal.Control.field_18405));
		}

		@Override
		public void stop() {
			this.trader.setWanderTarget(null);
			WanderingTraderEntity.this.navigation.stop();
		}

		@Override
		public boolean canStart() {
			BlockPos blockPos = this.trader.getWanderTarget();
			return blockPos != null && this.isTooFarFrom(blockPos, this.proximityDistance);
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.trader.getWanderTarget();
			if (blockPos != null && WanderingTraderEntity.this.navigation.isIdle()) {
				if (this.isTooFarFrom(blockPos, 10.0)) {
					Vec3d vec3d = new Vec3d((double)blockPos.getX() - this.trader.x, (double)blockPos.getY() - this.trader.y, (double)blockPos.getZ() - this.trader.z)
						.normalize();
					Vec3d vec3d2 = vec3d.multiply(10.0).add(this.trader.x, this.trader.y, this.trader.z);
					WanderingTraderEntity.this.navigation.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
				} else {
					WanderingTraderEntity.this.navigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.speed);
				}
			}
		}

		private boolean isTooFarFrom(BlockPos blockPos, double d) {
			return !blockPos.isWithinDistance(this.trader.getPos(), d);
		}
	}
}
