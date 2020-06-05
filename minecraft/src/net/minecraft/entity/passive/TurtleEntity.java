package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TurtleEntity extends AnimalEntity {
	private static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> HAS_EGG = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> DIGGING_SAND = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<BlockPos> TRAVEL_POS = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> LAND_BOUND = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> ACTIVELY_TRAVELLING = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int sandDiggingCounter;
	public static final Predicate<LivingEntity> BABY_TURTLE_ON_LAND_FILTER = livingEntity -> livingEntity.isBaby() && !livingEntity.isTouchingWater();

	public TurtleEntity(EntityType<? extends TurtleEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
		this.moveControl = new TurtleEntity.TurtleMoveControl(this);
		this.stepHeight = 1.0F;
	}

	public void setHomePos(BlockPos pos) {
		this.dataTracker.set(HOME_POS, pos);
	}

	private BlockPos getHomePos() {
		return this.dataTracker.get(HOME_POS);
	}

	private void setTravelPos(BlockPos pos) {
		this.dataTracker.set(TRAVEL_POS, pos);
	}

	private BlockPos getTravelPos() {
		return this.dataTracker.get(TRAVEL_POS);
	}

	public boolean hasEgg() {
		return this.dataTracker.get(HAS_EGG);
	}

	private void setHasEgg(boolean hasEgg) {
		this.dataTracker.set(HAS_EGG, hasEgg);
	}

	public boolean isDiggingSand() {
		return this.dataTracker.get(DIGGING_SAND);
	}

	private void setDiggingSand(boolean diggingSand) {
		this.sandDiggingCounter = diggingSand ? 1 : 0;
		this.dataTracker.set(DIGGING_SAND, diggingSand);
	}

	private boolean isLandBound() {
		return this.dataTracker.get(LAND_BOUND);
	}

	private void setLandBound(boolean landBound) {
		this.dataTracker.set(LAND_BOUND, landBound);
	}

	private boolean isActivelyTravelling() {
		return this.dataTracker.get(ACTIVELY_TRAVELLING);
	}

	private void setActivelyTravelling(boolean travelling) {
		this.dataTracker.set(ACTIVELY_TRAVELLING, travelling);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(HOME_POS, BlockPos.ORIGIN);
		this.dataTracker.startTracking(HAS_EGG, false);
		this.dataTracker.startTracking(TRAVEL_POS, BlockPos.ORIGIN);
		this.dataTracker.startTracking(LAND_BOUND, false);
		this.dataTracker.startTracking(ACTIVELY_TRAVELLING, false);
		this.dataTracker.startTracking(DIGGING_SAND, false);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("HomePosX", this.getHomePos().getX());
		tag.putInt("HomePosY", this.getHomePos().getY());
		tag.putInt("HomePosZ", this.getHomePos().getZ());
		tag.putBoolean("HasEgg", this.hasEgg());
		tag.putInt("TravelPosX", this.getTravelPos().getX());
		tag.putInt("TravelPosY", this.getTravelPos().getY());
		tag.putInt("TravelPosZ", this.getTravelPos().getZ());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		int i = tag.getInt("HomePosX");
		int j = tag.getInt("HomePosY");
		int k = tag.getInt("HomePosZ");
		this.setHomePos(new BlockPos(i, j, k));
		super.readCustomDataFromTag(tag);
		this.setHasEgg(tag.getBoolean("HasEgg"));
		int l = tag.getInt("TravelPosX");
		int m = tag.getInt("TravelPosY");
		int n = tag.getInt("TravelPosZ");
		this.setTravelPos(new BlockPos(l, m, n));
	}

	@Nullable
	@Override
	public EntityData initialize(
		WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		this.setHomePos(this.getBlockPos());
		this.setTravelPos(BlockPos.ORIGIN);
		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
	}

	public static boolean canSpawn(EntityType<TurtleEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return pos.getY() < world.getSeaLevel() + 4 && world.getBlockState(pos.down()).isOf(Blocks.SAND) && world.getBaseLightLevel(pos, 0) > 8;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new TurtleEntity.TurtleEscapeDangerGoal(this, 1.2));
		this.goalSelector.add(1, new TurtleEntity.MateGoal(this, 1.0));
		this.goalSelector.add(1, new TurtleEntity.LayEggGoal(this, 1.0));
		this.goalSelector.add(2, new TurtleEntity.ApproachFoodHoldingPlayerGoal(this, 1.1, Blocks.SEAGRASS.asItem()));
		this.goalSelector.add(3, new TurtleEntity.WanderInWaterGoal(this, 1.0));
		this.goalSelector.add(4, new TurtleEntity.GoHomeGoal(this, 1.0));
		this.goalSelector.add(7, new TurtleEntity.TravelGoal(this, 1.0));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(9, new TurtleEntity.WanderOnLandGoal(this, 1.0, 100));
	}

	public static DefaultAttributeContainer.Builder createTurtleAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
	}

	@Override
	public boolean canFly() {
		return false;
	}

	@Override
	public boolean canBreatheInWater() {
		return true;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.AQUATIC;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 200;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return !this.isTouchingWater() && this.onGround && !this.isBaby() ? SoundEvents.ENTITY_TURTLE_AMBIENT_LAND : super.getAmbientSound();
	}

	@Override
	protected void playSwimSound(float volume) {
		super.playSwimSound(volume * 1.5F);
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_TURTLE_SWIM;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isBaby() ? SoundEvents.ENTITY_TURTLE_HURT_BABY : SoundEvents.ENTITY_TURTLE_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return this.isBaby() ? SoundEvents.ENTITY_TURTLE_DEATH_BABY : SoundEvents.ENTITY_TURTLE_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		SoundEvent soundEvent = this.isBaby() ? SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY : SoundEvents.ENTITY_TURTLE_SHAMBLE;
		this.playSound(soundEvent, 0.15F, 1.0F);
	}

	@Override
	public boolean canEat() {
		return super.canEat() && !this.hasEgg();
	}

	@Override
	protected float calculateNextStepSoundDistance() {
		return this.distanceTraveled + 0.15F;
	}

	@Override
	public float getScaleFactor() {
		return this.isBaby() ? 0.3F : 1.0F;
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new TurtleEntity.TurtleSwimNavigation(this, world);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity mate) {
		return EntityType.TURTLE.create(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == Blocks.SEAGRASS.asItem();
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		if (!this.isLandBound() && world.getFluidState(pos).matches(FluidTags.WATER)) {
			return 10.0F;
		} else {
			return world.getBlockState(pos.down()).isOf(Blocks.SAND) ? 10.0F : world.getBrightness(pos) - 0.5F;
		}
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.isAlive() && this.isDiggingSand() && this.sandDiggingCounter >= 1 && this.sandDiggingCounter % 5 == 0) {
			BlockPos blockPos = this.getBlockPos();
			if (this.world.getBlockState(blockPos.down()).isOf(Blocks.SAND)) {
				this.world.syncWorldEvent(2001, blockPos, Block.getRawIdFromState(Blocks.SAND.getDefaultState()));
			}
		}
	}

	@Override
	protected void onGrowUp() {
		super.onGrowUp();
		if (!this.isBaby() && this.world.getGameRules().getBoolean(GameRules.field_19391)) {
			this.dropItem(Items.SCUTE, 1);
		}
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.updateVelocity(0.1F, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
			if (this.getTarget() == null && (!this.isLandBound() || !this.getHomePos().isWithinDistance(this.getPos(), 20.0))) {
				this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(movementInput);
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return false;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightning) {
		this.damage(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
	}

	static class ApproachFoodHoldingPlayerGoal extends Goal {
		private static final TargetPredicate CLOSE_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(10.0).includeTeammates().includeInvulnerable();
		private final TurtleEntity turtle;
		private final double speed;
		private PlayerEntity targetPlayer;
		private int cooldown;
		private final Set<Item> attractiveItems;

		ApproachFoodHoldingPlayerGoal(TurtleEntity turtle, double speed, Item attractiveItem) {
			this.turtle = turtle;
			this.speed = speed;
			this.attractiveItems = Sets.<Item>newHashSet(attractiveItem);
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			if (this.cooldown > 0) {
				this.cooldown--;
				return false;
			} else {
				this.targetPlayer = this.turtle.world.getClosestPlayer(CLOSE_ENTITY_PREDICATE, this.turtle);
				return this.targetPlayer == null
					? false
					: this.isAttractive(this.targetPlayer.getMainHandStack()) || this.isAttractive(this.targetPlayer.getOffHandStack());
			}
		}

		private boolean isAttractive(ItemStack stack) {
			return this.attractiveItems.contains(stack.getItem());
		}

		@Override
		public boolean shouldContinue() {
			return this.canStart();
		}

		@Override
		public void stop() {
			this.targetPlayer = null;
			this.turtle.getNavigation().stop();
			this.cooldown = 100;
		}

		@Override
		public void tick() {
			this.turtle.getLookControl().lookAt(this.targetPlayer, (float)(this.turtle.getBodyYawSpeed() + 20), (float)this.turtle.getLookPitchSpeed());
			if (this.turtle.squaredDistanceTo(this.targetPlayer) < 6.25) {
				this.turtle.getNavigation().stop();
			} else {
				this.turtle.getNavigation().startMovingTo(this.targetPlayer, this.speed);
			}
		}
	}

	static class GoHomeGoal extends Goal {
		private final TurtleEntity turtle;
		private final double speed;
		private boolean noPath;
		private int homeReachingTryTicks;

		GoHomeGoal(TurtleEntity turtle, double speed) {
			this.turtle = turtle;
			this.speed = speed;
		}

		@Override
		public boolean canStart() {
			if (this.turtle.isBaby()) {
				return false;
			} else if (this.turtle.hasEgg()) {
				return true;
			} else {
				return this.turtle.getRandom().nextInt(700) != 0 ? false : !this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 64.0);
			}
		}

		@Override
		public void start() {
			this.turtle.setLandBound(true);
			this.noPath = false;
			this.homeReachingTryTicks = 0;
		}

		@Override
		public void stop() {
			this.turtle.setLandBound(false);
		}

		@Override
		public boolean shouldContinue() {
			return !this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 7.0) && !this.noPath && this.homeReachingTryTicks <= 600;
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.turtle.getHomePos();
			boolean bl = blockPos.isWithinDistance(this.turtle.getPos(), 16.0);
			if (bl) {
				this.homeReachingTryTicks++;
			}

			if (this.turtle.getNavigation().isIdle()) {
				Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
				Vec3d vec3d2 = TargetFinder.findTargetTowards(this.turtle, 16, 3, vec3d, (float) (Math.PI / 10));
				if (vec3d2 == null) {
					vec3d2 = TargetFinder.findTargetTowards(this.turtle, 8, 7, vec3d);
				}

				if (vec3d2 != null && !bl && !this.turtle.world.getBlockState(new BlockPos(vec3d2)).isOf(Blocks.WATER)) {
					vec3d2 = TargetFinder.findTargetTowards(this.turtle, 16, 5, vec3d);
				}

				if (vec3d2 == null) {
					this.noPath = true;
					return;
				}

				this.turtle.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
			}
		}
	}

	static class LayEggGoal extends MoveToTargetPosGoal {
		private final TurtleEntity turtle;

		LayEggGoal(TurtleEntity turtle, double speed) {
			super(turtle, speed, 16);
			this.turtle = turtle;
		}

		@Override
		public boolean canStart() {
			return this.turtle.hasEgg() && this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 9.0) ? super.canStart() : false;
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.turtle.hasEgg() && this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 9.0);
		}

		@Override
		public void tick() {
			super.tick();
			BlockPos blockPos = this.turtle.getBlockPos();
			if (!this.turtle.isTouchingWater() && this.hasReached()) {
				if (this.turtle.sandDiggingCounter < 1) {
					this.turtle.setDiggingSand(true);
				} else if (this.turtle.sandDiggingCounter > 200) {
					World world = this.turtle.world;
					world.playSound(null, blockPos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
					world.setBlockState(
						this.targetPos.up(), Blocks.TURTLE_EGG.getDefaultState().with(TurtleEggBlock.EGGS, Integer.valueOf(this.turtle.random.nextInt(4) + 1)), 3
					);
					this.turtle.setHasEgg(false);
					this.turtle.setDiggingSand(false);
					this.turtle.setLoveTicks(600);
				}

				if (this.turtle.isDiggingSand()) {
					this.turtle.sandDiggingCounter++;
				}
			}
		}

		@Override
		protected boolean isTargetPos(WorldView world, BlockPos pos) {
			return !world.isAir(pos.up()) ? false : world.getBlockState(pos).isOf(Blocks.SAND);
		}
	}

	static class MateGoal extends AnimalMateGoal {
		private final TurtleEntity turtle;

		MateGoal(TurtleEntity turtle, double speed) {
			super(turtle, speed);
			this.turtle = turtle;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !this.turtle.hasEgg();
		}

		@Override
		protected void breed() {
			ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
			if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
				serverPlayerEntity = this.mate.getLovingPlayer();
			}

			if (serverPlayerEntity != null) {
				serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
				Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this.animal, this.mate, null);
			}

			this.turtle.setHasEgg(true);
			this.animal.resetLoveTicks();
			this.mate.resetLoveTicks();
			Random random = this.animal.getRandom();
			if (this.world.getGameRules().getBoolean(GameRules.field_19391)) {
				this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
			}
		}
	}

	static class TravelGoal extends Goal {
		private final TurtleEntity turtle;
		private final double speed;
		private boolean noPath;

		TravelGoal(TurtleEntity turtle, double speed) {
			this.turtle = turtle;
			this.speed = speed;
		}

		@Override
		public boolean canStart() {
			return !this.turtle.isLandBound() && !this.turtle.hasEgg() && this.turtle.isTouchingWater();
		}

		@Override
		public void start() {
			int i = 512;
			int j = 4;
			Random random = this.turtle.random;
			int k = random.nextInt(1025) - 512;
			int l = random.nextInt(9) - 4;
			int m = random.nextInt(1025) - 512;
			if ((double)l + this.turtle.getY() > (double)(this.turtle.world.getSeaLevel() - 1)) {
				l = 0;
			}

			BlockPos blockPos = new BlockPos((double)k + this.turtle.getX(), (double)l + this.turtle.getY(), (double)m + this.turtle.getZ());
			this.turtle.setTravelPos(blockPos);
			this.turtle.setActivelyTravelling(true);
			this.noPath = false;
		}

		@Override
		public void tick() {
			if (this.turtle.getNavigation().isIdle()) {
				Vec3d vec3d = Vec3d.ofBottomCenter(this.turtle.getTravelPos());
				Vec3d vec3d2 = TargetFinder.findTargetTowards(this.turtle, 16, 3, vec3d, (float) (Math.PI / 10));
				if (vec3d2 == null) {
					vec3d2 = TargetFinder.findTargetTowards(this.turtle, 8, 7, vec3d);
				}

				if (vec3d2 != null) {
					int i = MathHelper.floor(vec3d2.x);
					int j = MathHelper.floor(vec3d2.z);
					int k = 34;
					if (!this.turtle.world.isRegionLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
						vec3d2 = null;
					}
				}

				if (vec3d2 == null) {
					this.noPath = true;
					return;
				}

				this.turtle.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
			}
		}

		@Override
		public boolean shouldContinue() {
			return !this.turtle.getNavigation().isIdle() && !this.noPath && !this.turtle.isLandBound() && !this.turtle.isInLove() && !this.turtle.hasEgg();
		}

		@Override
		public void stop() {
			this.turtle.setActivelyTravelling(false);
			super.stop();
		}
	}

	static class TurtleEscapeDangerGoal extends EscapeDangerGoal {
		TurtleEscapeDangerGoal(TurtleEntity turtle, double speed) {
			super(turtle, speed);
		}

		@Override
		public boolean canStart() {
			if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
				return false;
			} else {
				BlockPos blockPos = this.locateClosestWater(this.mob.world, this.mob, 7, 4);
				if (blockPos != null) {
					this.targetX = (double)blockPos.getX();
					this.targetY = (double)blockPos.getY();
					this.targetZ = (double)blockPos.getZ();
					return true;
				} else {
					return this.findTarget();
				}
			}
		}
	}

	static class TurtleMoveControl extends MoveControl {
		private final TurtleEntity turtle;

		TurtleMoveControl(TurtleEntity turtle) {
			super(turtle);
			this.turtle = turtle;
		}

		private void updateVelocity() {
			if (this.turtle.isTouchingWater()) {
				this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, 0.005, 0.0));
				if (!this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 16.0)) {
					this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0F, 0.08F));
				}

				if (this.turtle.isBaby()) {
					this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 3.0F, 0.06F));
				}
			} else if (this.turtle.onGround) {
				this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0F, 0.06F));
			}
		}

		@Override
		public void tick() {
			this.updateVelocity();
			if (this.state == MoveControl.State.MOVE_TO && !this.turtle.getNavigation().isIdle()) {
				double d = this.targetX - this.turtle.getX();
				double e = this.targetY - this.turtle.getY();
				double f = this.targetZ - this.turtle.getZ();
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.turtle.yaw = this.changeAngle(this.turtle.yaw, h, 90.0F);
				this.turtle.bodyYaw = this.turtle.yaw;
				float i = (float)(this.speed * this.turtle.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
				this.turtle.setMovementSpeed(MathHelper.lerp(0.125F, this.turtle.getMovementSpeed(), i));
				this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, (double)this.turtle.getMovementSpeed() * e * 0.1, 0.0));
			} else {
				this.turtle.setMovementSpeed(0.0F);
			}
		}
	}

	static class TurtleSwimNavigation extends SwimNavigation {
		TurtleSwimNavigation(TurtleEntity owner, World world) {
			super(owner, world);
		}

		@Override
		protected boolean isAtValidPosition() {
			return true;
		}

		@Override
		protected PathNodeNavigator createPathNodeNavigator(int range) {
			this.nodeMaker = new AmphibiousPathNodeMaker();
			return new PathNodeNavigator(this.nodeMaker, range);
		}

		@Override
		public boolean isValidPosition(BlockPos pos) {
			if (this.entity instanceof TurtleEntity) {
				TurtleEntity turtleEntity = (TurtleEntity)this.entity;
				if (turtleEntity.isActivelyTravelling()) {
					return this.world.getBlockState(pos).isOf(Blocks.WATER);
				}
			}

			return !this.world.getBlockState(pos.down()).isAir();
		}
	}

	static class WanderInWaterGoal extends MoveToTargetPosGoal {
		private final TurtleEntity turtle;

		private WanderInWaterGoal(TurtleEntity turtle, double speed) {
			super(turtle, turtle.isBaby() ? 2.0 : speed, 24);
			this.turtle = turtle;
			this.lowestY = -1;
		}

		@Override
		public boolean shouldContinue() {
			return !this.turtle.isTouchingWater() && this.tryingTime <= 1200 && this.isTargetPos(this.turtle.world, this.targetPos);
		}

		@Override
		public boolean canStart() {
			if (this.turtle.isBaby() && !this.turtle.isTouchingWater()) {
				return super.canStart();
			} else {
				return !this.turtle.isLandBound() && !this.turtle.isTouchingWater() && !this.turtle.hasEgg() ? super.canStart() : false;
			}
		}

		@Override
		public boolean shouldResetPath() {
			return this.tryingTime % 160 == 0;
		}

		@Override
		protected boolean isTargetPos(WorldView world, BlockPos pos) {
			return world.getBlockState(pos).isOf(Blocks.WATER);
		}
	}

	static class WanderOnLandGoal extends WanderAroundGoal {
		private final TurtleEntity turtle;

		private WanderOnLandGoal(TurtleEntity turtle, double speed, int chance) {
			super(turtle, speed, chance);
			this.turtle = turtle;
		}

		@Override
		public boolean canStart() {
			return !this.mob.isTouchingWater() && !this.turtle.isLandBound() && !this.turtle.hasEgg() ? super.canStart() : false;
		}
	}
}
