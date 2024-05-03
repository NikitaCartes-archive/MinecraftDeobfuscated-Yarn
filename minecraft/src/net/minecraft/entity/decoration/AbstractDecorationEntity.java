package net.minecraft.entity.decoration;

import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class AbstractDecorationEntity extends BlockAttachedEntity {
	protected static final Predicate<Entity> PREDICATE = entity -> entity instanceof AbstractDecorationEntity;
	protected Direction facing = Direction.SOUTH;

	protected AbstractDecorationEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
		super(entityType, world);
	}

	protected AbstractDecorationEntity(EntityType<? extends AbstractDecorationEntity> type, World world, BlockPos pos) {
		this(type, world);
		this.attachedBlockPos = pos;
	}

	protected void setFacing(Direction facing) {
		Objects.requireNonNull(facing);
		Validate.isTrue(facing.getAxis().isHorizontal());
		this.facing = facing;
		this.setYaw((float)(this.facing.getHorizontal() * 90));
		this.prevYaw = this.getYaw();
		this.updateAttachmentPosition();
	}

	@Override
	protected final void updateAttachmentPosition() {
		if (this.facing != null) {
			Box box = this.calculateBoundingBox(this.attachedBlockPos, this.facing);
			Vec3d vec3d = box.getCenter();
			this.setPos(vec3d.x, vec3d.y, vec3d.z);
			this.setBoundingBox(box);
		}
	}

	protected abstract Box calculateBoundingBox(BlockPos pos, Direction side);

	@Override
	public boolean canStayAttached() {
		if (!this.getWorld().isSpaceEmpty(this)) {
			return false;
		} else {
			boolean bl = BlockPos.stream(this.getAttachmentBox()).allMatch(pos -> {
				BlockState blockState = this.getWorld().getBlockState(pos);
				return blockState.isSolid() || AbstractRedstoneGateBlock.isRedstoneGate(blockState);
			});
			return !bl ? false : this.getWorld().getOtherEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
		}
	}

	protected Box getAttachmentBox() {
		return this.getBoundingBox().offset(this.facing.getUnitVector().mul(-0.5F)).contract(1.0E-7);
	}

	@Override
	public Direction getHorizontalFacing() {
		return this.facing;
	}

	public abstract void onPlace();

	@Override
	public ItemEntity dropStack(ItemStack stack, float yOffset) {
		ItemEntity itemEntity = new ItemEntity(
			this.getWorld(),
			this.getX() + (double)((float)this.facing.getOffsetX() * 0.15F),
			this.getY() + (double)yOffset,
			this.getZ() + (double)((float)this.facing.getOffsetZ() * 0.15F),
			stack
		);
		itemEntity.setToDefaultPickupDelay();
		this.getWorld().spawnEntity(itemEntity);
		return itemEntity;
	}

	@Override
	public float applyRotation(BlockRotation rotation) {
		if (this.facing.getAxis() != Direction.Axis.Y) {
			switch (rotation) {
				case CLOCKWISE_180:
					this.facing = this.facing.getOpposite();
					break;
				case COUNTERCLOCKWISE_90:
					this.facing = this.facing.rotateYCounterclockwise();
					break;
				case CLOCKWISE_90:
					this.facing = this.facing.rotateYClockwise();
			}
		}

		float f = MathHelper.wrapDegrees(this.getYaw());

		return switch (rotation) {
			case CLOCKWISE_180 -> f + 180.0F;
			case COUNTERCLOCKWISE_90 -> f + 90.0F;
			case CLOCKWISE_90 -> f + 270.0F;
			default -> f;
		};
	}

	@Override
	public float applyMirror(BlockMirror mirror) {
		return this.applyRotation(mirror.getRotation(this.facing));
	}
}
