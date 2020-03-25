package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
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
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
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

public class class_4985 extends AnimalEntity implements class_4981 {
	private static final Ingredient field_23243 = Ingredient.ofItems(Items.WARPED_FUNGUS);
	private static final Ingredient field_23244 = Ingredient.ofItems(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
	private static final TrackedData<Integer> field_23245 = DataTracker.registerData(class_4985.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_23246 = DataTracker.registerData(class_4985.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_23247 = DataTracker.registerData(class_4985.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final class_4980 field_23240 = new class_4980(this.dataTracker, field_23245, field_23247);
	private TemptGoal field_23241;
	private EscapeDangerGoal field_23242;

	public class_4985(EntityType<? extends class_4985> entityType, World world) {
		super(entityType, world);
		this.inanimate = true;
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
		this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
	}

	public static boolean method_26344(EntityType<class_4985> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return blockPos.getY() <= 31;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (field_23245.equals(data) && this.world.isClient) {
			this.field_23240.method_26307();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_23245, 0);
		this.dataTracker.startTracking(field_23246, false);
		this.dataTracker.startTracking(field_23247, false);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		this.field_23240.method_26309(tag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.field_23240.method_26312(tag);
	}

	@Override
	public boolean isSaddled() {
		return this.field_23240.method_26311();
	}

	@Override
	public void setSaddled(boolean bl) {
		this.field_23240.method_26310(bl);
	}

	@Override
	protected void initGoals() {
		this.field_23242 = new EscapeDangerGoal(this, 1.65);
		this.goalSelector.add(1, this.field_23242);
		this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
		this.field_23241 = new TemptGoal(this, 1.4, false, field_23244);
		this.goalSelector.add(4, this.field_23241);
		this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(7, new WanderAroundGoal(this, 1.0, 60));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.goalSelector.add(9, new LookAtEntityGoal(this, class_4985.class, 8.0F));
	}

	public void method_26349(boolean bl) {
		this.dataTracker.set(field_23246, bl);
	}

	public boolean method_26348() {
		return this.dataTracker.get(field_23246);
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
		this.setMovementSpeed(this.method_26345());
		this.method_26313(this, this.field_23240, movementInput);
	}

	public float method_26345() {
		return this.method_26348() ? 0.1F : (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue();
	}

	@Override
	public float method_26316() {
		return this.method_26345() * 0.35F;
	}

	@Override
	public void method_26315(Vec3d vec3d) {
		super.travel(vec3d);
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
	public boolean method_6577() {
		return this.field_23240.method_26308(this.getRandom());
	}

	@Override
	public void tick() {
		if (this.field_23241 != null && this.field_23241.isActive() && this.random.nextInt(100) == 0) {
			this.playSound(SoundEvents.ENTITY_STRIDER_HAPPY, 1.0F, this.getSoundPitch());
		}

		if (this.field_23242 != null && this.field_23242.method_26337() && this.random.nextInt(60) == 0) {
			this.playSound(SoundEvents.ENTITY_STRIDER_RETREAT, 1.0F, this.getSoundPitch());
		}

		BlockState blockState = this.world.getBlockState(this.getBlockPos());
		BlockState blockState2 = this.method_25936();
		boolean bl = blockState.isIn(BlockTags.STRIDER_WARM_BLOCKS) || blockState2.isIn(BlockTags.STRIDER_WARM_BLOCKS);
		this.method_26349(!bl && !this.hasVehicle());
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

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.15F);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
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
		return new class_4985.class_4988(this, world);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return world.getBlockState(pos).getFluidState().matches(FluidTags.LAVA) ? 10.0F : 0.0F;
	}

	public class_4985 createChild(PassiveEntity passiveEntity) {
		return EntityType.STRIDER.create(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return field_23243.test(stack);
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
		if (!super.interactMob(player, hand)) {
			return this.method_26314(this, player, hand, false);
		} else {
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
		}
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		class_4985.class_4986.class_4987 lv;
		if (entityData instanceof class_4985.class_4986) {
			lv = ((class_4985.class_4986)entityData).field_23248;
		} else {
			if (this.random.nextInt(30) == 0) {
				lv = class_4985.class_4986.class_4987.field_23251;
			} else if (this.random.nextInt(10) == 0) {
				lv = class_4985.class_4986.class_4987.field_23250;
			} else {
				lv = class_4985.class_4986.class_4987.field_23249;
			}

			entityData = new class_4985.class_4986(lv);
			((PassiveEntity.PassiveData)entityData).setBabyChance(lv == class_4985.class_4986.class_4987.field_23249 ? 0.5F : 0.0F);
		}

		MobEntity mobEntity = null;
		if (lv == class_4985.class_4986.class_4987.field_23250) {
			class_4985 lv2 = EntityType.STRIDER.create(this.world);
			if (lv2 != null) {
				mobEntity = lv2;
				lv2.setBreedingAge(-24000);
			}
		} else if (lv == class_4985.class_4986.class_4987.field_23251) {
			ZombifiedPiglinEntity zombifiedPiglinEntity = EntityType.ZOMBIFIED_PIGLIN.create(this.world);
			if (zombifiedPiglinEntity != null) {
				mobEntity = zombifiedPiglinEntity;
				this.setSaddled(true);
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

	public static class class_4986 extends PassiveEntity.PassiveData {
		public final class_4985.class_4986.class_4987 field_23248;

		public class_4986(class_4985.class_4986.class_4987 arg) {
			this.field_23248 = arg;
		}

		public static enum class_4987 {
			field_23249,
			field_23250,
			field_23251;
		}
	}

	static class class_4988 extends MobNavigation {
		class_4988(class_4985 arg, World world) {
			super(arg, world);
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
}
