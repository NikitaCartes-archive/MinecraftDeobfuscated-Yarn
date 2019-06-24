package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;

public enum Direction implements StringIdentifiable {
	DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
	UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
	NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
	SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
	WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
	EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

	private final int id;
	private final int idOpposite;
	private final int idHorizontal;
	private final String name;
	private final Direction.Axis axis;
	private final Direction.AxisDirection direction;
	private final Vec3i vector;
	private static final Direction[] ALL = values();
	private static final Map<String, Direction> NAME_MAP = (Map<String, Direction>)Arrays.stream(ALL)
		.collect(Collectors.toMap(Direction::getName, direction -> direction));
	private static final Direction[] ID_TO_DIRECTION = (Direction[])Arrays.stream(ALL)
		.sorted(Comparator.comparingInt(direction -> direction.id))
		.toArray(Direction[]::new);
	private static final Direction[] HORIZONTAL = (Direction[])Arrays.stream(ALL)
		.filter(direction -> direction.getAxis().isHorizontal())
		.sorted(Comparator.comparingInt(direction -> direction.idHorizontal))
		.toArray(Direction[]::new);
	private static final Long2ObjectMap<Direction> VECTOR_TO_DIRECTION = (Long2ObjectMap<Direction>)Arrays.stream(ALL)
		.collect(Collectors.toMap(direction -> new BlockPos(direction.getVector()).asLong(), direction -> direction, (direction, direction2) -> {
			throw new IllegalArgumentException("Duplicate keys");
		}, Long2ObjectOpenHashMap::new));

	private Direction(int j, int k, int l, String string2, Direction.AxisDirection axisDirection, Direction.Axis axis, Vec3i vec3i) {
		this.id = j;
		this.idHorizontal = l;
		this.idOpposite = k;
		this.name = string2;
		this.axis = axis;
		this.direction = axisDirection;
		this.vector = vec3i;
	}

	public static Direction[] getEntityFacingOrder(Entity entity) {
		float f = entity.getPitch(1.0F) * (float) (Math.PI / 180.0);
		float g = -entity.getYaw(1.0F) * (float) (Math.PI / 180.0);
		float h = MathHelper.sin(f);
		float i = MathHelper.cos(f);
		float j = MathHelper.sin(g);
		float k = MathHelper.cos(g);
		boolean bl = j > 0.0F;
		boolean bl2 = h < 0.0F;
		boolean bl3 = k > 0.0F;
		float l = bl ? j : -j;
		float m = bl2 ? -h : h;
		float n = bl3 ? k : -k;
		float o = l * i;
		float p = n * i;
		Direction direction = bl ? EAST : WEST;
		Direction direction2 = bl2 ? UP : DOWN;
		Direction direction3 = bl3 ? SOUTH : NORTH;
		if (l > n) {
			if (m > o) {
				return method_10145(direction2, direction, direction3);
			} else {
				return p > m ? method_10145(direction, direction3, direction2) : method_10145(direction, direction2, direction3);
			}
		} else if (m > p) {
			return method_10145(direction2, direction3, direction);
		} else {
			return o > m ? method_10145(direction3, direction, direction2) : method_10145(direction3, direction2, direction);
		}
	}

	private static Direction[] method_10145(Direction direction, Direction direction2, Direction direction3) {
		return new Direction[]{direction, direction2, direction3, direction3.getOpposite(), direction2.getOpposite(), direction.getOpposite()};
	}

	public int getId() {
		return this.id;
	}

	public int getHorizontal() {
		return this.idHorizontal;
	}

	public Direction.AxisDirection getDirection() {
		return this.direction;
	}

	public Direction getOpposite() {
		return byId(this.idOpposite);
	}

	@Environment(EnvType.CLIENT)
	public Direction rotateClockwise(Direction.Axis axis) {
		switch (axis) {
			case X:
				if (this != WEST && this != EAST) {
					return this.rotateXClockwise();
				}

				return this;
			case Y:
				if (this != UP && this != DOWN) {
					return this.rotateYClockwise();
				}

				return this;
			case Z:
				if (this != NORTH && this != SOUTH) {
					return this.rotateZClockwise();
				}

				return this;
			default:
				throw new IllegalStateException("Unable to get CW facing for axis " + axis);
		}
	}

