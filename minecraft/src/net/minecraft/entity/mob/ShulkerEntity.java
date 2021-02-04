package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.entity.ai.goal.FollowTargetGoal;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
	private float prevOpenProgress;
	private float openProgress;
	@Nullable
	private BlockPos prevAttachedBlock;
	private int teleportLerpTimer;

	public ShulkerEntity(EntityType<? extends ShulkerEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(4, new ShulkerEntity.ShootBulletGoal());
		this.goalSelector.add(7, new ShulkerEntity.PeekGoal());
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this, this.getClass()).setGroupRevenge());
		this.targetSelector.add(2, new ShulkerEntity.SearchForPlayerGoal(this));
		this.targetSelector.add(3, new ShulkerEntity.SearchForTargetGoal(this));
	}

	@Override
	protected Entity.class_5799 method_33570() {
		return Entity.class_5799.NONE;
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
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.dataTracker.set(ATTACHED_FACE, Direction.byId(tag.getByte("AttachFace")));
		this.dataTracker.set(PEEK_AMOUNT, tag.getByte("Peek"));
		if (tag.contains("Color", 99)) {
			this.dataTracker.set(COLOR, tag.getByte("Color"));
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putByte("AttachFace", (byte)this.dataTracker.get(ATTACHED_FACE).getId());
		tag.putByte("Peek", this.dataTracker.get(PEEK_AMOUNT));
		tag.putByte("Color", this.dataTracker.get(COLOR));
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient && !this.hasVehicle() && !this.canStay(this.getBlockPos(), this.getAttachedFace())) {
			this.method_33348();
		}

		if (this.method_33349()) {
			this.method_33350();
		}

		if (this.world.isClient) {
			if (this.teleportLerpTimer > 0) {
				this.teleportLerpTimer--;
			} else {
				this.prevAttachedBlock = null;
			}
		}
	}

	private void method_33348() {
		Direction direction = this.findAttachSide(this.getBlockPos());
		if (direction != null) {
			this.dataTracker.set(ATTACHED_FACE, direction);
		} else {
			this.tryTeleport();
		}
	}

	@Override
	protected Box method_33332() {
		float f = method_33342(this.openProgress);
		Direction direction = this.getAttachedFace().getOpposite();
		float g = this.getType().getWidth() / 2.0F;
		return method_33346(direction, f).offset(this.getX() - (double)g, this.getY(), this.getZ() - (double)g);
	}

	private static float method_33342(float f) {
		return 0.5F - MathHelper.sin((0.5F + f) * (float) Math.PI) * 0.5F;
	}

	private boolean method_33349() {
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

	private void method_33350() {
		this.refreshPosition();
		float f = method_33342(this.openProgress);
		float g = method_33342(this.prevOpenProgress);
		Direction direction = this.getAttachedFace().getOpposite();
		float h = f - g;
		if (!(h <= 0.0F)) {
			for (Entity entity : this.world
				.getOtherEntities(
					this,
					method_33347(direction, g, f).offset(this.getX() - 0.5, this.getY(), this.getZ() - 0.5),
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

	public static Box method_33346(Direction direction, float f) {
		return method_33347(direction, -1.0F, f);
	}

	public static Box method_33347(Direction direction, float f, float g) {
		double d = (double)Math.max(f, g);
		double e = (double)Math.min(f, g);
		return new Box(BlockPos.ORIGIN)
			.stretch((double)direction.getOffsetX() * d, (double)direction.getOffsetY() * d, (double)direction.getOffsetZ() * d)
			.shrink((double)(-direction.getOffsetX()) * (1.0 + e), (double)(-direction.getOffsetY()) * (1.0 + e), (double)(-direction.getOffsetZ()) * (1.0 + e));
	}

	@Override
	public double getHeightOffset() {
		return 0.1875 - this.getVehicle().getMountedHeightOffset();
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (this.world.isClient()) {
			this.prevAttachedBlock = null;
			this.teleportLerpTimer = 0;
		}

		this.dataTracker.set(ATTACHED_FACE, Direction.DOWN);
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
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		this.yaw = 0.0F;
		this.headYaw = this.yaw;
		this.resetPosition();
		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
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

	private boolean canStay(BlockPos pos, Direction direction) {
		if (this.method_33351(pos)) {
			return false;
		} else {
			Direction direction2 = direction.getOpposite();
			if (!this.world.isDirectionSolid(pos.offset(direction), this, direction2)) {
				return false;
			} else {
				Box box = method_33346(direction2, 1.0F).offset(pos).contract(1.0E-6);
				return this.world.isSpaceEmpty(this, box);
			}
		}
	}

	private boolean method_33351(BlockPos blockPos) {
		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.isAir()) {
			return false;
		} else {
			boolean bl = blockState.isOf(Blocks.MOVING_PISTON) && blockPos.equals(this.getBlockPos());
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
						this.dataTracker.set(ATTACHED_FACE, direction);
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

	@Environment(EnvType.CLIENT)
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

	private int getPeekAmount() {
		return this.dataTracker.get(PEEK_AMOUNT);
	}

	private void setPeekAmount(int peekAmount) {
		if (!this.world.isClient) {
			this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(COVERED_ARMOR_BONUS);
			if (peekAmount == 0) {
				this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(COVERED_ARMOR_BONUS);
				this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
				this.emitGameEvent(GameEvent.SHULKER_CLOSE);
			} else {
				this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
				this.emitGameEvent(GameEvent.SHULKER_OPEN);
			}
		}

		this.dataTracker.set(PEEK_AMOUNT, (byte)peekAmount);
	}

	@Environment(EnvType.CLIENT)
	public float getOpenProgress(float delta) {
		return MathHelper.lerp(delta, this.prevOpenProgress, this.openProgress);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.5F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_33579(MobSpawnS2CPacket mobSpawnS2CPacket) {
		super.method_33579(mobSpawnS2CPacket);
		this.bodyYaw = 0.0F;
	}

	@Override
	public int getLookPitchSpeed() {
		return 180;
	}

	@Override
	public int getBodyYawSpeed() {
		return 180;
	}

	@Override
	public void pushAwayFrom(Entity entity) {
	}

	@Override
	public float getTargetingMargin() {
		return 0.0F;
	}

	@Environment(EnvType.CLIENT)
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

		private PeekGoal() {
		}

		@Override
		public boolean canStart() {
			return ShulkerEntity.this.getTarget() == null
				&& ShulkerEntity.this.random.nextInt(40) == 0
				&& ShulkerEntity.this.canStay(ShulkerEntity.this.getBlockPos(), ShulkerEntity.this.getAttachedFace());
		}

		@Override
		public boolean shouldContinue() {
			return ShulkerEntity.this.getTarget() == null && this.counter > 0;
		}

		@Override
		public void start() {
			this.counter = 20 * (1 + ShulkerEntity.this.random.nextInt(3));
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

	class SearchForPlayerGoal extends FollowTargetGoal<PlayerEntity> {
		public SearchForPlayerGoal(ShulkerEntity shulker) {
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

	static class SearchForTargetGoal extends FollowTargetGoal<LivingEntity> {
		public SearchForTargetGoal(ShulkerEntity shulker) {
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
		public void tick() {
			if (ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL) {
				this.counter--;
				LivingEntity livingEntity = ShulkerEntity.this.getTarget();
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

	static class ShulkerBodyControl extends BodyControl {
		public ShulkerBodyControl(MobEntity mobEntity) {
			super(mobEntity);
		}

		@Override
		public void tick() {
		}
	}
}
