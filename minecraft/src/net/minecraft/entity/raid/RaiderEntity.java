package net.minecraft.entity.raid;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToRaidCenterGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class RaiderEntity extends PatrolEntity {
	private static final Predicate<ItemEntity> OBTAINABLE_ILLAGER_BANNER_ITEM = itemEntity -> !itemEntity.cannotPickup()
			&& itemEntity.isValid()
			&& ItemStack.areEqual(itemEntity.getStack(), Raid.ILLAGER_BANNER);
	@Nullable
	protected Raid raid;
	private int wave;
	private boolean hasRaidGoal;
	private int outOfRaidCounter;

	protected RaiderEntity(EntityType<? extends RaiderEntity> entityType, World world) {
		super(entityType, world);
	}

	public abstract void addBonusForWave(int i, boolean bl);

	public boolean hasRaidGoal() {
		return this.hasRaidGoal;
	}

	public void setHasRaidGoal(boolean bl) {
		this.hasRaidGoal = bl;
	}

	@Override
	public void updateMovement() {
		if (this.world instanceof ServerWorld && this.isValid()) {
			Raid raid = this.getRaid();
			if (raid == null) {
				if (this.world.getTime() % 20L == 0L) {
					Raid raid2 = ((ServerWorld)this.world).getRaidAt(new BlockPos(this));
					if (raid2 != null && RaidManager.isValidRaiderFor(this, raid2)) {
						raid2.addRaider(raid2.getGroupsSpawned(), this, null, true);
					}
				}
			} else {
				LivingEntity livingEntity = this.getTarget();
				if (livingEntity instanceof PlayerEntity || livingEntity instanceof GolemEntity) {
					this.despawnCounter = 0;
				}
			}
		}

		super.updateMovement();
	}

	@Override
	protected void method_16827() {
		this.despawnCounter += 2;
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (this.world instanceof ServerWorld) {
			if (this.getRaid() != null) {
				if (this.isPatrolLeader()) {
					this.getRaid().removeLeader(this.getWave());
				}

				this.getRaid().removeFromWave(this, false);
			}

			if (this.isPatrolLeader() && this.getRaid() == null && ((ServerWorld)this.world).getRaidAt(new BlockPos(this)) == null) {
				ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
				PlayerEntity playerEntity = null;
				Entity entity = damageSource.getAttacker();
				if (entity instanceof PlayerEntity) {
					playerEntity = (PlayerEntity)entity;
				} else if (entity instanceof WolfEntity) {
					WolfEntity wolfEntity = (WolfEntity)entity;
					LivingEntity livingEntity = wolfEntity.getOwner();
					if (wolfEntity.isTamed() && livingEntity instanceof PlayerEntity) {
						playerEntity = (PlayerEntity)livingEntity;
					}
				}

				if (!itemStack.isEmpty() && ItemStack.areEqual(itemStack, Raid.ILLAGER_BANNER) && playerEntity != null) {
					StatusEffectInstance statusEffectInstance = playerEntity.getPotionEffect(StatusEffects.field_16595);
					int i = Raid.getBadOmenLevel(this.random, this.isRaidCenterSet());
					if (statusEffectInstance != null) {
						i += statusEffectInstance.getAmplifier();
						playerEntity.removePotionEffect(StatusEffects.field_16595);
					} else {
						i--;
					}

					i = MathHelper.clamp(i, 0, 5);
					StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(StatusEffects.field_16595, 120000, i, false, false, true);
					playerEntity.addPotionEffect(statusEffectInstance2);
				}
			}
		}

		super.onDeath(damageSource);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(2, new RaiderEntity.PickupBannerAsLeaderGoal<>(this));
		this.goalSelector.add(3, new MoveToRaidCenterGoal<>(this));
	}

	@Override
	public boolean hasNoRaid() {
		return !this.hasActiveRaid();
	}

	public void setRaid(Raid raid) {
		this.raid = raid;
	}

	@Nullable
	public Raid getRaid() {
		return this.raid;
	}

	public boolean hasActiveRaid() {
		return this.getRaid() != null && this.getRaid().isActive();
	}

	public void setWave(int i) {
		this.wave = i;
	}

	public int getWave() {
		return this.wave;
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Wave", this.wave);
		compoundTag.putBoolean("HasRaidGoal", this.hasRaidGoal);
		if (this.raid != null) {
			compoundTag.putInt("RaidId", this.raid.getRaidId());
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.wave = compoundTag.getInt("Wave");
		this.hasRaidGoal = compoundTag.getBoolean("HasRaidGoal");
		if (compoundTag.containsKey("RaidId", 3)) {
			if (this.world instanceof ServerWorld) {
				this.raid = ((ServerWorld)this.world).getRaidManager().getRaid(compoundTag.getInt("RaidId"));
			}

			if (this.raid != null) {
				this.raid.addToWave(this.wave, this, false);
				if (this.isPatrolLeader()) {
					this.raid.setRaidLeader(this.wave, this);
				}
			}
		}
	}

	@Override
	protected void pickupItem(ItemEntity itemEntity) {
		ItemStack itemStack = itemEntity.getStack();
		boolean bl = this.hasActiveRaid() && this.getRaid().getLeader(this.getWave()) != null;
		if (this.hasActiveRaid() && !bl && ItemStack.areEqual(itemStack, Raid.ILLAGER_BANNER)) {
			EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;
			ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
			double d = (double)this.method_5929(equipmentSlot);
			if (!itemStack2.isEmpty() && (double)(this.random.nextFloat() - 0.1F) < d) {
				this.dropStack(itemStack2);
			}

			this.setEquippedStack(equipmentSlot, itemStack);
			this.pickUpEntity(itemEntity, itemStack.getAmount());
			itemEntity.invalidate();
			this.getRaid().setRaidLeader(this.getWave(), this);
			this.setPatrolLeader(true);
		} else {
			super.pickupItem(itemEntity);
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return this.getRaid() != null || super.canImmediatelyDespawn(d);
	}

	@Override
	public boolean cannotDespawn() {
		return this.getRaid() != null;
	}

	public int getOutOfRaidCounter() {
		return this.outOfRaidCounter;
	}

	public void setOutOfRaidCounter(int i) {
		this.outOfRaidCounter = i;
	}

	public class PickupBannerAsLeaderGoal<T extends RaiderEntity> extends Goal {
		private final T field_16603;

		public PickupBannerAsLeaderGoal(T raiderEntity2) {
			this.field_16603 = raiderEntity2;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		}

		@Override
		public boolean canStart() {
			Raid raid = this.field_16603.getRaid();
			if (!RaiderEntity.this.hasActiveRaid()
				|| !this.field_16603.canLead()
				|| ItemStack.areEqual(this.field_16603.getEquippedStack(EquipmentSlot.HEAD), Raid.ILLAGER_BANNER)) {
				return false;
			} else if (raid.getLeader(this.field_16603.getWave()) != null && raid.getLeader(this.field_16603.getWave()).isValid()) {
				return false;
			} else {
				List<ItemEntity> list = this.field_16603
					.world
					.getEntities(ItemEntity.class, this.field_16603.getBoundingBox().expand(16.0, 8.0, 16.0), RaiderEntity.OBTAINABLE_ILLAGER_BANNER_ITEM);
				if (!list.isEmpty()) {
					this.field_16603.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
				}

				return !list.isEmpty();
			}
		}

		@Override
		public void tick() {
			if (this.field_16603.getNavigation().getTargetPos().method_19769(this.field_16603.getPos(), 1.414)) {
				List<ItemEntity> list = this.field_16603
					.world
					.getEntities(ItemEntity.class, this.field_16603.getBoundingBox().expand(4.0, 4.0, 4.0), RaiderEntity.OBTAINABLE_ILLAGER_BANNER_ITEM);
				if (!list.isEmpty()) {
					this.field_16603.pickupItem((ItemEntity)list.get(0));
				}
			}
		}
	}

	public class class_4223 extends Goal {
		private final RaiderEntity field_18883;
		private final float field_18884;
		public final TargetPredicate field_18881 = new TargetPredicate()
			.setBaseMaxDistance(8.0)
			.ignoreEntityTargetRules()
			.includeInvulnerable()
			.includeTeammates()
			.includeHidden()
			.ignoreDistanceScalingFactor();

		public class_4223(IllagerEntity illagerEntity, float f) {
			this.field_18883 = illagerEntity;
			this.field_18884 = f * f;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			return this.field_18883.isRaidCenterSet()
				&& this.field_18883.getTarget() != null
				&& !this.field_18883.method_6510()
				&& !(this.field_18883.getAttacker() instanceof PlayerEntity);
		}

		@Override
		public void start() {
			super.start();
			this.field_18883.getNavigation().stop();

			for (RaiderEntity raiderEntity : this.field_18883
				.world
				.method_18466(RaiderEntity.class, this.field_18881, this.field_18883, this.field_18883.getBoundingBox().expand(8.0, 8.0, 8.0))) {
				raiderEntity.setTarget(this.field_18883.getTarget());
			}
		}

		@Override
		public void onRemove() {
			super.onRemove();
			LivingEntity livingEntity = this.field_18883.getTarget();
			if (livingEntity != null) {
				for (RaiderEntity raiderEntity : this.field_18883
					.world
					.method_18466(RaiderEntity.class, this.field_18881, this.field_18883, this.field_18883.getBoundingBox().expand(8.0, 8.0, 8.0))) {
					raiderEntity.setTarget(livingEntity);
					raiderEntity.method_19540(true);
				}

				this.field_18883.method_19540(true);
			}
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.field_18883.getTarget();
			if (livingEntity != null) {
				if (this.field_18883.squaredDistanceTo(livingEntity) > (double)this.field_18884) {
					this.field_18883.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
					if (this.field_18883.random.nextInt(50) == 0) {
						this.field_18883.playAmbientSound();
					}
				} else {
					this.field_18883.method_19540(true);
				}

				super.tick();
			}
		}
	}
}
