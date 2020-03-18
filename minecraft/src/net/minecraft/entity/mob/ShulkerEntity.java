package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ShulkerBulletEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ShulkerEntity extends GolemEntity implements Monster {
	private static final UUID ATTR_COVERED_ARMOR_BONUS_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
	private static final EntityAttributeModifier ATTR_COVERED_ARMOR_BONUS = new EntityAttributeModifier(
			ATTR_COVERED_ARMOR_BONUS_UUID, "Covered armor bonus", 20.0, EntityAttributeModifier.Operation.ADDITION
		)
		.setSerialize(false);
	protected static final TrackedData<Direction> ATTACHED_FACE = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.FACING);
	protected static final TrackedData<Optional<BlockPos>> ATTACHED_BLOCK = DataTracker.registerData(
		ShulkerEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS
	);
	protected static final TrackedData<Byte> PEEK_AMOUNT = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> COLOR = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
	private float field_7339;
	private float field_7337;
	private BlockPos field_7345;
	private int field_7340;

	public ShulkerEntity(EntityType<? extends ShulkerEntity> entityType, World world) {
		super(entityType, world);
		this.prevBodyYaw = 180.0F;
		this.bodyYaw = 180.0F;
		this.field_7345 = null;
		this.experiencePoints = 5;
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		this.bodyYaw = 180.0F;
		this.prevBodyYaw = 180.0F;
		this.yaw = 180.0F;
		this.prevYaw = 180.0F;
		this.headYaw = 180.0F;
		this.prevHeadYaw = 180.0F;
		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(4, new ShulkerEntity.ShootBulletGoal());
		this.goalSelector.add(7, new ShulkerEntity.PeekGoal());
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(2, new ShulkerEntity.SearchForPlayerGoal(this));
		this.targetSelector.add(3, new ShulkerEntity.SearchForTargetGoal(this));
	}

	@Override
	protected boolean canClimb() {
		return false;
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
		if (!this.method_7124()) {
			super.playAmbientSound();
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SHULKER_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return this.method_7124() ? SoundEvents.ENTITY_SHULKER_HURT_CLOSED : SoundEvents.ENTITY_SHULKER_HURT;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ATTACHED_FACE, Direction.DOWN);
		this.dataTracker.startTracking(ATTACHED_BLOCK, Optional.empty());
		this.dataTracker.startTracking(PEEK_AMOUNT, (byte)0);
		this.dataTracker.startTracking(COLOR, (byte)16);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
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
		this.dataTracker.set(COLOR, tag.getByte("Color"));
		if (tag.contains("APX")) {
			int i = tag.getInt("APX");
			int j = tag.getInt("APY");
			int k = tag.getInt("APZ");
			this.dataTracker.set(ATTACHED_BLOCK, Optional.of(new BlockPos(i, j, k)));
		} else {
			this.dataTracker.set(ATTACHED_BLOCK, Optional.empty());
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putByte("AttachFace", (byte)this.dataTracker.get(ATTACHED_FACE).getId());
		tag.putByte("Peek", this.dataTracker.get(PEEK_AMOUNT));
		tag.putByte("Color", this.dataTracker.get(COLOR));
		BlockPos blockPos = this.getAttachedBlock();
		if (blockPos != null) {
			tag.putInt("APX", blockPos.getX());
			tag.putInt("APY", blockPos.getY());
			tag.putInt("APZ", blockPos.getZ());
		}
	}

	@Override
	public void tick() {
		super.tick();
		BlockPos blockPos = (BlockPos)this.dataTracker.get(ATTACHED_BLOCK).orElse(null);
		if (blockPos == null && !this.world.isClient) {
			blockPos = this.getBlockPos();
			this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
		}

		if (this.hasVehicle()) {
			blockPos = null;
			float f = this.getVehicle().yaw;
			this.yaw = f;
			this.bodyYaw = f;
			this.prevBodyYaw = f;
			this.field_7340 = 0;
		} else if (!this.world.isClient) {
			BlockState blockState = this.world.getBlockState(blockPos);
			if (!blockState.isAir()) {
				if (blockState.getBlock() == Blocks.MOVING_PISTON) {
					Direction direction = blockState.get(PistonBlock.FACING);
					if (this.world.isAir(blockPos.offset(direction))) {
						blockPos = blockPos.offset(direction);
						this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
					} else {
						this.method_7127();
					}
				} else if (blockState.getBlock() == Blocks.PISTON_HEAD) {
					Direction direction = blockState.get(PistonHeadBlock.FACING);
					if (this.world.isAir(blockPos.offset(direction))) {
						blockPos = blockPos.offset(direction);
						this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
					} else {
						this.method_7127();
					}
				} else {
					this.method_7127();
				}
			}

			Direction direction = this.getAttachedFace();
			if (!this.method_24350(blockPos, direction)) {
				Direction direction2 = this.method_24351(blockPos);
				if (direction2 != null) {
					this.dataTracker.set(ATTACHED_FACE, direction2);
				} else {
					this.method_7127();
				}
			}
		}

		float f = (float)this.getPeekAmount() * 0.01F;
		this.field_7339 = this.field_7337;
		if (this.field_7337 > f) {
			this.field_7337 = MathHelper.clamp(this.field_7337 - 0.05F, f, 1.0F);
		} else if (this.field_7337 < f) {
			this.field_7337 = MathHelper.clamp(this.field_7337 + 0.05F, 0.0F, f);
		}

		if (blockPos != null) {
			if (this.world.isClient) {
				if (this.field_7340 > 0 && this.field_7345 != null) {
					this.field_7340--;
				} else {
					this.field_7345 = blockPos;
				}
			}

			this.resetPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5);
			double d = 0.5 - (double)MathHelper.sin((0.5F + this.field_7337) * (float) Math.PI) * 0.5;
			double e = 0.5 - (double)MathHelper.sin((0.5F + this.field_7339) * (float) Math.PI) * 0.5;
			Direction direction3 = this.getAttachedFace().getOpposite();
			this.setBoundingBox(
				new Box(this.getX() - 0.5, this.getY(), this.getZ() - 0.5, this.getX() + 0.5, this.getY() + 1.0, this.getZ() + 0.5)
					.stretch((double)direction3.getOffsetX() * d, (double)direction3.getOffsetY() * d, (double)direction3.getOffsetZ() * d)
			);
			double g = d - e;
			if (g > 0.0) {
				List<Entity> list = this.world.getEntities(this, this.getBoundingBox());
				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
							entity.move(
								MovementType.SHULKER, new Vec3d(g * (double)direction3.getOffsetX(), g * (double)direction3.getOffsetY(), g * (double)direction3.getOffsetZ())
							);
						}
					}
				}
			}
		}
	}

	@Override
	public void move(MovementType type, Vec3d movement) {
		if (type == MovementType.SHULKER_BOX) {
			this.method_7127();
		} else {
			super.move(type, movement);
		}
	}

	@Override
	public void updatePosition(double x, double y, double z) {
		super.updatePosition(x, y, z);
		if (this.dataTracker != null && this.age != 0) {
			Optional<BlockPos> optional = this.dataTracker.get(ATTACHED_BLOCK);
			Optional<BlockPos> optional2 = Optional.of(new BlockPos(x, y, z));
			if (!optional2.equals(optional)) {
				this.dataTracker.set(ATTACHED_BLOCK, optional2);
				this.dataTracker.set(PEEK_AMOUNT, (byte)0);
				this.velocityDirty = true;
			}
		}
	}

	@Nullable
	protected Direction method_24351(BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			if (this.method_24350(blockPos, direction)) {
				return direction;
			}
		}

		return null;
	}

	private boolean method_24350(BlockPos blockPos, Direction direction) {
		return this.world.method_24368(blockPos.offset(direction), this, direction.getOpposite())
			&& this.world.doesNotCollide(this, ShulkerLidCollisions.getLidCollisionBox(blockPos, direction.getOpposite()));
	}

	protected boolean method_7127() {
		if (!this.isAiDisabled() && this.isAlive()) {
			BlockPos blockPos = this.getBlockPos();

			for (int i = 0; i < 5; i++) {
				BlockPos blockPos2 = blockPos.add(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
				if (blockPos2.getY() > 0
					&& this.world.isAir(blockPos2)
					&& this.world.getWorldBorder().contains(blockPos2)
					&& this.world.doesNotCollide(this, new Box(blockPos2))) {
					Direction direction = this.method_24351(blockPos2);
					if (direction != null) {
						this.dataTracker.set(ATTACHED_FACE, direction);
						this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
						this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos2));
						this.dataTracker.set(PEEK_AMOUNT, (byte)0);
						this.setTarget(null);
						return true;
					}
				}
			}

			return false;
		} else {
			return true;
		}
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.setVelocity(Vec3d.ZERO);
		this.prevBodyYaw = 180.0F;
		this.bodyYaw = 180.0F;
		this.yaw = 180.0F;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (ATTACHED_BLOCK.equals(data) && this.world.isClient && !this.hasVehicle()) {
			BlockPos blockPos = this.getAttachedBlock();
			if (blockPos != null) {
				if (this.field_7345 == null) {
					this.field_7345 = blockPos;
				} else {
					this.field_7340 = 6;
				}

				this.resetPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5);
			}
		}

		super.onTrackedDataSet(data);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.bodyTrackingIncrements = 0;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.method_7124()) {
			Entity entity = source.getSource();
			if (entity instanceof PersistentProjectileEntity) {
				return false;
			}
		}

		if (super.damage(source, amount)) {
			if ((double)this.getHealth() < (double)this.getMaximumHealth() * 0.5 && this.random.nextInt(4) == 0) {
				this.method_7127();
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean method_7124() {
		return this.getPeekAmount() == 0;
	}

	@Nullable
	@Override
	public Box getCollisionBox() {
		return this.isAlive() ? this.getBoundingBox() : null;
	}

	public Direction getAttachedFace() {
		return this.dataTracker.get(ATTACHED_FACE);
	}

	@Nullable
	public BlockPos getAttachedBlock() {
		return (BlockPos)this.dataTracker.get(ATTACHED_BLOCK).orElse(null);
	}

	public void setAttachedBlock(@Nullable BlockPos blockPos) {
		this.dataTracker.set(ATTACHED_BLOCK, Optional.ofNullable(blockPos));
	}

	public int getPeekAmount() {
		return this.dataTracker.get(PEEK_AMOUNT);
	}

	public void setPeekAmount(int i) {
		if (!this.world.isClient) {
			this.getAttributeInstance(EntityAttributes.ARMOR).removeModifier(ATTR_COVERED_ARMOR_BONUS);
			if (i == 0) {
				this.getAttributeInstance(EntityAttributes.ARMOR).addModifier(ATTR_COVERED_ARMOR_BONUS);
				this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
			} else {
				this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
			}
		}

		this.dataTracker.set(PEEK_AMOUNT, (byte)i);
	}

	@Environment(EnvType.CLIENT)
	public float method_7116(float f) {
		return MathHelper.lerp(f, this.field_7339, this.field_7337);
	}

	@Environment(EnvType.CLIENT)
	public int method_7113() {
		return this.field_7340;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos method_7120() {
		return this.field_7345;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.5F;
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
	public boolean method_7117() {
		return this.field_7345 != null && this.getAttachedBlock() != null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public DyeColor getColor() {
		Byte byte_ = this.dataTracker.get(COLOR);
		return byte_ != 16 && byte_ <= 15 ? DyeColor.byId(byte_) : null;
	}

	class PeekGoal extends Goal {
		private int counter;

		private PeekGoal() {
		}

		@Override
		public boolean canStart() {
			return ShulkerEntity.this.getTarget() == null && ShulkerEntity.this.random.nextInt(40) == 0;
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

	class ShulkerBodyControl extends BodyControl {
		public ShulkerBodyControl(MobEntity mobEntity) {
			super(mobEntity);
		}

		@Override
		public void tick() {
		}
	}
}
