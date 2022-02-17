package net.minecraft;

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

public class class_6982 {
	public static final class_6982.class_6987[] field_36837 = new class_6982.class_6987[]{
		class_6982.class_6987.SAME_POSITION, class_6982.class_6987.SAME_PLANE, class_6982.class_6987.WRAP_AROUND
	};
	private final class_6982.class_6984 field_36838;

	public class_6982(AbstractLichenBlock abstractLichenBlock) {
		this(new class_6982.class_6983(abstractLichenBlock));
	}

	public class_6982(class_6982.class_6984 arg) {
		this.field_36838 = arg;
	}

	public boolean method_40755(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return Direction.stream()
			.anyMatch(direction2 -> this.method_40757(blockState, blockView, blockPos, direction, direction2, this.field_36838::method_40770).isPresent());
	}

	public Optional<class_6982.class_6985> method_40764(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Random random) {
		return (Optional<class_6982.class_6985>)Direction.shuffle(random)
			.stream()
			.filter(direction -> this.field_36838.method_40777(blockState, direction))
			.map(direction -> this.method_40761(blockState, worldAccess, blockPos, direction, random, false))
			.filter(Optional::isPresent)
			.findFirst()
			.orElse(Optional.empty());
	}

	public long method_40758(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		return (Long)Direction.stream()
			.filter(direction -> this.field_36838.method_40777(blockState, direction))
			.map(direction -> this.method_40762(blockState, worldAccess, blockPos, direction, false))
			.reduce(0L, Long::sum);
	}

	public Optional<class_6982.class_6985> method_40761(
		BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, Random random, boolean bl
	) {
		return (Optional<class_6982.class_6985>)Direction.shuffle(random)
			.stream()
			.map(direction2 -> this.method_40760(blockState, worldAccess, blockPos, direction, direction2, bl))
			.filter(Optional::isPresent)
			.findFirst()
			.orElse(Optional.empty());
	}

	public long method_40762(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, boolean bl) {
		return Direction.stream()
			.map(direction2 -> this.method_40760(blockState, worldAccess, blockPos, direction, direction2, bl))
			.filter(Optional::isPresent)
			.count();
	}

	public Optional<class_6982.class_6985> method_40760(
		BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, Direction direction2, boolean bl
	) {
		Optional<class_6982.class_6985> optional = this.method_40756(blockState, worldAccess, blockPos, direction, direction2);
		if (optional.isPresent()) {
			class_6982.class_6985 lv = (class_6982.class_6985)optional.get();
			return this.method_40754(worldAccess, lv.pos, lv.face, bl);
		} else {
			return Optional.empty();
		}
	}

	public Optional<class_6982.class_6985> method_40756(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction, Direction direction2) {
		return this.method_40757(blockState, blockView, blockPos, direction, direction2, this.field_36838::method_40770);
	}

	public Optional<class_6982.class_6985> method_40757(
		BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction, Direction direction2, class_6982.class_6986 arg
	) {
		if (direction2.getAxis() == direction.getAxis()) {
			return Optional.empty();
		} else if (this.field_36838.method_40775(blockState)
			|| this.field_36838.method_40776(blockState, direction) && !this.field_36838.method_40776(blockState, direction2)) {
			for (class_6982.class_6987 lv : this.field_36838.method_40773()) {
				BlockPos blockPos2 = lv.method_40779(blockPos, direction2, direction);
				Direction direction3 = lv.method_40780(direction2, direction);
				if (arg.test(blockView, blockPos, blockPos2, direction3)) {
					return Optional.of(new class_6982.class_6985(blockPos2, direction3));
				}
			}

			return Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	public Optional<class_6982.class_6985> method_40754(WorldAccess worldAccess, BlockPos blockPos, Direction direction, boolean bl) {
		BlockState blockState = worldAccess.getBlockState(blockPos);
		return this.field_36838.method_40774(worldAccess, blockPos, direction, blockState, bl)
			? Optional.of(new class_6982.class_6985(blockPos, direction))
			: Optional.empty();
	}

	public static class class_6983 implements class_6982.class_6984 {
		protected AbstractLichenBlock field_36839;

		public class_6983(AbstractLichenBlock abstractLichenBlock) {
			this.field_36839 = abstractLichenBlock;
		}

		@Nullable
		@Override
		public BlockState method_40772(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
			return this.field_36839.withDirection(blockState, blockView, blockPos, direction);
		}

		protected boolean method_40771(BlockView blockView, BlockPos blockPos, BlockPos blockPos2, Direction direction, BlockState blockState) {
			return blockState.isAir() || blockState.isOf(this.field_36839) || blockState.isOf(Blocks.WATER) && blockState.getFluidState().isStill();
		}

		@Override
		public boolean method_40770(BlockView blockView, BlockPos blockPos, BlockPos blockPos2, Direction direction) {
			BlockState blockState = blockView.getBlockState(blockPos2);
			return this.method_40771(blockView, blockPos, blockPos2, direction, blockState)
				&& this.field_36839.canGrowWithDirection(blockView, blockState, blockPos2, direction);
		}
	}

	public interface class_6984 {
		@Nullable
		BlockState method_40772(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction);

		boolean method_40770(BlockView blockView, BlockPos blockPos, BlockPos blockPos2, Direction direction);

		default class_6982.class_6987[] method_40773() {
			return class_6982.field_36837;
		}

		default boolean method_40776(BlockState blockState, Direction direction) {
			return AbstractLichenBlock.hasDirection(blockState, direction);
		}

		default boolean method_40775(BlockState blockState) {
			return false;
		}

		default boolean method_40777(BlockState blockState, Direction direction) {
			return this.method_40775(blockState) || this.method_40776(blockState, direction);
		}

		default boolean method_40774(WorldAccess worldAccess, BlockPos blockPos, Direction direction, BlockState blockState, boolean bl) {
			BlockState blockState2 = this.method_40772(blockState, worldAccess, blockPos, direction);
			if (blockState2 != null) {
				if (bl) {
					worldAccess.getChunk(blockPos).markBlockForPostProcessing(blockPos);
				}

				return worldAccess.setBlockState(blockPos, blockState2, Block.NOTIFY_LISTENERS);
			} else {
				return false;
			}
		}
	}

	public static record class_6985(BlockPos pos, Direction face) {
	}

	@FunctionalInterface
	public interface class_6986 {
		boolean test(BlockView blockView, BlockPos blockPos, BlockPos blockPos2, Direction direction);
	}

	public static enum class_6987 {
		SAME_POSITION {
			@Override
			public BlockPos method_40779(BlockPos blockPos, Direction direction, Direction direction2) {
				return blockPos;
			}

			@Override
			public Direction method_40780(Direction direction, Direction direction2) {
				return direction;
			}
		},
		SAME_PLANE {
			@Override
			public BlockPos method_40779(BlockPos blockPos, Direction direction, Direction direction2) {
				return blockPos.offset(direction);
			}

			@Override
			public Direction method_40780(Direction direction, Direction direction2) {
				return direction2;
			}
		},
		WRAP_AROUND {
			@Override
			public BlockPos method_40779(BlockPos blockPos, Direction direction, Direction direction2) {
				return blockPos.offset(direction).offset(direction2);
			}

			@Override
			public Direction method_40780(Direction direction, Direction direction2) {
				return direction.getOpposite();
			}
		};

		public abstract BlockPos method_40779(BlockPos blockPos, Direction direction, Direction direction2);

		public abstract Direction method_40780(Direction direction, Direction direction2);
	}
}
