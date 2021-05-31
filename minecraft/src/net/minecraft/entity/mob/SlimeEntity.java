package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.ChunkRandom;

public class SlimeEntity extends MobEntity implements Monster {
	private static final TrackedData<Integer> SLIME_SIZE = DataTracker.registerData(SlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final int MIN_SIZE = 1;
	public static final int MAX_SIZE = 127;
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
		this.goalSelector.add(1, new SlimeEntity.SwimmingGoal(this));
		this.goalSelector.add(2, new SlimeEntity.FaceTowardTargetGoal(this));
		this.goalSelector.add(3, new SlimeEntity.RandomLookGoal(this));
		this.goalSelector.add(5, new SlimeEntity.MoveGoal(this));
		this.targetSelector
			.add(1, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, livingEntity -> Math.abs(livingEntity.getY() - this.getY()) <= 4.0));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SLIME_SIZE, 1);
	}

	protected void setSize(int size, boolean heal) {
		int i = MathHelper.clamp(size, 1, 127);
		this.dataTracker.set(SLIME_SIZE, i);
		this.refreshPosition();
		this.calculateDimensions();
		this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)(i * i));
		this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)i));
		this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue((double)i);
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
				this.world.addParticle(this.getParticles(), this.getX() + (double)h, this.getY(), this.getZ() + (double)k, 0.0, 0.0, 0.0);
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
		if (!this.world.isClient && i > 1 && this.isDead()) {
			Text text = this.getCustomName();
			boolean bl = this.isAiDisabled();
			float f = (float)i / 4.0F;
			int j = i / 2;
			int k = 2 + this.random.nextInt(3);

			for (int l = 0; l < k; l++) {
				float g = ((float)(l % 2) - 0.5F) * f;
				float h = ((float)(l / 2) - 0.5F) * f;
				SlimeEntity slimeEntity = this.getType().create(this.world);
				if (this.isPersistent()) {
					slimeEntity.setPersistent();
				}

				slimeEntity.setCustomName(text);
				slimeEntity.setAiDisabled(bl);
				slimeEntity.setInvulnerable(this.isInvulnerable());
				slimeEntity.setSize(j, true);
				slimeEntity.refreshPositionAndAngles(this.getX() + (double)g, this.getY() + 0.5, this.getZ() + (double)h, this.random.nextFloat() * 360.0F, 0.0F);
				this.world.spawnEntity(slimeEntity);
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
		if (this.isAlive()) {
			int i = this.getSize();
			if (this.squaredDistanceTo(target) < 0.6 * (double)i * 0.6 * (double)i
				&& this.canSee(target)
				&& target.damage(DamageSource.mob(this), this.getDamageAmount())) {
				this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				this.applyDamageEffects(this, target);
			}
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.625F * dimensions.height;
	}

	protected boolean canAttack() {
		return !this.isSmall() && this.canMoveVoluntarily();
	}

	protected float getDamageAmount() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
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

	public static boolean canSpawn(EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		if (world.getDifficulty() != Difficulty.PEACEFUL) {
			if (Objects.equals(world.getBiomeKey(pos), Optional.of(BiomeKeys.SWAMP))
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
	public int getLookPitchSpeed() {
		return 0;
	}

	protected boolean makesJumpSound() {
		return this.getSize() > 0;
	}

	@Override
	protected void jump() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, (double)this.getJumpVelocity(), vec3d.z);
		this.velocityDirty = true;
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		int i = this.random.nextInt(3);
		if (i < 2 && this.random.nextFloat() < 0.5F * difficulty.getClampedLocalDifficulty()) {
			i++;
		}

		int j = 1 << i;
		this.setSize(j, true);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	float getJumpSoundPitch() {
		float f = this.isSmall() ? 1.4F : 0.8F;
		return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
	}

	protected SoundEvent getJumpSound() {
		return this.isSmall() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return super.getDimensions(pose).scaled(0.255F * (float)this.getSize());
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
			this.ticksLeft = 300;
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
		public void tick() {
			this.slime.lookAtEntity(this.slime.getTarget(), 10.0F, 10.0F);
			((SlimeEntity.SlimeMoveControl)this.slime.getMoveControl()).look(this.slime.getYaw(), this.slime.canAttack());
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
			((SlimeEntity.SlimeMoveControl)this.slime.getMoveControl()).move(1.0);
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
				&& (this.slime.onGround || this.slime.isTouchingWater() || this.slime.isInLava() || this.slime.hasStatusEffect(StatusEffects.LEVITATION))
				&& this.slime.getMoveControl() instanceof SlimeEntity.SlimeMoveControl;
		}

		@Override
		public void tick() {
			if (--this.timer <= 0) {
				this.timer = 40 + this.slime.getRandom().nextInt(60);
				this.targetYaw = (float)this.slime.getRandom().nextInt(360);
			}

			((SlimeEntity.SlimeMoveControl)this.slime.getMoveControl()).look(this.targetYaw, false);
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
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
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
					this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
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
		public void tick() {
			if (this.slime.getRandom().nextFloat() < 0.8F) {
				this.slime.getJumpControl().setActive();
			}

			((SlimeEntity.SlimeMoveControl)this.slime.getMoveControl()).move(1.2);
		}
	}
}
