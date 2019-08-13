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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.TagHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class EndermanEntity extends HostileEntity {
	private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(
			ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", 0.15F, EntityAttributeModifier.Operation.field_6328
		)
		.setSerialize(false);
	private static final TrackedData<Optional<BlockState>> CARRIED_BLOCK = DataTracker.registerData(
		EndermanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE
	);
	private static final TrackedData<Boolean> ANGRY = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final Predicate<LivingEntity> PLAYER_ENDERMITE_PREDICATE = livingEntity -> livingEntity instanceof EndermiteEntity
			&& ((EndermiteEntity)livingEntity).isPlayerSpawned();
	private int lastAngrySoundAge;
	private int ageWhenTargetSet;

	public EndermanEntity(EntityType<? extends EndermanEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
		this.setPathNodeTypeWeight(PathNodeType.field_18, -1.0F);
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
	public void setTarget(@Nullable LivingEntity livingEntity) {
		super.setTarget(livingEntity);
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		if (livingEntity == null) {
			this.ageWhenTargetSet = 0;
			this.dataTracker.set(ANGRY, false);
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
	}

	public void playAngrySound() {
		if (this.age >= this.lastAngrySoundAge + 400) {
			this.lastAngrySoundAge = this.age;
			if (!this.isSilent()) {
				this.world.playSound(this.x, this.y + (double)this.getStandingEyeHeight(), this.z, SoundEvents.field_14967, this.getSoundCategory(), 2.5F, 1.0F, false);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (ANGRY.equals(trackedData) && this.isAngry() && this.world.isClient) {
			this.playAngrySound();
		}

		super.onTrackedDataSet(trackedData);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		BlockState blockState = this.getCarriedBlock();
		if (blockState != null) {
			compoundTag.put("carriedBlockState", TagHelper.serializeBlockState(blockState));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		BlockState blockState = null;
		if (compoundTag.containsKey("carriedBlockState", 10)) {
			blockState = TagHelper.deserializeBlockState(compoundTag.getCompound("carriedBlockState"));
			if (blockState.isAir()) {
				blockState = null;
			}
		}

		this.setCarriedBlock(blockState);
	}

	private boolean isPlayerStaring(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.inventory.armor.get(3);
		if (itemStack.getItem() == Blocks.field_10147.asItem()) {
			return false;
		} else {
			Vec3d vec3d = playerEntity.getRotationVec(1.0F).normalize();
			Vec3d vec3d2 = new Vec3d(
				this.x - playerEntity.x,
				this.getBoundingBox().minY + (double)this.getStandingEyeHeight() - (playerEntity.y + (double)playerEntity.getStandingEyeHeight()),
				this.z - playerEntity.z
			);
			double d = vec3d2.length();
			vec3d2 = vec3d2.normalize();
			double e = vec3d.dotProduct(vec3d2);
			return e > 1.0 - 0.025 / d ? playerEntity.canSee(this) : false;
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 2.55F;
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			for (int i = 0; i < 2; i++) {
				this.world
					.addParticle(
						ParticleTypes.field_11214,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						this.y + this.random.nextDouble() * (double)this.getHeight() - 0.25,
						this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
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
		double d = this.x + (this.random.nextDouble() - 0.5) * 64.0;
		double e = this.y + (double)(this.random.nextInt(64) - 32);
		double f = this.z + (this.random.nextDouble() - 0.5) * 64.0;
		return this.teleport(d, e, f);
	}

	private boolean teleportTo(Entity entity) {
		Vec3d vec3d = new Vec3d(
			this.x - entity.x, this.getBoundingBox().minY + (double)(this.getHeight() / 2.0F) - entity.y + (double)entity.getStandingEyeHeight(), this.z - entity.z
		);
		vec3d = vec3d.normalize();
		double d = 16.0;
		double e = this.x + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.x * 16.0;
		double f = this.y + (double)(this.random.nextInt(16) - 8) - vec3d.y * 16.0;
		double g = this.z + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.z * 16.0;
		return this.teleport(e, f, g);
	}

	private boolean teleport(double d, double e, double f) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(d, e, f);

		while (mutable.getY() > 0 && !this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
			mutable.setOffset(Direction.field_11033);
		}

		if (!this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
			return false;
		} else {
			boolean bl = this.teleport(d, e, f, true);
			if (bl) {
				this.world.playSound(null, this.prevX, this.prevY, this.prevZ, SoundEvents.field_14879, this.getSoundCategory(), 1.0F, 1.0F);
				this.playSound(SoundEvents.field_14879, 1.0F, 1.0F);
			}

			return bl;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isAngry() ? SoundEvents.field_14713 : SoundEvents.field_14696;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14797;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14608;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
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
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (!(damageSource instanceof ProjectileDamageSource) && damageSource != DamageSource.FIREWORKS) {
			boolean bl = super.damage(damageSource, f);
			if (damageSource.bypassesArmor() && this.random.nextInt(10) != 0) {
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

	static class ChasePlayerGoal extends Goal {
		private final EndermanEntity enderman;

		public ChasePlayerGoal(EndermanEntity endermanEntity) {
			this.enderman = endermanEntity;
			this.setControls(EnumSet.of(Goal.Control.field_18407, Goal.Control.field_18405));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.enderman.getTarget();
			if (!(livingEntity instanceof PlayerEntity)) {
				return false;
			} else {
				double d = livingEntity.squaredDistanceTo(this.enderman);
				return d > 256.0 ? false : this.enderman.isPlayerStaring((PlayerEntity)livingEntity);
			}
		}

		@Override
		public void start() {
			this.enderman.getNavigation().stop();
		}
	}

	static class PickUpBlockGoal extends Goal {
		private final EndermanEntity enderman;

		public PickUpBlockGoal(EndermanEntity endermanEntity) {
			this.enderman = endermanEntity;
		}

		@Override
		public boolean canStart() {
			if (this.enderman.getCarriedBlock() != null) {
				return false;
			} else {
				return !this.enderman.world.getGameRules().getBoolean(GameRules.field_19388) ? false : this.enderman.getRand().nextInt(20) == 0;
			}
		}

		@Override
		public void tick() {
			Random random = this.enderman.getRand();
			World world = this.enderman.world;
			int i = MathHelper.floor(this.enderman.x - 2.0 + random.nextDouble() * 4.0);
			int j = MathHelper.floor(this.enderman.y + random.nextDouble() * 3.0);
			int k = MathHelper.floor(this.enderman.z - 2.0 + random.nextDouble() * 4.0);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			Vec3d vec3d = new Vec3d((double)MathHelper.floor(this.enderman.x) + 0.5, (double)j + 0.5, (double)MathHelper.floor(this.enderman.z) + 0.5);
			Vec3d vec3d2 = new Vec3d((double)i + 0.5, (double)j + 0.5, (double)k + 0.5);
			BlockHitResult blockHitResult = world.rayTrace(
				new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.field_1348, this.enderman)
			);
			boolean bl = blockHitResult.getType() != HitResult.Type.field_1333 && blockHitResult.getBlockPos().equals(blockPos);
			if (block.matches(BlockTags.field_15460) && bl) {
				this.enderman.setCarriedBlock(blockState);
				world.clearBlockState(blockPos, false);
			}
		}
	}

	static class PlaceBlockGoal extends Goal {
		private final EndermanEntity enderman;

		public PlaceBlockGoal(EndermanEntity endermanEntity) {
			this.enderman = endermanEntity;
		}

		@Override
		public boolean canStart() {
			if (this.enderman.getCarriedBlock() == null) {
				return false;
			} else {
				return !this.enderman.world.getGameRules().getBoolean(GameRules.field_19388) ? false : this.enderman.getRand().nextInt(2000) == 0;
			}
		}

		@Override
		public void tick() {
			Random random = this.enderman.getRand();
			IWorld iWorld = this.enderman.world;
			int i = MathHelper.floor(this.enderman.x - 1.0 + random.nextDouble() * 2.0);
			int j = MathHelper.floor(this.enderman.y + random.nextDouble() * 2.0);
			int k = MathHelper.floor(this.enderman.z - 1.0 + random.nextDouble() * 2.0);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = iWorld.getBlockState(blockPos);
			BlockPos blockPos2 = blockPos.down();
			BlockState blockState2 = iWorld.getBlockState(blockPos2);
			BlockState blockState3 = this.enderman.getCarriedBlock();
			if (blockState3 != null && this.method_7033(iWorld, blockPos, blockState3, blockState, blockState2, blockPos2)) {
				iWorld.setBlockState(blockPos, blockState3, 3);
				this.enderman.setCarriedBlock(null);
			}
		}

		private boolean method_7033(
			ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState, BlockState blockState2, BlockState blockState3, BlockPos blockPos2
		) {
			return blockState2.isAir() && !blockState3.isAir() && blockState3.method_21743(viewableWorld, blockPos2) && blockState.canPlaceAt(viewableWorld, blockPos);
		}
	}

	static class TeleportTowardsPlayerGoal extends FollowTargetGoal<PlayerEntity> {
		private final EndermanEntity enderman;
		private PlayerEntity targetPlayer;
		private int lookAtPlayerWarmup;
		private int ticksSinceUnseenTeleport;
		private final TargetPredicate staringPlayerPredicate;
		private final TargetPredicate validTargetPredicate = new TargetPredicate().includeHidden();

		public TeleportTowardsPlayerGoal(EndermanEntity endermanEntity) {
			super(endermanEntity, PlayerEntity.class, false);
			this.enderman = endermanEntity;
			this.staringPlayerPredicate = new TargetPredicate()
				.setBaseMaxDistance(this.getFollowRange())
				.setPredicate(livingEntity -> endermanEntity.isPlayerStaring((PlayerEntity)livingEntity));
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
