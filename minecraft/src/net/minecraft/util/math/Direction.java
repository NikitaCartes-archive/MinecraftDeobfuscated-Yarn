package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
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
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

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
	private static final Direction[] VALUES = (Direction[])Arrays.stream(ALL).sorted(Comparator.comparingInt(direction -> direction.id)).toArray(Direction[]::new);
	private static final Direction[] HORIZONTAL = (Direction[])Arrays.stream(ALL)
		.filter(direction -> direction.getAxis().isHorizontal())
		.sorted(Comparator.comparingInt(direction -> direction.idHorizontal))
		.toArray(Direction[]::new);
	private static final Long2ObjectMap<Direction> VECTOR_TO_DIRECTION = (Long2ObjectMap<Direction>)Arrays.stream(ALL)
		.collect(Collectors.toMap(direction -> new BlockPos(direction.getVector()).asLong(), direction -> direction, (direction, direction2) -> {
			throw new IllegalArgumentException("Duplicate keys");
		}, Long2ObjectOpenHashMap::new));

	private Direction(int id, int idOpposite, int idHorizontal, String name, Direction.AxisDirection direction, Direction.Axis axis, Vec3i vector) {
		this.id = id;
		this.idHorizontal = idHorizontal;
		this.idOpposite = idOpposite;
		this.name = name;
		this.axis = axis;
		this.direction = direction;
		this.vector = vector;
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
				return listClosest(direction2, direction, direction3);
			} else {
				return p > m ? listClosest(direction, direction3, direction2) : listClosest(direction, direction2, direction3);
			}
		} else if (m > p) {
			return listClosest(direction2, direction3, direction);
		} else {
			return o > m ? listClosest(direction3, direction, direction2) : listClosest(direction3, direction2, direction);
		}
	}

	/**
	 * Helper function that returns the 3 directions given, followed by the 3 opposite given in opposite order.
	 */
	private static Direction[] listClosest(Direction first, Direction second, Direction third) {
		return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
	}

	@Environment(EnvType.CLIENT)
	public static Direction transform(Matrix4f matrix, Direction direction) {
		Vec3i vec3i = direction.getVector();
		Vector4f vector4f = new Vector4f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ(), 0.0F);
		vector4f.transform(matrix);
		return getFacing(vector4f.getX(), vector4f.getY(), vector4f.getZ());
	}

	@Environment(EnvType.CLIENT)
	public Quaternion getRotationQuaternion() {
		Quaternion quaternion = Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F);
		switch (this) {
			case DOWN:
				return Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F);
			case UP:
				return Quaternion.IDENTITY.copy();
			case NORTH:
				quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
				return quaternion;
			case SOUTH:
				return quaternion;
			case WEST:
				quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
				return quaternion;
			case EAST:
			default:
				quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
				return quaternion;
		}
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

	public Direction rotateYClockwise() {
		switch (this) {
			case NORTH:
				return EAST;
			case SOUTH:
				return WEST;
			case WEST:
				return NORTH;
			case EAST:
				return SOUTH;
			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	public Direction rotateYCounterclockwise() {
		switch (this) {
			case NORTH:
				return WEST;
			case SOUTH:
				return EAST;
			case WEST:
				return SOUTH;
			case EAST:
				return NORTH;
			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	public int getOffsetX() {
		return this.vector.getX();
	}

	public int getOffsetY() {
		return this.vector.getY();
	}

	public int getOffsetZ() {
		return this.vector.getZ();
	}

	@Environment(EnvType.CLIENT)
	public Vector3f getUnitVector() {
		return new Vector3f((float)this.getOffsetX(), (float)this.getOffsetY(), (float)this.getOffsetZ());
	}

	public String getName() {
		return this.name;
	}

	public Direction.Axis getAxis() {
		return this.axis;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Direction byName(@Nullable String name) {
		return name == null ? null : (Direction)NAME_MAP.get(name.toLowerCase(Locale.ROOT));
	}

	public static Direction byId(int id) {
		return VALUES[MathHelper.abs(id % VALUES.length)];
	}

	public static Direction fromHorizontal(int value) {
		return HORIZONTAL[MathHelper.abs(value % HORIZONTAL.length)];
	}

	@Nullable
	public static Direction fromVector(int x, int y, int z) {
		return VECTOR_TO_DIRECTION.get(BlockPos.asLong(x, y, z));
	}

	public static Direction fromRotation(double rotation) {
		return fromHorizontal(MathHelper.floor(rotation / 90.0 + 0.5) & 3);
	}

	public static Direction from(Direction.Axis axis, Direction.AxisDirection direction) {
		switch (axis) {
			case X:
				return direction == Direction.AxisDirection.POSITIVE ? EAST : WEST;
			case Y:
				return direction == Direction.AxisDirection.POSITIVE ? UP : DOWN;
			case Z:
			default:
				return direction == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
		}
	}

	public float asRotation() {
		return (float)((this.idHorizontal & 3) * 90);
	}

	public static Direction random(Random random) {
		return Util.getRandom(ALL, random);
	}

	public static Direction getFacing(double x, double y, double z) {
		return getFacing((float)x, (float)y, (float)z);
	}

	public static Direction getFacing(float x, float y, float z) {
		Direction direction = NORTH;
		float f = Float.MIN_VALUE;

		for (Direction direction2 : ALL) {
			float g = x * (float)direction2.vector.getX() + y * (float)direction2.vector.getY() + z * (float)direction2.vector.getZ();
			if (g > f) {
				f = g;
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

	public static Direction get(Direction.AxisDirection direction, Direction.Axis axis) {
		for (Direction direction2 : ALL) {
			if (direction2.getDirection() == direction && direction2.getAxis() == axis) {
				return direction2;
			}
		}

		throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
	}

	public Vec3i getVector() {
		return this.vector;
	}

	public boolean method_30928(float f) {
		float g = f * (float) (Math.PI / 180.0);
		float h = -MathHelper.sin(g);
		float i = MathHelper.cos(g);
		return (float)this.vector.getX() * h + (float)this.vector.getZ() * i > 0.0F;
	}

	public static enum Axis implements StringIdentifiable, Predicate<Direction> {
		X("x") {
			@Override
			public int choose(int x, int y, int z) {
				return x;
			}

			@Override
			public double choose(double x, double y, double z) {
				return x;
			}
		},
		Y("y") {
			@Override
			public int choose(int x, int y, int z) {
				return y;
			}

			@Override
			public double choose(double x, double y, double z) {
				return y;
			}
		},
		Z("z") {
			@Override
			public int choose(int x, int y, int z) {
				return z;
			}

			@Override
			public double choose(double x, double y, double z) {
				return z;
			}
		};

		private static final Direction.Axis[] VALUES = values();
		public static final Codec<Direction.Axis> CODEC = StringIdentifiable.createCodec(Direction.Axis::values, Direction.Axis::fromName);
		private static final Map<String, Direction.Axis> BY_NAME = (Map<String, Direction.Axis>)Arrays.stream(VALUES)
			.collect(Collectors.toMap(Direction.Axis::getName, axis -> axis));
		private final String name;

		private Axis(String name) {
			this.name = name;
		}

		@Nullable
		public static Direction.Axis fromName(String name) {
			return (Direction.Axis)BY_NAME.get(name.toLowerCase(Locale.ROOT));
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

		public static Direction.Axis pickRandomAxis(Random random) {
			return Util.getRandom(VALUES, random);
		}

		public boolean test(@Nullable Direction direction) {
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

		public abstract int choose(int x, int y, int z);

		public abstract double choose(double x, double y, double z);
	}

	public static enum AxisDirection {
		POSITIVE(1, "Towards positive"),
		NEGATIVE(-1, "Towards negative");

		private final int offset;
		private final String description;

		private AxisDirection(int offset, String description) {
			this.offset = offset;
			this.description = description;
		}

		public int offset() {
			return this.offset;
		}

		public String toString() {
			return this.description;
		}

		public Direction.AxisDirection getOpposite() {
			return this == POSITIVE ? NEGATIVE : POSITIVE;
		}
	}

	public static enum Type implements Iterable<Direction>, Predicate<Direction> {
		HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
		VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

		private final Direction[] facingArray;
		private final Direction.Axis[] axisArray;

		private Type(Direction[] facingArray, Direction.Axis[] axisArray) {
			this.facingArray = facingArray;
			this.axisArray = axisArray;
		}

		public Direction random(Random random) {
			return Util.getRandom(this.facingArray, random);
		}

		public Direction.Axis randomAxis(Random random) {
			return Util.getRandom(this.axisArray, random);
		}

		public boolean test(@Nullable Direction direction) {
			return direction != null && direction.getAxis().getType() == this;
		}

		public Iterator<Direction> iterator() {
			return Iterators.forArray(this.facingArray);
		}

		public Stream<Direction> stream() {
			return Arrays.stream(this.facingArray);
		}
	}
}
