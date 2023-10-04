package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.ai.goal.ChaseBoatGoal;
import net.minecraft.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class DolphinEntity extends WaterCreatureEntity {
	private static final TrackedData<BlockPos> TREASURE_POS = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> HAS_FISH = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> MOISTNESS = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.INTEGER);
	static final TargetPredicate CLOSE_PLAYER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(10.0).ignoreVisibility();
	public static final int MAX_AIR = 4800;
	private static final int MAX_MOISTNESS = 2400;
	public static final Predicate<ItemEntity> CAN_TAKE = item -> !item.cannotPickup() && item.isAlive() && item.isTouchingWater();

	public DolphinEntity(EntityType<? extends DolphinEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02F, 0.1F, true);
		this.lookControl = new YawAdjustingLookControl(this, 10);
		this.setCanPickUpLoot(true);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		this.setAir(this.getMaxAir());
		this.setPitch(0.0F);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	protected void tickWaterBreathingAir(int air) {
	}

	public void setTreasurePos(BlockPos treasurePos) {
		this.dataTracker.set(TREASURE_POS, treasurePos);
	}

	public BlockPos getTreasurePos() {
		return this.dataTracker.get(TREASURE_POS);
	}

	public boolean hasFish() {
		return this.dataTracker.get(HAS_FISH);
	}

	public void setHasFish(boolean hasFish) {
		this.dataTracker.set(HAS_FISH, hasFish);
	}

	public int getMoistness() {
		return this.dataTracker.get(MOISTNESS);
	}

	public void setMoistness(int moistness) {
		this.dataTracker.set(MOISTNESS, moistness);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TREASURE_POS, BlockPos.ORIGIN);
		this.dataTracker.startTracking(HAS_FISH, false);
		this.dataTracker.startTracking(MOISTNESS, 2400);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("TreasurePosX", this.getTreasurePos().getX());
		nbt.putInt("TreasurePosY", this.getTreasurePos().getY());
		nbt.putInt("TreasurePosZ", this.getTreasurePos().getZ());
		nbt.putBoolean("GotFish", this.hasFish());
		nbt.putInt("Moistness", this.getMoistness());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		int i = nbt.getInt("TreasurePosX");
		int j = nbt.getInt("TreasurePosY");
		int k = nbt.getInt("TreasurePosZ");
		this.setTreasurePos(new BlockPos(i, j, k));
		super.readCustomDataFromNbt(nbt);
		this.setHasFish(nbt.getBoolean("GotFish"));
		this.setMoistness(nbt.getInt("Moistness"));
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new BreatheAirGoal(this));
		this.goalSelector.add(0, new MoveIntoWaterGoal(this));
		this.goalSelector.add(1, new DolphinEntity.LeadToNearbyTreasureGoal(this));
		this.goalSelector.add(2, new DolphinEntity.SwimWithPlayerGoal(this, 4.0));
		this.goalSelector.add(4, new SwimAroundGoal(this, 1.0, 10));
		this.goalSelector.add(4, new LookAroundGoal(this));
		this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(5, new DolphinJumpGoal(this, 10));
		this.goalSelector.add(6, new MeleeAttackGoal(this, 1.2F, true));
		this.goalSelector.add(8, new DolphinEntity.PlayWithItemsGoal());
		this.goalSelector.add(8, new ChaseBoatGoal(this));
		this.goalSelector.add(9, new FleeEntityGoal(this, GuardianEntity.class, 8.0F, 1.0, 1.0));
		this.targetSelector.add(1, new RevengeGoal(this, GuardianEntity.class).setGroupRevenge());
	}

	public static DefaultAttributeContainer.Builder createDolphinAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.2F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = target.damage(this.getDamageSources().mobAttack(this), (float)((int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)));
		if (bl) {
			this.applyDamageEffects(this, target);
			this.playSound(SoundEvents.ENTITY_DOLPHIN_ATTACK, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	public int getMaxAir() {
		return 4800;
	}

	@Override
	protected int getNextAirOnLand(int air) {
		return this.getMaxAir();
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.3F;
	}

	@Override
	public int getMaxLookPitchChange() {
		return 1;
	}

	@Override
	public int getMaxHeadRotation() {
		return 1;
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack stack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
		return !this.getEquippedStack(equipmentSlot).isEmpty() ? false : equipmentSlot == EquipmentSlot.MAINHAND && super.canEquip(stack);
	}

	@Override
	protected void loot(ItemEntity item) {
		if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
			ItemStack itemStack = item.getStack();
			if (this.canPickupItem(itemStack)) {
				this.triggerItemPickedUpByEntityCriteria(item);
				this.equipStack(EquipmentSlot.MAINHAND, itemStack);
				this.updateDropChances(EquipmentSlot.MAINHAND);
				this.sendPickup(item, itemStack.getCount());
				item.discard();
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isAiDisabled()) {
			this.setAir(this.getMaxAir());
		} else {
			if (this.isWet()) {
				this.setMoistness(2400);
			} else {
				this.setMoistness(this.getMoistness() - 1);
				if (this.getMoistness() <= 0) {
					this.damage(this.getDamageSources().dryOut(), 1.0F);
				}

				if (this.isOnGround()) {
					this.setVelocity(
						this.getVelocity().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F))
					);
					this.setYaw(this.random.nextFloat() * 360.0F);
					this.setOnGround(false);
					this.velocityDirty = true;
				}
			}

			if (this.getWorld().isClient && this.isTouchingWater() && this.getVelocity().lengthSquared() > 0.03) {
				Vec3d vec3d = this.getRotationVec(0.0F);
				float f = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * 0.3F;
				float g = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)) * 0.3F;
				float h = 1.2F - this.random.nextFloat() * 0.7F;

				for (int i = 0; i < 2; i++) {
					this.getWorld()
						.addParticle(
							ParticleTypes.DOLPHIN,
							this.getX() - vec3d.x * (double)h + (double)f,
							this.getY() - vec3d.y,
							this.getZ() - vec3d.z * (double)h + (double)g,
							0.0,
							0.0,
							0.0
						);
					this.getWorld()
						.addParticle(
							ParticleTypes.DOLPHIN,
							this.getX() - vec3d.x * (double)h - (double)f,
							this.getY() - vec3d.y,
							this.getZ() - vec3d.z * (double)h - (double)g,
							0.0,
							0.0,
							0.0
						);
				}
			}
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.ADD_DOLPHIN_HAPPY_VILLAGER_PARTICLES) {
			this.spawnParticlesAround(ParticleTypes.HAPPY_VILLAGER);
		} else {
			super.handleStatus(status);
		}
	}

	private void spawnParticlesAround(ParticleEffect parameters) {
		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.01;
			double e = this.random.nextGaussian() * 0.01;
			double f = this.random.nextGaussian() * 0.01;
			this.getWorld().addParticle(parameters, this.getParticleX(1.0), this.getRandomBodyY() + 0.2, this.getParticleZ(1.0), d, e, f);
		}
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isEmpty() && itemStack.isIn(ItemTags.FISHES)) {
			if (!this.getWorld().isClient) {
				this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, 1.0F, 1.0F);
			}

			this.setHasFish(true);
			if (!player.getAbilities().creativeMode) {
				itemStack.decrement(1);
			}

			return ActionResult.success(this.getWorld().isClient);
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_DOLPHIN_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_DOLPHIN_DEATH;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return this.isTouchingWater() ? SoundEvents.ENTITY_DOLPHIN_AMBIENT_WATER : SoundEvents.ENTITY_DOLPHIN_AMBIENT;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_DOLPHIN_SPLASH;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_DOLPHIN_SWIM;
	}

	protected boolean isNearTarget() {
		BlockPos blockPos = this.getNavigation().getTargetPos();
		return blockPos != null ? blockPos.isWithinDistance(this.getPos(), 12.0) : false;
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.updateVelocity(this.getMovementSpeed(), movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
			if (this.getTarget() == null) {
				this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(movementInput);
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return true;
	}

	static class LeadToNearbyTreasureGoal extends Goal {
		private final DolphinEntity dolphin;
		private boolean noPathToStructure;

		LeadToNearbyTreasureGoal(DolphinEntity dolphin) {
			this.dolphin = dolphin;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStop() {
			return false;
		}

		@Override
		public boolean canStart() {
			return this.dolphin.hasFish() && this.dolphin.getAir() >= 100;
		}

		@Override
		public boolean shouldContinue() {
			BlockPos blockPos = this.dolphin.getTreasurePos();
			return !BlockPos.ofFloored((double)blockPos.getX(), this.dolphin.getY(), (double)blockPos.getZ()).isWithinDistance(this.dolphin.getPos(), 4.0)
				&& !this.noPathToStructure
				&& this.dolphin.getAir() >= 100;
		}

		@Override
		public void start() {
			if (this.dolphin.getWorld() instanceof ServerWorld) {
				ServerWorld serverWorld = (ServerWorld)this.dolphin.getWorld();
				this.noPathToStructure = false;
				this.dolphin.getNavigation().stop();
				BlockPos blockPos = this.dolphin.getBlockPos();
				BlockPos blockPos2 = serverWorld.locateStructure(StructureTags.DOLPHIN_LOCATED, blockPos, 50, false);
				if (blockPos2 != null) {
					this.dolphin.setTreasurePos(blockPos2);
					serverWorld.sendEntityStatus(this.dolphin, EntityStatuses.ADD_DOLPHIN_HAPPY_VILLAGER_PARTICLES);
				} else {
					this.noPathToStructure = true;
				}
			}
		}

		@Override
		public void stop() {
			BlockPos blockPos = this.dolphin.getTreasurePos();
			if (BlockPos.ofFloored((double)blockPos.getX(), this.dolphin.getY(), (double)blockPos.getZ()).isWithinDistance(this.dolphin.getPos(), 4.0)
				|| this.noPathToStructure) {
				this.dolphin.setHasFish(false);
			}
		}

		@Override
		public void tick() {
			World world = this.dolphin.getWorld();
			if (this.dolphin.isNearTarget() || this.dolphin.getNavigation().isIdle()) {
				Vec3d vec3d = Vec3d.ofCenter(this.dolphin.getTreasurePos());
				Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.dolphin, 16, 1, vec3d, (float) (Math.PI / 8));
				if (vec3d2 == null) {
					vec3d2 = NoPenaltyTargeting.findTo(this.dolphin, 8, 4, vec3d, (float) (Math.PI / 2));
				}

				if (vec3d2 != null) {
					BlockPos blockPos = BlockPos.ofFloored(vec3d2);
					if (!world.getFluidState(blockPos).isIn(FluidTags.WATER) || !world.getBlockState(blockPos).canPathfindThrough(world, blockPos, NavigationType.WATER)) {
						vec3d2 = NoPenaltyTargeting.findTo(this.dolphin, 8, 5, vec3d, (float) (Math.PI / 2));
					}
				}

				if (vec3d2 == null) {
					this.noPathToStructure = true;
					return;
				}

				this.dolphin
					.getLookControl()
					.lookAt(vec3d2.x, vec3d2.y, vec3d2.z, (float)(this.dolphin.getMaxHeadRotation() + 20), (float)this.dolphin.getMaxLookPitchChange());
				this.dolphin.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, 1.3);
				if (world.random.nextInt(this.getTickCount(80)) == 0) {
					world.sendEntityStatus(this.dolphin, EntityStatuses.ADD_DOLPHIN_HAPPY_VILLAGER_PARTICLES);
				}
			}
		}
	}

	class PlayWithItemsGoal extends Goal {
		private int nextPlayingTime;

		@Override
		public boolean canStart() {
			if (this.nextPlayingTime > DolphinEntity.this.age) {
				return false;
			} else {
				List<ItemEntity> list = DolphinEntity.this.getWorld()
					.getEntitiesByClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.CAN_TAKE);
				return !list.isEmpty() || !DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = DolphinEntity.this.getWorld()
				.getEntitiesByClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.CAN_TAKE);
			if (!list.isEmpty()) {
				DolphinEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
				DolphinEntity.this.playSound(SoundEvents.ENTITY_DOLPHIN_PLAY, 1.0F, 1.0F);
			}

			this.nextPlayingTime = 0;
		}

		@Override
		public void stop() {
			ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (!itemStack.isEmpty()) {
				this.spitOutItem(itemStack);
				DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				this.nextPlayingTime = DolphinEntity.this.age + DolphinEntity.this.random.nextInt(100);
			}
		}

		@Override
		public void tick() {
			List<ItemEntity> list = DolphinEntity.this.getWorld()
				.getEntitiesByClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.CAN_TAKE);
			ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (!itemStack.isEmpty()) {
				this.spitOutItem(itemStack);
				DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			} else if (!list.isEmpty()) {
				DolphinEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}

		private void spitOutItem(ItemStack stack) {
			if (!stack.isEmpty()) {
				double d = DolphinEntity.this.getEyeY() - 0.3F;
				ItemEntity itemEntity = new ItemEntity(DolphinEntity.this.getWorld(), DolphinEntity.this.getX(), d, DolphinEntity.this.getZ(), stack);
				itemEntity.setPickupDelay(40);
				itemEntity.setThrower(DolphinEntity.this.getUuid());
				float f = 0.3F;
				float g = DolphinEntity.this.random.nextFloat() * (float) (Math.PI * 2);
				float h = 0.02F * DolphinEntity.this.random.nextFloat();
				itemEntity.setVelocity(
					(double)(
						0.3F
								* -MathHelper.sin(DolphinEntity.this.getYaw() * (float) (Math.PI / 180.0))
								* MathHelper.cos(DolphinEntity.this.getPitch() * (float) (Math.PI / 180.0))
							+ MathHelper.cos(g) * h
					),
					(double)(0.3F * MathHelper.sin(DolphinEntity.this.getPitch() * (float) (Math.PI / 180.0)) * 1.5F),
					(double)(
						0.3F
								* MathHelper.cos(DolphinEntity.this.getYaw() * (float) (Math.PI / 180.0))
								* MathHelper.cos(DolphinEntity.this.getPitch() * (float) (Math.PI / 180.0))
							+ MathHelper.sin(g) * h
					)
				);
				DolphinEntity.this.getWorld().spawnEntity(itemEntity);
			}
		}
	}

	static class SwimWithPlayerGoal extends Goal {
		private final DolphinEntity dolphin;
		private final double speed;
		@Nullable
		private PlayerEntity closestPlayer;

		SwimWithPlayerGoal(DolphinEntity dolphin, double speed) {
			this.dolphin = dolphin;
			this.speed = speed;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			this.closestPlayer = this.dolphin.getWorld().getClosestPlayer(DolphinEntity.CLOSE_PLAYER_PREDICATE, this.dolphin);
			return this.closestPlayer == null ? false : this.closestPlayer.isSwimming() && this.dolphin.getTarget() != this.closestPlayer;
		}

		@Override
		public boolean shouldContinue() {
			return this.closestPlayer != null && this.closestPlayer.isSwimming() && this.dolphin.squaredDistanceTo(this.closestPlayer) < 256.0;
		}

		@Override
		public void start() {
			this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100), this.dolphin);
		}

		@Override
		public void stop() {
			this.closestPlayer = null;
			this.dolphin.getNavigation().stop();
		}

		@Override
		public void tick() {
			this.dolphin.getLookControl().lookAt(this.closestPlayer, (float)(this.dolphin.getMaxHeadRotation() + 20), (float)this.dolphin.getMaxLookPitchChange());
			if (this.dolphin.squaredDistanceTo(this.closestPlayer) < 6.25) {
				this.dolphin.getNavigation().stop();
			} else {
				this.dolphin.getNavigation().startMovingTo(this.closestPlayer, this.speed);
			}

			if (this.closestPlayer.isSwimming() && this.closestPlayer.getWorld().random.nextInt(6) == 0) {
				this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100), this.dolphin);
			}
		}
	}
}
