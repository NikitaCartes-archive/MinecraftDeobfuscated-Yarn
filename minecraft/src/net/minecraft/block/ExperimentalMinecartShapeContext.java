package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CollisionView;

public class ExperimentalMinecartShapeContext extends EntityShapeContext {
	@Nullable
	private BlockPos belowPos;
	@Nullable
	private BlockPos ascendingPos;

	protected ExperimentalMinecartShapeContext(AbstractMinecartEntity minecart, boolean collidesWithFluid) {
		super(minecart, collidesWithFluid);
		this.setIgnoredPositions(minecart);
	}

	private void setIgnoredPositions(AbstractMinecartEntity minecart) {
		BlockPos blockPos = minecart.getRailOrMinecartPos();
		BlockState blockState = minecart.getWorld().getBlockState(blockPos);
		boolean bl = AbstractRailBlock.isRail(blockState);
		if (bl) {
			this.belowPos = blockPos.down();
			RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			if (railShape.isAscending()) {
				this.ascendingPos = switch (railShape) {
					case ASCENDING_EAST -> blockPos.east();
					case ASCENDING_WEST -> blockPos.west();
					case ASCENDING_NORTH -> blockPos.north();
					case ASCENDING_SOUTH -> blockPos.south();
					default -> null;
				};
			}
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos) {
		return !pos.equals(this.belowPos) && !pos.equals(this.ascendingPos) ? super.getCollisionShape(state, world, pos) : VoxelShapes.empty();
	}
}
