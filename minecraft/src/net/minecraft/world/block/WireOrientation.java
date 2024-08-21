package net.minecraft.world.block;

import com.google.common.annotations.VisibleForTesting;
import io.netty.buffer.ByteBuf;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class WireOrientation {
	public static final PacketCodec<ByteBuf, WireOrientation> PACKET_CODEC = PacketCodecs.indexed(WireOrientation::fromOrdinal, WireOrientation::ordinal);
	private static final WireOrientation[] VALUES = Util.make(() -> {
		WireOrientation[] wireOrientations = new WireOrientation[48];
		initializeValuesArray(new WireOrientation(Direction.UP, Direction.NORTH, WireOrientation.SideBias.LEFT), wireOrientations);
		return wireOrientations;
	});
	private final Direction up;
	private final Direction front;
	private final Direction right;
	private final WireOrientation.SideBias sideBias;
	private final int ordinal;
	private final List<Direction> directionsByPriority;
	private final List<Direction> horizontalDirections;
	private final List<Direction> verticalDirections;
	private final Map<Direction, WireOrientation> siblingsByFront = new EnumMap(Direction.class);
	private final Map<Direction, WireOrientation> siblingsByUp = new EnumMap(Direction.class);
	private final Map<WireOrientation.SideBias, WireOrientation> siblingsBySideBias = new EnumMap(WireOrientation.SideBias.class);

	private WireOrientation(Direction up, Direction front, WireOrientation.SideBias sideBias) {
		this.up = up;
		this.front = front;
		this.sideBias = sideBias;
		this.ordinal = ordinalFromComponents(up, front, sideBias);
		Vec3i vec3i = front.getVector().crossProduct(up.getVector());
		Direction direction = Direction.fromVector(vec3i, null);
		Objects.requireNonNull(direction);
		if (this.sideBias == WireOrientation.SideBias.RIGHT) {
			this.right = direction;
		} else {
			this.right = direction.getOpposite();
		}

		this.directionsByPriority = List.of(this.front.getOpposite(), this.front, this.right, this.right.getOpposite(), this.up.getOpposite(), this.up);
		this.horizontalDirections = this.directionsByPriority.stream().filter(directionx -> directionx.getAxis() != this.up.getAxis()).toList();
		this.verticalDirections = this.directionsByPriority.stream().filter(directionx -> directionx.getAxis() == this.up.getAxis()).toList();
	}

	public static WireOrientation of(Direction up, Direction front, WireOrientation.SideBias sideBias) {
		return VALUES[ordinalFromComponents(up, front, sideBias)];
	}

	public WireOrientation withUp(Direction direction) {
		return (WireOrientation)this.siblingsByUp.get(direction);
	}

	public WireOrientation withFront(Direction direction) {
		return (WireOrientation)this.siblingsByFront.get(direction);
	}

	public WireOrientation withFrontIfNotUp(Direction direction) {
		return direction.getAxis() == this.up.getAxis() ? this : (WireOrientation)this.siblingsByFront.get(direction);
	}

	public WireOrientation withFrontAndSideBias(Direction direction) {
		WireOrientation wireOrientation = this.withFront(direction);
		return this.front == wireOrientation.right ? wireOrientation.withOppositeSideBias() : wireOrientation;
	}

	public WireOrientation withSideBias(WireOrientation.SideBias sideBias) {
		return (WireOrientation)this.siblingsBySideBias.get(sideBias);
	}

	public WireOrientation withOppositeSideBias() {
		return this.withSideBias(this.sideBias.opposite());
	}

	public Direction getFront() {
		return this.front;
	}

	public Direction getUp() {
		return this.up;
	}

	public Direction getRight() {
		return this.right;
	}

	public WireOrientation.SideBias getSideBias() {
		return this.sideBias;
	}

	public List<Direction> getDirectionsByPriority() {
		return this.directionsByPriority;
	}

	public List<Direction> getHorizontalDirections() {
		return this.horizontalDirections;
	}

	public List<Direction> getVerticalDirections() {
		return this.verticalDirections;
	}

	public String toString() {
		return "[up=" + this.up + ",front=" + this.front + ",sideBias=" + this.sideBias + "]";
	}

	public int ordinal() {
		return this.ordinal;
	}

	public static WireOrientation fromOrdinal(int ordinal) {
		return VALUES[ordinal];
	}

	public static WireOrientation random(Random random) {
		return Util.getRandom(VALUES, random);
	}

	private static WireOrientation initializeValuesArray(WireOrientation prime, WireOrientation[] valuesOut) {
		if (valuesOut[prime.ordinal()] != null) {
			return valuesOut[prime.ordinal()];
		} else {
			valuesOut[prime.ordinal()] = prime;

			for (WireOrientation.SideBias sideBias : WireOrientation.SideBias.values()) {
				prime.siblingsBySideBias.put(sideBias, initializeValuesArray(new WireOrientation(prime.up, prime.front, sideBias), valuesOut));
			}

			for (Direction direction : Direction.values()) {
				Direction direction2 = prime.up;
				if (direction == prime.up) {
					direction2 = prime.front.getOpposite();
				}

				if (direction == prime.up.getOpposite()) {
					direction2 = prime.front;
				}

				prime.siblingsByFront.put(direction, initializeValuesArray(new WireOrientation(direction2, direction, prime.sideBias), valuesOut));
			}

			for (Direction direction : Direction.values()) {
				Direction direction2x = prime.front;
				if (direction == prime.front) {
					direction2x = prime.up.getOpposite();
				}

				if (direction == prime.front.getOpposite()) {
					direction2x = prime.up;
				}

				prime.siblingsByUp.put(direction, initializeValuesArray(new WireOrientation(direction, direction2x, prime.sideBias), valuesOut));
			}

			return prime;
		}
	}

	@VisibleForTesting
	protected static int ordinalFromComponents(Direction up, Direction front, WireOrientation.SideBias sideBias) {
		if (up.getAxis() == front.getAxis()) {
			throw new IllegalStateException("Up-vector and front-vector can not be on the same axis");
		} else {
			int i;
			if (up.getAxis() == Direction.Axis.Y) {
				i = front.getAxis() == Direction.Axis.X ? 1 : 0;
			} else {
				i = front.getAxis() == Direction.Axis.Y ? 1 : 0;
			}

			int j = i << 1 | front.getDirection().ordinal();
			return ((up.ordinal() << 2) + j << 1) + sideBias.ordinal();
		}
	}

	public static enum SideBias {
		LEFT("left"),
		RIGHT("right");

		private final String name;

		private SideBias(final String name) {
			this.name = name;
		}

		public WireOrientation.SideBias opposite() {
			return this == LEFT ? RIGHT : LEFT;
		}

		public String toString() {
			return this.name;
		}
	}
}
