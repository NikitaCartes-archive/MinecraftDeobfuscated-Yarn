package net.minecraft.item;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AutomaticItemPlacementContext extends ItemPlacementContext {
	private final Direction facing;

	public AutomaticItemPlacementContext(World world, BlockPos pos, Direction facing, ItemStack stack, Direction side) {
		super(world, null, Hand.MAIN_HAND, stack, new BlockHitResult(Vec3d.ofBottomCenter(pos), side, pos, false));
		this.facing = facing;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.getHitResult().getBlockPos();
	}

	@Override
	public boolean canPlace() {
		return this.getWorld().getBlockState(this.getHitResult().getBlockPos()).canReplace(this);
	}

	@Override
	public boolean canReplaceExisting() {
		return this.canPlace();
	}

	@Override
	public Direction getPlayerLookDirection() {
		return Direction.DOWN;
	}

	@Override
	public Direction[] getPlacementDirections() {
		switch (this.facing) {
			case DOWN:
			default:
				return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
			case UP:
				return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
			case NORTH:
				return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
			case SOUTH:
				return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
			case WEST:
				return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
			case EAST:
				return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
		}
	}

	@Override
	public Direction getHorizontalPlayerFacing() {
		return this.facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.facing;
	}

	@Override
	public boolean shouldCancelInteraction() {
		return false;
	}

	@Override
	public float getPlayerYaw() {
		return (float)(this.facing.getHorizontal() * 90);
	}
}
