package net.minecraft.util;

import java.util.List;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.random.Random;

public enum BlockRotation implements StringIdentifiable {
	NONE("none", DirectionTransformation.IDENTITY),
	CLOCKWISE_90("clockwise_90", DirectionTransformation.ROT_90_Y_NEG),
	CLOCKWISE_180("180", DirectionTransformation.ROT_180_FACE_XZ),
	COUNTERCLOCKWISE_90("counterclockwise_90", DirectionTransformation.ROT_90_Y_POS);

	public static final com.mojang.serialization.Codec<BlockRotation> CODEC = StringIdentifiable.createCodec(BlockRotation::values);
	private final String id;
	private final DirectionTransformation directionTransformation;

	private BlockRotation(String id, DirectionTransformation directionTransformation) {
		this.id = id;
		this.directionTransformation = directionTransformation;
	}

	public BlockRotation rotate(BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch (this) {
					case NONE:
						return CLOCKWISE_180;
					case CLOCKWISE_90:
						return COUNTERCLOCKWISE_90;
					case CLOCKWISE_180:
						return NONE;
					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_90;
				}
			case COUNTERCLOCKWISE_90:
				switch (this) {
					case NONE:
						return COUNTERCLOCKWISE_90;
					case CLOCKWISE_90:
						return NONE;
					case CLOCKWISE_180:
						return CLOCKWISE_90;
					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_180;
				}
			case CLOCKWISE_90:
				switch (this) {
					case NONE:
						return CLOCKWISE_90;
					case CLOCKWISE_90:
						return CLOCKWISE_180;
					case CLOCKWISE_180:
						return COUNTERCLOCKWISE_90;
					case COUNTERCLOCKWISE_90:
						return NONE;
				}
			default:
				return this;
		}
	}

	public DirectionTransformation getDirectionTransformation() {
		return this.directionTransformation;
	}

	public Direction rotate(Direction direction) {
		if (direction.getAxis() == Direction.Axis.Y) {
			return direction;
		} else {
			switch (this) {
				case CLOCKWISE_90:
					return direction.rotateYClockwise();
				case CLOCKWISE_180:
					return direction.getOpposite();
				case COUNTERCLOCKWISE_90:
					return direction.rotateYCounterclockwise();
				default:
					return direction;
			}
		}
	}

	public int rotate(int rotation, int fullTurn) {
		switch (this) {
			case CLOCKWISE_90:
				return (rotation + fullTurn / 4) % fullTurn;
			case CLOCKWISE_180:
				return (rotation + fullTurn / 2) % fullTurn;
			case COUNTERCLOCKWISE_90:
				return (rotation + fullTurn * 3 / 4) % fullTurn;
			default:
				return rotation;
		}
	}

	public static BlockRotation random(Random random) {
		return Util.getRandom(values(), random);
	}

	public static List<BlockRotation> randomRotationOrder(Random random) {
		return Util.copyShuffled(values(), random);
	}

	@Override
	public String asString() {
		return this.id;
	}
}
