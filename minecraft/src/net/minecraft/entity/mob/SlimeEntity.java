package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.conversion.EntityConversionType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SlimeEntity extends MobEntity implements Monster {
	private static final TrackedData<Integer> SLIME_SIZE = DataTracker.registerData(SlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final int MIN_SIZE = 1;
	public static final int MAX_SIZE = 127;
	public static final int field_50136 = 4;
	public float targetStretch;
	public float stretch;
	public float lastStretch;
	private boolean onGroundLastTick;

	public SlimeEntity(EntityType<? extends SlimeEntity> entityType, World world) {
		super(entityType, world);
		this.reinitDimensions();
		this.moveControl = new SlimeEntity.SlimeMoveControl(this);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SlimeEntity.SwimmingGoal(this));
		this.goalSelector.add(2, new SlimeEntity.FaceTowardTargetGoal(this));
		this.goalSelector.add(3, new SlimeEntity.RandomLookGoal(this));
		this.goalSelector.add(5, new SlimeEntity.MoveGoal(this));
		this.targetSelector.add(1, new ActiveTargetGoal(this, PlayerEntity.class, 10, true, false, (target, world) -> Math.abs(target.getY() - this.getY()) <= 4.0));
		this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(SLIME_SIZE, 1);
	}

	@VisibleForTesting
	public void setSize(int size, boolean heal) {
		int i = MathHelper.clamp(size, 1, 127);
		this.dataTracker.set(SLIME_SIZE, i);
		this.refreshPosition();
		this.calculateDimensions();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue((double)(i * i));
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue((double)i);
		if (heal) {
			this.setHealth(this.getMaxHealth());
		}

		this.experiencePoints = i;
	}

	public int getSize() {
		return this.dataTracker.get(SLIME_SIZE);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Size", this.getSize() - 1);
		nbt.putBoolean("wasOnGround", this.onGroundLastTick);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setSize(nbt.getInt("Size") + 1, false);
		super.readCustomDataFromNbt(nbt);
		this.onGroundLastTick = nbt.getBoolean("wasOnGround");
	}

	public boolean isSmall() {
		return this.getSize() <= 1;
	}

	protected ParticleEffect getParticles() {
		return ParticleTypes.ITEM_SLIME;
	}

	@Override
	protected boolean isDisallowedInPeaceful() {
		return this.getSize() > 0;
	}

	@Override
	public void tick() {
		this.lastStretch = this.stretch;
		this.stretch = this.stretch + (this.targetStretch - this.stretch) * 0.5F;
		super.tick();
		if (this.isOnGround() && !this.onGroundLastTick) {
			float f = this.getDimensions(this.getPose()).width() * 2.0F;
			float g = f / 2.0F;

			for (int i = 0; (float)i < f * 16.0F; i++) {
				float h = this.random.nextFloat() * (float) (Math.PI * 2);
				float j = this.random.nextFloat() * 0.5F + 0.5F;
				float k = MathHelper.sin(h) * g * j;
				float l = MathHelper.cos(h) * g * j;
				this.getWorld().addParticle(this.getParticles(), this.getX() + (double)k, this.getY(), this.getZ() + (double)l, 0.0, 0.0, 0.0);
			}

			this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			this.targetStretch = -0.5F;
		} else if (!this.isOnGround() && this.onGroundLastTick) {
			this.targetStretch = 1.0F;
		}

		this.onGroundLastTick = this.isOnGround();
		this.updateStretch();
	}

	protected void updateStretch() {
		this.targetStretch *= 0.6F;
	}

	protected int getTicksUntilNextJump() {
		return this.random.nextInt(20) + 10;
	}

	@Override
	public void calculateDimensions() {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		super.calculateDimensions();
		this.setPosition(d, e, f);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (SLIME_SIZE.equals(data)) {
			this.calculateDimensions();
			this.setYaw(this.headYaw);
			this.bodyYaw = this.headYaw;
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
	public void remove(Entity.RemovalReason reason) {
		int i = this.getSize();
		if (!this.getWorld().isClient && i > 1 && this.isDead()) {
			float f = this.getDimensions(this.getPose()).width();
			float g = f / 2.0F;
			int j = i / 2;
			int k = 2 + this.random.nextInt(3);
			Team team = this.getScoreboardTeam();

			for (int l = 0; l < k; l++) {
				float h = ((float)(l % 2) - 0.5F) * g;
				float m = ((float)(l / 2) - 0.5F) * g;
				this.convertTo(this.getType(), new EntityConversionContext(EntityConversionType.SPLIT_ON_DEATH, false, false, team), SpawnReason.TRIGGERED, newSlime -> {
					newSlime.setSize(j, true);
					newSlime.refreshPositionAndAngles(this.getX() + (double)h, this.getY() + 0.5, this.getZ() + (double)m, this.random.nextFloat() * 360.0F, 0.0F);
				});
			}
		}

		super.remove(reason);
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		super.pushAwayFrom(entity);
		if (entity instanceof IronGolemEntity && this.canAttack()) {
			this.damage((LivingEntity)entity);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (this.canAttack()) {
			this.damage(player);
		}
	}

	protected void damage(LivingEntity target) {
		if (this.getWorld() instanceof ServerWorld serverWorld && this.isAlive() && this.isInAttackRange(target) && this.canSee(target)) {
			DamageSource damageSource = this.getDamageSources().mobAttack(this);
			if (target.damage(serverWorld, damageSource, this.getDamageAmount())) {
				this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				EnchantmentHelper.onTargetDamaged(serverWorld, target, damageSource);
			}
		}
	}

	@Override
	protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		return new Vec3d(0.0, (double)dimensions.height() - 0.015625 * (double)this.getSize() * (double)scaleFactor, 0.0);
	}

	protected boolean canAttack() {
		return !this.isSmall() && this.canMoveVoluntarily();
	}

	protected float getDamageAmount() {
		return (float)this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
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

	public static boolean canSpawn(EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		if (world.getDifficulty() != Difficulty.PEACEFUL) {
			if (SpawnReason.isAnySpawner(spawnReason)) {
				return canMobSpawn(type, world, spawnReason, pos, random);
			}

			if (world.getBiome(pos).isIn(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS)
				&& pos.getY() > 50
				&& pos.getY() < 70
				&& random.nextFloat() < 0.5F
				&& random.nextFloat() < world.getMoonSize()
				&& world.getLightLevel(pos) <= random.nextInt(8)) {
				return canMobSpawn(type, world, spawnReason, pos, random);
			}

			if (!(world instanceof StructureWorldAccess)) {
				return false;
			}

			ChunkPos chunkPos = new ChunkPos(pos);
			boolean bl = ChunkRandom.getSlimeRandom(chunkPos.x, chunkPos.z, ((StructureWorldAccess)world).getSeed(), 987234911L).nextInt(10) == 0;
			if (random.nextInt(10) == 0 && bl && pos.getY() < 40) {
				return canMobSpawn(type, world, spawnReason, pos, random);
			}
		}

		return false;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F * (float)this.getSize();
	}

	@Override
	public int getMaxLookPitchChange() {
		return 0;
	}

	protected boolean makesJumpSound() {
		return this.getSize() > 0;
	}

	@Override
	public void jump() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, (double)this.getJumpVelocity(), vec3d.z);
		this.velocityDirty = true;
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		Random random = world.getRandom();
		int i = random.nextInt(3);
		if (i < 2 && random.nextFloat() < 0.5F * difficulty.getClampedLocalDifficulty()) {
			i++;
		}

		int j = 1 << i;
		this.setSize(j, true);
		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	float getJumpSoundPitch() {
		float f = this.isSmall() ? 1.4F : 0.8F;
		return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
	}

	protected SoundEvent getJumpSound() {
		return this.isSmall() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
	}

	@Override
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return super.getBaseDimensions(pose).scaled((float)this.getSize());
	}

	static class FaceTowardTargetGoal extends Goal {
		private final SlimeEntity slime;
		private int ticksLeft;

		public FaceTowardTargetGoal(SlimeEntity slime) {
			this.slime = slime;
			this.setControls(EnumSet.of(Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.slime.getTarget();
			if (livingEntity == null) {
				return false;
			} else {
				return !this.slime.canTarget(livingEntity) ? false : this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
			}
		}

		@Override
		public void start() {
			this.ticksLeft = toGoalTicks(300);
			super.start();
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = this.slime.getTarget();
			if (livingEntity == null) {
				return false;
			} else {
				return !this.slime.canTarget(livingEntity) ? false : --this.ticksLeft > 0;
			}
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.slime.getTarget();
			if (livingEntity != null) {
				this.slime.lookAtEntity(livingEntity, 10.0F, 10.0F);
			}

			if (this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl slimeMoveControl) {
				slimeMoveControl.look(this.slime.getYaw(), this.slime.canAttack());
			}
		}
	}

	static class MoveGoal extends Goal {
		private final SlimeEntity slime;

		public MoveGoal(SlimeEntity slime) {
			this.slime = slime;
			this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return !this.slime.hasVehicle();
		}

		@Override
		public void tick() {
			if (this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl slimeMoveControl) {
				slimeMoveControl.move(1.0);
			}
		}
	}

	static class RandomLookGoal extends Goal {
		private final SlimeEntity slime;
		private float targetYaw;
		private int timer;

		public RandomLookGoal(SlimeEntity slime) {
			this.slime = slime;
			this.setControls(EnumSet.of(Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			return this.slime.getTarget() == null
				&& (this.slime.isOnGround() || this.slime.isTouchingWater() || this.slime.isInLava() || this.slime.hasStatusEffect(StatusEffects.LEVITATION))
				&& this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
		}

		@Override
		public void tick() {
			if (--this.timer <= 0) {
				this.timer = this.getTickCount(40 + this.slime.getRandom().nextInt(60));
				this.targetYaw = (float)this.slime.getRandom().nextInt(360);
			}

			if (this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl slimeMoveControl) {
				slimeMoveControl.look(this.targetYaw, false);
			}
		}
	}

	static class SlimeMoveControl extends MoveControl {
		private float targetYaw;
		private int ticksUntilJump;
		private final SlimeEntity slime;
		private boolean jumpOften;

		public SlimeMoveControl(SlimeEntity slime) {
			super(slime);
			this.slime = slime;
			this.targetYaw = 180.0F * slime.getYaw() / (float) Math.PI;
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
			this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), this.targetYaw, 90.0F));
			this.entity.headYaw = this.entity.getYaw();
			this.entity.bodyYaw = this.entity.getYaw();
			if (this.state != MoveControl.State.MOVE_TO) {
				this.entity.setForwardSpeed(0.0F);
			} else {
				this.state = MoveControl.State.WAIT;
				if (this.entity.isOnGround()) {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED)));
					if (this.ticksUntilJump-- <= 0) {
						this.ticksUntilJump = this.slime.getTicksUntilNextJump();
						if (this.jumpOften) {
							this.ticksUntilJump /= 3;
						}

						this.slime.getJumpControl().setActive();
						if (this.slime.makesJumpSound()) {
							this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getJumpSoundPitch());
						}
					} else {
						this.slime.sidewaysSpeed = 0.0F;
						this.slime.forwardSpeed = 0.0F;
						this.entity.setMovementSpeed(0.0F);
					}
				} else {
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED)));
				}
			}
		}
	}

	static class SwimmingGoal extends Goal {
		private final SlimeEntity slime;

		public SwimmingGoal(SlimeEntity slime) {
			this.slime = slime;
			this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
			slime.getNavigation().setCanSwim(true);
		}

		@Override
		public boolean canStart() {
			return (this.slime.isTouchingWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			if (this.slime.getRandom().nextFloat() < 0.8F) {
				this.slime.getJumpControl().setActive();
			}

			if (this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl slimeMoveControl) {
				slimeMoveControl.move(1.2);
			}
		}
	}
}
