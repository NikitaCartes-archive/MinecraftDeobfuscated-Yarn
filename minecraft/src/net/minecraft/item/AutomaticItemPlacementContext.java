package net.minecraft.item;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AutomaticItemPlacementContext extends ItemPlacementContext {
	private final Direction direction;

	public AutomaticItemPlacementContext(World world, BlockPos blockPos, Direction direction, ItemStack itemStack, Direction direction2) {
		super(
			world,
			null,
			Hand.field_5808,
			itemStack,
			new BlockHitResult(new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5), direction2, blockPos, false)
		);
		this.direction = direction;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.hitResult.getBlockPos();
	}

	@Override
	public boolean canPlace() {
		return this.world.getBlockState(this.hitResult.getBlockPos()).canReplace(this);
	}

	@Override
	public boolean canReplaceHitBlock() {
		return this.canPlace();
	}

	@Override
	public Direction getPlayerFacing() {
		return Direction.field_11033;
	}

	@Override
	public Direction[] getPlacementFacings() {
		switch (this.direction) {
			case field_11033:
			default:
				return new Direction[]{
					Direction.field_11033, Direction.field_11043, Direction.field_11034, Direction.field_11035, Direction.field_11039, Direction.field_11036
				};
			case field_11036:
				return new Direction[]{
					Direction.field_11033, Direction.field_11036, Direction.field_11043, Direction.field_11034, Direction.field_11035, Direction.field_11039
				};
			case field_11043:
				return new Direction[]{
					Direction.field_11033, Direction.field_11043, Direction.field_11034, Direction.field_11039, Direction.field_11036, Direction.field_11035
				};
			case field_11035:
				return new Direction[]{
					Direction.field_11033, Direction.field_11035, Direction.field_11034, Direction.field_11039, Direction.field_11036, Direction.field_11043
				};
			case field_11039:
				return new Direction[]{
					Direction.field_11033, Direction.field_11039, Direction.field_11035, Direction.field_11036, Direction.field_11043, Direction.field_11034
				};
			case field_11034:
				return new Direction[]{
					Direction.field_11033, Direction.field_11034, Direction.field_11035, Direction.field_11036, Direction.field_11043, Direction.field_11039
				};
		}
	}

	@Override
	public Direction getPlayerHorizontalFacing() {
		return this.direction.getAxis() == Direction.Axis.Y ? Direction.field_11043 : this.direction;
	}

	@Override
	public boolean isPlayerSneaking() {
		return false;
	}

	@Override
	public float getPlayerYaw() {
		return (float)(this.direction.getHorizontal() * 90);
	}
}
