package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class LichenGrower {
	public static final LichenGrower.GrowType[] GROW_TYPES = new LichenGrower.GrowType[]{
		LichenGrower.GrowType.SAME_POSITION, LichenGrower.GrowType.SAME_PLANE, LichenGrower.GrowType.WRAP_AROUND
	};
	private final LichenGrower.GrowChecker growChecker;

	public LichenGrower(MultifaceGrowthBlock lichen) {
		this(new LichenGrower.LichenGrowChecker(lichen));
	}

	public LichenGrower(LichenGrower.GrowChecker growChecker) {
		this.growChecker = growChecker;
	}

	public boolean canGrow(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return Direction.stream().anyMatch(direction2 -> this.getGrowPos(state, world, pos, direction, direction2, this.growChecker::canGrow).isPresent());
	}

	public Optional<LichenGrower.GrowPos> grow(BlockState state, WorldAccess world, BlockPos pos, Random random) {
		return (Optional<LichenGrower.GrowPos>)Direction.shuffle(random)
			.stream()
			.filter(direction -> this.growChecker.canGrow(state, direction))
			.map(direction -> this.grow(state, world, pos, direction, random, false))
			.filter(Optional::isPresent)
			.findFirst()
			.orElse(Optional.empty());
	}

	public long grow(BlockState state, WorldAccess world, BlockPos pos, boolean markForPostProcessing) {
		return (Long)Direction.stream()
			.filter(direction -> this.growChecker.canGrow(state, direction))
			.map(direction -> this.grow(state, world, pos, direction, markForPostProcessing))
			.reduce(0L, Long::sum);
	}

	public Optional<LichenGrower.GrowPos> grow(
		BlockState state, WorldAccess world, BlockPos pos, Direction direction, Random random, boolean markForPostProcessing
	) {
		return (Optional<LichenGrower.GrowPos>)Direction.shuffle(random)
			.stream()
			.map(direction2 -> this.grow(state, world, pos, direction, direction2, markForPostProcessing))
			.filter(Optional::isPresent)
			.findFirst()
			.orElse(Optional.empty());
	}

	private long grow(BlockState state, WorldAccess world, BlockPos pos, Direction direction, boolean markForPostProcessing) {
		return Direction.stream().map(direction2 -> this.grow(state, world, pos, direction, direction2, markForPostProcessing)).filter(Optional::isPresent).count();
	}

	@VisibleForTesting
	public Optional<LichenGrower.GrowPos> grow(
		BlockState state, WorldAccess world, BlockPos pos, Direction oldDirection, Direction newDirection, boolean markForPostProcessing
	) {
		return this.getGrowPos(state, world, pos, oldDirection, newDirection, this.growChecker::canGrow)
			.flatMap(growPos -> this.place(world, growPos, markForPostProcessing));
	}

	public Optional<LichenGrower.GrowPos> getGrowPos(
		BlockState state, BlockView world, BlockPos pos, Direction oldDirection, Direction newDirection, LichenGrower.GrowPosPredicate predicate
	) {
		if (newDirection.getAxis() == oldDirection.getAxis()) {
			return Optional.empty();
		} else if (this.growChecker.canGrow(state) || this.growChecker.hasDirection(state, oldDirection) && !this.growChecker.hasDirection(state, newDirection)) {
			for (LichenGrower.GrowType growType : this.growChecker.getGrowTypes()) {
				LichenGrower.GrowPos growPos = growType.getGrowPos(pos, newDirection, oldDirection);
				if (predicate.test(world, pos, growPos)) {
					return Optional.of(growPos);
				}
			}

			return Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	public Optional<LichenGrower.GrowPos> place(WorldAccess world, LichenGrower.GrowPos pos, boolean markForPostProcessing) {
		BlockState blockState = world.getBlockState(pos.pos());
		return this.growChecker.place(world, pos, blockState, markForPostProcessing) ? Optional.of(pos) : Optional.empty();
	}

	public interface GrowChecker {
		@Nullable
		BlockState getStateWithDirection(BlockState state, BlockView world, BlockPos pos, Direction direction);

		boolean canGrow(BlockView world, BlockPos pos, LichenGrower.GrowPos growPos);

		default LichenGrower.GrowType[] getGrowTypes() {
			return LichenGrower.GROW_TYPES;
		}

		default boolean hasDirection(BlockState state, Direction direction) {
			return MultifaceGrowthBlock.hasDirection(state, direction);
		}

		default boolean canGrow(BlockState state) {
			return false;
		}

		default boolean canGrow(BlockState state, Direction direction) {
			return this.canGrow(state) || this.hasDirection(state, direction);
		}

		default boolean place(WorldAccess world, LichenGrower.GrowPos growPos, BlockState state, boolean markForPostProcessing) {
			BlockState blockState = this.getStateWithDirection(state, world, growPos.pos(), growPos.face());
			if (blockState != null) {
				if (markForPostProcessing) {
					world.getChunk(growPos.pos()).markBlockForPostProcessing(growPos.pos());
				}

				return world.setBlockState(growPos.pos(), blockState, Block.NOTIFY_LISTENERS);
			} else {
				return false;
			}
		}
	}

	public static record GrowPos(BlockPos pos, Direction face) {
	}

	@FunctionalInterface
	public interface GrowPosPredicate {
		boolean test(BlockView world, BlockPos pos, LichenGrower.GrowPos growPos);
	}

	public static enum GrowType {
		SAME_POSITION {
			@Override
			public LichenGrower.GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
				return new LichenGrower.GrowPos(pos, newDirection);
			}
		},
		SAME_PLANE {
			@Override
			public LichenGrower.GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
				return new LichenGrower.GrowPos(pos.offset(newDirection), oldDirection);
			}
		},
		WRAP_AROUND {
			@Override
			public LichenGrower.GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
				return new LichenGrower.GrowPos(pos.offset(newDirection).offset(oldDirection), newDirection.getOpposite());
			}
		};

		public abstract LichenGrower.GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection);
	}

	public static class LichenGrowChecker implements LichenGrower.GrowChecker {
		protected MultifaceGrowthBlock lichen;

		public LichenGrowChecker(MultifaceGrowthBlock lichen) {
			this.lichen = lichen;
		}

		@Nullable
		@Override
		public BlockState getStateWithDirection(BlockState state, BlockView world, BlockPos pos, Direction direction) {
			return this.lichen.withDirection(state, world, pos, direction);
		}

		protected boolean canGrow(BlockView world, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
			return state.isAir() || state.isOf(this.lichen) || state.isOf(Blocks.WATER) && state.getFluidState().isStill();
		}

		@Override
		public boolean canGrow(BlockView world, BlockPos pos, LichenGrower.GrowPos growPos) {
			BlockState blockState = world.getBlockState(growPos.pos());
			return this.canGrow(world, pos, growPos.pos(), growPos.face(), blockState)
				&& this.lichen.canGrowWithDirection(world, blockState, growPos.pos(), growPos.face());
		}
	}
}
