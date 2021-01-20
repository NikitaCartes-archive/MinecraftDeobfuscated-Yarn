package net.minecraft.world.gen.feature.util;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TestableWorld;

public abstract class DripstoneColumn {
	public static DripstoneColumn.Bounded createBounded(int floor, int ceiling) {
		return new DripstoneColumn.Bounded(floor, ceiling);
	}

	public static DripstoneColumn createHalfWithCeiling(int ceiling) {
		return new DripstoneColumn.Half(ceiling, false);
	}

	public static DripstoneColumn createHalfWithFloor(int floor) {
		return new DripstoneColumn.Half(floor, true);
	}

	public static DripstoneColumn createEmpty() {
		return DripstoneColumn.Empty.INSTANCE;
	}

	public static DripstoneColumn create(OptionalInt ceilingHeight, OptionalInt floorHeight) {
		if (ceilingHeight.isPresent() && floorHeight.isPresent()) {
			return createBounded(ceilingHeight.getAsInt(), floorHeight.getAsInt());
		} else if (ceilingHeight.isPresent()) {
			return createHalfWithFloor(ceilingHeight.getAsInt());
		} else {
			return floorHeight.isPresent() ? createHalfWithCeiling(floorHeight.getAsInt()) : createEmpty();
		}
	}

	public abstract OptionalInt getCeilingHeight();

	public abstract OptionalInt getFloorHeight();

	public abstract OptionalInt getOptionalHeight();

	public DripstoneColumn withFloor(OptionalInt floor) {
		return create(floor, this.getCeilingHeight());
	}

	public static Optional<DripstoneColumn> create(
		TestableWorld world, BlockPos pos, int height, Predicate<BlockState> canGenerate, Predicate<BlockState> canReplace
	) {
		BlockPos.Mutable mutable = pos.mutableCopy();
		if (!world.testBlockState(pos, canGenerate)) {
			return Optional.empty();
		} else {
			int i = pos.getY();
			mutable.setY(i);

			for (int j = 1; j < height && world.testBlockState(mutable, canGenerate); j++) {
				mutable.move(Direction.UP);
			}

			OptionalInt optionalInt = world.testBlockState(mutable, canReplace) ? OptionalInt.of(mutable.getY()) : OptionalInt.empty();
			mutable.setY(i);

			for (int k = 1; k < height && world.testBlockState(mutable, canGenerate); k++) {
				mutable.move(Direction.DOWN);
			}

			OptionalInt optionalInt2 = world.testBlockState(mutable, canReplace) ? OptionalInt.of(mutable.getY()) : OptionalInt.empty();
			return Optional.of(create(optionalInt2, optionalInt));
		}
	}

	public static final class Bounded extends DripstoneColumn {
		private final int floor;
		private final int ceiling;

		protected Bounded(int floor, int ceiling) {
			this.floor = floor;
			this.ceiling = ceiling;
			if (this.getHeight() < 0) {
				throw new IllegalArgumentException("Column of negative height: " + this);
			}
		}

		@Override
		public OptionalInt getCeilingHeight() {
			return OptionalInt.of(this.ceiling);
		}

		@Override
		public OptionalInt getFloorHeight() {
			return OptionalInt.of(this.floor);
		}

		@Override
		public OptionalInt getOptionalHeight() {
			return OptionalInt.of(this.getHeight());
		}

		public int getCeiling() {
			return this.ceiling;
		}

		public int getFloor() {
			return this.floor;
		}

		public int getHeight() {
			return this.ceiling - this.floor - 1;
		}

		public String toString() {
			return "C(" + this.ceiling + "-" + this.floor + ')';
		}
	}

	public static final class Empty extends DripstoneColumn {
		private static final DripstoneColumn.Empty INSTANCE = new DripstoneColumn.Empty();

		private Empty() {
		}

		@Override
		public OptionalInt getCeilingHeight() {
			return OptionalInt.empty();
		}

		@Override
		public OptionalInt getFloorHeight() {
			return OptionalInt.empty();
		}

		@Override
		public OptionalInt getOptionalHeight() {
			return OptionalInt.empty();
		}

		public String toString() {
			return "C(-)";
		}
	}

	public static final class Half extends DripstoneColumn {
		private final int height;
		private final boolean floor;

		public Half(int height, boolean floor) {
			this.height = height;
			this.floor = floor;
		}

		@Override
		public OptionalInt getCeilingHeight() {
			return this.floor ? OptionalInt.empty() : OptionalInt.of(this.height);
		}

		@Override
		public OptionalInt getFloorHeight() {
			return this.floor ? OptionalInt.of(this.height) : OptionalInt.empty();
		}

		@Override
		public OptionalInt getOptionalHeight() {
			return OptionalInt.empty();
		}

		public String toString() {
			return this.floor ? "C(" + this.height + "-)" : "C(-" + this.height + ")";
		}
	}
}
