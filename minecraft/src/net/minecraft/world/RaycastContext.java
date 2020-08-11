package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class RaycastContext {
	private final Vec3d start;
	private final Vec3d end;
	private final RaycastContext.ShapeType shapeType;
	private final RaycastContext.FluidHandling fluid;
	private final ShapeContext entityPosition;

	public RaycastContext(Vec3d start, Vec3d end, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, Entity entity) {
		this.start = start;
		this.end = end;
		this.shapeType = shapeType;
		this.fluid = fluidHandling;
		this.entityPosition = ShapeContext.of(entity);
	}

	public Vec3d getEnd() {
		return this.end;
	}

	public Vec3d getStart() {
		return this.start;
	}

	public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
		return this.shapeType.get(state, world, pos, this.entityPosition);
	}

	public VoxelShape getFluidShape(FluidState state, BlockView world, BlockPos pos) {
		return this.fluid.handled(state) ? state.getShape(world, pos) : VoxelShapes.empty();
	}

	public static enum FluidHandling {
		NONE(fluidState -> false),
		SOURCE_ONLY(FluidState::isStill),
		ANY(fluidState -> !fluidState.isEmpty());

		private final Predicate<FluidState> predicate;

		private FluidHandling(Predicate<FluidState> predicate) {
			this.predicate = predicate;
		}

		public boolean handled(FluidState state) {
			return this.predicate.test(state);
		}
	}

	public interface ShapeProvider {
		VoxelShape get(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
	}

	public static enum ShapeType implements RaycastContext.ShapeProvider {
		COLLIDER(AbstractBlock.AbstractBlockState::getCollisionShape),
		OUTLINE(AbstractBlock.AbstractBlockState::getOutlineShape),
		VISUAL(AbstractBlock.AbstractBlockState::getVisualShape);

		private final RaycastContext.ShapeProvider provider;

		private ShapeType(RaycastContext.ShapeProvider provider) {
			this.provider = provider;
		}

		@Override
		public VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
			return this.provider.get(blockState, blockView, blockPos, shapeContext);
		}
	}
}
