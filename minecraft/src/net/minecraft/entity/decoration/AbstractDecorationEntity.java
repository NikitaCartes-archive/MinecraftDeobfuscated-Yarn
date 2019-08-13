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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class AbstractDecorationEntity extends Entity {
	protected static final Predicate<Entity> PREDICATE = entity -> entity instanceof AbstractDecorationEntity;
	private int field_7097;
	protected BlockPos blockPos;
	protected Direction facing = Direction.field_11035;

	protected AbstractDecorationEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
		super(entityType, world);
	}

	protected AbstractDecorationEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world, BlockPos blockPos) {
		this(entityType, world);
		this.blockPos = blockPos;
	}

	@Override
	protected void initDataTracker() {
	}

	protected void setFacing(Direction direction) {
		Validate.notNull(direction);
		Validate.isTrue(direction.getAxis().isHorizontal());
		this.facing = direction;
		this.yaw = (float)(this.facing.getHorizontal() * 90);
		this.prevYaw = this.yaw;
		this.method_6895();
	}

	protected void method_6895() {
		if (this.facing != null) {
			double d = (double)this.blockPos.getX() + 0.5;
			double e = (double)this.blockPos.getY() + 0.5;
			double f = (double)this.blockPos.getZ() + 0.5;
			double g = 0.46875;
			double h = this.method_6893(this.getWidthPixels());
			double i = this.method_6893(this.getHeightPixels());
			d -= (double)this.facing.getOffsetX() * 0.46875;
			f -= (double)this.facing.getOffsetZ() * 0.46875;
			e += i;
			Direction direction = this.facing.rotateYCounterclockwise();
			d += h * (double)direction.getOffsetX();
			f += h * (double)direction.getOffsetZ();
			this.x = d;
			this.y = e;
			this.z = f;
			double j = (double)this.getWidthPixels();
			double k = (double)this.getHeightPixels();
			double l = (double)this.getWidthPixels();
			if (this.facing.getAxis() == Direction.Axis.field_11051) {
				l = 1.0;
			} else {
				j = 1.0;
			}

			j /= 32.0;
			k /= 32.0;
			l /= 32.0;
			this.setBoundingBox(new Box(d - j, e - k, f - l, d + j, e + k, f + l));
		}
	}

	private double method_6893(int i) {
		return i % 32 == 0 ? 0.5 : 0.0;
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (this.field_7097++ == 100 && !this.world.isClient) {
			this.field_7097 = 0;
			if (!this.removed && !this.method_6888()) {
				this.remove();
				this.onBreak(null);
			}
		}
	}

	public boolean method_6888() {
		if (!this.world.doesNotCollide(this)) {
			return false;
		} else {
			int i = Math.max(1, this.getWidthPixels() / 16);
			int j = Math.max(1, this.getHeightPixels() / 16);
			BlockPos blockPos = this.blockPos.offset(this.facing.getOpposite());
			Direction direction = this.facing.rotateYCounterclockwise();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = 0; k < i; k++) {
				for (int l = 0; l < j; l++) {
					int m = (i - 1) / -2;
					int n = (j - 1) / -2;
					mutable.set(blockPos).setOffset(direction, k + m).setOffset(Direction.field_11036, l + n);
					BlockState blockState = this.world.getBlockState(mutable);
					if (!blockState.getMaterial().isSolid() && !AbstractRedstoneGateBlock.isRedstoneGate(blockState)) {
						return false;
					}
				}
			}

			return this.world.getEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
		}
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean handleAttack(Entity entity) {
		return entity instanceof PlayerEntity ? this.damage(DamageSource.player((PlayerEntity)entity), 0.0F) : false;
	}

	@Override
	public Direction getHorizontalFacing() {
		return this.facing;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			if (!this.removed && !this.world.isClient) {
				this.remove();
				this.scheduleVelocityUpdate();
				this.onBreak(damageSource.getAttacker());
			}

			return true;
		}
	}

	@Override
	public void move(MovementType movementType, Vec3d vec3d) {
		if (!this.world.isClient && !this.removed && vec3d.lengthSquared() > 0.0) {
			this.remove();
			this.onBreak(null);
		}
	}

	@Override
	public void addVelocity(double d, double e, double f) {
		if (!this.world.isClient && !this.removed && d * d + e * e + f * f > 0.0) {
			this.remove();
			this.onBreak(null);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putByte("Facing", (byte)this.facing.getHorizontal());
		BlockPos blockPos = this.getDecorationBlockPos();
		compoundTag.putInt("TileX", blockPos.getX());
		compoundTag.putInt("TileY", blockPos.getY());
		compoundTag.putInt("TileZ", blockPos.getZ());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.blockPos = new BlockPos(compoundTag.getInt("TileX"), compoundTag.getInt("TileY"), compoundTag.getInt("TileZ"));
		this.facing = Direction.fromHorizontal(compoundTag.getByte("Facing"));
	}

	public abstract int getWidthPixels();

	public abstract int getHeightPixels();

	public abstract void onBreak(@Nullable Entity entity);

	public abstract void onPlace();

	@Override
	public ItemEntity dropStack(ItemStack itemStack, float f) {
		ItemEntity itemEntity = new ItemEntity(
			this.world,
			this.x + (double)((float)this.facing.getOffsetX() * 0.15F),
			this.y + (double)f,
			this.z + (double)((float)this.facing.getOffsetZ() * 0.15F),
			itemStack
		);
		itemEntity.setToDefaultPickupDelay();
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
	public float applyRotation(BlockRotation blockRotation) {
		if (this.facing.getAxis() != Direction.Axis.field_11052) {
			switch (blockRotation) {
				case field_11464:
					this.facing = this.facing.getOpposite();
					break;
				case field_11465:
					this.facing = this.facing.rotateYCounterclockwise();
					break;
				case field_11463:
					this.facing = this.facing.rotateYClockwise();
			}
		}

		float f = MathHelper.wrapDegrees(this.yaw);
		switch (blockRotation) {
			case field_11464:
				return f + 180.0F;
			case field_11465:
				return f + 90.0F;
			case field_11463:
				return f + 270.0F;
			default:
				return f;
		}
	}

	@Override
	public float applyMirror(BlockMirror blockMirror) {
		return this.applyRotation(blockMirror.getRotation(this.facing));
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
	}

	@Override
	public void calculateDimensions() {
	}
}
