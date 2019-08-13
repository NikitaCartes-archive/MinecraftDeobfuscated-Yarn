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
	field_11033(0, 1, -1, "down", Direction.AxisDirection.field_11060, Direction.Axis.field_11052, new Vec3i(0, -1, 0)),
	field_11036(1, 0, -1, "up", Direction.AxisDirection.field_11056, Direction.Axis.field_11052, new Vec3i(0, 1, 0)),
	field_11043(2, 3, 2, "north", Direction.AxisDirection.field_11060, Direction.Axis.field_11051, new Vec3i(0, 0, -1)),
	field_11035(3, 2, 0, "south", Direction.AxisDirection.field_11056, Direction.Axis.field_11051, new Vec3i(0, 0, 1)),
	field_11039(4, 5, 1, "west", Direction.AxisDirection.field_11060, Direction.Axis.field_11048, new Vec3i(-1, 0, 0)),
	field_11034(5, 4, 3, "east", Direction.AxisDirection.field_11056, Direction.Axis.field_11048, new Vec3i(1, 0, 0));

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
		Direction direction = bl ? field_11034 : field_11039;
		Direction direction2 = bl2 ? field_11036 : field_11033;
		Direction direction3 = bl3 ? field_11035 : field_11043;
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
			case field_11048:
				if (this != field_11039 && this != field_11034) {
					return this.rotateXClockwise();
				}

				return this;
			case field_11052:
				if (this != field_11036 && this != field_11033) {
					return this.rotateYClockwise();
				}

				return this;
			case field_11051:
				if (this != field_11043 && this != field_11035) {
					return this.rotateZClockwise();
				}

				return this;
			default:
				throw new IllegalStateException("Unable to get CW facing for axis " + axis);
		}
	}

	public Direction rotateYClockwise() {
		switch (this) {
			case field_11043:
				return field_11034;
			case field_11034:
				return field_11035;
			case field_11035:
				return field_11039;
			case field_11039:
				return field_11043;
			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	@Environment(EnvType.CLIENT)
	private Direction rotateXClockwise() {
		switch (this) {
			case field_11043:
				return field_11033;
			case field_11034:
			case field_11039:
			default:
				throw new IllegalStateException("Unable to get X-rotated facing of " + this);
			case field_11035:
				return field_11036;
			case field_11036:
				return field_11043;
			case field_11033:
				return field_11035;
		}
	}

	@Environment(EnvType.CLIENT)
	private Direction rotateZClockwise() {
		switch (this) {
			case field_11034:
				return field_11033;
			case field_11035:
			default:
				throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
			case field_11039:
				return field_11036;
			case field_11036:
				return field_11034;
			case field_11033:
				return field_11039;
		}
	}

	public Direction rotateYCounterclockwise() {
		switch (this) {
			case field_11043:
				return field_11039;
			case field_11034:
				return field_11043;
			case field_11035:
				return field_11034;
			case field_11039:
				return field_11035;
			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	public int getOffsetX() {
		return this.axis == Direction.Axis.field_11048 ? this.direction.offset() : 0;
	}

	public int getOffsetY() {
		return this.axis == Direction.Axis.field_11052 ? this.direction.offset() : 0;
	}

	public int getOffsetZ() {
		return this.axis == Direction.Axis.field_11051 ? this.direction.offset() : 0;
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
			case field_11048:
				return axisDirection == Direction.AxisDirection.field_11056 ? field_11034 : field_11039;
			case field_11052:
				return axisDirection == Direction.AxisDirection.field_11056 ? field_11036 : field_11033;
			case field_11051:
			default:
				return axisDirection == Direction.AxisDirection.field_11056 ? field_11035 : field_11043;
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
		Direction direction = field_11043;
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
		field_11048("x") {
			@Override
			public int choose(int i, int j, int k) {
				return i;
			}

			@Override
			public double choose(double d, double e, double f) {
				return d;
			}
		},
		field_11052("y") {
			@Override
			public int choose(int i, int j, int k) {
				return j;
			}

			@Override
			public double choose(double d, double e, double f) {
				return e;
			}
		},
		field_11051("z") {
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
			return this == field_11052;
		}

		public boolean isHorizontal() {
			return this == field_11048 || this == field_11051;
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
				case field_11048:
				case field_11051:
					return Direction.Type.field_11062;
				case field_11052:
					return Direction.Type.field_11064;
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
		field_11056(1, "Towards positive"),
		field_11060(-1, "Towards negative");

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
		field_11062(
			new Direction[]{Direction.field_11043, Direction.field_11034, Direction.field_11035, Direction.field_11039},
			new Direction.Axis[]{Direction.Axis.field_11048, Direction.Axis.field_11051}
		),
		field_11064(new Direction[]{Direction.field_11036, Direction.field_11033}, new Direction.Axis[]{Direction.Axis.field_11052});

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
