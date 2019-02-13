package net.minecraft.item;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AutomaticItemPlacementContext extends ItemPlacementContext {
	private final Direction field_13362;

	public AutomaticItemPlacementContext(World world, BlockPos blockPos, Direction direction, ItemStack itemStack, Direction direction2) {
		super(
			world,
			null,
			itemStack,
			new BlockHitResult(new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5), direction2, blockPos, false)
		);
		this.field_13362 = direction;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.hitResult.getBlockPos();
	}

	@Override
	public boolean canPlace() {
		return this.world.getBlockState(this.hitResult.getBlockPos()).method_11587(this);
	}

	@Override
	public boolean method_7717() {
		return this.canPlace();
	}

	@Override
	public Direction getPlayerFacing() {
		return Direction.DOWN;
	}

	@Override
	public Direction[] getPlacementFacings() {
		switch (this.field_13362) {
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
	public Direction getPlayerHorizontalFacing() {
		return this.field_13362.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.field_13362;
	}

	@Override
	public boolean isPlayerSneaking() {
		return false;
	}

	@Override
	public float getPlayerYaw() {
		return (float)(this.field_13362.getHorizontal() * 90);
	}
}
