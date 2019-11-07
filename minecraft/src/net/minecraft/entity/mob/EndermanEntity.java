package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class EndermanEntity extends HostileEntity {
	private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(
			ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", 0.15F, EntityAttributeModifier.Operation.ADDITION
		)
		.setSerialize(false);
	private static final TrackedData<Optional<BlockState>> CARRIED_BLOCK = DataTracker.registerData(
		EndermanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE
	);
	private static final TrackedData<Boolean> ANGRY = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_20618 = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final Predicate<LivingEntity> PLAYER_ENDERMITE_PREDICATE = livingEntity -> livingEntity instanceof EndermiteEntity
			&& ((EndermiteEntity)livingEntity).isPlayerSpawned();
	private int lastAngrySoundAge = Integer.MIN_VALUE;
	private int ageWhenTargetSet;

	public EndermanEntity(EntityType<? extends EndermanEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EndermanEntity.ChasePlayerGoal(this));
		this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0, 0.0F));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.goalSelector.add(10, new EndermanEntity.PlaceBlockGoal(this));
		this.goalSelector.add(11, new EndermanEntity.PickUpBlockGoal(this));
		this.targetSelector.add(1, new EndermanEntity.TeleportTowardsPlayerGoal(this));
		this.targetSelector.add(2, new RevengeGoal(this));
		this.targetSelector.add(3, new FollowTargetGoal(this, EndermiteEntity.class, 10, true, false, PLAYER_ENDERMITE_PREDICATE));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(64.0);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		super.setTarget(target);
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		if (target == null) {
			this.ageWhenTargetSet = 0;
			this.dataTracker.set(ANGRY, false);
			this.dataTracker.set(field_20618, false);
			entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
		} else {
			this.ageWhenTargetSet = this.age;
			this.dataTracker.set(ANGRY, true);
			if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
				entityAttributeInstance.addModifier(ATTACKING_SPEED_BOOST);
			}
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CARRIED_BLOCK, Optional.empty());
		this.dataTracker.startTracking(ANGRY, false);
		this.dataTracker.startTracking(field_20618, false);
	}

	public void playAngrySound() {
		if (this.age >= this.lastAngrySoundAge + 400) {
			this.lastAngrySoundAge = this.age;
			if (!this.isSilent()) {
				this.world.playSound(this.getX(), this.method_23320(), this.getZ(), SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (ANGRY.equals(data) && this.method_22330() && this.world.isClient) {
			this.playAngrySound();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		BlockState blockState = this.getCarriedBlock();
		if (blockState != null) {
			tag.put("carriedBlockState", NbtHelper.fromBlockState(blockState));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		BlockState blockState = null;
		if (tag.contains("carriedBlockState", 10)) {
			blockState = NbtHelper.toBlockState(tag.getCompound("carriedBlockState"));
			if (blockState.isAir()) {
				blockState = null;
			}
		}

		this.setCarriedBlock(blockState);
	}

	private boolean isPlayerStaring(PlayerEntity player) {
		ItemStack itemStack = player.inventory.armor.get(3);
		if (itemStack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
			return false;
		} else {
			Vec3d vec3d = player.getRotationVec(1.0F).normalize();
			Vec3d vec3d2 = new Vec3d(this.getX() - player.getX(), this.method_23320() - player.method_23320(), this.getZ() - player.getZ());
			double d = vec3d2.length();
			vec3d2 = vec3d2.normalize();
			double e = vec3d.dotProduct(vec3d2);
			return e > 1.0 - 0.025 / d ? player.canSee(this) : false;
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 2.55F;
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			for (int i = 0; i < 2; i++) {
				this.world
					.addParticle(
						ParticleTypes.PORTAL,
						this.method_23322(0.5),
						this.method_23319() - 0.25,
						this.method_23325(0.5),
						(this.random.nextDouble() - 0.5) * 2.0,
						-this.random.nextDouble(),
						(this.random.nextDouble() - 0.5) * 2.0
					);
			}
		}

		this.jumping = false;
		super.tickMovement();
	}

	@Override
	protected void mobTick() {
		if (this.isTouchingWater()) {
			this.damage(DamageSource.DROWN, 1.0F);
		}

		if (this.world.isDaylight() && this.age >= this.ageWhenTargetSet + 600) {
			float f = this.getBrightnessAtEyes();
			if (f > 0.5F && this.world.isSkyVisible(new BlockPos(this)) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				this.setTarget(null);
				this.teleportRandomly();
			}
		}

		super.mobTick();
	}

	protected boolean teleportRandomly() {
		if (!this.world.isClient() && this.isAlive()) {
			double d = this.getX() + (this.random.nextDouble() - 0.5) * 64.0;
			double e = this.getY() + (double)(this.random.nextInt(64) - 32);
			double f = this.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
			return this.teleport(d, e, f);
		} else {
			return false;
		}
	}

	private boolean teleportTo(Entity entity) {
		Vec3d vec3d = new Vec3d(this.getX() - entity.getX(), this.getHeightAt(0.5) - entity.method_23320(), this.getZ() - entity.getZ());
		vec3d = vec3d.normalize();
		double d = 16.0;
		double e = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.x * 16.0;
		double f = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3d.y * 16.0;
		double g = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.z * 16.0;
		return this.teleport(e, f, g);
	}

	private boolean teleport(double x, double y, double z) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

		while (mutable.getY() > 0 && !this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
			mutable.setOffset(Direction.DOWN);
		}

		BlockState blockState = this.world.getBlockState(mutable);
		boolean bl = blockState.getMaterial().blocksMovement();
		boolean bl2 = blockState.getFluidState().matches(FluidTags.WATER);
		if (bl && !bl2) {
			boolean bl3 = this.teleport(x, y, z, true);
			if (bl3) {
				this.world.playSound(null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
				this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
			}

			return bl3;
		} else {
			return false;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isAngry() ? SoundEvents.ENTITY_ENDERMAN_SCREAM : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ENDERMAN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ENDERMAN_DEATH;
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		BlockState blockState = this.getCarriedBlock();
		if (blockState != null) {
			this.dropItem(blockState.getBlock());
		}
	}

	public void setCarriedBlock(@Nullable BlockState blockState) {
		this.dataTracker.set(CARRIED_BLOCK, Optional.ofNullable(blockState));
	}

	@Nullable
	public BlockState getCarriedBlock() {
		return (BlockState)this.dataTracker.get(CARRIED_BLOCK).orElse(null);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (!(source instanceof ProjectileDamageSource) && source != DamageSource.FIREWORKS) {
			boolean bl = super.damage(source, amount);
			if (!this.world.isClient() && source.bypassesArmor() && this.random.nextInt(10) != 0) {
				this.teleportRandomly();
			}

			return bl;
		} else {
			for (int i = 0; i < 64; i++) {
				if (this.teleportRandomly()) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean isAngry() {
		return this.dataTracker.get(ANGRY);
	}

	public boolean method_22330() {
		return this.dataTracker.get(field_20618);
	}

	public void method_22331() {
		this.dataTracker.set(field_20618, true);
	}

	static class ChasePlayerGoal extends Goal {
		private final EndermanEntity enderman;
		private LivingEntity field_21513;

		public ChasePlayerGoal(EndermanEntity enderman) {
			this.enderman = enderman;
			this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			this.field_21513 = this.enderman.getTarget();
			if (!(this.field_21513 instanceof PlayerEntity)) {
				return false;
			} else {
				double d = this.field_21513.squaredDistanceTo(this.enderman);
				return d > 256.0 ? false : this.enderman.isPlayerStaring((PlayerEntity)this.field_21513);
			}
		}

		@Override
		public void start() {
			this.enderman.getNavigation().stop();
		}

		@Override
		public void tick() {
			this.enderman.getLookControl().lookAt(this.field_21513.getX(), this.field_21513.method_23320(), this.field_21513.getZ());
		}
	}

	static class PickUpBlockGoal extends Goal {
		private final EndermanEntity enderman;

		public PickUpBlockGoal(EndermanEntity enderman) {
			this.enderman = enderman;
		}

		@Override
		public boolean canStart() {
			if (this.enderman.getCarriedBlock() != null) {
				return false;
			} else {
				return !this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? false : this.enderman.getRandom().nextInt(20) == 0;
			}
		}

		@Override
		public void tick() {
			Random random = this.enderman.getRandom();
			World world = this.enderman.world;
			int i = MathHelper.floor(this.enderman.getX() - 2.0 + random.nextDouble() * 4.0);
			int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 3.0);
			int k = MathHelper.floor(this.enderman.getZ() - 2.0 + random.nextDouble() * 4.0);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			Vec3d vec3d = new Vec3d((double)MathHelper.floor(this.enderman.getX()) + 0.5, (double)j + 0.5, (double)MathHelper.floor(this.enderman.getZ()) + 0.5);
			Vec3d vec3d2 = new Vec3d((double)i + 0.5, (double)j + 0.5, (double)k + 0.5);
			BlockHitResult blockHitResult = world.rayTrace(
				new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, this.enderman)
			);
			boolean bl = blockHitResult.getBlockPos().equals(blockPos);
			if (block.matches(BlockTags.ENDERMAN_HOLDABLE) && bl) {
				this.enderman.setCarriedBlock(blockState);
				world.removeBlock(blockPos, false);
			}
		}
	}

	static class PlaceBlockGoal extends Goal {
		private final EndermanEntity enderman;

		public PlaceBlockGoal(EndermanEntity enderman) {
			this.enderman = enderman;
		}

		@Override
		public boolean canStart() {
			if (this.enderman.getCarriedBlock() == null) {
				return false;
			} else {
				return !this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? false : this.enderman.getRandom().nextInt(2000) == 0;
			}
		}

		@Override
		public void tick() {
			Random random = this.enderman.getRandom();
			IWorld iWorld = this.enderman.world;
			int i = MathHelper.floor(this.enderman.getX() - 1.0 + random.nextDouble() * 2.0);
			int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 2.0);
			int k = MathHelper.floor(this.enderman.getZ() - 1.0 + random.nextDouble() * 2.0);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = iWorld.getBlockState(blockPos);
			BlockPos blockPos2 = blockPos.method_10074();
			BlockState blockState2 = iWorld.getBlockState(blockPos2);
			BlockState blockState3 = this.enderman.getCarriedBlock();
			if (blockState3 != null && this.method_7033(iWorld, blockPos, blockState3, blockState, blockState2, blockPos2)) {
				iWorld.setBlockState(blockPos, blockState3, 3);
				this.enderman.setCarriedBlock(null);
			}
		}

		private boolean method_7033(WorldView worldView, BlockPos blockPos, BlockState blockState, BlockState blockState2, BlockState blockState3, BlockPos blockPos2) {
			return blockState2.isAir() && !blockState3.isAir() && blockState3.isFullCube(worldView, blockPos2) && blockState.canPlaceAt(worldView, blockPos);
		}
	}

	static class TeleportTowardsPlayerGoal extends FollowTargetGoal<PlayerEntity> {
		private final EndermanEntity enderman;
		private PlayerEntity targetPlayer;
		private int lookAtPlayerWarmup;
		private int ticksSinceUnseenTeleport;
		private final TargetPredicate staringPlayerPredicate;
		private final TargetPredicate validTargetPredicate = new TargetPredicate().includeHidden();

		public TeleportTowardsPlayerGoal(EndermanEntity enderman) {
			super(enderman, PlayerEntity.class, false);
			this.enderman = enderman;
			this.staringPlayerPredicate = new TargetPredicate()
				.setBaseMaxDistance(this.getFollowRange())
				.setPredicate(playerEntity -> enderman.isPlayerStaring((PlayerEntity)playerEntity));
		}

		@Override
		public boolean canStart() {
			this.targetPlayer = this.enderman.world.getClosestPlayer(this.staringPlayerPredicate, this.enderman);
			return this.targetPlayer != null;
		}

		@Override
		public void start() {
			this.lookAtPlayerWarmup = 5;
			this.ticksSinceUnseenTeleport = 0;
			this.enderman.method_22331();
		}

		@Override
		public void stop() {
			this.targetPlayer = null;
			super.stop();
		}

		@Override
		public boolean shouldContinue() {
			if (this.targetPlayer != null) {
				if (!this.enderman.isPlayerStaring(this.targetPlayer)) {
					return false;
				} else {
					this.enderman.lookAtEntity(this.targetPlayer, 10.0F, 10.0F);
					return true;
				}
			} else {
				return this.targetEntity != null && this.validTargetPredicate.test(this.enderman, this.targetEntity) ? true : super.shouldContinue();
			}
		}

		@Override
		public void tick() {
			if (this.targetPlayer != null) {
				if (--this.lookAtPlayerWarmup <= 0) {
					this.targetEntity = this.targetPlayer;
					this.targetPlayer = null;
					super.start();
				}
			} else {
				if (this.targetEntity != null && !this.enderman.hasVehicle()) {
					if (this.enderman.isPlayerStaring((PlayerEntity)this.targetEntity)) {
						if (this.targetEntity.squaredDistanceTo(this.enderman) < 16.0) {
							this.enderman.teleportRandomly();
						}

						this.ticksSinceUnseenTeleport = 0;
					} else if (this.targetEntity.squaredDistanceTo(this.enderman) > 256.0
						&& this.ticksSinceUnseenTeleport++ >= 30
						&& this.enderman.teleportTo(this.targetEntity)) {
						this.ticksSinceUnseenTeleport = 0;
					}
				}

				super.tick();
			}
		}
	}
}
