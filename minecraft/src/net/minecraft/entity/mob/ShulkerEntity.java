package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1399;
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
	protected static final TrackedData<Direction> field_7344 = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.FACING);
	protected static final TrackedData<Optional<BlockPos>> field_7338 = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS);
	protected static final TrackedData<Byte> field_7346 = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Byte> field_7343 = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
	private float field_7339;
	private float field_7337;
	private BlockPos field_7345;
	private int field_7340;

	public ShulkerEntity(EntityType<? extends ShulkerEntity> entityType, World world) {
		super(entityType, world);
		this.field_6220 = 180.0F;
		this.field_6283 = 180.0F;
		this.fireImmune = true;
		this.field_7345 = null;
		this.experiencePoints = 5;
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.field_6283 = 180.0F;
		this.field_6220 = 180.0F;
		this.yaw = 180.0F;
		this.prevYaw = 180.0F;
		this.headYaw = 180.0F;
		this.prevHeadYaw = 180.0F;
		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(4, new ShulkerEntity.ShootBulletGoal());
		this.field_6201.add(7, new ShulkerEntity.PeekGoal());
		this.field_6201.add(8, new LookAroundGoal(this));
		this.field_6185.add(1, new class_1399(this).method_6318());
		this.field_6185.add(2, new ShulkerEntity.SearchForPlayerGoal(this));
		this.field_6185.add(3, new ShulkerEntity.SearchForTargetGoal(this));
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public SoundCategory method_5634() {
		return SoundCategory.field_15251;
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14690;
	}

	@Override
	public void playAmbientSound() {
		if (!this.method_7124()) {
			super.playAmbientSound();
		}
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15160;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return this.method_7124() ? SoundEvents.field_15135 : SoundEvents.field_15229;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7344, Direction.DOWN);
		this.field_6011.startTracking(field_7338, Optional.empty());
		this.field_6011.startTracking(field_7346, (byte)0);
		this.field_6011.startTracking(field_7343, (byte)16);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
	}

	@Override
	protected BodyControl method_5963() {
		return new ShulkerEntity.class_1608(this);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.field_6011.set(field_7344, Direction.byId(compoundTag.getByte("AttachFace")));
		this.field_6011.set(field_7346, compoundTag.getByte("Peek"));
		this.field_6011.set(field_7343, compoundTag.getByte("Color"));
		if (compoundTag.containsKey("APX")) {
			int i = compoundTag.getInt("APX");
			int j = compoundTag.getInt("APY");
			int k = compoundTag.getInt("APZ");
			this.field_6011.set(field_7338, Optional.of(new BlockPos(i, j, k)));
		} else {
			this.field_6011.set(field_7338, Optional.empty());
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putByte("AttachFace", (byte)this.field_6011.get(field_7344).getId());
		compoundTag.putByte("Peek", this.field_6011.get(field_7346));
		compoundTag.putByte("Color", this.field_6011.get(field_7343));
		BlockPos blockPos = this.method_7123();
		if (blockPos != null) {
			compoundTag.putInt("APX", blockPos.getX());
			compoundTag.putInt("APY", blockPos.getY());
			compoundTag.putInt("APZ", blockPos.getZ());
		}
	}

	@Override
	public void update() {
		super.update();
		BlockPos blockPos = (BlockPos)this.field_6011.get(field_7338).orElse(null);
		if (blockPos == null && !this.field_6002.isClient) {
			blockPos = new BlockPos(this);
			this.field_6011.set(field_7338, Optional.of(blockPos));
		}

		if (this.hasVehicle()) {
			blockPos = null;
			float f = this.getRiddenEntity().yaw;
			this.yaw = f;
			this.field_6283 = f;
			this.field_6220 = f;
			this.field_7340 = 0;
		} else if (!this.field_6002.isClient) {
			BlockState blockState = this.field_6002.method_8320(blockPos);
			if (!blockState.isAir()) {
				if (blockState.getBlock() == Blocks.field_10008) {
					Direction direction = blockState.method_11654(PistonBlock.field_10927);
					if (this.field_6002.method_8623(blockPos.method_10093(direction))) {
						blockPos = blockPos.method_10093(direction);
						this.field_6011.set(field_7338, Optional.of(blockPos));
					} else {
						this.method_7127();
					}
				} else if (blockState.getBlock() == Blocks.field_10379) {
					Direction direction = blockState.method_11654(PistonHeadBlock.field_10927);
					if (this.field_6002.method_8623(blockPos.method_10093(direction))) {
						blockPos = blockPos.method_10093(direction);
						this.field_6011.set(field_7338, Optional.of(blockPos));
					} else {
						this.method_7127();
					}
				} else {
					this.method_7127();
				}
			}

			BlockPos blockPos2 = blockPos.method_10093(this.method_7119());
			if (!this.field_6002.method_8515(blockPos2)) {
				boolean bl = false;

				for (Direction direction2 : Direction.values()) {
					blockPos2 = blockPos.method_10093(direction2);
					if (this.field_6002.method_8515(blockPos2)) {
						this.field_6011.set(field_7344, direction2);
						bl = true;
						break;
					}
				}

				if (!bl) {
					this.method_7127();
				}
			}

			BlockPos blockPos3 = blockPos.method_10093(this.method_7119().getOpposite());
			if (this.field_6002.method_8515(blockPos3)) {
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
			if (this.field_6002.isClient) {
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
			Direction direction3 = this.method_7119().getOpposite();
			this.method_5857(
				new BoundingBox(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5)
					.stretch((double)direction3.getOffsetX() * d, (double)direction3.getOffsetY() * d, (double)direction3.getOffsetZ() * d)
			);
			double g = d - e;
			if (g > 0.0) {
				List<Entity> list = this.field_6002.method_8335(this, this.method_5829());
				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
							entity.method_5784(
								MovementType.field_6309, new Vec3d(g * (double)direction3.getOffsetX(), g * (double)direction3.getOffsetY(), g * (double)direction3.getOffsetZ())
							);
						}
					}
				}
			}
		}
	}

	@Override
	public void method_5784(MovementType movementType, Vec3d vec3d) {
		if (movementType == MovementType.field_6306) {
			this.method_7127();
		} else {
			super.method_5784(movementType, vec3d);
		}
	}

	@Override
	public void setPosition(double d, double e, double f) {
		super.setPosition(d, e, f);
		if (this.field_6011 != null && this.age != 0) {
			Optional<BlockPos> optional = this.field_6011.get(field_7338);
			Optional<BlockPos> optional2 = Optional.of(new BlockPos(d, e, f));
			if (!optional2.equals(optional)) {
				this.field_6011.set(field_7338, optional2);
				this.field_6011.set(field_7346, (byte)0);
				this.velocityDirty = true;
			}
		}
	}

	protected boolean method_7127() {
		if (!this.isAiDisabled() && this.isValid()) {
			BlockPos blockPos = new BlockPos(this);

			for (int i = 0; i < 5; i++) {
				BlockPos blockPos2 = blockPos.add(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
				if (blockPos2.getY() > 0
					&& this.field_6002.method_8623(blockPos2)
					&& this.field_6002.method_8621().method_11952(blockPos2)
					&& this.field_6002.method_8587(this, new BoundingBox(blockPos2))) {
					boolean bl = false;

					for (Direction direction : Direction.values()) {
						if (this.field_6002.method_8515(blockPos2.method_10093(direction))) {
							this.field_6011.set(field_7344, direction);
							bl = true;
							break;
						}
					}

					if (bl) {
						this.method_5783(SoundEvents.field_14915, 1.0F, 1.0F);
						this.field_6011.set(field_7338, Optional.of(blockPos2));
						this.field_6011.set(field_7346, (byte)0);
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
	public void updateMovement() {
		super.updateMovement();
		this.method_18799(Vec3d.ZERO);
		this.field_6220 = 180.0F;
		this.field_6283 = 180.0F;
		this.yaw = 180.0F;
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		if (field_7338.equals(trackedData) && this.field_6002.isClient && !this.hasVehicle()) {
			BlockPos blockPos = this.method_7123();
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

		super.method_5674(trackedData);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_6210 = 0;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.method_7124()) {
			Entity entity = damageSource.method_5526();
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
		return this.isValid() ? this.method_5829() : null;
	}

	public Direction method_7119() {
		return this.field_6011.get(field_7344);
	}

	@Nullable
	public BlockPos method_7123() {
		return (BlockPos)this.field_6011.get(field_7338).orElse(null);
	}

	public void method_7125(@Nullable BlockPos blockPos) {
		this.field_6011.set(field_7338, Optional.ofNullable(blockPos));
	}

	public int getPeekAmount() {
		return this.field_6011.get(field_7346);
	}

	public void setPeekAmount(int i) {
		if (!this.field_6002.isClient) {
			this.method_5996(EntityAttributes.ARMOR).method_6202(ATTR_COVERED_ARMOR_BONUS);
			if (i == 0) {
				this.method_5996(EntityAttributes.ARMOR).method_6197(ATTR_COVERED_ARMOR_BONUS);
				this.method_5783(SoundEvents.field_15050, 1.0F, 1.0F);
			} else {
				this.method_5783(SoundEvents.field_15017, 1.0F, 1.0F);
			}
		}

		this.field_6011.set(field_7346, (byte)i);
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
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return 0.5F;
	}

	@Override
	public int method_5978() {
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
		return this.field_7345 != null && this.method_7123() != null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public DyeColor method_7121() {
		Byte byte_ = this.field_6011.get(field_7343);
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
		public void onRemove() {
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
			return ShulkerEntity.this.field_6002.getDifficulty() == Difficulty.PEACEFUL ? false : super.canStart();
		}

		@Override
		protected BoundingBox method_6321(double d) {
			Direction direction = ((ShulkerEntity)this.entity).method_7119();
			if (direction.getAxis() == Direction.Axis.X) {
				return this.entity.method_5829().expand(4.0, d, d);
			} else {
				return direction.getAxis() == Direction.Axis.Z ? this.entity.method_5829().expand(d, d, 4.0) : this.entity.method_5829().expand(d, 4.0, d);
			}
		}
	}

	static class SearchForTargetGoal extends FollowTargetGoal<LivingEntity> {
		public SearchForTargetGoal(ShulkerEntity shulkerEntity) {
			super(shulkerEntity, LivingEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Monster);
		}

		@Override
		public boolean canStart() {
			return this.entity.method_5781() == null ? false : super.canStart();
		}

		@Override
		protected BoundingBox method_6321(double d) {
			Direction direction = ((ShulkerEntity)this.entity).method_7119();
			if (direction.getAxis() == Direction.Axis.X) {
				return this.entity.method_5829().expand(4.0, d, d);
			} else {
				return direction.getAxis() == Direction.Axis.Z ? this.entity.method_5829().expand(d, d, 4.0) : this.entity.method_5829().expand(d, 4.0, d);
			}
		}
	}

	class ShootBulletGoal extends Goal {
		private int counter;

		public ShootBulletGoal() {
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = ShulkerEntity.this.getTarget();
			return livingEntity != null && livingEntity.isValid() ? ShulkerEntity.this.field_6002.getDifficulty() != Difficulty.PEACEFUL : false;
		}

		@Override
		public void start() {
			this.counter = 20;
			ShulkerEntity.this.setPeekAmount(100);
		}

		@Override
		public void onRemove() {
			ShulkerEntity.this.setPeekAmount(0);
		}

		@Override
		public void tick() {
			if (ShulkerEntity.this.field_6002.getDifficulty() != Difficulty.PEACEFUL) {
				this.counter--;
				LivingEntity livingEntity = ShulkerEntity.this.getTarget();
				ShulkerEntity.this.method_5988().lookAt(livingEntity, 180.0F, 180.0F);
				double d = ShulkerEntity.this.squaredDistanceTo(livingEntity);
				if (d < 400.0) {
					if (this.counter <= 0) {
						this.counter = 20 + ShulkerEntity.this.random.nextInt(10) * 20 / 2;
						ShulkerEntity.this.field_6002
							.spawnEntity(new ShulkerBulletEntity(ShulkerEntity.this.field_6002, ShulkerEntity.this, livingEntity, ShulkerEntity.this.method_7119().getAxis()));
						ShulkerEntity.this.method_5783(
							SoundEvents.field_15000, 2.0F, (ShulkerEntity.this.random.nextFloat() - ShulkerEntity.this.random.nextFloat()) * 0.2F + 1.0F
						);
					}
				} else {
					ShulkerEntity.this.setTarget(null);
				}

				super.tick();
			}
		}
	}

	class class_1608 extends BodyControl {
		public class_1608(LivingEntity livingEntity) {
			super(livingEntity);
		}

		@Override
		public void method_6224() {
		}
	}
}
