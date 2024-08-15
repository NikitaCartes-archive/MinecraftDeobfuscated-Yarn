package net.minecraft.entity.raid;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToRaidCenterGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

public abstract class RaiderEntity extends PatrolEntity {
	protected static final TrackedData<Boolean> CELEBRATING = DataTracker.registerData(RaiderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	static final Predicate<ItemEntity> OBTAINABLE_OMINOUS_BANNER_PREDICATE = itemEntity -> !itemEntity.cannotPickup()
			&& itemEntity.isAlive()
			&& ItemStack.areEqual(itemEntity.getStack(), Raid.createOminousBanner(itemEntity.getRegistryManager().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN)));
	@Nullable
	protected Raid raid;
	private int wave;
	private boolean ableToJoinRaid;
	private int outOfRaidCounter;

	protected RaiderEntity(EntityType<? extends RaiderEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(1, new RaiderEntity.PickUpBannerAsLeaderGoal<>(this));
		this.goalSelector.add(3, new MoveToRaidCenterGoal<>(this));
		this.goalSelector.add(4, new RaiderEntity.AttackHomeGoal(this, 1.05F, 1));
		this.goalSelector.add(5, new RaiderEntity.CelebrateGoal(this));
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(CELEBRATING, false);
	}

	public abstract void addBonusForWave(ServerWorld world, int wave, boolean unused);

	public boolean canJoinRaid() {
		return this.ableToJoinRaid;
	}

	public void setAbleToJoinRaid(boolean ableToJoinRaid) {
		this.ableToJoinRaid = ableToJoinRaid;
	}

