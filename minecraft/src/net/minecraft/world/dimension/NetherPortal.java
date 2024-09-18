package net.minecraft.world.dimension;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.mutable.MutableInt;

public class NetherPortal {
	private static final int MIN_WIDTH = 2;
	public static final int MAX_WIDTH = 21;
	private static final int MIN_HEIGHT = 3;
	public static final int MAX_HEIGHT = 21;
	private static final AbstractBlock.ContextPredicate IS_VALID_FRAME_BLOCK = (state, world, pos) -> state.isOf(Blocks.OBSIDIAN);
	private static final float FALLBACK_THRESHOLD = 4.0F;
	private static final double HEIGHT_STRETCH = 1.0;
	private final Direction.Axis axis;
	private final Direction negativeDir;
	private final int foundPortalBlocks;
	private final BlockPos lowerCorner;
	private final int height;
	private final int width;

	private NetherPortal(Direction.Axis axis, int foundPortalBlocks, Direction negativeDir, BlockPos lowerCorner, int width, int height) {
		this.axis = axis;
		this.foundPortalBlocks = foundPortalBlocks;
		this.negativeDir = negativeDir;
		this.lowerCorner = lowerCorner;
		this.width = width;
		this.height = height;
	}

	public static Optional<NetherPortal> getNewPortal(WorldAccess world, BlockPos pos, Direction.Axis firstCheckedAxis) {
		return getOrEmpty(world, pos, areaHelper -> areaHelper.isValid() && areaHelper.foundPortalBlocks == 0, firstCheckedAxis);
	}

	public static Optional<NetherPortal> getOrEmpty(WorldAccess world, BlockPos pos, Predicate<NetherPortal> validator, Direction.Axis firstCheckedAxis) {
		Optional<NetherPortal> optional = Optional.of(getOnAxis(world, pos, firstCheckedAxis)).filter(validator);
		if (optional.isPresent()) {
			return optional;
		} else {
			Direction.Axis axis = firstCheckedAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
			return Optional.of(getOnAxis(world, pos, axis)).filter(validator);
		}
	}

	public static NetherPortal getOnAxis(BlockView world, BlockPos pos, Direction.Axis axis) {
		Direction direction = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
		BlockPos blockPos = getLowerCorner(world, direction, pos);
		if (blockPos == null) {
			return new NetherPortal(axis, 0, direction, pos, 0, 0);
		} else {
			int i = getValidatedWidth(world, blockPos, direction);
			if (i == 0) {
				return new NetherPortal(axis, 0, direction, blockPos, 0, 0);
			} else {
				MutableInt mutableInt = new MutableInt();
				int j = getHeight(world, blockPos, direction, i, mutableInt);
				return new NetherPortal(axis, mutableInt.getValue(), direction, blockPos, i, j);
			}
		}
	}

	@Nullable
	private static BlockPos getLowerCorner(BlockView world, Direction direction, BlockPos pow) {
		int i = Math.max(world.getBottomY(), pow.getY() - 21);

		while (pow.getY() > i && validStateInsidePortal(world.getBlockState(pow.down()))) {
			pow = pow.down();
		}

		Direction direction2 = direction.getOpposite();
		int j = getWidth(world, pow, direction2) - 1;
		return j < 0 ? null : pow.offset(direction2, j);
	}

	private static int getValidatedWidth(BlockView world, BlockPos lowerCorner, Direction negativeDir) {
		int i = getWidth(world, lowerCorner, negativeDir);
		return i >= 2 && i <= 21 ? i : 0;
	}

