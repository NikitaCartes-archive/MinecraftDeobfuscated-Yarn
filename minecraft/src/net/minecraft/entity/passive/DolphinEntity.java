package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.DolphinLookControl;
import net.minecraft.entity.ai.control.MoveControl;
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
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

public class DolphinEntity extends WaterCreatureEntity {
	private static final TrackedData<BlockPos> TREASURE_POS = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> HAS_FISH = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> MOISTNESS = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate()
		.setBaseMaxDistance(10.0)
		.includeTeammates()
		.includeInvulnerable()
		.includeHidden();
	public static final Predicate<ItemEntity> CAN_TAKE = itemEntity -> !itemEntity.cannotPickup() && itemEntity.isAlive() && itemEntity.isInsideWater();

	public DolphinEntity(EntityType<? extends DolphinEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new DolphinEntity.DolphinMoveControl(this);
		this.lookControl = new DolphinLookControl(this, 10);
		this.setCanPickUpLoot(true);
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.setBreath(this.getMaxBreath());
		this.pitch = 0.0F;
		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	public boolean canBreatheInWater() {
		return false;
	}

	@Override
	protected void tickBreath(int i) {
	}

	public void setTreasurePos(BlockPos blockPos) {
		this.dataTracker.set(TREASURE_POS, blockPos);
	}

	public BlockPos getTreasurePos() {
		return this.dataTracker.get(TREASURE_POS);
	}

	public boolean hasFish() {
		return this.dataTracker.get(HAS_FISH);
	}

	public void setHasFish(boolean bl) {
		this.dataTracker.set(HAS_FISH, bl);
	}

	public int getMoistness() {
		return this.dataTracker.get(MOISTNESS);
	}

	public void setMoistness(int i) {
		this.dataTracker.set(MOISTNESS, i);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TREASURE_POS, BlockPos.ORIGIN);
		this.dataTracker.startTracking(HAS_FISH, false);
		this.dataTracker.startTracking(MOISTNESS, 2400);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("TreasurePosX", this.getTreasurePos().getX());
		compoundTag.putInt("TreasurePosY", this.getTreasurePos().getY());
		compoundTag.putInt("TreasurePosZ", this.getTreasurePos().getZ());
		compoundTag.putBoolean("GotFish", this.hasFish());
		compoundTag.putInt("Moistness", this.getMoistness());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		int i = compoundTag.getInt("TreasurePosX");
		int j = compoundTag.getInt("TreasurePosY");
		int k = compoundTag.getInt("TreasurePosZ");
		this.setTreasurePos(new BlockPos(i, j, k));
		super.readCustomDataFromTag(compoundTag);
		this.setHasFish(compoundTag.getBoolean("GotFish"));
		this.setMoistness(compoundTag.getInt("Moistness"));
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

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(1.2F);
		this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	public boolean tryAttack(Entity entity) {
		boolean bl = entity.damage(DamageSource.mob(this), (float)((int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.dealDamage(this, entity);
			this.playSound(SoundEvents.ENTITY_DOLPHIN_ATTACK, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	public int getMaxBreath() {
		return 4800;
	}

	@Override
	protected int getNextBreathInAir(int i) {
		return this.getMaxBreath();
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 0.3F;
	}

	@Override
	public int getLookPitchSpeed() {
		return 1;
	}

	@Override
	public int method_5986() {
		return 1;
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return true;
	}

	@Override
	public boolean canPickUp(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		return !this.getEquippedStack(equipmentSlot).isEmpty() ? false : equipmentSlot == EquipmentSlot.MAINHAND && super.canPickUp(itemStack);
	}

	@Override
	protected void loot(ItemEntity itemEntity) {
		if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
			ItemStack itemStack = itemEntity.getStack();
			if (this.canPickupItem(itemStack)) {
				this.equipStack(EquipmentSlot.MAINHAND, itemStack);
				this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0F;
				this.sendPickup(itemEntity, itemStack.getCount());
				itemEntity.remove();
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.isAiDisabled()) {
			if (this.isTouchingWater()) {
				this.setMoistness(2400);
			} else {
				this.setMoistness(this.getMoistness() - 1);
				if (this.getMoistness() <= 0) {
					this.damage(DamageSource.DRYOUT, 1.0F);
				}

				if (this.onGround) {
					this.setVelocity(
						this.getVelocity().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F))
					);
					this.yaw = this.random.nextFloat() * 360.0F;
					this.onGround = false;
					this.velocityDirty = true;
				}
			}

			if (this.world.isClient && this.isInsideWater() && this.getVelocity().lengthSquared() > 0.03) {
				Vec3d vec3d = this.getRotationVec(0.0F);
				float f = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * 0.3F;
				float g = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * 0.3F;
				float h = 1.2F - this.random.nextFloat() * 0.7F;

				for (int i = 0; i < 2; i++) {
					this.world
						.addParticle(ParticleTypes.DOLPHIN, this.x - vec3d.x * (double)h + (double)f, this.y - vec3d.y, this.z - vec3d.z * (double)h + (double)g, 0.0, 0.0, 0.0);
					this.world
						.addParticle(ParticleTypes.DOLPHIN, this.x - vec3d.x * (double)h - (double)f, this.y - vec3d.y, this.z - vec3d.z * (double)h - (double)g, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 38) {
			this.spawnParticlesAround(ParticleTypes.HAPPY_VILLAGER);
		} else {
			super.handleStatus(b);
		}
	}

	@Environment(EnvType.CLIENT)
	private void spawnParticlesAround(ParticleEffect particleEffect) {
		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.01;
			double e = this.random.nextGaussian() * 0.01;
			double f = this.random.nextGaussian() * 0.01;
			this.world
				.addParticle(
					particleEffect,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.2F + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
		}
	}

	@Override
	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!itemStack.isEmpty() && itemStack.getItem().isIn(ItemTags.FISHES)) {
			if (!this.world.isClient) {
				this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, 1.0F, 1.0F);
			}

			this.setHasFish(true);
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	public static boolean method_20664(EntityType<DolphinEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return blockPos.getY() > 45
			&& blockPos.getY() < iWorld.getSeaLevel()
			&& (iWorld.getBiome(blockPos) != Biomes.OCEAN || iWorld.getBiome(blockPos) != Biomes.DEEP_OCEAN)
			&& iWorld.getFluidState(blockPos).matches(FluidTags.WATER);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
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
		return this.isInsideWater() ? SoundEvents.ENTITY_DOLPHIN_AMBIENT_WATER : SoundEvents.ENTITY_DOLPHIN_AMBIENT;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_DOLPHIN_SPLASH;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_DOLPHIN_SWIM;
	}

	protected boolean isCloseToTarget() {
		BlockPos blockPos = this.getNavigation().getTargetPos();
		return blockPos != null ? blockPos.isWithinDistance(this.getPos(), 12.0) : false;
	}

	@Override
	public void travel(Vec3d vec3d) {
		if (this.canMoveVoluntarily() && this.isInsideWater()) {
			this.updateVelocity(this.getMovementSpeed(), vec3d);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
			if (this.getTarget() == null) {
				this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(vec3d);
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return true;
	}

	static class DolphinMoveControl extends MoveControl {
		private final DolphinEntity dolphin;

		public DolphinMoveControl(DolphinEntity dolphinEntity) {
			super(dolphinEntity);
			this.dolphin = dolphinEntity;
		}

		@Override
		public void tick() {
			if (this.dolphin.isInsideWater()) {
				this.dolphin.setVelocity(this.dolphin.getVelocity().add(0.0, 0.005, 0.0));
			}

			if (this.state == MoveControl.State.MOVE_TO && !this.dolphin.getNavigation().isIdle()) {
				double d = this.targetX - this.dolphin.x;
				double e = this.targetY - this.dolphin.y;
				double f = this.targetZ - this.dolphin.z;
				double g = d * d + e * e + f * f;
				if (g < 2.5000003E-7F) {
					this.entity.setForwardSpeed(0.0F);
				} else {
					float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
					this.dolphin.yaw = this.changeAngle(this.dolphin.yaw, h, 10.0F);
					this.dolphin.bodyYaw = this.dolphin.yaw;
					this.dolphin.headYaw = this.dolphin.yaw;
					float i = (float)(this.speed * this.dolphin.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
					if (this.dolphin.isInsideWater()) {
						this.dolphin.setMovementSpeed(i * 0.02F);
						float j = -((float)(MathHelper.atan2(e, (double)MathHelper.sqrt(d * d + f * f)) * 180.0F / (float)Math.PI));
						j = MathHelper.clamp(MathHelper.wrapDegrees(j), -85.0F, 85.0F);
						this.dolphin.pitch = this.changeAngle(this.dolphin.pitch, j, 5.0F);
						float k = MathHelper.cos(this.dolphin.pitch * (float) (Math.PI / 180.0));
						float l = MathHelper.sin(this.dolphin.pitch * (float) (Math.PI / 180.0));
						this.dolphin.forwardSpeed = k * i;
						this.dolphin.upwardSpeed = -l * i;
					} else {
						this.dolphin.setMovementSpeed(i * 0.1F);
					}
				}
			} else {
				this.dolphin.setMovementSpeed(0.0F);
				this.dolphin.setSidewaysSpeed(0.0F);
				this.dolphin.setUpwardSpeed(0.0F);
				this.dolphin.setForwardSpeed(0.0F);
			}
		}
	}

	static class LeadToNearbyTreasureGoal extends Goal {
		private final DolphinEntity dolphin;
		private boolean field_6753;

		LeadToNearbyTreasureGoal(DolphinEntity dolphinEntity) {
			this.dolphin = dolphinEntity;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStop() {
			return false;
		}

		@Override
		public boolean canStart() {
			return this.dolphin.hasFish() && this.dolphin.getBreath() >= 100;
		}

		@Override
		public boolean shouldContinue() {
			BlockPos blockPos = this.dolphin.getTreasurePos();
			return !new BlockPos((double)blockPos.getX(), this.dolphin.y, (double)blockPos.getZ()).isWithinDistance(this.dolphin.getPos(), 4.0)
				&& !this.field_6753
				&& this.dolphin.getBreath() >= 100;
		}

		@Override
		public void start() {
			this.field_6753 = false;
			this.dolphin.getNavigation().stop();
			World world = this.dolphin.world;
			BlockPos blockPos = new BlockPos(this.dolphin);
			String string = (double)world.random.nextFloat() >= 0.5 ? "Ocean_Ruin" : "Shipwreck";
			BlockPos blockPos2 = world.locateStructure(string, blockPos, 50, false);
			if (blockPos2 == null) {
				BlockPos blockPos3 = world.locateStructure(string.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", blockPos, 50, false);
				if (blockPos3 == null) {
					this.field_6753 = true;
					return;
				}

				this.dolphin.setTreasurePos(blockPos3);
			} else {
				this.dolphin.setTreasurePos(blockPos2);
			}

			world.sendEntityStatus(this.dolphin, (byte)38);
		}

		@Override
		public void stop() {
			BlockPos blockPos = this.dolphin.getTreasurePos();
			if (new BlockPos((double)blockPos.getX(), this.dolphin.y, (double)blockPos.getZ()).isWithinDistance(this.dolphin.getPos(), 4.0) || this.field_6753) {
				this.dolphin.setHasFish(false);
			}
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.dolphin.getTreasurePos();
			World world = this.dolphin.world;
			if (this.dolphin.isCloseToTarget() || this.dolphin.getNavigation().isIdle()) {
				Vec3d vec3d = PathfindingUtil.method_6377(
					this.dolphin, 16, 1, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), (float) (Math.PI / 8)
				);
				if (vec3d == null) {
					vec3d = PathfindingUtil.method_6373(this.dolphin, 8, 4, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d != null) {
					BlockPos blockPos2 = new BlockPos(vec3d);
					if (!world.getFluidState(blockPos2).matches(FluidTags.WATER)
						|| !world.getBlockState(blockPos2).canPlaceAtSide(world, blockPos2, BlockPlacementEnvironment.WATER)) {
						vec3d = PathfindingUtil.method_6373(this.dolphin, 8, 5, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
					}
				}

				if (vec3d == null) {
					this.field_6753 = true;
					return;
				}

				this.dolphin.getLookControl().lookAt(vec3d.x, vec3d.y, vec3d.z, (float)(this.dolphin.method_5986() + 20), (float)this.dolphin.getLookPitchSpeed());
				this.dolphin.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.3);
				if (world.random.nextInt(80) == 0) {
					world.sendEntityStatus(this.dolphin, (byte)38);
				}
			}
		}
	}

	class PlayWithItemsGoal extends Goal {
		private int field_6758;

		private PlayWithItemsGoal() {
		}

		@Override
		public boolean canStart() {
			if (this.field_6758 > DolphinEntity.this.age) {
				return false;
			} else {
				List<ItemEntity> list = DolphinEntity.this.world
					.getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.CAN_TAKE);
				return !list.isEmpty() || !DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = DolphinEntity.this.world
				.getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.CAN_TAKE);
			if (!list.isEmpty()) {
				DolphinEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
				DolphinEntity.this.playSound(SoundEvents.ENTITY_DOLPHIN_PLAY, 1.0F, 1.0F);
			}

			this.field_6758 = 0;
		}

		@Override
		public void stop() {
			ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (!itemStack.isEmpty()) {
				this.spitOutItem(itemStack);
				DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				this.field_6758 = DolphinEntity.this.age + DolphinEntity.this.random.nextInt(100);
			}
		}

		@Override
		public void tick() {
			List<ItemEntity> list = DolphinEntity.this.world
				.getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.CAN_TAKE);
			ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (!itemStack.isEmpty()) {
				this.spitOutItem(itemStack);
				DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			} else if (!list.isEmpty()) {
				DolphinEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}

		private void spitOutItem(ItemStack itemStack) {
			if (!itemStack.isEmpty()) {
				double d = DolphinEntity.this.y - 0.3F + (double)DolphinEntity.this.getStandingEyeHeight();
				ItemEntity itemEntity = new ItemEntity(DolphinEntity.this.world, DolphinEntity.this.x, d, DolphinEntity.this.z, itemStack);
				itemEntity.setPickupDelay(40);
				itemEntity.setThrower(DolphinEntity.this.getUuid());
				float f = 0.3F;
				float g = DolphinEntity.this.random.nextFloat() * (float) (Math.PI * 2);
				float h = 0.02F * DolphinEntity.this.random.nextFloat();
				itemEntity.setVelocity(
					(double)(
						0.3F * -MathHelper.sin(DolphinEntity.this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(DolphinEntity.this.pitch * (float) (Math.PI / 180.0))
							+ MathHelper.cos(g) * h
					),
					(double)(0.3F * MathHelper.sin(DolphinEntity.this.pitch * (float) (Math.PI / 180.0)) * 1.5F),
					(double)(
						0.3F * MathHelper.cos(DolphinEntity.this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(DolphinEntity.this.pitch * (float) (Math.PI / 180.0))
							+ MathHelper.sin(g) * h
					)
				);
				DolphinEntity.this.world.spawnEntity(itemEntity);
			}
		}
	}

	static class SwimWithPlayerGoal extends Goal {
		private final DolphinEntity dolphin;
		private final double speed;
		private PlayerEntity closestPlayer;

		SwimWithPlayerGoal(DolphinEntity dolphinEntity, double d) {
			this.dolphin = dolphinEntity;
			this.speed = d;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			this.closestPlayer = this.dolphin.world.getClosestPlayer(DolphinEntity.CLOSE_PLAYER_PREDICATE, this.dolphin);
			return this.closestPlayer == null ? false : this.closestPlayer.isSwimming();
		}

		@Override
		public boolean shouldContinue() {
			return this.closestPlayer != null && this.closestPlayer.isSwimming() && this.dolphin.squaredDistanceTo(this.closestPlayer) < 256.0;
		}

		@Override
		public void start() {
			this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100));
		}

		@Override
		public void stop() {
			this.closestPlayer = null;
			this.dolphin.getNavigation().stop();
		}

		@Override
		public void tick() {
			this.dolphin.getLookControl().lookAt(this.closestPlayer, (float)(this.dolphin.method_5986() + 20), (float)this.dolphin.getLookPitchSpeed());
			if (this.dolphin.squaredDistanceTo(this.closestPlayer) < 6.25) {
				this.dolphin.getNavigation().stop();
			} else {
				this.dolphin.getNavigation().startMovingTo(this.closestPlayer, this.speed);
			}

			if (this.closestPlayer.isSwimming() && this.closestPlayer.world.random.nextInt(6) == 0) {
				this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100));
			}
		}
	}
}
