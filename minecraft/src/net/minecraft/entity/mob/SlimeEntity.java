package net.minecraft.entity.mob;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.loot.LootTables;

public class SlimeEntity extends MobEntity implements Monster {
	private static final TrackedData<Integer> SLIME_SIZE = DataTracker.registerData(SlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public float sizeX;
	public float sizeY;
	public float sizeZ;
	private boolean onGroundLastTick;

	public SlimeEntity(EntityType<? extends SlimeEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new SlimeEntity.SlimeMoveControl(this);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SlimeEntity.class_1623(this));
		this.goalSelector.add(2, new SlimeEntity.class_1622(this));
		this.goalSelector.add(3, new SlimeEntity.class_1626(this));
		this.goalSelector.add(5, new SlimeEntity.class_1624(this));
		this.targetSelector.add(1, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, livingEntity -> Math.abs(livingEntity.y - this.y) <= 4.0));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SLIME_SIZE, 1);
	}

	protected void method_7161(int i, boolean bl) {
		this.dataTracker.set(SLIME_SIZE, i);
		this.setPosition(this.x, this.y, this.z);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue((double)(i * i));
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
		if (bl) {
			this.setHealth(this.getHealthMaximum());
		}

		this.experiencePoints = i;
	}

	public int getSize() {
		return this.dataTracker.get(SLIME_SIZE);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Size", this.getSize() - 1);
		compoundTag.putBoolean("wasOnGround", this.onGroundLastTick);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		int i = compoundTag.getInt("Size");
		if (i < 0) {
			i = 0;
		}

		this.method_7161(i + 1, false);
		this.onGroundLastTick = compoundTag.getBoolean("wasOnGround");
	}

	public boolean isSmall() {
		return this.getSize() <= 1;
	}

	protected ParticleParameters getParticles() {
		return ParticleTypes.field_11246;
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL && this.getSize() > 0) {
			this.invalid = true;
		}

		this.sizeY = this.sizeY + (this.sizeX - this.sizeY) * 0.5F;
		this.sizeZ = this.sizeY;
		super.tick();
		if (this.onGround && !this.onGroundLastTick) {
			int i = this.getSize();

			for (int j = 0; j < i * 8; j++) {
				float f = this.random.nextFloat() * (float) (Math.PI * 2);
				float g = this.random.nextFloat() * 0.5F + 0.5F;
				float h = MathHelper.sin(f) * (float)i * 0.5F * g;
				float k = MathHelper.cos(f) * (float)i * 0.5F * g;
				World var10000 = this.world;
				ParticleParameters var10001 = this.getParticles();
				double var10002 = this.x + (double)h;
				double var10004 = this.z + (double)k;
				var10000.addParticle(var10001, var10002, this.getBoundingBox().minY, var10004, 0.0, 0.0, 0.0);
			}

			this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			this.sizeX = -0.5F;
		} else if (!this.onGround && this.onGroundLastTick) {
			this.sizeX = 1.0F;
		}

		this.onGroundLastTick = this.onGround;
		this.method_7156();
	}

	protected void method_7156() {
		this.sizeX *= 0.6F;
	}

	protected int method_7154() {
		return this.random.nextInt(20) + 10;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (SLIME_SIZE.equals(trackedData)) {
			this.refreshSize();
			this.yaw = this.headYaw;
			this.field_6283 = this.headYaw;
			if (this.isInsideWater() && this.random.nextInt(20) == 0) {
				this.onSwimmingStart();
			}
		}

		super.onTrackedDataSet(trackedData);
	}

	@Override
	public EntityType<? extends SlimeEntity> getType() {
		return (EntityType<? extends SlimeEntity>)super.getType();
	}

	@Override
	public void invalidate() {
		int i = this.getSize();
		if (!this.world.isClient && i > 1 && this.getHealth() <= 0.0F) {
			int j = 2 + this.random.nextInt(3);

			for (int k = 0; k < j; k++) {
				float f = ((float)(k % 2) - 0.5F) * (float)i / 4.0F;
				float g = ((float)(k / 2) - 0.5F) * (float)i / 4.0F;
				SlimeEntity slimeEntity = this.getType().create(this.world);
				if (this.hasCustomName()) {
					slimeEntity.setCustomName(this.getCustomName());
				}

				if (this.isPersistent()) {
					slimeEntity.setPersistent();
				}

				slimeEntity.method_7161(i / 2, true);
				slimeEntity.setPositionAndAngles(this.x + (double)f, this.y + 0.5, this.z + (double)g, this.random.nextFloat() * 360.0F, 0.0F);
				this.world.spawnEntity(slimeEntity);
			}
		}

		super.invalidate();
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		super.pushAwayFrom(entity);
		if (entity instanceof IronGolemEntity && this.method_7163()) {
			this.method_7155((LivingEntity)entity);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity playerEntity) {
		if (this.method_7163()) {
			this.method_7155(playerEntity);
		}
	}

	protected void method_7155(LivingEntity livingEntity) {
		if (this.isValid()) {
			int i = this.getSize();
			if (this.squaredDistanceTo(livingEntity) < 0.6 * (double)i * 0.6 * (double)i
				&& this.canSee(livingEntity)
				&& livingEntity.damage(DamageSource.mob(this), (float)this.method_7158())) {
				this.playSound(SoundEvents.field_14863, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				this.dealDamage(this, livingEntity);
			}
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
		return 0.625F * entitySize.height;
	}

	protected boolean method_7163() {
		return !this.isSmall() && this.method_6034();
	}

	protected int method_7158() {
		return this.getSize();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return this.isSmall() ? SoundEvents.field_14620 : SoundEvents.field_15014;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isSmall() ? SoundEvents.field_14849 : SoundEvents.field_14763;
	}

	protected SoundEvent getSquishSound() {
		return this.isSmall() ? SoundEvents.field_15148 : SoundEvents.field_15095;
	}

	@Override
	protected Identifier getLootTableId() {
		return this.getSize() == 1 ? this.getType().getLootTableId() : LootTables.EMPTY;
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		BlockPos blockPos = new BlockPos(MathHelper.floor(this.x), 0, MathHelper.floor(this.z));
		if (iWorld.getLevelProperties().getGeneratorType() == LevelGeneratorType.FLAT && this.random.nextInt(4) != 1) {
			return false;
		} else {
			if (iWorld.getDifficulty() != Difficulty.PEACEFUL) {
				Biome biome = iWorld.getBiome(blockPos);
				if (biome == Biomes.field_9471
					&& this.y > 50.0
					&& this.y < 70.0
					&& this.random.nextFloat() < 0.5F
					&& this.random.nextFloat() < iWorld.method_8391()
					&& iWorld.getLightLevel(new BlockPos(this)) <= this.random.nextInt(8)) {
					return super.canSpawn(iWorld, spawnType);
				}

				ChunkPos chunkPos = new ChunkPos(blockPos);
				boolean bl = ChunkRandom.create(chunkPos.x, chunkPos.z, iWorld.getSeed(), 987234911L).nextInt(10) == 0;
				if (this.random.nextInt(10) == 0 && bl && this.y < 40.0) {
					return super.canSpawn(iWorld, spawnType);
				}
			}

			return false;
		}
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F * (float)this.getSize();
	}

	@Override
	public int method_5978() {
		return 0;
	}

	protected boolean method_7159() {
		return this.getSize() > 0;
	}

	@Override
	protected void jump() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, 0.42F, vec3d.z);
		this.velocityDirty = true;
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		int i = this.random.nextInt(3);
		if (i < 2 && this.random.nextFloat() < 0.5F * localDifficulty.getClampedLocalDifficulty()) {
			i++;
		}

		int j = 1 << i;
		this.method_7161(j, true);
		return super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	protected SoundEvent getJumpSound() {
		return this.isSmall() ? SoundEvents.field_14694 : SoundEvents.field_14919;
	}

	@Override
	public EntitySize getSize(EntityPose entityPose) {
		return super.getSize(entityPose).scaled(0.255F * (float)this.getSize());
	}

	static class SlimeMoveControl extends MoveControl {
		private float field_7397;
		private int field_7399;
		private final SlimeEntity slime;
		private boolean field_7398;

		public SlimeMoveControl(SlimeEntity slimeEntity) {
			super(slimeEntity);
			this.slime = slimeEntity;
			this.field_7397 = 180.0F * slimeEntity.yaw / (float) Math.PI;
		}

		public void method_7165(float f, boolean bl) {
			this.field_7397 = f;
			this.field_7398 = bl;
		}

		public void method_7164(double d) {
			this.speed = d;
			this.state = MoveControl.State.field_6378;
		}

		@Override
		public void tick() {
			this.entity.yaw = this.method_6238(this.entity.yaw, this.field_7397, 90.0F);
			this.entity.headYaw = this.entity.yaw;
			this.entity.field_6283 = this.entity.yaw;
			if (this.state != MoveControl.State.field_6378) {
				this.entity.method_5930(0.0F);
			} else {
				this.state = MoveControl.State.field_6377;
				if (this.entity.onGround) {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
					if (this.field_7399-- <= 0) {
						this.field_7399 = this.slime.method_7154();
						if (this.field_7398) {
							this.field_7399 /= 3;
						}

						this.slime.getJumpControl().setActive();
						if (this.slime.method_7159()) {
							this.slime
								.playSound(
									this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRand().nextFloat() - this.slime.getRand().nextFloat()) * 0.2F + 1.0F) * 0.8F
								);
						}
					} else {
						this.slime.movementInputSideways = 0.0F;
						this.slime.movementInputForward = 0.0F;
						this.entity.setMovementSpeed(0.0F);
					}
				} else {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
				}
			}
		}
	}

	static class class_1622 extends Goal {
		private final SlimeEntity field_7393;
		private int field_7392;

		public class_1622(SlimeEntity slimeEntity) {
			this.field_7393 = slimeEntity;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.field_7393.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isValid()) {
				return false;
			} else {
				return livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable
					? false
					: this.field_7393.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
			}
		}

		@Override
		public void start() {
			this.field_7392 = 300;
			super.start();
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = this.field_7393.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isValid()) {
				return false;
			} else {
				return livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable ? false : --this.field_7392 > 0;
			}
		}

		@Override
		public void tick() {
			this.field_7393.method_5951(this.field_7393.getTarget(), 10.0F, 10.0F);
			((SlimeEntity.SlimeMoveControl)this.field_7393.getMoveControl()).method_7165(this.field_7393.yaw, this.field_7393.method_7163());
		}
	}

	static class class_1623 extends Goal {
		private final SlimeEntity field_7394;

		public class_1623(SlimeEntity slimeEntity) {
			this.field_7394 = slimeEntity;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18407, Goal.class_4134.field_18405));
			slimeEntity.getNavigation().setCanSwim(true);
		}

		@Override
		public boolean canStart() {
			return (this.field_7394.isInsideWater() || this.field_7394.isTouchingLava()) && this.field_7394.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
		}

		@Override
		public void tick() {
			if (this.field_7394.getRand().nextFloat() < 0.8F) {
				this.field_7394.getJumpControl().setActive();
			}

			((SlimeEntity.SlimeMoveControl)this.field_7394.getMoveControl()).method_7164(1.2);
		}
	}

	static class class_1624 extends Goal {
		private final SlimeEntity field_7395;

		public class_1624(SlimeEntity slimeEntity) {
			this.field_7395 = slimeEntity;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18407, Goal.class_4134.field_18405));
		}

		@Override
		public boolean canStart() {
			return !this.field_7395.hasVehicle();
		}

		@Override
		public void tick() {
			((SlimeEntity.SlimeMoveControl)this.field_7395.getMoveControl()).method_7164(1.0);
		}
	}

	static class class_1626 extends Goal {
		private final SlimeEntity field_7402;
		private float field_7400;
		private int field_7401;

		public class_1626(SlimeEntity slimeEntity) {
			this.field_7402 = slimeEntity;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			return this.field_7402.getTarget() == null
				&& (
					this.field_7402.onGround
						|| this.field_7402.isInsideWater()
						|| this.field_7402.isTouchingLava()
						|| this.field_7402.hasPotionEffect(StatusEffects.field_5902)
				)
				&& this.field_7402.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
		}

		@Override
		public void tick() {
			if (--this.field_7401 <= 0) {
				this.field_7401 = 40 + this.field_7402.getRand().nextInt(60);
				this.field_7400 = (float)this.field_7402.getRand().nextInt(360);
			}

			((SlimeEntity.SlimeMoveControl)this.field_7402.getMoveControl()).method_7165(this.field_7400, false);
		}
	}
}
