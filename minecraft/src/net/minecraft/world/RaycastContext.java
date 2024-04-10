package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class RaycastContext {
	private final Vec3d start;
	private final Vec3d end;
	private final RaycastContext.ShapeType shapeType;
	private final RaycastContext.FluidHandling fluid;
	private final ShapeContext shapeContext;

	public RaycastContext(Vec3d start, Vec3d end, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, Entity entity) {
		this(start, end, shapeType, fluidHandling, ShapeContext.of(entity));
	}

	public RaycastContext(Vec3d start, Vec3d end, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, ShapeContext shapeContext) {
		this.start = start;
		this.end = end;
		this.shapeType = shapeType;
		this.fluid = fluidHandling;
		this.shapeContext = shapeContext;
	}

	public Vec3d getEnd() {
		return this.end;
	}

	public Vec3d getStart() {
		return this.start;
	}

	public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
		return this.shapeType.get(state, world, pos, this.shapeContext);
	}

	public VoxelShape getFluidShape(FluidState state, BlockView world, BlockPos pos) {
		return this.fluid.handled(state) ? state.getShape(world, pos) : VoxelShapes.empty();
	}

	public static enum FluidHandling {
		NONE(state -> false),
		SOURCE_ONLY(FluidState::isStill),
		ANY(state -> !state.isEmpty()),
		WATER(state -> state.isIn(FluidTags.WATER));

		private final Predicate<FluidState> predicate;

		private FluidHandling(final Predicate<FluidState> predicate) {
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
		VISUAL(AbstractBlock.AbstractBlockState::getCameraCollisionShape),
		FALLDAMAGE_RESETTING((state, world, pos, context) -> state.isIn(BlockTags.FALL_DAMAGE_RESETTING) ? VoxelShapes.fullCube() : VoxelShapes.empty());

		private final RaycastContext.ShapeProvider provider;

		private ShapeType(final RaycastContext.ShapeProvider provider) {
			this.provider = provider;
		}

		@Override
		public VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
			return this.provider.get(blockState, blockView, blockPos, shapeContext);
		}
	}
}
