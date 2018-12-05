package net.minecraft.entity.decoration;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class AbstractDecorationEntity extends Entity {
	protected static final Predicate<Entity> PREDICATE = entity -> entity instanceof AbstractDecorationEntity;
	private int field_7097;
	protected BlockPos blockPos;
	@Nullable
	public Direction field_7099;

	protected AbstractDecorationEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.setSize(0.5F, 0.5F);
	}

	protected AbstractDecorationEntity(EntityType<?> entityType, World world, BlockPos blockPos) {
		this(entityType, world);
		this.blockPos = blockPos;
	}

	@Override
	protected void initDataTracker() {
	}

	protected void method_6892(Direction direction) {
		Validate.notNull(direction);
		Validate.isTrue(direction.getAxis().isHorizontal());
		this.field_7099 = direction;
		this.yaw = (float)(this.field_7099.getHorizontal() * 90);
		this.prevYaw = this.yaw;
		this.method_6895();
	}

	protected void method_6895() {
		if (this.field_7099 != null) {
			double d = (double)this.blockPos.getX() + 0.5;
			double e = (double)this.blockPos.getY() + 0.5;
			double f = (double)this.blockPos.getZ() + 0.5;
			double g = 0.46875;
			double h = this.method_6893(this.getWidthPixels());
			double i = this.method_6893(this.getHeightPixels());
			d -= (double)this.field_7099.getOffsetX() * 0.46875;
			f -= (double)this.field_7099.getOffsetZ() * 0.46875;
			e += i;
			Direction direction = this.field_7099.rotateYCounterclockwise();
			d += h * (double)direction.getOffsetX();
			f += h * (double)direction.getOffsetZ();
			this.x = d;
			this.y = e;
			this.z = f;
			double j = (double)this.getWidthPixels();
			double k = (double)this.getHeightPixels();
			double l = (double)this.getWidthPixels();
			if (this.field_7099.getAxis() == Direction.Axis.Z) {
				l = 1.0;
			} else {
				j = 1.0;
			}

			j /= 32.0;
			k /= 32.0;
			l /= 32.0;
			this.setBoundingBox(new BoundingBox(d - j, e - k, f - l, d + j, e + k, f + l));
		}
	}

	private double method_6893(int i) {
		return i % 32 == 0 ? 0.5 : 0.0;
	}

	@Override
	public void update() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (this.field_7097++ == 100 && !this.world.isRemote) {
			this.field_7097 = 0;
			if (!this.invalid && !this.method_6888()) {
				this.invalidate();
				this.copyEntityData(null);
			}
		}
	}

	public boolean method_6888() {
		if (!this.world.method_8587(this, this.getBoundingBox())) {
			return false;
		} else {
			int i = Math.max(1, this.getWidthPixels() / 16);
			int j = Math.max(1, this.getHeightPixels() / 16);
			BlockPos blockPos = this.blockPos.method_10093(this.field_7099.getOpposite());
			Direction direction = this.field_7099.rotateYCounterclockwise();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = 0; k < i; k++) {
				for (int l = 0; l < j; l++) {
					int m = (i - 1) / -2;
					int n = (j - 1) / -2;
					mutable.set(blockPos).method_10104(direction, k + m).method_10104(Direction.UP, l + n);
					BlockState blockState = this.world.getBlockState(mutable);
					if (!blockState.getMaterial().method_15799() && !AbstractRedstoneGateBlock.method_9999(blockState)) {
						return false;
					}
				}
			}

			return this.world.getEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
		}
	}

	@Override
	public boolean doesCollide() {
		return true;
	}

	@Override
	public boolean method_5698(Entity entity) {
		return entity instanceof PlayerEntity ? this.damage(DamageSource.player((PlayerEntity)entity), 0.0F) : false;
	}

	@Override
	public Direction method_5735() {
		return this.field_7099;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			if (!this.invalid && !this.world.isRemote) {
				this.invalidate();
				this.method_5785();
				this.copyEntityData(damageSource.getAttacker());
			}

			return true;
		}
	}

	@Override
	public void move(MovementType movementType, double d, double e, double f) {
		if (!this.world.isRemote && !this.invalid && d * d + e * e + f * f > 0.0) {
			this.invalidate();
			this.copyEntityData(null);
		}
	}

	@Override
	public void addVelocity(double d, double e, double f) {
		if (!this.world.isRemote && !this.invalid && d * d + e * e + f * f > 0.0) {
			this.invalidate();
			this.copyEntityData(null);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putByte("Facing", (byte)this.field_7099.getHorizontal());
		BlockPos blockPos = this.getDecorationBlockPos();
		compoundTag.putInt("TileX", blockPos.getX());
		compoundTag.putInt("TileY", blockPos.getY());
		compoundTag.putInt("TileZ", blockPos.getZ());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.blockPos = new BlockPos(compoundTag.getInt("TileX"), compoundTag.getInt("TileY"), compoundTag.getInt("TileZ"));
		this.method_6892(Direction.fromHorizontal(compoundTag.getByte("Facing")));
	}

	public abstract int getWidthPixels();

	public abstract int getHeightPixels();

	public abstract void copyEntityData(@Nullable Entity entity);

	public abstract void onDecorationPlaced();

	@Override
	public ItemEntity dropStack(ItemStack itemStack, float f) {
		ItemEntity itemEntity = new ItemEntity(
			this.world,
			this.x + (double)((float)this.field_7099.getOffsetX() * 0.15F),
			this.y + (double)f,
			this.z + (double)((float)this.field_7099.getOffsetZ() * 0.15F),
			itemStack
		);
		itemEntity.setPickupDelayDefault();
		this.world.spawnEntity(itemEntity);
		return itemEntity;
	}

	@Override
	protected boolean shouldSetPositionOnLoad() {
		return false;
	}

	@Override
	public void setPosition(double d, double e, double f) {
		this.blockPos = new BlockPos(d, e, f);
		this.method_6895();
		this.velocityDirty = true;
	}

	public BlockPos getDecorationBlockPos() {
		return this.blockPos;
	}

	@Override
	public float applyRotation(Rotation rotation) {
		if (this.field_7099 != null && this.field_7099.getAxis() != Direction.Axis.Y) {
			switch (rotation) {
				case ROT_180:
					this.field_7099 = this.field_7099.getOpposite();
					break;
				case ROT_270:
					this.field_7099 = this.field_7099.rotateYCounterclockwise();
					break;
				case ROT_90:
					this.field_7099 = this.field_7099.rotateYClockwise();
			}
		}

		float f = MathHelper.wrapDegrees(this.yaw);
		switch (rotation) {
			case ROT_180:
				return f + 180.0F;
			case ROT_270:
				return f + 90.0F;
			case ROT_90:
				return f + 270.0F;
			default:
				return f;
		}
	}

	@Override
	public float applyMirror(Mirror mirror) {
		return this.applyRotation(mirror.method_10345(this.field_7099));
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
	}
}
