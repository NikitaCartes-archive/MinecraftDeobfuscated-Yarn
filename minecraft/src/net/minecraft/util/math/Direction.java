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

	@Environment(EnvType.CLIENT)
	public static Direction transform(Matrix4f matrix4f, Direction direction) {
		Vec3i vec3i = direction.getVector();
		Vector4f vector4f = new Vector4f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ(), 0.0F);
		vector4f.transform(matrix4f);
		return getFacing(vector4f.getX(), vector4f.getY(), vector4f.getZ());
	}

	@Environment(EnvType.CLIENT)
	public Quaternion getRotationQuaternion() {
		Quaternion quaternion = Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F);
		switch (this) {
			case field_11033:
				return Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F);
			case field_11036:
				return Quaternion.IDENTITY.copy();
			case field_11043:
				quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
				return quaternion;
			case field_11035:
				return quaternion;
			case field_11039:
				quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
				return quaternion;
			case field_11034:
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
			case field_11043:
				return field_11034;
			case field_11035:
				return field_11039;
			case field_11039:
				return field_11043;
			case field_11034:
				return field_11035;
			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	public Direction rotateYCounterclockwise() {
		switch (this) {
			case field_11043:
				return field_11039;
			case field_11035:
				return field_11034;
			case field_11039:
				return field_11035;
			case field_11034:
				return field_11043;
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
			case field_11048:
				return direction == Direction.AxisDirection.field_11056 ? field_11034 : field_11039;
			case field_11052:
				return direction == Direction.AxisDirection.field_11056 ? field_11036 : field_11033;
			case field_11051:
			default:
				return direction == Direction.AxisDirection.field_11056 ? field_11035 : field_11043;
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
		Direction direction = field_11043;
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
		field_11048("x") {
			@Override
			public int choose(int x, int y, int z) {
				return x;
			}

			@Override
			public double choose(double x, double y, double z) {
				return x;
			}
		},
		field_11052("y") {
			@Override
			public int choose(int x, int y, int z) {
				return y;
			}

			@Override
			public double choose(double x, double y, double z) {
				return y;
			}
		},
		field_11051("z") {
			@Override
			public int choose(int x, int y, int z) {
				return z;
			}

			@Override
			public double choose(double x, double y, double z) {
				return z;
			}
		};

		private static final Direction.Axis[] field_23780 = values();
		public static final Codec<Direction.Axis> field_25065 = StringIdentifiable.createCodec(Direction.Axis::values, Direction.Axis::fromName);
		private static final Map<String, Direction.Axis> BY_NAME = (Map<String, Direction.Axis>)Arrays.stream(field_23780)
			.collect(Collectors.toMap(Direction.Axis::getName, axis -> axis));
		private final String name;

		private Axis(String string2) {
			this.name = string2;
		}

		@Nullable
		public static Direction.Axis fromName(String name) {
			return (Direction.Axis)BY_NAME.get(name.toLowerCase(Locale.ROOT));
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

		public static Direction.Axis pickRandomAxis(Random random) {
			return Util.getRandom(field_23780, random);
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

		public abstract int choose(int x, int y, int z);

		public abstract double choose(double x, double y, double z);
	}

	public static enum AxisDirection {
		field_11056(1, "Towards positive"),
		field_11060(-1, "Towards negative");

		private final int offset;
		private final String desc;

		private AxisDirection(int offset, String description) {
			this.offset = offset;
			this.desc = description;
		}

		public int offset() {
			return this.offset;
		}

		public String toString() {
			return this.desc;
		}

		public Direction.AxisDirection getOpposite() {
			return this == field_11056 ? field_11060 : field_11056;
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
			return Util.getRandom(this.facingArray, random);
		}

		public boolean method_10182(@Nullable Direction direction) {
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
