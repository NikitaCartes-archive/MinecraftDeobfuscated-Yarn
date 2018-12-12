package net.minecraft.entity.mob;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1376;
import net.minecraft.class_1399;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
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
	protected static final TrackedData<Byte> field_7343 = DataTracker.registerData(ShulkerEntity.class, TrackedDataHandlerRegistry.BYTE);
	private float field_7339;
	private float field_7337;
	private BlockPos field_7345;
	private int field_7340;

	public ShulkerEntity(World world) {
		super(EntityType.SHULKER, world);
		this.setSize(1.0F, 1.0F);
		this.field_6220 = 180.0F;
		this.field_6283 = 180.0F;
		this.fireImmune = true;
		this.field_7345 = null;
		this.experiencePoints = 5;
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.field_6283 = 180.0F;
		this.field_6220 = 180.0F;
		this.yaw = 180.0F;
		this.prevYaw = 180.0F;
		this.headYaw = 180.0F;
		this.prevHeadYaw = 180.0F;
		return super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(1, new class_1361(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(4, new ShulkerEntity.ShootBulletGoal());
		this.goalSelector.add(7, new ShulkerEntity.PeekGoal());
		this.goalSelector.add(8, new class_1376(this));
		this.targetSelector.add(1, new class_1399(this).method_6318());
		this.targetSelector.add(2, new ShulkerEntity.SearchForPlayerGoal(this));
		this.targetSelector.add(3, new ShulkerEntity.SearchForTargetGoal(this));
	}

	@Override
	protected boolean method_5658() {
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
		this.dataTracker.startTracking(field_7343, (byte)16);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
	}

	@Override
	protected BodyControl createBodyControl() {
		return new ShulkerEntity.class_1608(this);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.dataTracker.set(ATTACHED_FACE, Direction.byId(compoundTag.getByte("AttachFace")));
		this.dataTracker.set(PEEK_AMOUNT, compoundTag.getByte("Peek"));
		this.dataTracker.set(field_7343, compoundTag.getByte("Color"));
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
		compoundTag.putByte("Color", this.dataTracker.get(field_7343));
		BlockPos blockPos = this.getAttachedBlock();
		if (blockPos != null) {
			compoundTag.putInt("APX", blockPos.getX());
			compoundTag.putInt("APY", blockPos.getY());
			compoundTag.putInt("APZ", blockPos.getZ());
		}
	}

	@Override
	public void update() {
		super.update();
		BlockPos blockPos = (BlockPos)this.dataTracker.get(ATTACHED_BLOCK).orElse(null);
		if (blockPos == null && !this.world.isClient) {
			blockPos = new BlockPos(this);
			this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
		}

		if (this.hasVehicle()) {
			blockPos = null;
			float f = this.getRiddenEntity().yaw;
			this.yaw = f;
			this.field_6283 = f;
			this.field_6220 = f;
			this.field_7340 = 0;
		} else if (!this.world.isClient) {
			BlockState blockState = this.world.getBlockState(blockPos);
			if (!blockState.isAir()) {
				if (blockState.getBlock() == Blocks.field_10008) {
					Direction direction = blockState.get(PistonBlock.field_10927);
					if (this.world.isAir(blockPos.offset(direction))) {
						blockPos = blockPos.offset(direction);
						this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
					} else {
						this.method_7127();
					}
				} else if (blockState.getBlock() == Blocks.field_10379) {
					Direction direction = blockState.get(PistonHeadBlock.field_10927);
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
			if (!this.world.doesBlockHaveSolidTopSurface(blockPos2)) {
				boolean bl = false;

				for (Direction direction2 : Direction.values()) {
					blockPos2 = blockPos.offset(direction2);
					if (this.world.doesBlockHaveSolidTopSurface(blockPos2)) {
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
			if (this.world.doesBlockHaveSolidTopSurface(blockPos3)) {
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
			double g = d - e;
			double h = 0.0;
			double i = 0.0;
			double j = 0.0;
			Direction direction3 = this.getAttachedFace();
			switch (direction3) {
				case DOWN:
					this.setBoundingBox(new BoundingBox(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0 + d, this.z + 0.5));
					i = g;
					break;
				case UP:
					this.setBoundingBox(new BoundingBox(this.x - 0.5, this.y - d, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5));
					i = -g;
					break;
				case NORTH:
					this.setBoundingBox(new BoundingBox(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5 + d));
					j = g;
					break;
				case SOUTH:
					this.setBoundingBox(new BoundingBox(this.x - 0.5, this.y, this.z - 0.5 - d, this.x + 0.5, this.y + 1.0, this.z + 0.5));
					j = -g;
					break;
				case WEST:
					this.setBoundingBox(new BoundingBox(this.x - 0.5, this.y, this.z - 0.5, this.x + 0.5 + d, this.y + 1.0, this.z + 0.5));
					h = g;
					break;
				case EAST:
					this.setBoundingBox(new BoundingBox(this.x - 0.5 - d, this.y, this.z - 0.5, this.x + 0.5, this.y + 1.0, this.z + 0.5));
					h = -g;
			}

			if (g > 0.0) {
				List<Entity> list = this.world.getVisibleEntities(this, this.getBoundingBox());
				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
							entity.move(MovementType.SHULKER_ENTITY, h, i, j);
						}
					}
				}
			}
		}
	}

	@Override
	public void move(MovementType movementType, double d, double e, double f) {
		if (movementType == MovementType.SHULKER_BOX) {
			this.method_7127();
		} else {
			super.move(movementType, d, e, f);
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
		if (!this.isAiDisabled() && this.isValid()) {
			BlockPos blockPos = new BlockPos(this);

			for (int i = 0; i < 5; i++) {
				BlockPos blockPos2 = blockPos.add(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
				if (blockPos2.getY() > 0 && this.world.isAir(blockPos2) && this.world.method_8625(this) && this.world.method_8587(this, new BoundingBox(blockPos2))) {
					boolean bl = false;

					for (Direction direction : Direction.values()) {
						if (this.world.doesBlockHaveSolidTopSurface(blockPos2.offset(direction))) {
							this.dataTracker.set(ATTACHED_FACE, direction);
							bl = true;
							break;
						}
					}

					if (bl) {
						this.playSoundAtEntity(SoundEvents.field_14915, 1.0F, 1.0F);
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
	public void updateMovement() {
		super.updateMovement();
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
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
		return this.isValid() ? this.getBoundingBox() : null;
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
				this.playSoundAtEntity(SoundEvents.field_15050, 1.0F, 1.0F);
			} else {
				this.playSoundAtEntity(SoundEvents.field_15017, 1.0F, 1.0F);
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
	public float getEyeHeight() {
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
	public float method_5871() {
		return 0.0F;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7117() {
		return this.field_7345 != null && this.getAttachedBlock() != null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public DyeColor method_7121() {
		Byte byte_ = this.dataTracker.get(field_7343);
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
			this.setControlBits(3);
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = ShulkerEntity.this.getTarget();
			return livingEntity != null && livingEntity.isValid() ? ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL : false;
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
			if (ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL) {
				this.counter--;
				LivingEntity livingEntity = ShulkerEntity.this.getTarget();
				ShulkerEntity.this.getLookControl().lookAt(livingEntity, 180.0F, 180.0F);
				double d = ShulkerEntity.this.squaredDistanceTo(livingEntity);
				if (d < 400.0) {
					if (this.counter <= 0) {
						this.counter = 20 + ShulkerEntity.this.random.nextInt(10) * 20 / 2;
						ShulkerBulletEntity shulkerBulletEntity = new ShulkerBulletEntity(
							ShulkerEntity.this.world, ShulkerEntity.this, livingEntity, ShulkerEntity.this.getAttachedFace().getAxis()
						);
						ShulkerEntity.this.world.spawnEntity(shulkerBulletEntity);
						ShulkerEntity.this.playSoundAtEntity(
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
