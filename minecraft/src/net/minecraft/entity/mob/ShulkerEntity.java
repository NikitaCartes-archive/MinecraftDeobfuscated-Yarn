package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ShulkerEntity extends GolemEntity implements Monster {
	private static final UUID COVERED_ARMOR_BONUS_ID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
	private static final EntityAttributeModifier COVERED_ARMOR_BONUS = new EntityAttributeModifier(
		COVERED_ARMOR_BONUS_ID, "Covered armor bonus", 20.0, EntityAttributeModifier.Operation.ADDITION
	);
	protected static final TrackedData<Direction> ATTACHED_FACE = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.FACING);
	protected static final TrackedData<Byte> PEEK_AMOUNT = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> COLOR = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int field_30487 = 6;
	private static final byte field_30488 = 16;
	private static final byte field_30489 = 16;
	private static final int field_30490 = 8;
	private static final int field_30491 = 8;
	private static final int field_30492 = 5;
	private static final float field_30493 = 0.05F;
	static final Vec3f SOUTH_VECTOR = Util.make(() -> {
		Vec3i vec3i = Direction.SOUTH.getVector();
		return new Vec3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
	});
	private float prevOpenProgress;
	private float openProgress;
	@Nullable
	private BlockPos prevAttachedBlock;
	private int teleportLerpTimer;
	private static final float field_30494 = 1.0F;

	public ShulkerEntity(EntityType<? extends ShulkerEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
		this.lookControl = new ShulkerEntity.ShulkerLookControl(this);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F, 0.02F, true));
		this.goalSelector.add(4, new ShulkerEntity.ShootBulletGoal());
		this.goalSelector.add(7, new ShulkerEntity.PeekGoal());
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this, this.getClass()).setGroupRevenge());
		this.targetSelector.add(2, new ShulkerEntity.TargetPlayerGoal(this));
		this.targetSelector.add(3, new ShulkerEntity.TargetOtherTeamGoal(this));
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SHULKER_AMBIENT;
	}

	@Override
	public void playAmbientSound() {
		if (!this.isClosed()) {
			super.playAmbientSound();
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SHULKER_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.isClosed() ? SoundEvents.ENTITY_SHULKER_HURT_CLOSED : SoundEvents.ENTITY_SHULKER_HURT;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ATTACHED_FACE, Direction.DOWN);
		this.dataTracker.startTracking(PEEK_AMOUNT, (byte)0);
		this.dataTracker.startTracking(COLOR, (byte)16);
	}

	public static DefaultAttributeContainer.Builder createShulkerAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0);
	}

	@Override
	protected BodyControl createBodyControl() {
		return new ShulkerEntity.ShulkerBodyControl(this);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setAttachedFace(Direction.byId(nbt.getByte("AttachFace")));
		this.dataTracker.set(PEEK_AMOUNT, nbt.getByte("Peek"));
		if (nbt.contains("Color", NbtElement.NUMBER_TYPE)) {
			this.dataTracker.set(COLOR, nbt.getByte("Color"));
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("AttachFace", (byte)this.getAttachedFace().getId());
		nbt.putByte("Peek", this.dataTracker.get(PEEK_AMOUNT));
		nbt.putByte("Color", this.dataTracker.get(COLOR));
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient && !this.hasVehicle() && !this.canStay(this.getBlockPos(), this.getAttachedFace())) {
			this.tryAttachOrTeleport();
		}

		if (this.tickOpenProgress()) {
			this.moveEntities();
		}

		if (this.world.isClient) {
			if (this.teleportLerpTimer > 0) {
				this.teleportLerpTimer--;
			} else {
				this.prevAttachedBlock = null;
			}
		}
	}

	private void tryAttachOrTeleport() {
		Direction direction = this.findAttachSide(this.getBlockPos());
		if (direction != null) {
			this.setAttachedFace(direction);
		} else {
			this.tryTeleport();
		}
	}

	@Override
	protected Box calculateBoundingBox() {
		float f = getExtraLength(this.openProgress);
		Direction direction = this.getAttachedFace().getOpposite();
		float g = this.getType().getWidth() / 2.0F;
		return calculateBoundingBox(direction, f).offset(this.getX() - (double)g, this.getY(), this.getZ() - (double)g);
	}

	private static float getExtraLength(float openProgress) {
		return 0.5F - MathHelper.sin((0.5F + openProgress) * (float) Math.PI) * 0.5F;
	}

	private boolean tickOpenProgress() {
		this.prevOpenProgress = this.openProgress;
		float f = (float)this.getPeekAmount() * 0.01F;
		if (this.openProgress == f) {
			return false;
		} else {
			if (this.openProgress > f) {
				this.openProgress = MathHelper.clamp(this.openProgress - 0.05F, f, 1.0F);
			} else {
				this.openProgress = MathHelper.clamp(this.openProgress + 0.05F, 0.0F, f);
			}

			return true;
		}
	}

	private void moveEntities() {
		this.refreshPosition();
		float f = getExtraLength(this.openProgress);
		float g = getExtraLength(this.prevOpenProgress);
		Direction direction = this.getAttachedFace().getOpposite();
		float h = f - g;
		if (!(h <= 0.0F)) {
			for (Entity entity : this.world
				.getOtherEntities(
					this,
					calculateBoundingBox(direction, g, f).offset(this.getX() - 0.5, this.getY(), this.getZ() - 0.5),
					EntityPredicates.EXCEPT_SPECTATOR.and(entityx -> !entityx.isConnectedThroughVehicle(this))
				)) {
				if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
					entity.move(
						MovementType.SHULKER,
						new Vec3d((double)(h * (float)direction.getOffsetX()), (double)(h * (float)direction.getOffsetY()), (double)(h * (float)direction.getOffsetZ()))
					);
				}
			}
		}
	}

	public static Box calculateBoundingBox(Direction direction, float extraLength) {
		return calculateBoundingBox(direction, -1.0F, extraLength);
	}

	public static Box calculateBoundingBox(Direction direction, float prevExtraLength, float extraLength) {
		double d = (double)Math.max(prevExtraLength, extraLength);
		double e = (double)Math.min(prevExtraLength, extraLength);
		return new Box(BlockPos.ORIGIN)
			.stretch((double)direction.getOffsetX() * d, (double)direction.getOffsetY() * d, (double)direction.getOffsetZ() * d)
			.shrink((double)(-direction.getOffsetX()) * (1.0 + e), (double)(-direction.getOffsetY()) * (1.0 + e), (double)(-direction.getOffsetZ()) * (1.0 + e));
	}

	@Override
	public double getHeightOffset() {
		EntityType<?> entityType = this.getVehicle().getType();
		return entityType != EntityType.BOAT && entityType != EntityType.MINECART ? super.getHeightOffset() : 0.1875 - this.getVehicle().getMountedHeightOffset();
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (this.world.isClient()) {
			this.prevAttachedBlock = null;
			this.teleportLerpTimer = 0;
		}

		this.setAttachedFace(Direction.DOWN);
		return super.startRiding(entity, force);
	}

	@Override
	public void stopRiding() {
		super.stopRiding();
		if (this.world.isClient) {
			this.prevAttachedBlock = this.getBlockPos();
		}

		this.prevBodyYaw = 0.0F;
		this.bodyYaw = 0.0F;
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		this.setYaw(0.0F);
		this.headYaw = this.getYaw();
		this.resetPosition();
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	public void move(MovementType movementType, Vec3d movement) {
		if (movementType == MovementType.SHULKER_BOX) {
			this.tryTeleport();
		} else {
			super.move(movementType, movement);
		}
	}

	@Override
	public Vec3d getVelocity() {
		return Vec3d.ZERO;
	}

	@Override
	public void setVelocity(Vec3d velocity) {
	}

	@Override
	public void setPosition(double x, double y, double z) {
		BlockPos blockPos = this.getBlockPos();
		if (this.hasVehicle()) {
			super.setPosition(x, y, z);
		} else {
			super.setPosition((double)MathHelper.floor(x) + 0.5, (double)MathHelper.floor(y + 0.5), (double)MathHelper.floor(z) + 0.5);
		}

		if (this.age != 0) {
			BlockPos blockPos2 = this.getBlockPos();
			if (!blockPos2.equals(blockPos)) {
				this.dataTracker.set(PEEK_AMOUNT, (byte)0);
				this.velocityDirty = true;
				if (this.world.isClient && !this.hasVehicle() && !blockPos2.equals(this.prevAttachedBlock)) {
					this.prevAttachedBlock = blockPos;
					this.teleportLerpTimer = 6;
					this.lastRenderX = this.getX();
					this.lastRenderY = this.getY();
					this.lastRenderZ = this.getZ();
				}
			}
		}
	}

	@Nullable
	protected Direction findAttachSide(BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (this.canStay(pos, direction)) {
				return direction;
			}
		}

		return null;
	}

	boolean canStay(BlockPos pos, Direction direction) {
		if (this.isInvalidPosition(pos)) {
			return false;
		} else {
			Direction direction2 = direction.getOpposite();
			if (!this.world.isDirectionSolid(pos.offset(direction), this, direction2)) {
				return false;
			} else {
				Box box = calculateBoundingBox(direction2, 1.0F).offset(pos).contract(1.0E-6);
				return this.world.isSpaceEmpty(this, box);
			}
		}
	}

	private boolean isInvalidPosition(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);
		if (blockState.isAir()) {
			return false;
		} else {
			boolean bl = blockState.isOf(Blocks.MOVING_PISTON) && pos.equals(this.getBlockPos());
			return !bl;
		}
	}

	protected boolean tryTeleport() {
		if (!this.isAiDisabled() && this.isAlive()) {
			BlockPos blockPos = this.getBlockPos();

			for (int i = 0; i < 5; i++) {
				BlockPos blockPos2 = blockPos.add(
					MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8)
				);
				if (blockPos2.getY() > this.world.getBottomY()
					&& this.world.isAir(blockPos2)
					&& this.world.getWorldBorder().contains(blockPos2)
					&& this.world.isSpaceEmpty(this, new Box(blockPos2).contract(1.0E-6))) {
					Direction direction = this.findAttachSide(blockPos2);
					if (direction != null) {
						this.detach();
						this.setAttachedFace(direction);
						this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
						this.setPosition((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
						this.dataTracker.set(PEEK_AMOUNT, (byte)0);
						this.setTarget(null);
						return true;
					}
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.bodyTrackingIncrements = 0;
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isClosed()) {
			Entity entity = source.getSource();
			if (entity instanceof PersistentProjectileEntity) {
				return false;
			}
		}

		if (!super.damage(source, amount)) {
			return false;
		} else {
			if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5 && this.random.nextInt(4) == 0) {
				this.tryTeleport();
			} else if (source.isProjectile()) {
				Entity entity = source.getSource();
				if (entity != null && entity.getType() == EntityType.SHULKER_BULLET) {
					this.spawnNewShulker();
				}
			}

			return true;
		}
	}

	private boolean isClosed() {
		return this.getPeekAmount() == 0;
	}

	private void spawnNewShulker() {
		Vec3d vec3d = this.getPos();
		Box box = this.getBoundingBox();
		if (!this.isClosed() && this.tryTeleport()) {
			int i = this.world.getEntitiesByType(EntityType.SHULKER, box.expand(8.0), Entity::isAlive).size();
			float f = (float)(i - 1) / 5.0F;
			if (!(this.world.random.nextFloat() < f)) {
				ShulkerEntity shulkerEntity = EntityType.SHULKER.create(this.world);
				DyeColor dyeColor = this.getColor();
				if (dyeColor != null) {
					shulkerEntity.setColor(dyeColor);
				}

				shulkerEntity.refreshPositionAfterTeleport(vec3d);
				this.world.spawnEntity(shulkerEntity);
			}
		}
	}

	@Override
	public boolean isCollidable() {
		return this.isAlive();
	}

	public Direction getAttachedFace() {
		return this.dataTracker.get(ATTACHED_FACE);
	}

	private void setAttachedFace(Direction face) {
		this.dataTracker.set(ATTACHED_FACE, face);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (ATTACHED_FACE.equals(data)) {
			this.setBoundingBox(this.calculateBoundingBox());
		}

		super.onTrackedDataSet(data);
	}

	private int getPeekAmount() {
		return this.dataTracker.get(PEEK_AMOUNT);
	}

	void setPeekAmount(int peekAmount) {
		if (!this.world.isClient) {
			this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(COVERED_ARMOR_BONUS);
			if (peekAmount == 0) {
				this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(COVERED_ARMOR_BONUS);
				this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
				this.emitGameEvent(GameEvent.CONTAINER_CLOSE);
			} else {
				this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
				this.emitGameEvent(GameEvent.CONTAINER_OPEN);
			}
		}

		this.dataTracker.set(PEEK_AMOUNT, (byte)peekAmount);
	}

	public float getOpenProgress(float delta) {
		return MathHelper.lerp(delta, this.prevOpenProgress, this.openProgress);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.5F;
	}

	@Override
	public void readFromPacket(MobSpawnS2CPacket packet) {
		super.readFromPacket(packet);
		this.bodyYaw = 0.0F;
		this.prevBodyYaw = 0.0F;
	}

	@Override
	public int getMaxLookPitchChange() {
		return 180;
	}

	@Override
	public int getMaxHeadRotation() {
		return 180;
	}

	@Override
	public void pushAwayFrom(Entity entity) {
	}

	@Override
	public float getTargetingMargin() {
		return 0.0F;
	}

	public Optional<Vec3d> method_33352(float f) {
		if (this.prevAttachedBlock != null && this.teleportLerpTimer > 0) {
			double d = (double)((float)this.teleportLerpTimer - f) / 6.0;
			d *= d;
			BlockPos blockPos = this.getBlockPos();
			double e = (double)(blockPos.getX() - this.prevAttachedBlock.getX()) * d;
			double g = (double)(blockPos.getY() - this.prevAttachedBlock.getY()) * d;
			double h = (double)(blockPos.getZ() - this.prevAttachedBlock.getZ()) * d;
			return Optional.of(new Vec3d(-e, -g, -h));
		} else {
			return Optional.empty();
		}
	}

	private void setColor(DyeColor color) {
		this.dataTracker.set(COLOR, (byte)color.getId());
	}

	@Nullable
	public DyeColor getColor() {
		byte b = this.dataTracker.get(COLOR);
		return b != 16 && b <= 15 ? DyeColor.byId(b) : null;
	}

	class PeekGoal extends Goal {
		private int counter;

		@Override
		public boolean canStart() {
			return ShulkerEntity.this.getTarget() == null
				&& ShulkerEntity.this.random.nextInt(toGoalTicks(40)) == 0
				&& ShulkerEntity.this.canStay(ShulkerEntity.this.getBlockPos(), ShulkerEntity.this.getAttachedFace());
		}

		@Override
		public boolean shouldContinue() {
			return ShulkerEntity.this.getTarget() == null && this.counter > 0;
		}

		@Override
		public void start() {
			this.counter = this.getTickCount(20 * (1 + ShulkerEntity.this.random.nextInt(3)));
			ShulkerEntity.this.setPeekAmount(30);
		}

		@Override
		public void stop() {
			if (ShulkerEntity.this.getTarget() == null) {
				ShulkerEntity.this.setPeekAmount(0);
			}
		}

		@Override
		public void tick() {
			this.counter--;
		}
	}

	class ShootBulletGoal extends Goal {
		private int counter;

		public ShootBulletGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = ShulkerEntity.this.getTarget();
			return livingEntity != null && livingEntity.isAlive() ? ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL : false;
		}

		@Override
		public void start() {
			this.counter = 20;
			ShulkerEntity.this.setPeekAmount(100);
		}

		@Override
		public void stop() {
			ShulkerEntity.this.setPeekAmount(0);
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			if (ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL) {
				this.counter--;
				LivingEntity livingEntity = ShulkerEntity.this.getTarget();
				if (livingEntity != null) {
					ShulkerEntity.this.getLookControl().lookAt(livingEntity, 180.0F, 180.0F);
					double d = ShulkerEntity.this.squaredDistanceTo(livingEntity);
					if (d < 400.0) {
						if (this.counter <= 0) {
							this.counter = 20 + ShulkerEntity.this.random.nextInt(10) * 20 / 2;
							ShulkerEntity.this.world
								.spawnEntity(new ShulkerBulletEntity(ShulkerEntity.this.world, ShulkerEntity.this, livingEntity, ShulkerEntity.this.getAttachedFace().getAxis()));
							ShulkerEntity.this.playSound(
								SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (ShulkerEntity.this.random.nextFloat() - ShulkerEntity.this.random.nextFloat()) * 0.2F + 1.0F
							);
						}
					} else {
						ShulkerEntity.this.setTarget(null);
					}

					super.tick();
				}
			}
		}
	}

	static class ShulkerBodyControl extends BodyControl {
		public ShulkerBodyControl(MobEntity mobEntity) {
			super(mobEntity);
		}

		@Override
		public void tick() {
		}
	}

	class ShulkerLookControl extends LookControl {
		public ShulkerLookControl(MobEntity entity) {
			super(entity);
		}

		@Override
		protected void clampHeadYaw() {
		}

		@Override
		protected Optional<Float> getTargetYaw() {
			Direction direction = ShulkerEntity.this.getAttachedFace().getOpposite();
			Vec3f vec3f = ShulkerEntity.SOUTH_VECTOR.copy();
			vec3f.rotate(direction.getRotationQuaternion());
			Vec3i vec3i = direction.getVector();
			Vec3f vec3f2 = new Vec3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
			vec3f2.cross(vec3f);
			double d = this.x - this.entity.getX();
			double e = this.y - this.entity.getEyeY();
			double f = this.z - this.entity.getZ();
			Vec3f vec3f3 = new Vec3f((float)d, (float)e, (float)f);
			float g = vec3f2.dot(vec3f3);
			float h = vec3f.dot(vec3f3);
			return !(Math.abs(g) > 1.0E-5F) && !(Math.abs(h) > 1.0E-5F)
				? Optional.empty()
				: Optional.of((float)(MathHelper.atan2((double)(-g), (double)h) * 180.0F / (float)Math.PI));
		}

		@Override
		protected Optional<Float> getTargetPitch() {
			return Optional.of(0.0F);
		}
	}

	/**
	 * A target goal on other teams' entities if this shulker belongs
	 * to a team.
	 */
	static class TargetOtherTeamGoal extends ActiveTargetGoal<LivingEntity> {
		public TargetOtherTeamGoal(ShulkerEntity shulker) {
			super(shulker, LivingEntity.class, 10, true, false, entity -> entity instanceof Monster);
		}

		@Override
		public boolean canStart() {
			return this.mob.getScoreboardTeam() == null ? false : super.canStart();
		}

		@Override
		protected Box getSearchBox(double distance) {
			Direction direction = ((ShulkerEntity)this.mob).getAttachedFace();
			if (direction.getAxis() == Direction.Axis.X) {
				return this.mob.getBoundingBox().expand(4.0, distance, distance);
			} else {
				return direction.getAxis() == Direction.Axis.Z
					? this.mob.getBoundingBox().expand(distance, distance, 4.0)
					: this.mob.getBoundingBox().expand(distance, 4.0, distance);
			}
		}
	}

	/**
	 * A hostile target goal on players.
	 */
	class TargetPlayerGoal extends ActiveTargetGoal<PlayerEntity> {
		public TargetPlayerGoal(ShulkerEntity shulker) {
			super(shulker, PlayerEntity.class, true);
		}

		@Override
		public boolean canStart() {
			return ShulkerEntity.this.world.getDifficulty() == Difficulty.PEACEFUL ? false : super.canStart();
		}

		@Override
		protected Box getSearchBox(double distance) {
			Direction direction = ((ShulkerEntity)this.mob).getAttachedFace();
			if (direction.getAxis() == Direction.Axis.X) {
				return this.mob.getBoundingBox().expand(4.0, distance, distance);
			} else {
				return direction.getAxis() == Direction.Axis.Z
					? this.mob.getBoundingBox().expand(distance, distance, 4.0)
					: this.mob.getBoundingBox().expand(distance, 4.0, distance);
			}
		}
	}
}
