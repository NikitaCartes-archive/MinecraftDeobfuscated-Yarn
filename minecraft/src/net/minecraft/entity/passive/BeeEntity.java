package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.entity.BeeHiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class BeeEntity extends AnimalEntity implements Flutterer {
	private static final TrackedData<Byte> multipleByteTracker = DataTracker.registerData(BeeEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> anger = DataTracker.registerData(BeeEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private UUID targetPlayer;
	private float currentPitch;
	private float lastPitch;
	private int ticksSinceSting;
	private int ticksSincePollination;
	private int cannotEnterHiveTicks;
	private int cropsGrownSincePollination;
	private BlockPos flowerPos = BlockPos.ORIGIN;
	private BlockPos hivePos = BlockPos.ORIGIN;

	public BeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FlightMoveControl(this, 20, true);
		this.lookControl = new BeeEntity.BeeLookControl(this);
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(multipleByteTracker, (byte)0);
		this.dataTracker.startTracking(anger, 0);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new BeeEntity.FindHiveGoal());
		this.goalSelector.add(0, new BeeEntity.StingGoal(this, 1.4F, true));
		this.goalSelector.add(1, new BeeEntity.EnterHiveGoal());
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.fromTag(ItemTags.SMALL_FLOWERS), false));
		this.goalSelector.add(4, new BeeEntity.PollinateGoal());
		this.goalSelector.add(5, new FollowParentGoal(this, 1.25));
		this.goalSelector.add(5, new BeeEntity.MoveToHiveGoal());
		this.goalSelector.add(6, new BeeEntity.MoveToFlowerGoal());
		this.goalSelector.add(7, new BeeEntity.GrowCropsGoal());
		this.goalSelector.add(8, new BeeEntity.BeeWanderAroundGoal());
		this.targetSelector.add(1, new BeeEntity.BeeRevengeGoal(this).setGroupRevenge(new Class[0]));
		this.targetSelector.add(2, new BeeEntity.BeeFollowTargetGoal(this));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.put("HivePos", NbtHelper.fromBlockPos(this.hivePos));
		compoundTag.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
		compoundTag.putBoolean("HasNectar", this.hasNectar());
		compoundTag.putBoolean("HasStung", this.hasStung());
		compoundTag.putInt("TicksSincePollination", this.ticksSincePollination);
		compoundTag.putInt("CannotEnterHiveTicks", this.cannotEnterHiveTicks);
		compoundTag.putInt("CropsGrownSincePollination", this.cropsGrownSincePollination);
		compoundTag.putInt("Anger", this.getAnger());
		if (this.targetPlayer != null) {
			compoundTag.putString("HurtBy", this.targetPlayer.toString());
		} else {
			compoundTag.putString("HurtBy", "");
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.hivePos = NbtHelper.toBlockPos(compoundTag.getCompound("HivePos"));
		this.flowerPos = NbtHelper.toBlockPos(compoundTag.getCompound("FlowerPos"));
		super.readCustomDataFromTag(compoundTag);
		this.setHasNectar(compoundTag.getBoolean("HasNectar"));
		this.setHasStung(compoundTag.getBoolean("HasStung"));
		this.setAnger(compoundTag.getInt("Anger"));
		this.ticksSincePollination = compoundTag.getInt("TicksSincePollination");
		this.cannotEnterHiveTicks = compoundTag.getInt("CannotEnterHiveTicks");
		this.cropsGrownSincePollination = compoundTag.getInt("NumCropsGrownSincePollination");
		String string = compoundTag.getString("HurtBy");
		if (!string.isEmpty()) {
			this.targetPlayer = UUID.fromString(string);
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.targetPlayer);
			this.setAttacker(playerEntity);
			if (playerEntity != null) {
				this.attackingPlayer = playerEntity;
				this.playerHitTimer = this.getLastAttackedTime();
			}
		}
	}

	@Override
	public boolean tryAttack(Entity entity) {
		boolean bl = entity.damage(DamageSource.sting(this), (float)((int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.dealDamage(this, entity);
			if (entity instanceof LivingEntity) {
				((LivingEntity)entity).setStingerCount(((LivingEntity)entity).getStingerCount() + 1);
				int i = 0;
				if (this.world.getDifficulty() == Difficulty.NORMAL) {
					i = 10;
				} else if (this.world.getDifficulty() == Difficulty.HARD) {
					i = 18;
				}

				if (i > 0) {
					((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, i * 20, 0));
				}
			}

			this.setHasStung(true);
			this.setTarget(null);
			this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05F) {
			for (int i = 0; i < this.random.nextInt(2) + 1; i++) {
				this.addParticle(
					this.world, this.x - 0.3F, this.x + 0.3F, this.z - 0.3F, this.z + 0.3F, this.y + (double)(this.getHeight() / 2.0F), ParticleTypes.FALLING_NECTAR
				);
			}
		}

		this.updateBodyPitch();
	}

	private void addParticle(World world, double d, double e, double f, double g, double h, ParticleEffect particleEffect) {
		world.addParticle(particleEffect, MathHelper.lerp(world.random.nextDouble(), d, e), h, MathHelper.lerp(world.random.nextDouble(), f, g), 0.0, 0.0, 0.0);
	}

	public BlockPos getFlowerPos() {
		return this.flowerPos;
	}

	public boolean hasFlower() {
		return this.flowerPos != BlockPos.ORIGIN;
	}

	public void setFlowerPos(BlockPos blockPos) {
		this.flowerPos = blockPos;
	}

	private boolean canEnterHive() {
		if (this.cannotEnterHiveTicks > 0) {
			return false;
		} else {
			return !this.hasHive() ? false : this.hasNectar() || !this.world.isDaylight() || this.world.hasRain(this.getBlockPos()) || this.ticksSincePollination > 3600;
		}
	}

	public void setCannotEnterHiveTicks(int i) {
		this.cannotEnterHiveTicks = i;
	}

	@Environment(EnvType.CLIENT)
	public float getBodyPitch(float f) {
		return MathHelper.lerp(f, this.lastPitch, this.currentPitch);
	}

	private void updateBodyPitch() {
		this.lastPitch = this.currentPitch;
		if (this.isNearTarget()) {
			this.currentPitch = Math.min(1.0F, this.currentPitch + 0.2F);
		} else {
			this.currentPitch = Math.max(0.0F, this.currentPitch - 0.24F);
		}
	}

	@Override
	public void setAttacker(@Nullable LivingEntity livingEntity) {
		super.setAttacker(livingEntity);
		if (livingEntity != null) {
			this.targetPlayer = livingEntity.getUuid();
		}
	}

	@Override
	protected void mobTick() {
		boolean bl = this.hasStung();
		if (bl) {
			this.ticksSinceSting++;
			if (this.ticksSinceSting % 5 == 0 && this.random.nextInt(MathHelper.clamp(1200 - this.ticksSinceSting, 1, 1200)) == 0) {
				this.damage(DamageSource.GENERIC, this.getHealth());
			}
		}

		if (this.isAngry()) {
			int i = this.getAnger();
			this.setAnger(i - 1);
			LivingEntity livingEntity = this.getTarget();
			if (i == 0 && livingEntity != null) {
				this.setBeeAttacker(livingEntity);
			}
		}

		if (!this.hasNectar()) {
			this.ticksSincePollination++;
		}
	}

	public void resetPollinationTicks() {
		this.ticksSincePollination = 0;
	}

	public boolean isAngry() {
		return this.getAnger() > 0;
	}

	public int getAnger() {
		return this.dataTracker.get(anger);
	}

	public void setAnger(int i) {
		this.dataTracker.set(anger, i);
	}

	private boolean hasHive() {
		return this.hivePos != BlockPos.ORIGIN;
	}

	private int getCropsGrownSincePollination() {
		return this.cropsGrownSincePollination;
	}

	public void resetCropCounter() {
		this.cropsGrownSincePollination = 0;
	}

	private void addCropCounter() {
		this.cropsGrownSincePollination++;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.world.isClient) {
			if (this.cannotEnterHiveTicks > 0) {
				this.cannotEnterHiveTicks--;
			}

			if (this.isPollinating() && !this.isNavigating()) {
				float f = this.random.nextBoolean() ? 2.0F : -2.0F;
				Vec3d vec3d;
				if (this.hasFlower()) {
					BlockPos blockPos = this.flowerPos.add(0.0, (double)f, 0.0);
					vec3d = new Vec3d(blockPos);
				} else {
					vec3d = this.getPos().add(0.0, (double)f, 0.0);
				}

				this.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 0.4F);
			}

			boolean bl = this.isAngry() && !this.hasStung() && this.getTarget() != null && this.getTarget().squaredDistanceTo(this) < 4.0;
			this.setNearTarget(bl);
			if (this.hasHive() && this.age % 20 == 0 && !this.isHiveValid()) {
				this.hivePos = BlockPos.ORIGIN;
			}
		}
	}

	private boolean isHiveValid() {
		if (!this.hasHive()) {
			return false;
		} else {
			BlockEntity blockEntity = this.world.getBlockEntity(this.hivePos);
			return blockEntity != null && blockEntity.getType() == BlockEntityType.BEEHIVE;
		}
	}

	public boolean hasNectar() {
		return this.getBeeFlag(8);
	}

	public void setHasNectar(boolean bl) {
		this.setBeeFlag(8, bl);
	}

	public boolean hasStung() {
		return this.getBeeFlag(4);
	}

	public void setHasStung(boolean bl) {
		this.setBeeFlag(4, bl);
	}

	public boolean isNearTarget() {
		return this.getBeeFlag(2);
	}

	public void setNearTarget(boolean bl) {
		this.setBeeFlag(2, bl);
	}

	public boolean isPollinating() {
		return this.getBeeFlag(1);
	}

	public void setPollinating(boolean bl) {
		this.setBeeFlag(1, bl);
	}

	private void setBeeFlag(int i, boolean bl) {
		if (bl) {
			this.dataTracker.set(multipleByteTracker, (byte)(this.dataTracker.get(multipleByteTracker) | i));
		} else {
			this.dataTracker.set(multipleByteTracker, (byte)(this.dataTracker.get(multipleByteTracker) & ~i));
		}
	}

	private boolean getBeeFlag(int i) {
		return (this.dataTracker.get(multipleByteTracker) & i) != 0;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributes().register(EntityAttributes.FLYING_SPEED);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.FLYING_SPEED).setBaseValue(0.6F);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
		this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		BirdNavigation birdNavigation = new BirdNavigation(this, world) {
			@Override
			public boolean isValidPosition(BlockPos blockPos) {
				return !this.world.getBlockState(blockPos.method_10074()).isAir();
			}
		};
		birdNavigation.setCanPathThroughDoors(false);
		birdNavigation.setCanSwim(false);
		birdNavigation.setCanEnterOpenDoors(true);
		return birdNavigation;
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return itemStack.getItem().isIn(ItemTags.FLOWERS);
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.ENTITY_BEE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BEE_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	public BeeEntity method_21771(PassiveEntity passiveEntity) {
		return EntityType.BEE.create(this.world);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return this.isBaby() ? entityDimensions.height * 0.95F : entityDimensions.height * 0.5F;
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
	}

	@Override
	protected boolean hasWings() {
		return true;
	}

	public void onHoneyDelivered() {
		this.setHasNectar(false);
		this.resetCropCounter();
	}

	private Optional<BlockPos> findPointOfInterest(int i) {
		BlockPos blockPos = this.getBlockPos();
		if (this.world instanceof ServerWorld) {
			Optional<PointOfInterest> optional = ((ServerWorld)this.world)
				.getPointOfInterestStorage()
				.get(
					pointOfInterestType -> pointOfInterestType == PointOfInterestType.BEE_HIVE || pointOfInterestType == PointOfInterestType.BEE_NEST,
					blockPos,
					i,
					PointOfInterestStorage.OccupationStatus.ANY
				)
				.findFirst();
			return optional.map(PointOfInterest::getPos);
		} else {
			return Optional.empty();
		}
	}

	public boolean setBeeAttacker(Entity entity) {
		this.setAnger(400 + this.random.nextInt(400));
		if (entity instanceof LivingEntity) {
			this.setAttacker((LivingEntity)entity);
		}

		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			Entity entity = damageSource.getAttacker();
			if (entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative() && this.canSee(entity)) {
				this.setPollinating(false);
				this.setBeeAttacker(entity);
			}

			return super.damage(damageSource, f);
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
	}

	static class BeeFollowTargetGoal extends FollowTargetGoal<PlayerEntity> {
		public BeeFollowTargetGoal(BeeEntity beeEntity) {
			super(beeEntity, PlayerEntity.class, true);
		}

		@Override
		public boolean canStart() {
			return this.canSting() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			boolean bl = this.canSting();
			if (bl && this.mob.getTarget() != null) {
				return super.shouldContinue();
			} else {
				this.target = null;
				return false;
			}
		}

		private boolean canSting() {
			BeeEntity beeEntity = (BeeEntity)this.mob;
			return beeEntity.isAngry() && !beeEntity.hasStung();
		}
	}

	class BeeLookControl extends LookControl {
		public BeeLookControl(MobEntity mobEntity) {
			super(mobEntity);
		}

		@Override
		public void tick() {
			if (!BeeEntity.this.isAngry()) {
				super.tick();
			}
		}
	}

	abstract class BeeMoveToTargetGoal extends BeeEntity.NotAngryGoal {
		protected boolean failedToFindPath = false;
		protected int range;

		public BeeMoveToTargetGoal(int i) {
			this.range = i;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		protected abstract BlockPos getTargetPos();

		@Override
		public boolean canBeeContinue() {
			return !this.getTargetPos().isWithinDistance(BeeEntity.this.getPos(), (double)this.range);
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.getTargetPos();
			boolean bl = blockPos.isWithinDistance(BeeEntity.this.getPos(), 8.0);
			if (BeeEntity.this.getNavigation().isIdle()) {
				Vec3d vec3d = TargetFinder.findTargetTowards(
					BeeEntity.this, 8, 6, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), (float) (Math.PI / 10)
				);
				if (vec3d == null) {
					vec3d = TargetFinder.findTargetTowards(BeeEntity.this, 3, 3, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d != null && !bl && BeeEntity.this.world.getBlockState(new BlockPos(vec3d)).getBlock() != Blocks.WATER) {
					vec3d = TargetFinder.findTargetTowards(BeeEntity.this, 8, 6, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d == null) {
					this.failedToFindPath = true;
					return;
				}

				BeeEntity.this.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
			}
		}
	}

	class BeeRevengeGoal extends RevengeGoal {
		public BeeRevengeGoal(BeeEntity beeEntity2) {
			super(beeEntity2);
		}

		@Override
		protected void setMobEntityTarget(MobEntity mobEntity, LivingEntity livingEntity) {
			if (mobEntity instanceof BeeEntity && this.mob.canSee(livingEntity) && ((BeeEntity)mobEntity).setBeeAttacker(livingEntity)) {
				mobEntity.setTarget(livingEntity);
			}
		}
	}

	class BeeWanderAroundGoal extends Goal {
		public BeeWanderAroundGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return BeeEntity.this.getNavigation().isIdle() && BeeEntity.this.random.nextInt(10) == 0;
		}

		@Override
		public boolean shouldContinue() {
			return BeeEntity.this.getNavigation().getCurrentPath() != null && !BeeEntity.this.getNavigation().isIdle();
		}

		@Override
		public void start() {
			Vec3d vec3d = this.getRandomLocation();
			if (vec3d != null) {
				EntityNavigation entityNavigation = BeeEntity.this.getNavigation();
				entityNavigation.startMovingAlong(entityNavigation.findPathTo(new BlockPos(vec3d), 1), 1.0);
			}
		}

		@Nullable
		private Vec3d getRandomLocation() {
			Vec3d vec3d = BeeEntity.this.getRotationVec(0.5F);
			int i = 8;
			Vec3d vec3d2 = TargetFinder.method_21757(BeeEntity.this, 8, 7, vec3d, (float) (Math.PI / 2), 2, 1);
			return vec3d2 != null ? vec3d2 : TargetFinder.method_21756(BeeEntity.this, 8, 4, -2, vec3d, (float) (Math.PI / 2));
		}
	}

	class EnterHiveGoal extends BeeEntity.NotAngryGoal {
		private EnterHiveGoal() {
		}

		@Override
		public boolean canBeeStart() {
			if (BeeEntity.this.hasNectar() && BeeEntity.this.hasHive() && !BeeEntity.this.hasStung() && BeeEntity.this.canEnterHive()) {
				if (BeeEntity.this.hivePos.getSquaredDistance(BeeEntity.this.getBlockPos()) < 4.0) {
					BlockEntity blockEntity = BeeEntity.this.world.getBlockEntity(BeeEntity.this.hivePos);
					if (blockEntity instanceof BeeHiveBlockEntity) {
						BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
						if (!beeHiveBlockEntity.isFullOfBees()) {
							return true;
						}

						BeeEntity.this.hivePos = BlockPos.ORIGIN;
					}
				}

				return false;
			} else {
				return false;
			}
		}

		@Override
		public boolean canBeeContinue() {
			return false;
		}

		@Override
		public void start() {
			BlockEntity blockEntity = BeeEntity.this.world.getBlockEntity(BeeEntity.this.hivePos);
			if (blockEntity instanceof BeeHiveBlockEntity) {
				BeeHiveBlockEntity beeHiveBlockEntity = (BeeHiveBlockEntity)blockEntity;
				beeHiveBlockEntity.tryEnterHive(BeeEntity.this, BeeEntity.this.hasNectar());
			}
		}
	}

	class FindHiveGoal extends BeeEntity.NotAngryGoal {
		private FindHiveGoal() {
		}

		@Override
		public boolean canBeeStart() {
			return BeeEntity.this.age % 10 == 0 && !BeeEntity.this.hasHive();
		}

		@Override
		public boolean canBeeContinue() {
			return false;
		}

		@Override
		public void start() {
			Optional<BlockPos> optional = BeeEntity.this.findPointOfInterest(5);
			if (optional.isPresent()) {
				BlockPos blockPos = (BlockPos)optional.get();
				BlockEntity blockEntity = BeeEntity.this.world.getBlockEntity(blockPos);
				if (blockEntity instanceof BeeHiveBlockEntity && !((BeeHiveBlockEntity)blockEntity).isFullOfBees()) {
					BeeEntity.this.hivePos = blockPos;
				}
			}
		}
	}

	class GrowCropsGoal extends BeeEntity.NotAngryGoal {
		private GrowCropsGoal() {
		}

		@Override
		public boolean canBeeStart() {
			if (BeeEntity.this.getCropsGrownSincePollination() >= 10) {
				return false;
			} else {
				return BeeEntity.this.random.nextFloat() < 0.3F ? false : BeeEntity.this.hasNectar() && BeeEntity.this.isHiveValid();
			}
		}

		@Override
		public boolean canBeeContinue() {
			return this.canBeeStart();
		}

		@Override
		public void tick() {
			if (BeeEntity.this.random.nextInt(30) == 0) {
				for (int i = 1; i <= 2; i++) {
					BlockPos blockPos = BeeEntity.this.getBlockPos().down(i);
					BlockState blockState = BeeEntity.this.world.getBlockState(blockPos);
					Block block = blockState.getBlock();
					boolean bl = false;
					IntProperty intProperty = null;
					if (block.matches(BlockTags.BEE_GROWABLES)) {
						if (block instanceof CropBlock) {
							CropBlock cropBlock = (CropBlock)block;
							if (!cropBlock.isMature(blockState)) {
								bl = true;
								intProperty = cropBlock.getAgeProperty();
							}
						} else if (block instanceof StemBlock) {
							int j = (Integer)blockState.get(StemBlock.AGE);
							if (j < 7) {
								bl = true;
								intProperty = StemBlock.AGE;
							}
						} else if (block == Blocks.SWEET_BERRY_BUSH) {
							int j = (Integer)blockState.get(SweetBerryBushBlock.AGE);
							if (j < 3) {
								bl = true;
								intProperty = SweetBerryBushBlock.AGE;
							}
						}

						if (bl) {
							BeeEntity.this.world.playLevelEvent(2005, blockPos, 0);
							BeeEntity.this.world.setBlockState(blockPos, blockState.with(intProperty, Integer.valueOf((Integer)blockState.get(intProperty) + 1)));
							BeeEntity.this.addCropCounter();
						}
					}
				}
			}
		}
	}

	public class MoveToFlowerGoal extends BeeEntity.BeeMoveToTargetGoal {
		public MoveToFlowerGoal() {
			super(3);
		}

		@Override
		public boolean canBeeStart() {
			return this.isHiveValid() && BeeEntity.this.ticksSincePollination > 3600;
		}

		@Override
		public boolean canBeeContinue() {
			return this.canBeeStart() && super.canBeeContinue();
		}

		@Override
		public void stop() {
			if (!BeeEntity.this.world.getBlockState(BeeEntity.this.flowerPos).getBlock().matches(BlockTags.FLOWERS)) {
				BeeEntity.this.flowerPos = BlockPos.ORIGIN;
			}
		}

		@Override
		protected BlockPos getTargetPos() {
			return BeeEntity.this.flowerPos;
		}

		private boolean isHiveValid() {
			return this.getTargetPos() != BlockPos.ORIGIN;
		}
	}

	class MoveToHiveGoal extends BeeEntity.BeeMoveToTargetGoal {
		public MoveToHiveGoal() {
			super(2);
		}

		@Override
		protected BlockPos getTargetPos() {
			return BeeEntity.this.hivePos;
		}

		@Override
		public boolean canBeeStart() {
			return BeeEntity.this.canEnterHive();
		}

		@Override
		public boolean canBeeContinue() {
			return this.canBeeStart() && super.canBeeContinue();
		}
	}

	abstract class NotAngryGoal extends Goal {
		private NotAngryGoal() {
		}

		public abstract boolean canBeeStart();

		public abstract boolean canBeeContinue();

		@Override
		public boolean canStart() {
			return this.canBeeStart() && !BeeEntity.this.isAngry();
		}

		@Override
		public boolean shouldContinue() {
			return this.canBeeContinue() && !BeeEntity.this.isAngry();
		}
	}

	class PollinateGoal extends BeeEntity.NotAngryGoal {
		private final Predicate<BlockState> field_20617 = blockState -> {
			if (blockState.matches(BlockTags.TALL_FLOWERS)) {
				return blockState.getBlock() == Blocks.SUNFLOWER ? blockState.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER : true;
			} else {
				return blockState.matches(BlockTags.SMALL_FLOWERS);
			}
		};
		private int pollinationTicks = 0;
		private int lastPollinationTick = 0;

		public PollinateGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canBeeStart() {
			if (BeeEntity.this.hasNectar()) {
				return false;
			} else if (BeeEntity.this.random.nextFloat() < 0.7F) {
				return false;
			} else {
				Optional<BlockPos> optional = this.getFlower();
				if (optional.isPresent()) {
					BeeEntity.this.flowerPos = (BlockPos)optional.get();
					BeeEntity.this.getNavigation()
						.startMovingTo((double)BeeEntity.this.flowerPos.getX(), (double)BeeEntity.this.flowerPos.getY(), (double)BeeEntity.this.flowerPos.getZ(), 1.2F);
					return true;
				} else {
					return false;
				}
			}
		}

		@Override
		public boolean canBeeContinue() {
			if (this.completedPollination()) {
				return BeeEntity.this.random.nextFloat() < 0.2F;
			} else if (BeeEntity.this.age % 20 == 0) {
				Optional<BlockPos> optional = this.getFlower();
				return optional.isPresent();
			} else {
				return true;
			}
		}

		private boolean completedPollination() {
			return this.pollinationTicks > 400;
		}

		@Override
		public void start() {
			BeeEntity.this.setPollinating(true);
			this.pollinationTicks = 0;
			this.lastPollinationTick = 0;
		}

		@Override
		public void stop() {
			BeeEntity.this.setPollinating(false);
			if (this.completedPollination()) {
				BeeEntity.this.setHasNectar(true);
			}
		}

		@Override
		public void tick() {
			this.pollinationTicks++;
			if (BeeEntity.this.random.nextFloat() < 0.05F && this.pollinationTicks > this.lastPollinationTick + 60) {
				this.lastPollinationTick = this.pollinationTicks;
				BeeEntity.this.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0F, 1.0F);
			}
		}

		private Optional<BlockPos> getFlower() {
			return this.method_22326(this.field_20617, 2.0);
		}

		private Optional<BlockPos> method_22326(Predicate<BlockState> predicate, double d) {
			BlockPos blockPos = BeeEntity.this.getBlockPos();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; (double)i <= d; i = i > 0 ? -i : 1 - i) {
				for (int j = 0; (double)j < d; j++) {
					for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
						for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
							mutable.set(blockPos).setOffset(k, i - 1, l);
							if (blockPos.getSquaredDistance(mutable) < d * d && predicate.test(BeeEntity.this.world.getBlockState(mutable))) {
								return Optional.of(mutable);
							}
						}
					}
				}
			}

			return Optional.empty();
		}
	}

	class StingGoal extends MeleeAttackGoal {
		public StingGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl) {
			super(mobEntityWithAi, d, bl);
		}

		@Override
		public boolean canStart() {
			return super.canStart() && BeeEntity.this.isAngry() && !BeeEntity.this.hasStung();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && BeeEntity.this.isAngry() && !BeeEntity.this.hasStung();
		}
	}
}
