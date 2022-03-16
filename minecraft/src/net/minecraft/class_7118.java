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

public class class_7118 {
	public static final class_7118.class_7123[] field_37595 = new class_7118.class_7123[]{
		class_7118.class_7123.SAME_POSITION, class_7118.class_7123.SAME_PLANE, class_7118.class_7123.WRAP_AROUND
	};
	private final class_7118.class_7120 field_37596;

	public class_7118(AbstractLichenBlock abstractLichenBlock) {
		this(new class_7118.class_7119(abstractLichenBlock));
	}

	public class_7118(class_7118.class_7120 arg) {
		this.field_37596 = arg;
	}

	public boolean method_41443(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return Direction.stream()
			.anyMatch(direction2 -> this.method_41445(blockState, blockView, blockPos, direction, direction2, this.field_37596::method_41457).isPresent());
	}

	public Optional<class_7118.class_7121> method_41450(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Random random) {
		return (Optional<class_7118.class_7121>)Direction.shuffle(random)
			.stream()
			.filter(direction -> this.field_37596.method_41464(blockState, direction))
			.map(direction -> this.method_41447(blockState, worldAccess, blockPos, direction, random, false))
			.filter(Optional::isPresent)
			.findFirst()
			.orElse(Optional.empty());
	}

	public long method_41452(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, boolean bl) {
		return (Long)Direction.stream()
			.filter(direction -> this.field_37596.method_41464(blockState, direction))
			.map(direction -> this.method_41448(blockState, worldAccess, blockPos, direction, bl))
			.reduce(0L, Long::sum);
	}

	public Optional<class_7118.class_7121> method_41447(
		BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, Random random, boolean bl
	) {
		return (Optional<class_7118.class_7121>)Direction.shuffle(random)
			.stream()
			.map(direction2 -> this.method_41446(blockState, worldAccess, blockPos, direction, direction2, bl))
			.filter(Optional::isPresent)
			.findFirst()
			.orElse(Optional.empty());
	}

	private long method_41448(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, boolean bl) {
		return Direction.stream()
			.map(direction2 -> this.method_41446(blockState, worldAccess, blockPos, direction, direction2, bl))
			.filter(Optional::isPresent)
			.count();
	}

	@VisibleForTesting
	public Optional<class_7118.class_7121> method_41446(
		BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, Direction direction2, boolean bl
	) {
		return this.method_41445(blockState, worldAccess, blockPos, direction, direction2, this.field_37596::method_41457)
			.flatMap(arg -> this.method_41441(worldAccess, arg, bl));
	}

	public Optional<class_7118.class_7121> method_41445(
		BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction, Direction direction2, class_7118.class_7122 arg
	) {
		if (direction2.getAxis() == direction.getAxis()) {
			return Optional.empty();
		} else if (this.field_37596.method_41462(blockState)
			|| this.field_37596.method_41463(blockState, direction) && !this.field_37596.method_41463(blockState, direction2)) {
			for (class_7118.class_7123 lv : this.field_37596.method_41460()) {
				class_7118.class_7121 lv2 = lv.method_41466(blockPos, direction2, direction);
				if (arg.test(blockView, blockPos, lv2)) {
					return Optional.of(lv2);
				}
			}

			return Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	public Optional<class_7118.class_7121> method_41441(WorldAccess worldAccess, class_7118.class_7121 arg, boolean bl) {
		BlockState blockState = worldAccess.getBlockState(arg.pos());
		return this.field_37596.method_41461(worldAccess, arg, blockState, bl) ? Optional.of(arg) : Optional.empty();
	}

	public static class class_7119 implements class_7118.class_7120 {
		protected AbstractLichenBlock field_37597;

		public class_7119(AbstractLichenBlock abstractLichenBlock) {
			this.field_37597 = abstractLichenBlock;
		}

		@Nullable
		@Override
		public BlockState method_41459(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
			return this.field_37597.withDirection(blockState, blockView, blockPos, direction);
		}

		protected boolean method_41458(BlockView blockView, BlockPos blockPos, BlockPos blockPos2, Direction direction, BlockState blockState) {
			return blockState.isAir() || blockState.isOf(this.field_37597) || blockState.isOf(Blocks.WATER) && blockState.getFluidState().isStill();
		}

		@Override
		public boolean method_41457(BlockView blockView, BlockPos blockPos, class_7118.class_7121 arg) {
			BlockState blockState = blockView.getBlockState(arg.pos());
			return this.method_41458(blockView, blockPos, arg.pos(), arg.face(), blockState)
				&& this.field_37597.canGrowWithDirection(blockView, blockState, arg.pos(), arg.face());
		}
	}

	public interface class_7120 {
		@Nullable
		BlockState method_41459(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction);

		boolean method_41457(BlockView blockView, BlockPos blockPos, class_7118.class_7121 arg);

		default class_7118.class_7123[] method_41460() {
			return class_7118.field_37595;
		}

		default boolean method_41463(BlockState blockState, Direction direction) {
			return AbstractLichenBlock.hasDirection(blockState, direction);
		}

		default boolean method_41462(BlockState blockState) {
			return false;
		}

		default boolean method_41464(BlockState blockState, Direction direction) {
			return this.method_41462(blockState) || this.method_41463(blockState, direction);
		}

		default boolean method_41461(WorldAccess worldAccess, class_7118.class_7121 arg, BlockState blockState, boolean bl) {
			BlockState blockState2 = this.method_41459(blockState, worldAccess, arg.pos(), arg.face());
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

	public static record class_7121(BlockPos pos, Direction face) {
	}

	@FunctionalInterface
	public interface class_7122 {
		boolean test(BlockView blockView, BlockPos blockPos, class_7118.class_7121 arg);
	}

	public static enum class_7123 {
		SAME_POSITION {
			@Override
			public class_7118.class_7121 method_41466(BlockPos blockPos, Direction direction, Direction direction2) {
				return new class_7118.class_7121(blockPos, direction);
			}
		},
		SAME_PLANE {
			@Override
			public class_7118.class_7121 method_41466(BlockPos blockPos, Direction direction, Direction direction2) {
				return new class_7118.class_7121(blockPos.offset(direction), direction2);
			}
		},
		WRAP_AROUND {
			@Override
			public class_7118.class_7121 method_41466(BlockPos blockPos, Direction direction, Direction direction2) {
				return new class_7118.class_7121(blockPos.offset(direction).offset(direction2), direction.getOpposite());
			}
		};

		public abstract class_7118.class_7121 method_41466(BlockPos blockPos, Direction direction, Direction direction2);
	}
}
