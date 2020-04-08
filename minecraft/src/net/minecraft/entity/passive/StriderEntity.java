package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class StriderEntity extends AnimalEntity implements ItemSteerable, Saddleable {
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.WARPED_FUNGUS);
	private static final Ingredient ATTRACTING_INGREDIENT = Ingredient.ofItems(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
	private static final TrackedData<Integer> BOOST_TIME = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> COLD = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final SaddledComponent saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);
	private TemptGoal temptGoal;
	private EscapeDangerGoal escapeDangerGoal;

	public StriderEntity(EntityType<? extends StriderEntity> entityType, World world) {
		super(entityType, world);
		this.field_23807 = true;
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
		this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
	}

	public static boolean canSpawn(EntityType<StriderEntity> type, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
		return world.getBlockState(pos.up()).isAir();
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (BOOST_TIME.equals(data) && this.world.isClient) {
			this.saddledComponent.boost();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BOOST_TIME, 0);
		this.dataTracker.startTracking(COLD, false);
		this.dataTracker.startTracking(SADDLED, false);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		this.saddledComponent.toTag(tag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.saddledComponent.fromTag(tag);
	}

	@Override
	public boolean isSaddled() {
		return this.saddledComponent.isSaddled();
	}

	@Override
	public boolean canBeSaddled() {
		return this.isAlive() && !this.isBaby();
	}

	@Override
	public void saddle(@Nullable SoundCategory sound) {
		this.saddledComponent.setSaddled(true);
		if (sound != null) {
			this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_STRIDER_SADDLE, sound, 0.5F, 1.0F);
		}
	}

	@Override
	protected void initGoals() {
		this.escapeDangerGoal = new EscapeDangerGoal(this, 1.65);
		this.goalSelector.add(1, this.escapeDangerGoal);
		this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
		this.temptGoal = new TemptGoal(this, 1.4, false, ATTRACTING_INGREDIENT);
		this.goalSelector.add(4, this.temptGoal);
		this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(7, new WanderAroundGoal(this, 1.0, 60));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.goalSelector.add(9, new LookAtEntityGoal(this, StriderEntity.class, 8.0F));
	}

	public void setCold(boolean cold) {
		this.dataTracker.set(COLD, cold);
	}

	public boolean isCold() {
		return this.dataTracker.get(COLD);
	}

	@Override
	public boolean method_26319() {
		return true;
	}

	@Nullable
	@Override
	public Box getHardCollisionBox(Entity collidingEntity) {
		return collidingEntity.isPushable() ? collidingEntity.getBoundingBox() : null;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public double getMountedHeightOffset() {
		float f = Math.min(0.25F, this.limbDistance);
		float g = this.limbAngle;
		return 1.4 + (double)(0.12F * MathHelper.cos(g * 1.5F) * 2.0F * f);
	}

	@Override
	public boolean canBeControlledByRider() {
		Entity entity = this.getPrimaryPassenger();
		if (!(entity instanceof PlayerEntity)) {
			return false;
		} else {
			PlayerEntity playerEntity = (PlayerEntity)entity;
			return playerEntity.getMainHandStack().getItem() == Items.WARPED_FUNGUS_ON_A_STICK
				|| playerEntity.getOffHandStack().getItem() == Items.WARPED_FUNGUS_ON_A_STICK;
		}
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.intersectsEntities(this);
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		return this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
	}

	@Override
	public void travel(Vec3d movementInput) {
		this.setMovementSpeed(this.getSpeed());
		this.travel(this, this.saddledComponent, movementInput);
	}

	public float getSpeed() {
		return (float)this.method_26825(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (this.isCold() ? 0.66F : 1.0F);
	}

	@Override
	public float getSaddledSpeed() {
		return (float)this.method_26825(EntityAttributes.GENERIC_MOVEMENT_SPEED) * (this.isCold() ? 0.23F : 0.55F);
	}

	@Override
	public void setMovementInput(Vec3d movementInput) {
		super.travel(movementInput);
	}

	@Override
	protected float calculateNextStepSoundDistance() {
		return this.distanceTraveled + 0.6F;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(this.isInLava() ? SoundEvents.ENTITY_STRIDER_STEP_LAVA : SoundEvents.ENTITY_STRIDER_STEP, 1.0F, 1.0F);
	}

	@Override
	public boolean consumeOnAStickItem() {
		return this.saddledComponent.boost(this.getRandom());
	}

	@Override
	public void tick() {
		if (this.temptGoal != null && this.temptGoal.isActive() && this.random.nextInt(100) == 0) {
			this.playSound(SoundEvents.ENTITY_STRIDER_HAPPY, 1.0F, this.getSoundPitch());
		}

		if (this.escapeDangerGoal != null && this.escapeDangerGoal.method_26337() && this.random.nextInt(60) == 0) {
			this.playSound(SoundEvents.ENTITY_STRIDER_RETREAT, 1.0F, this.getSoundPitch());
		}

		BlockState blockState = this.world.getBlockState(this.getBlockPos());
		BlockState blockState2 = this.getLandingBlockState();
		boolean bl = blockState.isIn(BlockTags.STRIDER_WARM_BLOCKS) || blockState2.isIn(BlockTags.STRIDER_WARM_BLOCKS);
		this.setCold(!bl && !this.hasVehicle());
		if (this.isInLava()) {
			this.onGround = true;
		}

		super.tick();
		this.method_26347();
		this.checkBlockCollision();
	}

	@Override
	protected boolean method_26323() {
		return true;
	}

	public float method_26346() {
		Box box = this.getBoundingBox();
		float f = -1.0F;
		float g = 0.0F;
		BlockPos.Mutable mutable = new BlockPos.Mutable(box.getCenter().x, box.y1 + 0.5, box.getCenter().z);

		for (FluidState fluidState = this.world.getFluidState(mutable); fluidState.matches(FluidTags.LAVA); fluidState = this.world.getFluidState(mutable)) {
			f = (float)mutable.getY();
			g = fluidState.getHeight(this.world, mutable);
			mutable.move(0, 1, 0);
		}

		return f + g;
	}

	private void method_26347() {
		Vec3d vec3d = this.getVelocity();
		Box box = this.getBoundingBox();
		if (this.isInLava()) {
			boolean bl = box.y1 <= (double)this.method_26346() - (this.isBaby() ? 0.0 : 0.25);
			this.setVelocity(vec3d.x, bl ? vec3d.y + 0.01 : -0.01, vec3d.z);
		}
	}

	public static DefaultAttributeContainer.Builder createStriderAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15F).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_STRIDER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_STRIDER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_STRIDER_DEATH;
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().isEmpty() && !this.isSubmergedIn(FluidTags.LAVA);
	}

	@Override
	protected void mobTick() {
		if (this.isWet()) {
			this.damage(DamageSource.DROWN, 1.0F);
		}

		super.mobTick();
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	public boolean hasNoGravity() {
		return this.isInLava() || super.hasNoGravity();
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new StriderEntity.Navigation(this, world);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return world.getBlockState(pos).getFluidState().matches(FluidTags.LAVA) ? 10.0F : 0.0F;
	}

	public StriderEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.STRIDER.create(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_INGREDIENT.test(stack);
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.isSaddled()) {
			this.dropItem(Items.SADDLE);
		}
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		boolean bl = this.isBreedingItem(player.getStackInHand(hand));
		if (super.interactMob(player, hand)) {
			if (bl && !this.isSilent()) {
				this.world
					.playSound(
						null,
						this.getX(),
						this.getY(),
						this.getZ(),
						SoundEvents.ENTITY_STRIDER_EAT,
						this.getSoundCategory(),
						1.0F,
						1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
					);
			}

			return false;
		} else {
			ItemStack itemStack = player.getStackInHand(hand);
			if (itemStack.getItem() == Items.NAME_TAG) {
				itemStack.useOnEntity(player, this, hand);
				return true;
			} else if (this.isSaddled() && !this.hasPassengers() && !this.isBaby()) {
				if (!this.world.isClient) {
					player.startRiding(this);
				}

				return true;
			} else {
				return itemStack.getItem() == Items.SADDLE && itemStack.useOnEntity(player, this, hand);
			}
		}
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		StriderEntity.StriderData.RiderType riderType;
		if (entityData instanceof StriderEntity.StriderData) {
			riderType = ((StriderEntity.StriderData)entityData).type;
		} else {
			if (this.random.nextInt(30) == 0) {
				riderType = StriderEntity.StriderData.RiderType.PIGLIN_RIDER;
			} else if (this.random.nextInt(10) == 0) {
				riderType = StriderEntity.StriderData.RiderType.BABY_RIDER;
			} else {
				riderType = StriderEntity.StriderData.RiderType.NO_RIDER;
			}

			entityData = new StriderEntity.StriderData(riderType);
			((PassiveEntity.PassiveData)entityData).setBabyChance(riderType == StriderEntity.StriderData.RiderType.NO_RIDER ? 0.5F : 0.0F);
		}

		MobEntity mobEntity = null;
		if (riderType == StriderEntity.StriderData.RiderType.BABY_RIDER) {
			StriderEntity striderEntity = EntityType.STRIDER.create(this.world);
			if (striderEntity != null) {
				mobEntity = striderEntity;
				striderEntity.setBreedingAge(-24000);
			}
		} else if (riderType == StriderEntity.StriderData.RiderType.PIGLIN_RIDER) {
			ZombifiedPiglinEntity zombifiedPiglinEntity = EntityType.ZOMBIFIED_PIGLIN.create(this.world);
			if (zombifiedPiglinEntity != null) {
				mobEntity = zombifiedPiglinEntity;
				this.saddle(null);
			}
		}

		if (mobEntity != null) {
			mobEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, 0.0F);
			mobEntity.initialize(world, difficulty, SpawnType.JOCKEY, null, null);
			world.spawnEntity(mobEntity);
			mobEntity.startRiding(this);
		}

		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}

	static class Navigation extends MobNavigation {
		Navigation(StriderEntity entity, World world) {
			super(entity, world);
		}

		@Override
		protected PathNodeNavigator createPathNodeNavigator(int range) {
			this.nodeMaker = new LandPathNodeMaker();
			return new PathNodeNavigator(this.nodeMaker, range);
		}

		@Override
		protected boolean method_26338(PathNodeType pathNodeType) {
			return pathNodeType != PathNodeType.LAVA && pathNodeType != PathNodeType.DAMAGE_FIRE && pathNodeType != PathNodeType.DANGER_FIRE
				? super.method_26338(pathNodeType)
				: true;
		}

		@Override
		public boolean isValidPosition(BlockPos pos) {
			return this.world.getBlockState(pos).getBlock() == Blocks.LAVA || super.isValidPosition(pos);
		}
	}

	public static class StriderData extends PassiveEntity.PassiveData {
		public final StriderEntity.StriderData.RiderType type;

		public StriderData(StriderEntity.StriderData.RiderType type) {
			this.type = type;
		}

		public static enum RiderType {
			NO_RIDER,
			BABY_RIDER,
			PIGLIN_RIDER;
		}
	}
}
