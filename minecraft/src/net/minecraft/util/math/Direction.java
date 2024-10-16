package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * An enum representing 6 cardinal directions in Minecraft.
 * 
 * <p>In Minecraft, the X axis determines the east-west direction, the Y axis determines
 * the up-down direction, and the Z axis determines the south-north direction (note
 * that positive-Z direction is south, not north).
 */
public enum Direction implements StringIdentifiable {
	DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
	UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
	NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
	SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
	WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
	EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

	public static final StringIdentifiable.EnumCodec<Direction> CODEC = StringIdentifiable.createCodec(Direction::values);
	public static final Codec<Direction> VERTICAL_CODEC = CODEC.validate(Direction::validateVertical);
	public static final IntFunction<Direction> ID_TO_VALUE_FUNCTION = ValueLists.createIdToValueFunction(
		Direction::getId, values(), ValueLists.OutOfBoundsHandling.WRAP
	);
	public static final PacketCodec<ByteBuf, Direction> PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE_FUNCTION, Direction::getId);
	private final int id;
	private final int idOpposite;
	private final int idHorizontal;
	private final String name;
	private final Direction.Axis axis;
	private final Direction.AxisDirection direction;
	private final Vec3i vec3i;
	private final Vec3d doubleVector;
	private static final Direction[] ALL = values();
	private static final Direction[] VALUES = (Direction[])Arrays.stream(ALL).sorted(Comparator.comparingInt(direction -> direction.id)).toArray(Direction[]::new);
	private static final Direction[] HORIZONTAL = (Direction[])Arrays.stream(ALL)
		.filter(direction -> direction.getAxis().isHorizontal())
		.sorted(Comparator.comparingInt(direction -> direction.idHorizontal))
		.toArray(Direction[]::new);

	private Direction(
		final int id,
		final int idOpposite,
		final int idHorizontal,
		final String name,
		final Direction.AxisDirection direction,
		final Direction.Axis axis,
		final Vec3i vector
	) {
		this.id = id;
		this.idHorizontal = idHorizontal;
		this.idOpposite = idOpposite;
		this.name = name;
		this.axis = axis;
		this.direction = direction;
		this.vec3i = vector;
		this.doubleVector = Vec3d.of(vector);
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

	public static Direction transform(Matrix4f matrix, Direction direction) {
		Vec3i vec3i = direction.getVector();
		Vector4f vector4f = matrix.transform(new Vector4f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ(), 0.0F));
		return getFacing(vector4f.x(), vector4f.y(), vector4f.z());
	}

	/**
	 * {@return a shuffled collection of all directions}
	 */
	public static Collection<Direction> shuffle(Random random) {
		return Util.<Direction>copyShuffled(values(), random);
	}

	public static Stream<Direction> stream() {
		return Stream.of(ALL);
	}

	public static float getHorizontalDegrees(Direction direction) {
		return switch (direction) {
			case NORTH -> 180.0F;
			case SOUTH -> 0.0F;
			case WEST -> 90.0F;
			case EAST -> -90.0F;
			default -> throw new IllegalStateException("No y-Rot for vertical axis: " + direction);
		};
	}

	public Quaternionf getRotationQuaternion() {
		return switch (this) {
			case DOWN -> new Quaternionf().rotationX((float) Math.PI);
			case UP -> new Quaternionf();
			case NORTH -> new Quaternionf().rotationXYZ((float) (Math.PI / 2), 0.0F, (float) Math.PI);
			case SOUTH -> new Quaternionf().rotationX((float) (Math.PI / 2));
			case WEST -> new Quaternionf().rotationXYZ((float) (Math.PI / 2), 0.0F, (float) (Math.PI / 2));
			case EAST -> new Quaternionf().rotationXYZ((float) (Math.PI / 2), 0.0F, (float) (-Math.PI / 2));
		};
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

	public static Direction getLookDirectionForAxis(Entity entity, Direction.Axis axis) {
		return switch (axis) {
			case X -> EAST.pointsTo(entity.getYaw(1.0F)) ? EAST : WEST;
			case Y -> entity.getPitch(1.0F) < 0.0F ? UP : DOWN;
			case Z -> SOUTH.pointsTo(entity.getYaw(1.0F)) ? SOUTH : NORTH;
		};
	}

	public Direction getOpposite() {
		return byId(this.idOpposite);
	}

	public Direction rotateClockwise(Direction.Axis axis) {
		return switch (axis) {
			case X -> this != WEST && this != EAST ? this.rotateXClockwise() : this;
			case Y -> this != UP && this != DOWN ? this.rotateYClockwise() : this;
			case Z -> this != NORTH && this != SOUTH ? this.rotateZClockwise() : this;
		};
	}

	public Direction rotateCounterclockwise(Direction.Axis axis) {
		return switch (axis) {
			case X -> this != WEST && this != EAST ? this.rotateXCounterclockwise() : this;
			case Y -> this != UP && this != DOWN ? this.rotateYCounterclockwise() : this;
			case Z -> this != NORTH && this != SOUTH ? this.rotateZCounterclockwise() : this;
		};
	}

	public Direction rotateYClockwise() {
		return switch (this) {
			case NORTH -> EAST;
			case SOUTH -> WEST;
			case WEST -> NORTH;
			case EAST -> SOUTH;
			default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		};
	}

	private Direction rotateXClockwise() {
		return switch (this) {
			case DOWN -> SOUTH;
			case UP -> NORTH;
			case NORTH -> DOWN;
			case SOUTH -> UP;
			default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
		};
	}

	private Direction rotateXCounterclockwise() {
		return switch (this) {
			case DOWN -> NORTH;
			case UP -> SOUTH;
			case NORTH -> UP;
			case SOUTH -> DOWN;
			default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
		};
	}

	private Direction rotateZClockwise() {
		return switch (this) {
			case DOWN -> WEST;
			case UP -> EAST;
			default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
			case WEST -> UP;
			case EAST -> DOWN;
		};
	}

	private Direction rotateZCounterclockwise() {
		return switch (this) {
			case DOWN -> EAST;
			case UP -> WEST;
			default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
			case WEST -> DOWN;
			case EAST -> UP;
		};
	}

	public Direction rotateYCounterclockwise() {
		return switch (this) {
			case NORTH -> WEST;
			case SOUTH -> EAST;
			case WEST -> SOUTH;
			case EAST -> NORTH;
			default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
		};
	}

	public int getOffsetX() {
		return this.vec3i.getX();
	}

	public int getOffsetY() {
		return this.vec3i.getY();
	}

	public int getOffsetZ() {
		return this.vec3i.getZ();
	}

	public Vector3f getUnitVector() {
		return new Vector3f((float)this.getOffsetX(), (float)this.getOffsetY(), (float)this.getOffsetZ());
	}

	public String getName() {
		return this.name;
	}

	public Direction.Axis getAxis() {
		return this.axis;
	}

	/**
	 * {@return a direction with the given {@code name}, or {@code null} if there is
	 * no such direction}
	 */
	@Nullable
	public static Direction byName(@Nullable String name) {
		return (Direction)CODEC.byId(name);
	}

	public static Direction byId(int id) {
		return VALUES[MathHelper.abs(id % VALUES.length)];
	}

	public static Direction fromHorizontal(int value) {
		return HORIZONTAL[MathHelper.abs(value % HORIZONTAL.length)];
	}

	public static Direction fromRotation(double rotation) {
		return fromHorizontal(MathHelper.floor(rotation / 90.0 + 0.5) & 3);
	}

	public static Direction from(Direction.Axis axis, Direction.AxisDirection direction) {
		return switch (axis) {
			case X -> direction == Direction.AxisDirection.POSITIVE ? EAST : WEST;
			case Y -> direction == Direction.AxisDirection.POSITIVE ? UP : DOWN;
			case Z -> direction == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
		};
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
			float g = x * (float)direction2.vec3i.getX() + y * (float)direction2.vec3i.getY() + z * (float)direction2.vec3i.getZ();
			if (g > f) {
				f = g;
				direction = direction2;
			}
		}

		return direction;
	}

	public static Direction getFacing(Vec3d vec) {
		return getFacing(vec.x, vec.y, vec.z);
	}

	/**
	 * {@return the closest direction of the vector, or {@code fallback} for zero vector}
	 */
	@Nullable
	@Contract("_,_,_,!null->!null;_,_,_,_->_")
	public static Direction fromVector(int x, int y, int z, @Nullable Direction fallback) {
		int i = Math.abs(x);
		int j = Math.abs(y);
		int k = Math.abs(z);
		if (i > k && i > j) {
			return x < 0 ? WEST : EAST;
		} else if (k > i && k > j) {
			return z < 0 ? NORTH : SOUTH;
		} else if (j > i && j > k) {
			return y < 0 ? DOWN : UP;
		} else {
			return fallback;
		}
	}

	@Nullable
	@Contract("_,!null->!null;_,_->_")
	public static Direction fromVector(Vec3i vec, @Nullable Direction fallback) {
		return fromVector(vec.getX(), vec.getY(), vec.getZ(), fallback);
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}

	private static DataResult<Direction> validateVertical(Direction direction) {
		return direction.getAxis().isVertical() ? DataResult.success(direction) : DataResult.error(() -> "Expected a vertical direction");
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
		return this.vec3i;
	}

	public Vec3d getDoubleVector() {
		return this.doubleVector;
	}

	/**
	 * {@return whether the given yaw points to the direction}
	 * 
	 * @implNote This returns whether the yaw can make an acute angle with the direction.
	 * 
	 * <p>This always returns {@code false} for vertical directions.
	 */
	public boolean pointsTo(float yaw) {
		float f = yaw * (float) (Math.PI / 180.0);
		float g = -MathHelper.sin(f);
		float h = MathHelper.cos(f);
		return (float)this.vec3i.getX() * g + (float)this.vec3i.getZ() * h > 0.0F;
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

			@Override
			public Direction getPositiveDirection() {
				return Direction.EAST;
			}

			@Override
			public Direction getNegativeDirection() {
				return Direction.WEST;
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

			@Override
			public Direction getPositiveDirection() {
				return Direction.UP;
			}

			@Override
			public Direction getNegativeDirection() {
				return Direction.DOWN;
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

			@Override
			public Direction getPositiveDirection() {
				return Direction.SOUTH;
			}

			@Override
			public Direction getNegativeDirection() {
				return Direction.NORTH;
			}
		};

		public static final Direction.Axis[] VALUES = values();
		public static final StringIdentifiable.EnumCodec<Direction.Axis> CODEC = StringIdentifiable.createCodec(Direction.Axis::values);
		private final String name;

		Axis(final String name) {
			this.name = name;
		}

		@Nullable
		public static Direction.Axis fromName(String name) {
			return (Direction.Axis)CODEC.byId(name);
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

		public abstract Direction getPositiveDirection();

		public abstract Direction getNegativeDirection();

		public Direction[] getDirections() {
			return new Direction[]{this.getPositiveDirection(), this.getNegativeDirection()};
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
			return switch (this) {
				case X, Z -> Direction.Type.HORIZONTAL;
				case Y -> Direction.Type.VERTICAL;
			};
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

		private AxisDirection(final int offset, final String description) {
			this.offset = offset;
			this.description = description;
		}

		public int offset() {
			return this.offset;
		}

		public String getDescription() {
			return this.description;
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

		private Type(final Direction[] facingArray, final Direction.Axis[] axisArray) {
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

		public List<Direction> getShuffled(Random random) {
			return Util.copyShuffled(this.facingArray, random);
		}

		public int getFacingCount() {
			return this.facingArray.length;
		}
	}
}
