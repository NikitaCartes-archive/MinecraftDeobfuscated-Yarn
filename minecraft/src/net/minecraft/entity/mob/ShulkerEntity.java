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
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
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
			ATTR_COVERED_ARMOR_BONUS_UUID, "Covered armor bonus", 20.0, EntityAttributeModifier.Operation.field_6328
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
		this.field_6220 = 180.0F;
		this.field_6283 = 180.0F;
		this.field_7345 = null;
		this.experiencePoints = 5;
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.field_6283 = 180.0F;
		this.field_6220 = 180.0F;
		this.yaw = 180.0F;
		this.prevYaw = 180.0F;
		this.headYaw = 180.0F;
		this.prevHeadYaw = 180.0F;
		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
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
		return SoundCategory.field_15251;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14690;
	}

	@Override
	public void playAmbientSound() {
		if (!this.method_7124()) {
			super.playAmbientSound();
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15160;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return this.method_7124() ? SoundEvents.field_15135 : SoundEvents.field_15229;
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
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.dataTracker.set(ATTACHED_FACE, Direction.byId(compoundTag.getByte("AttachFace")));
		this.dataTracker.set(PEEK_AMOUNT, compoundTag.getByte("Peek"));
		this.dataTracker.set(COLOR, compoundTag.getByte("Color"));
		if (compoundTag.containsKey("APX")) {
			int i = compoundTag.getInt("APX");
			int j = compoundTag.getInt("APY");
			int k = compoundTag.getInt("APZ");
			this.dataTracker.set(ATTACHED_BLOCK, Optional.of(new BlockPos(i, j, k)));
		} else {
			this.dataTracker.set(ATTACHED_BLOCK, Optional.empty());
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putByte("AttachFace", (byte)this.dataTracker.get(ATTACHED_FACE).getId());
		compoundTag.putByte("Peek", this.dataTracker.get(PEEK_AMOUNT));
		compoundTag.putByte("Color", this.dataTracker.get(COLOR));
		BlockPos blockPos = this.getAttachedBlock();
		if (blockPos != null) {
			compoundTag.putInt("APX", blockPos.getX());
			compoundTag.putInt("APY", blockPos.getY());
			compoundTag.putInt("APZ", blockPos.getZ());
		}
	}

	@Override
	public void tick() {
		super.tick();
		BlockPos blockPos = (BlockPos)this.dataTracker.get(ATTACHED_BLOCK).orElse(null);
		if (blockPos == null && !this.world.isClient) {
			blockPos = new BlockPos(this);
			this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
		}

		if (this.hasVehicle()) {
			blockPos = null;
			float f = this.getVehicle().yaw;
			this.yaw = f;
			this.field_6283 = f;
			this.field_6220 = f;
			this.field_7340 = 0;
		} else if (!this.world.isClient) {
			BlockState blockState = this.world.getBlockState(blockPos);
			if (!blockState.isAir()) {
				if (blockState.getBlock() == Blocks.field_10008) {
					Direction direction = blockState.get(PistonBlock.FACING);
					if (this.world.isAir(blockPos.offset(direction))) {
						blockPos = blockPos.offset(direction);
						this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
					} else {
						this.method_7127();
					}
				} else if (blockState.getBlock() == Blocks.field_10379) {
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

			BlockPos blockPos2 = blockPos.offset(this.getAttachedFace());
			if (!this.world.doesBlockHaveSolidTopSurface(blockPos2, this)) {
				boolean bl = false;

				for (Direction direction2 : Direction.values()) {
					blockPos2 = blockPos.offset(direction2);
					if (this.world.doesBlockHaveSolidTopSurface(blockPos2, this)) {
						this.dataTracker.set(ATTACHED_FACE, direction2);
						bl = true;
						break;
					}
				}

				if (!bl) {
					this.method_7127();
				}
			}

			BlockPos blockPos3 = blockPos.offset(this.getAttachedFace().getOpposite());
			if (this.world.doesBlockHaveSolidTopSurface(blockPos3, this)) {
				this.method_7127();
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

			this.x = (double)blockPos.getX() + 0.5;
			this.y = (double)blockPos.getY();
			this.z = (double)blockPos.getZ() + 0.5;
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			this.prevRenderX = this.x;
			this.prevRenderY = this.y;
			this.prevRenderZ = this.z;
			double d = 0.5 - (double)MathHelper.sin((0.5F + this.field_7337) * (float) Math.PI) * 0.5;
			double e = 0.5 - (double)MathHelper.sin((0.5F + this.field_7339) * (float) Math.PI) * 0.5;
			Direction direction3 = this.getAttachedFace().getOpposite();
			this.setBoundingBox(
				new BoundingBox(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5)
					.stretch((double)direction3.getOffsetX() * d, (double)direction3.getOffsetY() * d, (double)direction3.getOffsetZ() * d)
			);
			double g = d - e;
			if (g > 0.0) {
				List<Entity> list = this.world.getEntities(this, this.getBoundingBox());
				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
							entity.move(
								MovementType.field_6309, new Vec3d(g * (double)direction3.getOffsetX(), g * (double)direction3.getOffsetY(), g * (double)direction3.getOffsetZ())
							);
						}
					}
				}
			}
		}
	}

	@Override
	public void move(MovementType movementType, Vec3d vec3d) {
		if (movementType == MovementType.field_6306) {
			this.method_7127();
		} else {
			super.move(movementType, vec3d);
		}
	}

	@Override
	public void setPosition(double d, double e, double f) {
		super.setPosition(d, e, f);
		if (this.dataTracker != null && this.age != 0) {
			Optional<BlockPos> optional = this.dataTracker.get(ATTACHED_BLOCK);
			Optional<BlockPos> optional2 = Optional.of(new BlockPos(d, e, f));
			if (!optional2.equals(optional)) {
				this.dataTracker.set(ATTACHED_BLOCK, optional2);
				this.dataTracker.set(PEEK_AMOUNT, (byte)0);
				this.velocityDirty = true;
			}
		}
	}

	protected boolean method_7127() {
		if (!this.isAiDisabled() && this.isAlive()) {
			BlockPos blockPos = new BlockPos(this);

			for (int i = 0; i < 5; i++) {
				BlockPos blockPos2 = blockPos.add(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
				if (blockPos2.getY() > 0
					&& this.world.isAir(blockPos2)
					&& this.world.getWorldBorder().contains(blockPos2)
					&& this.world.doesNotCollide(this, new BoundingBox(blockPos2))) {
					boolean bl = false;

					for (Direction direction : Direction.values()) {
						if (this.world.doesBlockHaveSolidTopSurface(blockPos2.offset(direction), this)) {
							this.dataTracker.set(ATTACHED_FACE, direction);
							bl = true;
							break;
						}
					}

					if (bl) {
						this.playSound(SoundEvents.field_14915, 1.0F, 1.0F);
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
	public void updateState() {
		super.updateState();
		this.setVelocity(Vec3d.ZERO);
		this.field_6220 = 180.0F;
		this.field_6283 = 180.0F;
		this.yaw = 180.0F;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (ATTACHED_BLOCK.equals(trackedData) && this.world.isClient && !this.hasVehicle()) {
			BlockPos blockPos = this.getAttachedBlock();
			if (blockPos != null) {
				if (this.field_7345 == null) {
					this.field_7345 = blockPos;
				} else {
					this.field_7340 = 6;
				}

				this.x = (double)blockPos.getX() + 0.5;
				this.y = (double)blockPos.getY();
				this.z = (double)blockPos.getZ() + 0.5;
				this.prevX = this.x;
				this.prevY = this.y;
				this.prevZ = this.z;
				this.prevRenderX = this.x;
				this.prevRenderY = this.y;
				this.prevRenderZ = this.z;
			}
		}

		super.onTrackedDataSet(trackedData);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_6210 = 0;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.method_7124()) {
			Entity entity = damageSource.getSource();
			if (entity instanceof ProjectileEntity) {
				return false;
			}
		}

		if (super.damage(damageSource, f)) {
			if ((double)this.getHealth() < (double)this.getHealthMaximum() * 0.5 && this.random.nextInt(4) == 0) {
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
	public BoundingBox method_5827() {
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
				this.playSound(SoundEvents.field_15050, 1.0F, 1.0F);
			} else {
				this.playSound(SoundEvents.field_15017, 1.0F, 1.0F);
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
	protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
		return 0.5F;
	}

	@Override
	public int getLookPitchSpeed() {
		return 180;
	}

	@Override
	public int method_5986() {
		return 180;
	}

	@Override
	public void pushAwayFrom(Entity entity) {
	}

	@Override
	public float getBoundingBoxMarginForTargeting() {
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
		public SearchForPlayerGoal(ShulkerEntity shulkerEntity2) {
			super(shulkerEntity2, PlayerEntity.class, true);
		}

		@Override
		public boolean canStart() {
			return ShulkerEntity.this.world.getDifficulty() == Difficulty.PEACEFUL ? false : super.canStart();
		}

		@Override
		protected BoundingBox getSearchBox(double d) {
			Direction direction = ((ShulkerEntity)this.entity).getAttachedFace();
			if (direction.getAxis() == Direction.Axis.X) {
				return this.entity.getBoundingBox().expand(4.0, d, d);
			} else {
				return direction.getAxis() == Direction.Axis.Z ? this.entity.getBoundingBox().expand(d, d, 4.0) : this.entity.getBoundingBox().expand(d, 4.0, d);
			}
		}
	}

	static class SearchForTargetGoal extends FollowTargetGoal<LivingEntity> {
		public SearchForTargetGoal(ShulkerEntity shulkerEntity) {
			super(shulkerEntity, LivingEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Monster);
		}

		@Override
		public boolean canStart() {
			return this.entity.getScoreboardTeam() == null ? false : super.canStart();
		}

		@Override
		protected BoundingBox getSearchBox(double d) {
			Direction direction = ((ShulkerEntity)this.entity).getAttachedFace();
			if (direction.getAxis() == Direction.Axis.X) {
				return this.entity.getBoundingBox().expand(4.0, d, d);
			} else {
				return direction.getAxis() == Direction.Axis.Z ? this.entity.getBoundingBox().expand(d, d, 4.0) : this.entity.getBoundingBox().expand(d, 4.0, d);
			}
		}
	}

	class ShootBulletGoal extends Goal {
		private int counter;

		public ShootBulletGoal() {
			this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
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
						ShulkerEntity.this.playSound(SoundEvents.field_15000, 2.0F, (ShulkerEntity.this.random.nextFloat() - ShulkerEntity.this.random.nextFloat()) * 0.2F + 1.0F);
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
		public void method_6224() {
		}
	}
}
