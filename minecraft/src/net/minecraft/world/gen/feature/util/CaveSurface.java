package net.minecraft.world.gen.feature.util;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TestableWorld;

public abstract class CaveSurface {
	public static CaveSurface.Bounded method_35326(int i, int j) {
		return new CaveSurface.Bounded(i - 1, j + 1);
	}

	public static CaveSurface.Bounded createBounded(int floor, int ceiling) {
		return new CaveSurface.Bounded(floor, ceiling);
	}

	public static CaveSurface createHalfWithCeiling(int ceiling) {
		return new CaveSurface.Half(ceiling, false);
	}

	public static CaveSurface method_35327(int i) {
		return new CaveSurface.Half(i + 1, false);
	}

	public static CaveSurface createHalfWithFloor(int floor) {
		return new CaveSurface.Half(floor, true);
	}

	public static CaveSurface method_35329(int i) {
		return new CaveSurface.Half(i - 1, true);
	}

	public static CaveSurface createEmpty() {
		return CaveSurface.Empty.INSTANCE;
	}

	public static CaveSurface create(OptionalInt ceilingHeight, OptionalInt floorHeight) {
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

	public CaveSurface withFloor(OptionalInt floor) {
		return create(floor, this.getCeilingHeight());
	}

	public CaveSurface withCeiling(OptionalInt ceiling) {
		return create(this.getFloorHeight(), ceiling);
	}

	public static Optional<CaveSurface> create(TestableWorld world, BlockPos pos, int height, Predicate<BlockState> canGenerate, Predicate<BlockState> canReplace) {
		BlockPos.Mutable mutable = pos.mutableCopy();
		if (!world.testBlockState(pos, canGenerate)) {
			return Optional.empty();
		} else {
			int i = pos.getY();
			OptionalInt optionalInt = getCaveSurface(world, height, canGenerate, canReplace, mutable, i, Direction.UP);
			OptionalInt optionalInt2 = getCaveSurface(world, height, canGenerate, canReplace, mutable, i, Direction.DOWN);
			return Optional.of(create(optionalInt2, optionalInt));
		}
	}

	private static OptionalInt getCaveSurface(
		TestableWorld world, int height, Predicate<BlockState> canGenerate, Predicate<BlockState> canReplace, BlockPos.Mutable mutablePos, int y, Direction direction
	) {
		mutablePos.setY(y);

		for (int i = 1; i < height && world.testBlockState(mutablePos, canGenerate); i++) {
			mutablePos.move(direction);
		}

		return world.testBlockState(mutablePos, canReplace) ? OptionalInt.of(mutablePos.getY()) : OptionalInt.empty();
	}

	public static final class Bounded extends CaveSurface {
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
			return "C(" + this.ceiling + "-" + this.floor + ")";
		}
	}

	public static final class Empty extends CaveSurface {
		static final CaveSurface.Empty INSTANCE = new CaveSurface.Empty();

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

	public static final class Half extends CaveSurface {
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