	@Override
	public void tickMovement() {
		if (this.getWorld() instanceof ServerWorld && this.isAlive()) {
			Raid raid = this.getRaid();
			if (this.canJoinRaid()) {
				if (raid == null) {
					if (this.getWorld().getTime() % 20L == 0L) {
						Raid raid2 = ((ServerWorld)this.getWorld()).getRaidAt(this.getBlockPos());
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
	public void onDeath(DamageSource damageSource) {
		if (this.getWorld() instanceof ServerWorld) {
			Entity entity = damageSource.getAttacker();
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
		}

		super.onDeath(damageSource);
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

	public boolean isCaptain() {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
		boolean bl = !itemStack.isEmpty()
			&& ItemStack.areEqual(itemStack, Raid.createOminousBanner(this.getRegistryManager().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN)));
		boolean bl2 = this.isPatrolLeader();
		return bl && bl2;
	}

	public boolean hasRaid() {
		return !(this.getWorld() instanceof ServerWorld serverWorld) ? false : this.getRaid() != null || serverWorld.getRaidAt(this.getBlockPos()) != null;
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

	public boolean isCelebrating() {
		return this.dataTracker.get(CELEBRATING);
	}

	public void setCelebrating(boolean celebrating) {
		this.dataTracker.set(CELEBRATING, celebrating);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Wave", this.wave);
		nbt.putBoolean("CanJoinRaid", this.ableToJoinRaid);
		if (this.raid != null) {
			nbt.putInt("RaidId", this.raid.getRaidId());
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.wave = nbt.getInt("Wave");
		this.ableToJoinRaid = nbt.getBoolean("CanJoinRaid");
		if (nbt.contains("RaidId", NbtElement.INT_TYPE)) {
			if (this.getWorld() instanceof ServerWorld) {
				this.raid = ((ServerWorld)this.getWorld()).getRaidManager().getRaid(nbt.getInt("RaidId"));
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
		if (this.hasActiveRaid()
			&& !bl
			&& ItemStack.areEqual(itemStack, Raid.createOminousBanner(this.getRegistryManager().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN)))) {
			EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;
			ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
			double d = (double)this.getDropChance(equipmentSlot);
			if (!itemStack2.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d) {
				this.dropStack(itemStack2);
			}

			this.triggerItemPickedUpByEntityCriteria(item);
			this.equipStack(equipmentSlot, itemStack);
			this.sendPickup(item, itemStack.getCount());
			item.discard();
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
		return super.cannotDespawn() || this.getRaid() != null;
	}

	public int getOutOfRaidCounter() {
		return this.outOfRaidCounter;
	}

	public void setOutOfRaidCounter(int outOfRaidCounter) {
		this.outOfRaidCounter = outOfRaidCounter;
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
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		this.setAbleToJoinRaid(this.getType() != EntityType.WITCH || spawnReason != SpawnReason.NATURAL);
		return super.initialize(world, difficulty, spawnReason, entityData);
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
			ServerWorld serverWorld = (ServerWorld)this.raider.getWorld();
			BlockPos blockPos = this.raider.getBlockPos();
			Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage()
				.getPosition(
					registryEntry -> registryEntry.matchesKey(PointOfInterestTypes.HOME),
					this::canLootHome,
					PointOfInterestStorage.OccupationStatus.ANY,
					blockPos,
					48,
					this.raider.random
				);
			if (optional.isEmpty()) {
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
				Vec3d vec3d = Vec3d.ofBottomCenter(this.home);
				Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.raider, 16, 7, vec3d, (float) (Math.PI / 10));
				if (vec3d2 == null) {
					vec3d2 = NoPenaltyTargeting.findTo(this.raider, 8, 7, vec3d, (float) (Math.PI / 2));
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

		CelebrateGoal(final RaiderEntity raider) {
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
			if (!this.raider.isSilent() && this.raider.random.nextInt(this.getTickCount(100)) == 0) {
				RaiderEntity.this.playSound(RaiderEntity.this.getCelebratingSound());
			}

			if (!this.raider.hasVehicle() && this.raider.random.nextInt(this.getTickCount(50)) == 0) {
				this.raider.getJumpControl().setActive();
			}

			super.tick();
		}
	}

	protected class PatrolApproachGoal extends Goal {
		private final float squaredDistance;
		public final TargetPredicate closeRaiderPredicate = TargetPredicate.createNonAttackable()
			.setBaseMaxDistance(8.0)
			.ignoreVisibility()
			.ignoreDistanceScalingFactor();

		public PatrolApproachGoal(final IllagerEntity illager, final float distance) {
			this.squaredDistance = distance * distance;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = RaiderEntity.this.getAttacker();
			return RaiderEntity.this.getRaid() == null
				&& RaiderEntity.this.isRaidCenterSet()
				&& RaiderEntity.this.getTarget() != null
				&& !RaiderEntity.this.isAttacking()
				&& (livingEntity == null || livingEntity.getType() != EntityType.PLAYER);
		}

		@Override
		public void start() {
			super.start();
			RaiderEntity.this.getNavigation().stop();

			for (RaiderEntity raiderEntity : RaiderEntity.this.getWorld()
				.getTargets(RaiderEntity.class, this.closeRaiderPredicate, RaiderEntity.this, RaiderEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0))) {
				raiderEntity.setTarget(RaiderEntity.this.getTarget());
			}
		}

		@Override
		public void stop() {
			super.stop();
			LivingEntity livingEntity = RaiderEntity.this.getTarget();
			if (livingEntity != null) {
				for (RaiderEntity raiderEntity : RaiderEntity.this.getWorld()
					.getTargets(RaiderEntity.class, this.closeRaiderPredicate, RaiderEntity.this, RaiderEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0))) {
					raiderEntity.setTarget(livingEntity);
					raiderEntity.setAttacking(true);
				}

				RaiderEntity.this.setAttacking(true);
			}
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = RaiderEntity.this.getTarget();
			if (livingEntity != null) {
				if (RaiderEntity.this.squaredDistanceTo(livingEntity) > (double)this.squaredDistance) {
					RaiderEntity.this.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
					if (RaiderEntity.this.random.nextInt(50) == 0) {
						RaiderEntity.this.playAmbientSound();
					}
				} else {
					RaiderEntity.this.setAttacking(true);
				}

				super.tick();
			}
		}
	}

	public class PickUpBannerAsLeaderGoal<T extends RaiderEntity> extends Goal {
		private final T actor;
		private Int2LongOpenHashMap bannerItemCache = new Int2LongOpenHashMap();
		@Nullable
		private Path path;
		@Nullable
		private ItemEntity bannerItemEntity;

		public PickUpBannerAsLeaderGoal(final T actor) {
			this.actor = actor;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (this.shouldStop()) {
				return false;
			} else {
				Int2LongOpenHashMap int2LongOpenHashMap = new Int2LongOpenHashMap();
				double d = RaiderEntity.this.getAttributeValue(EntityAttributes.FOLLOW_RANGE);

				for (ItemEntity itemEntity : this.actor
					.getWorld()
					.getEntitiesByClass((Class<T>)ItemEntity.class, this.actor.getBoundingBox().expand(d, 8.0, d), RaiderEntity.OBTAINABLE_OMINOUS_BANNER_PREDICATE)) {
					long l = this.bannerItemCache.getOrDefault(itemEntity.getId(), Long.MIN_VALUE);
					if (RaiderEntity.this.getWorld().getTime() < l) {
						int2LongOpenHashMap.put(itemEntity.getId(), l);
					} else {
						Path path = this.actor.getNavigation().findPathTo(itemEntity, 1);
						if (path != null && path.reachesTarget()) {
							this.path = path;
							this.bannerItemEntity = itemEntity;
							return true;
						}

						int2LongOpenHashMap.put(itemEntity.getId(), RaiderEntity.this.getWorld().getTime() + 600L);
					}
				}

				this.bannerItemCache = int2LongOpenHashMap;
				return false;
			}
		}

		@Override
		public boolean shouldContinue() {
			if (this.bannerItemEntity == null || this.path == null) {
				return false;
			} else if (this.bannerItemEntity.isRemoved()) {
				return false;
			} else {
				return this.path.isFinished() ? false : !this.shouldStop();
			}
		}

		private boolean shouldStop() {
			if (!this.actor.hasActiveRaid()) {
				return true;
			} else if (this.actor.getRaid().isFinished()) {
				return true;
			} else if (!this.actor.canLead()) {
				return true;
			} else if (ItemStack.areEqual(
				this.actor.getEquippedStack(EquipmentSlot.HEAD), Raid.createOminousBanner(this.actor.getRegistryManager().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN))
			)) {
				return true;
			} else {
				RaiderEntity raiderEntity = RaiderEntity.this.raid.getCaptain(this.actor.getWave());
				return raiderEntity != null && raiderEntity.isAlive();
			}
		}

		@Override
		public void start() {
			this.actor.getNavigation().startMovingAlong(this.path, 1.15F);
		}

		@Override
		public void stop() {
			this.path = null;
			this.bannerItemEntity = null;
		}

		@Override
		public void tick() {
			if (this.bannerItemEntity != null && this.bannerItemEntity.isInRange(this.actor, 1.414)) {
				this.actor.loot(this.bannerItemEntity);
			}
		}
	}
}
