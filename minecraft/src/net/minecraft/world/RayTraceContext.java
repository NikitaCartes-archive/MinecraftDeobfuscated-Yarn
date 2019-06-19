package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class RayTraceContext {
	private final Vec3d start;
	private final Vec3d end;
	private final RayTraceContext.ShapeType shapeType;
	private final RayTraceContext.FluidHandling fluid;
	private final EntityContext entityPosition;

	public RayTraceContext(Vec3d vec3d, Vec3d vec3d2, RayTraceContext.ShapeType shapeType, RayTraceContext.FluidHandling fluidHandling, Entity entity) {
		this.start = vec3d;
		this.end = vec3d2;
		this.shapeType = shapeType;
		this.fluid = fluidHandling;
		this.entityPosition = EntityContext.of(entity);
	}

	public Vec3d getEnd() {
		return this.end;
	}

	public Vec3d getStart() {
		return this.start;
	}

	public VoxelShape getBlockShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.shapeType.get(blockState, blockView, blockPos, this.entityPosition);
	}

	public VoxelShape getFluidShape(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
		return this.fluid.handled(fluidState) ? fluidState.getShape(blockView, blockPos) : VoxelShapes.empty();
	}

	public static enum FluidHandling {
		field_1348(fluidState -> false),
		field_1345(FluidState::isStill),
		field_1347(fluidState -> !fluidState.isEmpty());

		private final Predicate<FluidState> predicate;

		private FluidHandling(Predicate<FluidState> predicate) {
			this.predicate = predicate;
		}

		public boolean handled(FluidState fluidState) {
			return this.predicate.test(fluidState);
		}
	}

	public interface ShapeProvider {
		VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext);
	}

	public static enum ShapeType implements RayTraceContext.ShapeProvider {
		field_17558(BlockState::getCollisionShape),
		field_17559(BlockState::getOutlineShape);

		private final RayTraceContext.ShapeProvider provider;

		private ShapeType(RayTraceContext.ShapeProvider shapeProvider) {
			this.provider = shapeProvider;
		}

		@Override
		public VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
			return this.provider.get(blockState, blockView, blockPos, entityContext);
		}
	}
}
