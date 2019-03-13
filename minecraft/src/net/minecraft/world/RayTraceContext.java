package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class RayTraceContext {
	private final Vec3d field_17553;
	private final Vec3d field_17554;
	private final RayTraceContext.ShapeType shapeType;
	private final RayTraceContext.FluidHandling fluid;
	private final VerticalEntityPosition field_17557;

	public RayTraceContext(Vec3d vec3d, Vec3d vec3d2, RayTraceContext.ShapeType shapeType, RayTraceContext.FluidHandling fluidHandling, Entity entity) {
		this.field_17553 = vec3d;
		this.field_17554 = vec3d2;
		this.shapeType = shapeType;
		this.fluid = fluidHandling;
		this.field_17557 = VerticalEntityPosition.fromEntity(entity);
	}

	public Vec3d method_17747() {
		return this.field_17554;
	}

	public Vec3d method_17750() {
		return this.field_17553;
	}

	public VoxelShape method_17748(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.shapeType.get(blockState, blockView, blockPos, this.field_17557);
	}

	public VoxelShape method_17749(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
		return this.fluid.method_17751(fluidState) ? fluidState.method_17776(blockView, blockPos) : VoxelShapes.method_1073();
	}

	public static enum FluidHandling {
		NONE(fluidState -> false),
		field_1345(FluidState::isStill),
		field_1347(fluidState -> !fluidState.isEmpty());

		private final Predicate<FluidState> predicate;

		private FluidHandling(Predicate<FluidState> predicate) {
			this.predicate = predicate;
		}

		public boolean method_17751(FluidState fluidState) {
			return this.predicate.test(fluidState);
		}
	}

	public interface ShapeProvider {
		VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition);
	}

	public static enum ShapeType implements RayTraceContext.ShapeProvider {
		field_17558(BlockState::method_16337),
		field_17559(BlockState::method_11606);

		private final RayTraceContext.ShapeProvider provider;

		private ShapeType(RayTraceContext.ShapeProvider shapeProvider) {
			this.provider = shapeProvider;
		}

		@Override
		public VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
			return this.provider.get(blockState, blockView, blockPos, verticalEntityPosition);
		}
	}
}
