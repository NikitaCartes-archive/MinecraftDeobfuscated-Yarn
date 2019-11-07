package net.minecraft.entity.raid;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToRaidCenterGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class RaiderEntity extends PatrolEntity {
	protected static final TrackedData<Boolean> CELEBRATING = DataTracker.registerData(RaiderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final Predicate<ItemEntity> OBTAINABLE_OMINOUS_BANNER_PREDICATE = itemEntity -> !itemEntity.cannotPickup()
			&& itemEntity.isAlive()
			&& ItemStack.areEqualIgnoreDamage(itemEntity.getStack(), Raid.getOminousBanner());
	@Nullable
	protected Raid raid;
	private int wave;
	private boolean ableToJoinRaid;
	private int outOfRaidCounter;

	protected RaiderEntity(EntityType<? extends RaiderEntity> type, World world) {
		super(type, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(1, new RaiderEntity.PickupBannerAsLeaderGoal<>(this));
		this.goalSelector.add(3, new MoveToRaidCenterGoal<>(this));
		this.goalSelector.add(4, new RaiderEntity.AttackHomeGoal(this, 1.05F, 1));
		this.goalSelector.add(5, new RaiderEntity.CelebrateGoal(this));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CELEBRATING, false);
	}

	public abstract void addBonusForWave(int wave, boolean unused);

	public boolean canJoinRaid() {
		return this.ableToJoinRaid;
	}

	public void setAbleToJoinRaid(boolean ableToJoinRaid) {
		this.ableToJoinRaid = ableToJoinRaid;
	}

	@Override
	public void tickMovement() {
		if (this.world instanceof ServerWorld && this.isAlive()) {
			Raid raid = this.getRaid();
			if (this.canJoinRaid()) {
				if (raid == null) {
					if (this.world.getTime() % 20L == 0L) {
						Raid raid2 = ((ServerWorld)this.world).getRaidAt(new BlockPos(this));
						if (raid2 != null && RaidManager.isValidRaiderFor(this, raid2)) {
							raid2.addRaider(raid2.getGroupsSpawned(), this, null, true);
						}
					}
				} else {
					LivingEntity livingEntity = this.getTarget();
					if (livingEntity != null && (livingEntity.getType() == EntityType.PLAYER || livingEntity.getType() == EntityType.IRON_GOLEM)) {
						this.despawnCounter = 0;
					}
				}
			}
		}

		super.tickMovement();
	}

	@Override
	protected void updateDespawnCounter() {
		this.despawnCounter += 2;
	}

	@Override
	public void onDeath(DamageSource source) {
		if (this.world instanceof ServerWorld) {
			Entity entity = source.getAttacker();
			Raid raid = this.getRaid();
			if (raid != null) {
				if (this.isPatrolLeader()) {
					raid.removeLeader(this.getWave());
				}

				if (entity != null && entity.getType() == EntityType.PLAYER) {
					raid.addHero(entity);
				}

				raid.removeFromWave(this, false);
			}

			if (this.isPatrolLeader() && raid == null && ((ServerWorld)this.world).getRaidAt(new BlockPos(this)) == null) {
				ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
				PlayerEntity playerEntity = null;
				if (entity instanceof PlayerEntity) {
					playerEntity = (PlayerEntity)entity;
				} else if (entity instanceof WolfEntity) {
					WolfEntity wolfEntity = (WolfEntity)entity;
					LivingEntity livingEntity = wolfEntity.getOwner();
					if (wolfEntity.isTamed() && livingEntity instanceof PlayerEntity) {
						playerEntity = (PlayerEntity)livingEntity;
					}
				}

				if (!itemStack.isEmpty() && ItemStack.areEqualIgnoreDamage(itemStack, Raid.getOminousBanner()) && playerEntity != null) {
					StatusEffectInstance statusEffectInstance = playerEntity.getStatusEffect(StatusEffects.BAD_OMEN);
					int i = 1;
					if (statusEffectInstance != null) {
						i += statusEffectInstance.getAmplifier();
						playerEntity.removeStatusEffect(StatusEffects.BAD_OMEN);
					} else {
						i--;
					}

					i = MathHelper.clamp(i, 0, 5);
					StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, i, false, false, true);
					if (!this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
						playerEntity.addStatusEffect(statusEffectInstance2);
					}
				}
			}
		}

		super.onDeath(source);
	}

	@Override
	public boolean hasNoRaid() {
		return !this.hasActiveRaid();
	}

	public void setRaid(@Nullable Raid raid) {
		this.raid = raid;
	}

	@Nullable
	public Raid getRaid() {
		return this.raid;
	}

	public boolean hasActiveRaid() {
		return this.getRaid() != null && this.getRaid().isActive();
	}

	public void setWave(int wave) {
		this.wave = wave;
	}

	public int getWave() {
		return this.wave;
	}

	@Environment(EnvType.CLIENT)
	public boolean isCelebrating() {
		return this.dataTracker.get(CELEBRATING);
	}

	public void setCelebrating(boolean celebrating) {
		this.dataTracker.set(CELEBRATING, celebrating);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("Wave", this.wave);
		tag.putBoolean("CanJoinRaid", this.ableToJoinRaid);
		if (this.raid != null) {
			tag.putInt("RaidId", this.raid.getRaidId());
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.wave = tag.getInt("Wave");
		this.ableToJoinRaid = tag.getBoolean("CanJoinRaid");
		if (tag.contains("RaidId", 3)) {
			if (this.world instanceof ServerWorld) {
				this.raid = ((ServerWorld)this.world).getRaidManager().getRaid(tag.getInt("RaidId"));
			}

			if (this.raid != null) {
				this.raid.addToWave(this.wave, this, false);
				if (this.isPatrolLeader()) {
					this.raid.setWaveCaptain(this.wave, this);
				}
			}
		}
	}

	@Override
	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getStack();
		boolean bl = this.hasActiveRaid() && this.getRaid().getCaptain(this.getWave()) != null;
		if (this.hasActiveRaid() && !bl && ItemStack.areEqualIgnoreDamage(itemStack, Raid.getOminousBanner())) {
			EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;
			ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
			double d = (double)this.getDropChance(equipmentSlot);
			if (!itemStack2.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d) {
				this.dropStack(itemStack2);
			}

			this.equipStack(equipmentSlot, itemStack);
			this.sendPickup(item, itemStack.getCount());
			item.remove();
			this.getRaid().setWaveCaptain(this.getWave(), this);
			this.setPatrolLeader(true);
		} else {
			super.loot(item);
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return this.getRaid() == null ? super.canImmediatelyDespawn(distanceSquared) : false;
	}

	@Override
	public boolean cannotDespawn() {
		return this.getRaid() != null;
	}

	public int getOutOfRaidCounter() {
		return this.outOfRaidCounter;
	}

	public void setOutOfRaidCounter(int counter) {
		this.outOfRaidCounter = counter;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.hasActiveRaid()) {
			this.getRaid().updateBar();
		}

		return super.damage(source, amount);
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		this.setAbleToJoinRaid(this.getType() != EntityType.WITCH || spawnType != SpawnType.NATURAL);
		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}

	public abstract SoundEvent getCelebratingSound();

	static class AttackHomeGoal extends Goal {
		private final RaiderEntity raider;
		private final double speed;
		private BlockPos home;
		private final List<BlockPos> lastHomes = Lists.<BlockPos>newArrayList();
		private final int distance;
		private boolean finished;

		public AttackHomeGoal(RaiderEntity raider, double speed, int distance) {
			this.raider = raider;
			this.speed = speed;
			this.distance = distance;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			this.purgeMemory();
			return this.isRaiding() && this.tryFindHome() && this.raider.getTarget() == null;
		}

		private boolean isRaiding() {
			return this.raider.hasActiveRaid() && !this.raider.getRaid().isFinished();
		}

		private boolean tryFindHome() {
			ServerWorld serverWorld = (ServerWorld)this.raider.world;
			BlockPos blockPos = new BlockPos(this.raider);
			Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage()
				.getPosition(
					pointOfInterestType -> pointOfInterestType == PointOfInterestType.HOME,
					this::canLootHome,
					PointOfInterestStorage.OccupationStatus.ANY,
					blockPos,
					48,
					this.raider.random
				);
			if (!optional.isPresent()) {
				return false;
			} else {
				this.home = ((BlockPos)optional.get()).toImmutable();
				return true;
			}
		}

		@Override
		public boolean shouldContinue() {
			return this.raider.getNavigation().isIdle()
				? false
				: this.raider.getTarget() == null
					&& !this.home.isWithinDistance(this.raider.getPos(), (double)(this.raider.getWidth() + (float)this.distance))
					&& !this.finished;
		}

		@Override
		public void stop() {
			if (this.home.isWithinDistance(this.raider.getPos(), (double)this.distance)) {
				this.lastHomes.add(this.home);
			}
		}

		@Override
		public void start() {
			super.start();
			this.raider.setDespawnCounter(0);
			this.raider.getNavigation().startMovingTo((double)this.home.getX(), (double)this.home.getY(), (double)this.home.getZ(), this.speed);
			this.finished = false;
		}

		@Override
		public void tick() {
			if (this.raider.getNavigation().isIdle()) {
				Vec3d vec3d = new Vec3d(this.home);
				Vec3d vec3d2 = TargetFinder.findTargetTowards(this.raider, 16, 7, vec3d, (float) (Math.PI / 10));
				if (vec3d2 == null) {
					vec3d2 = TargetFinder.method_23735(this.raider, 8, 7, vec3d);
				}

				if (vec3d2 == null) {
					this.finished = true;
					return;
				}

				this.raider.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
			}
		}

		private boolean canLootHome(BlockPos pos) {
			for (BlockPos blockPos : this.lastHomes) {
				if (Objects.equals(pos, blockPos)) {
					return false;
				}
			}

			return true;
		}

		private void purgeMemory() {
			if (this.lastHomes.size() > 2) {
				this.lastHomes.remove(0);
			}
		}
	}

	public class CelebrateGoal extends Goal {
		private final RaiderEntity raider;

		CelebrateGoal(RaiderEntity raider) {
			this.raider = raider;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			Raid raid = this.raider.getRaid();
			return this.raider.isAlive() && this.raider.getTarget() == null && raid != null && raid.hasLost();
		}

		@Override
		public void start() {
			this.raider.setCelebrating(true);
			super.start();
		}

		@Override
		public void stop() {
			this.raider.setCelebrating(false);
			super.stop();
		}

		@Override
		public void tick() {
			if (!this.raider.isSilent() && this.raider.random.nextInt(100) == 0) {
				RaiderEntity.this.playSound(RaiderEntity.this.getCelebratingSound(), RaiderEntity.this.getSoundVolume(), RaiderEntity.this.getSoundPitch());
			}

			if (!this.raider.hasVehicle() && this.raider.random.nextInt(50) == 0) {
				this.raider.getJumpControl().setActive();
			}

			super.tick();
		}
	}

	public class PatrolApproachGoal extends Goal {
		private final RaiderEntity raider;
		private final float squaredDistance;
		public final TargetPredicate closeRaiderPredicate = new TargetPredicate()
			.setBaseMaxDistance(8.0)
			.ignoreEntityTargetRules()
			.includeInvulnerable()
			.includeTeammates()
			.includeHidden()
			.ignoreDistanceScalingFactor();

		public PatrolApproachGoal(IllagerEntity illager, float distance) {
			this.raider = illager;
			this.squaredDistance = distance * distance;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.raider.getAttacker();
			return this.raider.getRaid() == null
				&& this.raider.isRaidCenterSet()
				&& this.raider.getTarget() != null
				&& !this.raider.isAttacking()
				&& (livingEntity == null || livingEntity.getType() != EntityType.PLAYER);
		}

		@Override
		public void start() {
			super.start();
			this.raider.getNavigation().stop();

			for (RaiderEntity raiderEntity : this.raider
				.world
				.getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0))) {
				raiderEntity.setTarget(this.raider.getTarget());
			}
		}

		@Override
		public void stop() {
			super.stop();
			LivingEntity livingEntity = this.raider.getTarget();
			if (livingEntity != null) {
				for (RaiderEntity raiderEntity : this.raider
					.world
					.getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0))) {
					raiderEntity.setTarget(livingEntity);
					raiderEntity.setAttacking(true);
				}

				this.raider.setAttacking(true);
			}
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.raider.getTarget();
			if (livingEntity != null) {
				if (this.raider.squaredDistanceTo(livingEntity) > (double)this.squaredDistance) {
					this.raider.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
					if (this.raider.random.nextInt(50) == 0) {
						this.raider.playAmbientSound();
					}
				} else {
					this.raider.setAttacking(true);
				}

				super.tick();
			}
		}
	}

	public class PickupBannerAsLeaderGoal<T extends RaiderEntity> extends Goal {
		private final T actor;

		public PickupBannerAsLeaderGoal(T actor) {
			this.actor = actor;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			Raid raid = this.actor.getRaid();
			if (this.actor.hasActiveRaid()
				&& !this.actor.getRaid().isFinished()
				&& this.actor.canLead()
				&& !ItemStack.areEqualIgnoreDamage(this.actor.getEquippedStack(EquipmentSlot.HEAD), Raid.getOminousBanner())) {
				RaiderEntity raiderEntity = raid.getCaptain(this.actor.getWave());
				if (raiderEntity == null || !raiderEntity.isAlive()) {
					List<ItemEntity> list = this.actor
						.world
						.getEntities(ItemEntity.class, this.actor.getBoundingBox().expand(16.0, 8.0, 16.0), RaiderEntity.OBTAINABLE_OMINOUS_BANNER_PREDICATE);
					if (!list.isEmpty()) {
						return this.actor.getNavigation().startMovingTo((Entity)list.get(0), 1.15F);
					}
				}

				return false;
			} else {
				return false;
			}
		}

		@Override
		public void tick() {
			if (this.actor.getNavigation().getTargetPos().isWithinDistance(this.actor.getPos(), 1.414)) {
				List<ItemEntity> list = this.actor
					.world
					.getEntities(ItemEntity.class, this.actor.getBoundingBox().expand(4.0, 4.0, 4.0), RaiderEntity.OBTAINABLE_OMINOUS_BANNER_PREDICATE);
				if (!list.isEmpty()) {
					this.actor.loot((ItemEntity)list.get(0));
				}
			}
		}
	}
}