	private static int getWidth(BlockView world, BlockPos lowerCorner, Direction negativeDir) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i <= 21; i++) {
			mutable.set(lowerCorner).move(negativeDir, i);
			BlockState blockState = world.getBlockState(mutable);
			if (!validStateInsidePortal(blockState)) {
				if (IS_VALID_FRAME_BLOCK.test(blockState, world, mutable)) {
					return i;
				}
				break;
			}

			BlockState blockState2 = world.getBlockState(mutable.move(Direction.DOWN));
			if (!IS_VALID_FRAME_BLOCK.test(blockState2, world, mutable)) {
				break;
			}
		}

		return 0;
	}

	private static int getHeight(BlockView world, BlockPos lowerCorner, Direction negativeDir, int width, MutableInt foundPortalBlocks) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = getPotentialHeight(world, lowerCorner, negativeDir, mutable, width, foundPortalBlocks);
		return i >= 3 && i <= 21 && isHorizontalFrameValid(world, lowerCorner, negativeDir, mutable, width, i) ? i : 0;
	}

	private static boolean isHorizontalFrameValid(BlockView world, BlockPos lowerCorner, Direction direction, BlockPos.Mutable pos, int width, int height) {
		for (int i = 0; i < width; i++) {
			BlockPos.Mutable mutable = pos.set(lowerCorner).move(Direction.UP, height).move(direction, i);
			if (!IS_VALID_FRAME_BLOCK.test(world.getBlockState(mutable), world, mutable)) {
				return false;
			}
		}

		return true;
	}

	private static int getPotentialHeight(
		BlockView world, BlockPos lowerCorner, Direction negativeDir, BlockPos.Mutable pos, int width, MutableInt foundPortalBlocks
	) {
		for (int i = 0; i < 21; i++) {
			pos.set(lowerCorner).move(Direction.UP, i).move(negativeDir, -1);
			if (!IS_VALID_FRAME_BLOCK.test(world.getBlockState(pos), world, pos)) {
				return i;
			}

			pos.set(lowerCorner).move(Direction.UP, i).move(negativeDir, width);
			if (!IS_VALID_FRAME_BLOCK.test(world.getBlockState(pos), world, pos)) {
				return i;
			}

			for (int j = 0; j < width; j++) {
				pos.set(lowerCorner).move(Direction.UP, i).move(negativeDir, j);
				BlockState blockState = world.getBlockState(pos);
				if (!validStateInsidePortal(blockState)) {
					return i;
				}

				if (blockState.isOf(Blocks.NETHER_PORTAL)) {
					foundPortalBlocks.increment();
				}
			}
		}

		return 21;
	}

	private static boolean validStateInsidePortal(BlockState state) {
		return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(Blocks.NETHER_PORTAL);
	}

	public boolean isValid() {
		return this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
	}

	public void createPortal(WorldAccess world) {
		BlockState blockState = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis);
		BlockPos.iterate(this.lowerCorner, this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1))
			.forEach(pos -> world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE));
	}

	public boolean wasAlreadyValid() {
		return this.isValid() && this.foundPortalBlocks == this.width * this.height;
	}

	public static Vec3d entityPosInPortal(BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Vec3d entityPos, EntityDimensions entityDimensions) {
		double d = (double)portalRect.width - (double)entityDimensions.width();
		double e = (double)portalRect.height - (double)entityDimensions.height();
		BlockPos blockPos = portalRect.lowerLeft;
		double g;
		if (d > 0.0) {
			double f = (double)blockPos.getComponentAlongAxis(portalAxis) + (double)entityDimensions.width() / 2.0;
			g = MathHelper.clamp(MathHelper.getLerpProgress(entityPos.getComponentAlongAxis(portalAxis) - f, 0.0, d), 0.0, 1.0);
		} else {
			g = 0.5;
		}

		double f;
		if (e > 0.0) {
			Direction.Axis axis = Direction.Axis.Y;
			f = MathHelper.clamp(MathHelper.getLerpProgress(entityPos.getComponentAlongAxis(axis) - (double)blockPos.getComponentAlongAxis(axis), 0.0, e), 0.0, 1.0);
		} else {
			f = 0.0;
		}

		Direction.Axis axis = portalAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
		double h = entityPos.getComponentAlongAxis(axis) - ((double)blockPos.getComponentAlongAxis(axis) + 0.5);
		return new Vec3d(g, f, h);
	}

	public static Vec3d findOpenPosition(Vec3d fallback, ServerWorld world, Entity entity, EntityDimensions dimensions) {
		if (!(dimensions.width() > 4.0F) && !(dimensions.height() > 4.0F)) {
			double d = (double)dimensions.height() / 2.0;
			Vec3d vec3d = fallback.add(0.0, d, 0.0);
			VoxelShape voxelShape = VoxelShapes.cuboid(Box.of(vec3d, (double)dimensions.width(), 0.0, (double)dimensions.width()).stretch(0.0, 1.0, 0.0).expand(1.0E-6));
			Optional<Vec3d> optional = world.findClosestCollision(
				entity, voxelShape, vec3d, (double)dimensions.width(), (double)dimensions.height(), (double)dimensions.width()
			);
			Optional<Vec3d> optional2 = optional.map(pos -> pos.subtract(0.0, d, 0.0));
			return (Vec3d)optional2.orElse(fallback);
		} else {
			return fallback;
		}
	}
}
