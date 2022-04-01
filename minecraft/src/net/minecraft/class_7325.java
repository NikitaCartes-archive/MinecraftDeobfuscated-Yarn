package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class class_7325 {
	public static final class_7325.class_7330[] field_38564 = new class_7325.class_7330[]{
		class_7325.class_7330.SAME_POSITION, class_7325.class_7330.SAME_PLANE, class_7325.class_7330.WRAP_AROUND
	};
	private final class_7325.class_7327 field_38565;

	public class_7325(AbstractLichenBlock abstractLichenBlock) {
		this(new class_7325.class_7326(abstractLichenBlock));
	}

	public class_7325(class_7325.class_7327 arg) {
		this.field_38565 = arg;
	}

	public boolean method_42892(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return Direction.method_43008()
			.anyMatch(direction2 -> this.method_42894(blockState, blockView, blockPos, direction, direction2, this.field_38565::method_42906).isPresent());
	}

	public Optional<class_7325.class_7328> method_42901(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Random random) {
		return (Optional<class_7325.class_7328>)Direction.method_43009(random)
			.stream()
			.filter(direction -> this.field_38565.method_42913(blockState, direction))
			.map(direction -> this.method_42898(blockState, worldAccess, blockPos, direction, random, false))
			.filter(Optional::isPresent)
			.findFirst()
			.orElse(Optional.empty());
	}

	public long method_42895(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		return (Long)Direction.method_43008()
			.filter(direction -> this.field_38565.method_42913(blockState, direction))
			.map(direction -> this.method_42899(blockState, worldAccess, blockPos, direction, false))
			.reduce(0L, Long::sum);
	}

	public Optional<class_7325.class_7328> method_42898(
		BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, Random random, boolean bl
	) {
		return (Optional<class_7325.class_7328>)Direction.method_43009(random)
			.stream()
			.map(direction2 -> this.method_42897(blockState, worldAccess, blockPos, direction, direction2, bl))
			.filter(Optional::isPresent)
			.findFirst()
			.orElse(Optional.empty());
	}

	private long method_42899(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, boolean bl) {
		return Direction.method_43008()
			.map(direction2 -> this.method_42897(blockState, worldAccess, blockPos, direction, direction2, bl))
			.filter(Optional::isPresent)
			.count();
	}

	@VisibleForTesting
	public Optional<class_7325.class_7328> method_42897(
		BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, Direction direction2, boolean bl
	) {
		return this.method_42894(blockState, worldAccess, blockPos, direction, direction2, this.field_38565::method_42906)
			.flatMap(arg -> this.method_42890(worldAccess, arg, bl));
	}

	public Optional<class_7325.class_7328> method_42894(
		BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction, Direction direction2, class_7325.class_7329 arg
	) {
		if (direction2.getAxis() == direction.getAxis()) {
			return Optional.empty();
		} else if (this.field_38565.method_42911(blockState)
			|| this.field_38565.method_42912(blockState, direction) && !this.field_38565.method_42912(blockState, direction2)) {
			for (class_7325.class_7330 lv : this.field_38565.method_42909()) {
				class_7325.class_7328 lv2 = lv.method_42915(blockPos, direction2, direction);
				if (arg.test(blockView, blockPos, lv2)) {
					return Optional.of(lv2);
				}
			}

			return Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	public Optional<class_7325.class_7328> method_42890(WorldAccess worldAccess, class_7325.class_7328 arg, boolean bl) {
		BlockState blockState = worldAccess.getBlockState(arg.pos());
		return this.field_38565.method_42910(worldAccess, arg, blockState, bl) ? Optional.of(arg) : Optional.empty();
	}

	public static class class_7326 implements class_7325.class_7327 {
		protected AbstractLichenBlock field_38566;

		public class_7326(AbstractLichenBlock abstractLichenBlock) {
			this.field_38566 = abstractLichenBlock;
		}

		@Nullable
		@Override
		public BlockState method_42908(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
			return this.field_38566.withDirection(blockState, blockView, blockPos, direction);
		}

		protected boolean method_42907(BlockView blockView, BlockPos blockPos, BlockPos blockPos2, Direction direction, BlockState blockState) {
			return blockState.isAir() || blockState.isOf(this.field_38566) || blockState.isOf(Blocks.WATER) && blockState.getFluidState().isStill();
		}

		@Override
		public boolean method_42906(BlockView blockView, BlockPos blockPos, class_7325.class_7328 arg) {
			BlockState blockState = blockView.getBlockState(arg.pos());
			return this.method_42907(blockView, blockPos, arg.pos(), arg.face(), blockState)
				&& this.field_38566.method_42887(blockView, blockState, arg.pos(), arg.face());
		}
	}

	public interface class_7327 {
		@Nullable
		BlockState method_42908(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction);

		boolean method_42906(BlockView blockView, BlockPos blockPos, class_7325.class_7328 arg);

		default class_7325.class_7330[] method_42909() {
			return class_7325.field_38564;
		}

		default boolean method_42912(BlockState blockState, Direction direction) {
			return AbstractLichenBlock.hasDirection(blockState, direction);
		}

		default boolean method_42911(BlockState blockState) {
			return false;
		}

		default boolean method_42913(BlockState blockState, Direction direction) {
			return this.method_42911(blockState) || this.method_42912(blockState, direction);
		}

		default boolean method_42910(WorldAccess worldAccess, class_7325.class_7328 arg, BlockState blockState, boolean bl) {
			BlockState blockState2 = this.method_42908(blockState, worldAccess, arg.pos(), arg.face());
			if (blockState2 != null) {
				if (bl) {
					worldAccess.getChunk(arg.pos()).markBlockForPostProcessing(arg.pos());
				}

				return worldAccess.setBlockState(arg.pos(), blockState2, Block.NOTIFY_LISTENERS);
			} else {
				return false;
			}
		}
	}

	public static record class_7328(BlockPos pos, Direction face) {
	}

	@FunctionalInterface
	public interface class_7329 {
		boolean test(BlockView blockView, BlockPos blockPos, class_7325.class_7328 arg);
	}

	public static enum class_7330 {
		SAME_POSITION {
			@Override
			public class_7325.class_7328 method_42915(BlockPos blockPos, Direction direction, Direction direction2) {
				return new class_7325.class_7328(blockPos, direction);
			}
		},
		SAME_PLANE {
			@Override
			public class_7325.class_7328 method_42915(BlockPos blockPos, Direction direction, Direction direction2) {
				return new class_7325.class_7328(blockPos.offset(direction), direction2);
			}
		},
		WRAP_AROUND {
			@Override
			public class_7325.class_7328 method_42915(BlockPos blockPos, Direction direction, Direction direction2) {
				return new class_7325.class_7328(blockPos.offset(direction).offset(direction2), direction.getOpposite());
			}
		};

		public abstract class_7325.class_7328 method_42915(BlockPos blockPos, Direction direction, Direction direction2);
	}
}
