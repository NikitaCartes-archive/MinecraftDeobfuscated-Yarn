package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
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
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.level.LevelGeneratorType;

public class SlimeEntity extends MobEntity implements Monster {
	private static final TrackedData<Integer> SLIME_SIZE = DataTracker.registerData(SlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public float targetStretch;
	public float stretch;
	public float lastStretch;
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

	protected void setSize(int size, boolean heal) {
		this.dataTracker.set(SLIME_SIZE, size);
		this.updatePosition(this.x, this.y, this.z);
		this.calculateDimensions();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue((double)(size * size));
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)size));
		if (heal) {
			this.setHealth(this.getMaximumHealth());
		}

		this.experiencePoints = size;
	}

	public int getSize() {
		return this.dataTracker.get(SLIME_SIZE);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("Size", this.getSize() - 1);
		tag.putBoolean("wasOnGround", this.onGroundLastTick);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		int i = tag.getInt("Size");
		if (i < 0) {
			i = 0;
		}

		this.setSize(i + 1, false);
		this.onGroundLastTick = tag.getBoolean("wasOnGround");
	}

	public boolean isSmall() {
		return this.getSize() <= 1;
	}

	protected ParticleEffect getParticles() {
		return ParticleTypes.ITEM_SLIME;
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL && this.getSize() > 0) {
			this.removed = true;
		}

		this.stretch = this.stretch + (this.targetStretch - this.stretch) * 0.5F;
		this.lastStretch = this.stretch;
		super.tick();
		if (this.onGround && !this.onGroundLastTick) {
			int i = this.getSize();

			for (int j = 0; j < i * 8; j++) {
				float f = this.random.nextFloat() * (float) (Math.PI * 2);
				float g = this.random.nextFloat() * 0.5F + 0.5F;
				float h = MathHelper.sin(f) * (float)i * 0.5F * g;
				float k = MathHelper.cos(f) * (float)i * 0.5F * g;
				World var10000 = this.world;
				ParticleEffect var10001 = this.getParticles();
				double var10002 = this.x + (double)h;
				double var10004 = this.z + (double)k;
				var10000.addParticle(var10001, var10002, this.getBoundingBox().y1, var10004, 0.0, 0.0, 0.0);
			}

			this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			this.targetStretch = -0.5F;
		} else if (!this.onGround && this.onGroundLastTick) {
			this.targetStretch = 1.0F;
		}

		this.onGroundLastTick = this.onGround;
		this.updateStretch();
	}

	protected void updateStretch() {
		this.targetStretch *= 0.6F;
	}

	protected int getTicksUntilNextJump() {
		return this.random.nextInt(20) + 10;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (SLIME_SIZE.equals(data)) {
			this.calculateDimensions();
			this.yaw = this.headYaw;
			this.field_6283 = this.headYaw;
			if (this.isTouchingWater() && this.random.nextInt(20) == 0) {
				this.onSwimmingStart();
			}
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public EntityType<? extends SlimeEntity> getType() {
		return (EntityType<? extends SlimeEntity>)super.getType();
	}

	@Override
	public void remove() {
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

				slimeEntity.setSize(i / 2, true);
				slimeEntity.refreshPositionAndAngles(this.x + (double)f, this.y + 0.5, this.z + (double)g, this.random.nextFloat() * 360.0F, 0.0F);
				this.world.spawnEntity(slimeEntity);
			}
		}

		super.remove();
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		super.pushAwayFrom(entity);
		if (entity instanceof IronGolemEntity && this.isBig()) {
			this.damage((LivingEntity)entity);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (this.isBig()) {
			this.damage(player);
		}
	}

	protected void damage(LivingEntity target) {
		if (this.isAlive()) {
			int i = this.getSize();
			if (this.squaredDistanceTo(target) < 0.6 * (double)i * 0.6 * (double)i
				&& this.canSee(target)
				&& target.damage(DamageSource.mob(this), (float)this.getDamageAmount())) {
				this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				this.dealDamage(this, target);
			}
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.625F * dimensions.height;
	}

	protected boolean isBig() {
		return !this.isSmall() && this.canMoveVoluntarily();
	}

	protected int getDamageAmount() {
		return this.getSize();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isSmall() ? SoundEvents.ENTITY_SLIME_HURT_SMALL : SoundEvents.ENTITY_SLIME_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isSmall() ? SoundEvents.ENTITY_SLIME_DEATH_SMALL : SoundEvents.ENTITY_SLIME_DEATH;
	}

	protected SoundEvent getSquishSound() {
		return this.isSmall() ? SoundEvents.ENTITY_SLIME_SQUISH_SMALL : SoundEvents.ENTITY_SLIME_SQUISH;
	}

	@Override
	protected Identifier getLootTableId() {
		return this.getSize() == 1 ? this.getType().getLootTableId() : LootTables.EMPTY;
	}

	public static boolean method_20685(EntityType<SlimeEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		if (iWorld.getLevelProperties().getGeneratorType() == LevelGeneratorType.FLAT && random.nextInt(4) != 1) {
			return false;
		} else {
			if (iWorld.getDifficulty() != Difficulty.PEACEFUL) {
				Biome biome = iWorld.getBiome(blockPos);
				if (biome == Biomes.SWAMP
					&& blockPos.getY() > 50
					&& blockPos.getY() < 70
					&& random.nextFloat() < 0.5F
					&& random.nextFloat() < iWorld.getMoonSize()
					&& iWorld.getLightLevel(blockPos) <= random.nextInt(8)) {
					return method_20636(entityType, iWorld, spawnType, blockPos, random);
				}

				ChunkPos chunkPos = new ChunkPos(blockPos);
				boolean bl = ChunkRandom.create(chunkPos.x, chunkPos.z, iWorld.getSeed(), 987234911L).nextInt(10) == 0;
				if (random.nextInt(10) == 0 && bl && blockPos.getY() < 40) {
					return method_20636(entityType, iWorld, spawnType, blockPos, random);
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
	public int getLookPitchSpeed() {
		return 0;
	}

	protected boolean makesJumpSound() {
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
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		int i = this.random.nextInt(3);
		if (i < 2 && this.random.nextFloat() < 0.5F * difficulty.getClampedLocalDifficulty()) {
			i++;
		}

		int j = 1 << i;
		this.setSize(j, true);
		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}

	protected SoundEvent getJumpSound() {
		return this.isSmall() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return super.getDimensions(pose).scaled(0.255F * (float)this.getSize());
	}

	static class SlimeMoveControl extends MoveControl {
		private float targetYaw;
		private int ticksUntilJump;
		private final SlimeEntity slime;
		private boolean jumpOften;

		public SlimeMoveControl(SlimeEntity slime) {
			super(slime);
			this.slime = slime;
			this.targetYaw = 180.0F * slime.yaw / (float) Math.PI;
		}

		public void look(float targetYaw, boolean jumpOften) {
			this.targetYaw = targetYaw;
			this.jumpOften = jumpOften;
		}

		public void move(double speed) {
			this.speed = speed;
			this.state = MoveControl.State.MOVE_TO;
		}

		@Override
		public void tick() {
			this.entity.yaw = this.changeAngle(this.entity.yaw, this.targetYaw, 90.0F);
			this.entity.headYaw = this.entity.yaw;
			this.entity.field_6283 = this.entity.yaw;
			if (this.state != MoveControl.State.MOVE_TO) {
				this.entity.setForwardSpeed(0.0F);
			} else {
				this.state = MoveControl.State.WAIT;
				if (this.entity.onGround) {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
					if (this.ticksUntilJump-- <= 0) {
						this.ticksUntilJump = this.slime.getTicksUntilNextJump();
						if (this.jumpOften) {
							this.ticksUntilJump /= 3;
						}

						this.slime.getJumpControl().setActive();
						if (this.slime.makesJumpSound()) {
							this.slime
								.playSound(
									this.slime.getJumpSound(),
									this.slime.getSoundVolume(),
									((this.slime.getRandom().nextFloat() - this.slime.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F
								);
						}
					} else {
						this.slime.sidewaysSpeed = 0.0F;
						this.slime.forwardSpeed = 0.0F;
						this.entity.setMovementSpeed(0.0F);
					}
				} else {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue()));
				}
			}
		}
	}

	static class class_1622 extends Goal {
		private final SlimeEntity slime;
		private int field_7392;

		public class_1622(SlimeEntity slime) {
			this.slime = slime;
			this.setControls(EnumSet.of(Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.slime.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				return livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable
					? false
					: this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
			}
		}

		@Override
		public void start() {
			this.field_7392 = 300;
			super.start();
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = this.slime.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				return livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable ? false : --this.field_7392 > 0;
			}
		}

		@Override
		public void tick() {
			this.slime.lookAtEntity(this.slime.getTarget(), 10.0F, 10.0F);
			((SlimeEntity.SlimeMoveControl)this.slime.getMoveControl()).look(this.slime.yaw, this.slime.isBig());
		}
	}

	static class class_1623 extends Goal {
		private final SlimeEntity slime;

		public class_1623(SlimeEntity slime) {
			this.slime = slime;
			this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
			slime.getNavigation().setCanSwim(true);
		}

		@Override
		public boolean canStart() {
			return (this.slime.isTouchingWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
		}

		@Override
		public void tick() {
			if (this.slime.getRandom().nextFloat() < 0.8F) {
				this.slime.getJumpControl().setActive();
			}

			((SlimeEntity.SlimeMoveControl)this.slime.getMoveControl()).move(1.2);
		}
	}

	static class class_1624 extends Goal {
		private final SlimeEntity slime;

		public class_1624(SlimeEntity slime) {
			this.slime = slime;
			this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return !this.slime.hasVehicle();
		}

		@Override
		public void tick() {
			((SlimeEntity.SlimeMoveControl)this.slime.getMoveControl()).move(1.0);
		}
	}

	static class class_1626 extends Goal {
		private final SlimeEntity slime;
		private float field_7400;
		private int field_7401;

		public class_1626(SlimeEntity slime) {
			this.slime = slime;
			this.setControls(EnumSet.of(Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			return this.slime.getTarget() == null
				&& (this.slime.onGround || this.slime.isTouchingWater() || this.slime.isInLava() || this.slime.hasStatusEffect(StatusEffects.LEVITATION))
				&& this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
		}

		@Override
		public void tick() {
			if (--this.field_7401 <= 0) {
				this.field_7401 = 40 + this.slime.getRandom().nextInt(60);
				this.field_7400 = (float)this.slime.getRandom().nextInt(360);
			}

			((SlimeEntity.SlimeMoveControl)this.slime.getMoveControl()).look(this.field_7400, false);
		}
	}
}