	public Direction rotateYClockwise() {
		switch (this) {
			case NORTH:
				return EAST;
			case EAST:
				return SOUTH;
			case SOUTH:
				return WEST;
			case WEST:
				return NORTH;
			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	@Environment(EnvType.CLIENT)
	private Direction rotateXClockwise() {
		switch (this) {
			case NORTH:
				return DOWN;
			case EAST:
			case WEST:
			default:
				throw new IllegalStateException("Unable to get X-rotated facing of " + this);
			case SOUTH:
				return UP;
			case UP:
				return NORTH;
			case DOWN:
				return SOUTH;
		}
	}

	@Environment(EnvType.CLIENT)
	private Direction rotateZClockwise() {
		switch (this) {
			case EAST:
				return DOWN;
			case SOUTH:
			default:
				throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
			case WEST:
				return UP;
			case UP:
				return EAST;
			case DOWN:
				return WEST;
		}
	}

	public Direction rotateYCounterclockwise() {
		switch (this) {
			case NORTH:
				return WEST;
			case EAST:
				return NORTH;
			case SOUTH:
				return EAST;
			case WEST:
				return SOUTH;
			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	public int getOffsetX() {
		return this.axis == Direction.Axis.X ? this.direction.offset() : 0;
	}

	public int getOffsetY() {
		return this.axis == Direction.Axis.Y ? this.direction.offset() : 0;
	}

	public int getOffsetZ() {
		return this.axis == Direction.Axis.Z ? this.direction.offset() : 0;
	}

	public String getName() {
		return this.name;
	}

	public Direction.Axis getAxis() {
		return this.axis;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Direction byName(@Nullable String string) {
		return string == null ? null : (Direction)NAME_MAP.get(string.toLowerCase(Locale.ROOT));
	}

	public static Direction byId(int i) {
		return ID_TO_DIRECTION[MathHelper.abs(i % ID_TO_DIRECTION.length)];
	}

	public static Direction fromHorizontal(int i) {
		return HORIZONTAL[MathHelper.abs(i % HORIZONTAL.length)];
	}

	@Nullable
	public static Direction fromVector(int i, int j, int k) {
		return VECTOR_TO_DIRECTION.get(BlockPos.asLong(i, j, k));
	}

	public static Direction fromRotation(double d) {
		return fromHorizontal(MathHelper.floor(d / 90.0 + 0.5) & 3);
	}

	public static Direction from(Direction.Axis axis, Direction.AxisDirection axisDirection) {
		switch (axis) {
			case X:
				return axisDirection == Direction.AxisDirection.POSITIVE ? EAST : WEST;
			case Y:
				return axisDirection == Direction.AxisDirection.POSITIVE ? UP : DOWN;
			case Z:
			default:
				return axisDirection == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
		}
	}

	public float asRotation() {
		return (float)((this.idHorizontal & 3) * 90);
	}

	public static Direction random(Random random) {
		return values()[random.nextInt(values().length)];
	}

	public static Direction getFacing(double d, double e, double f) {
		return getFacing((float)d, (float)e, (float)f);
	}

	public static Direction getFacing(float f, float g, float h) {
		Direction direction = NORTH;
		float i = Float.MIN_VALUE;

		for (Direction direction2 : ALL) {
			float j = f * (float)direction2.vector.getX() + g * (float)direction2.vector.getY() + h * (float)direction2.vector.getZ();
			if (j > i) {
				i = j;
				direction = direction2;
			}
		}

		return direction;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public static Direction get(Direction.AxisDirection axisDirection, Direction.Axis axis) {
		for (Direction direction : values()) {
			if (direction.getDirection() == axisDirection && direction.getAxis() == axis) {
				return direction;
			}
		}

		throw new IllegalArgumentException("No such direction: " + axisDirection + " " + axis);
	}

	public Vec3i getVector() {
		return this.vector;
	}

	public static enum Axis implements StringIdentifiable, Predicate<Direction> {
		X("x") {
			@Override
			public int choose(int i, int j, int k) {
				return i;
			}

			@Override
			public double choose(double d, double e, double f) {
				return d;
			}
		},
		Y("y") {
			@Override
			public int choose(int i, int j, int k) {
				return j;
			}

			@Override
			public double choose(double d, double e, double f) {
				return e;
			}
		},
		Z("z") {
			@Override
			public int choose(int i, int j, int k) {
				return k;
			}

			@Override
			public double choose(double d, double e, double f) {
				return f;
			}
		};

		private static final Map<String, Direction.Axis> BY_NAME = (Map<String, Direction.Axis>)Arrays.stream(values())
			.collect(Collectors.toMap(Direction.Axis::getName, axis -> axis));
		private final String name;

		private Axis(String string2) {
			this.name = string2;
		}

		@Nullable
		@Environment(EnvType.CLIENT)
		public static Direction.Axis fromName(String string) {
			return (Direction.Axis)BY_NAME.get(string.toLowerCase(Locale.ROOT));
		}

		public String getName() {
			return this.name;
		}

		public boolean isVertical() {
			return this == Y;
		}

		public boolean isHorizontal() {
			return this == X || this == Z;
		}

		public String toString() {
			return this.name;
		}

		public static Direction.Axis method_16699(Random random) {
			return values()[random.nextInt(values().length)];
		}

		public boolean method_10176(@Nullable Direction direction) {
			return direction != null && direction.getAxis() == this;
		}

		public Direction.Type getType() {
			switch (this) {
				case X:
				case Z:
					return Direction.Type.HORIZONTAL;
				case Y:
					return Direction.Type.VERTICAL;
				default:
					throw new Error("Someone's been tampering with the universe!");
			}
		}

		@Override
		public String asString() {
			return this.name;
		}

		public abstract int choose(int i, int j, int k);

		public abstract double choose(double d, double e, double f);
	}

	public static enum AxisDirection {
		POSITIVE(1, "Towards positive"),
		NEGATIVE(-1, "Towards negative");

		private final int offset;
		private final String desc;

		private AxisDirection(int j, String string2) {
			this.offset = j;
			this.desc = string2;
		}

		public int offset() {
			return this.offset;
		}

		public String toString() {
			return this.desc;
		}
	}

	public static enum Type implements Iterable<Direction>, Predicate<Direction> {
		HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
		VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

		private final Direction[] facingArray;
		private final Direction.Axis[] axisArray;

		private Type(Direction[] directions, Direction.Axis[] axiss) {
			this.facingArray = directions;
			this.axisArray = axiss;
		}

		public Direction random(Random random) {
			return this.facingArray[random.nextInt(this.facingArray.length)];
		}

		public boolean method_10182(@Nullable Direction direction) {
			return direction != null && direction.getAxis().getType() == this;
		}

		public Iterator<Direction> iterator() {
			return Iterators.forArray(this.facingArray);
		}
	}
}
