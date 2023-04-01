package net.minecraft.item;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlaceBlockItemPlacementContext extends ItemPlacementContext {
	public PlaceBlockItemPlacementContext(World world, Hand hand, ItemStack stack, BlockHitResult hitResult) {
		super(world, null, hand, stack, hitResult);
		this.canReplaceExisting = world.getBlockState(hitResult.getBlockPos()).canReplace(this);
	}

	public static PlaceBlockItemPlacementContext of(World world, BlockPos pos, Direction direction, ItemStack stack) {
		return new PlaceBlockItemPlacementContext(
			world,
			Hand.MAIN_HAND,
			stack,
			new BlockHitResult(
				new Vec3d(
					(double)pos.getX() + 0.5 + (double)direction.getOffsetX() * 0.5,
					(double)pos.getY() + 0.5 + (double)direction.getOffsetY() * 0.5,
					(double)pos.getZ() + 0.5 + (double)direction.getOffsetZ() * 0.5
				),
				direction,
				pos,
				false
			)
		);
	}

	@Override
	public Direction getPlayerLookDirection() {
		return this.getHitResult().getSide();
	}

	@Override
	public Direction getVerticalPlayerLookDirection() {
		return this.getHitResult().getSide() == Direction.UP ? Direction.UP : Direction.DOWN;
	}

	@Override
	public Direction[] getPlacementDirections() {
		Direction direction = this.getHitResult().getSide();
		Direction[] directions = new Direction[]{direction, null, null, null, null, direction.getOpposite()};
		int i = 0;

		for (Direction direction2 : Direction.values()) {
			if (direction2 != direction && direction2 != direction.getOpposite()) {
				i++;
				directions[i] = direction;
			}
		}

		return directions;
	}
}
