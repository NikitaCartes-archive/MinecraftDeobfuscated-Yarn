package net.minecraft.util;

import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

public enum BlockMirror implements StringIdentifiable {
	NONE("none", DirectionTransformation.IDENTITY),
	LEFT_RIGHT("left_right", DirectionTransformation.INVERT_Z),
	FRONT_BACK("front_back", DirectionTransformation.INVERT_X);

	public static final com.mojang.serialization.Codec<BlockMirror> CODEC = StringIdentifiable.createCodec(BlockMirror::values);
	private final String id;
	private final Text name;
	private final DirectionTransformation directionTransformation;

	private BlockMirror(String id, DirectionTransformation directionTransformation) {
		this.id = id;
		this.name = Text.translatable("mirror." + id);
		this.directionTransformation = directionTransformation;
	}

	public int mirror(int rotation, int fullTurn) {
		int i = fullTurn / 2;
		int j = rotation > i ? rotation - fullTurn : rotation;
		switch (this) {
			case FRONT_BACK:
				return (fullTurn - j) % fullTurn;
			case LEFT_RIGHT:
				return (i - j + fullTurn) % fullTurn;
			default:
				return rotation;
		}
	}

	public BlockRotation getRotation(Direction direction) {
		Direction.Axis axis = direction.getAxis();
		return (this != LEFT_RIGHT || axis != Direction.Axis.Z) && (this != FRONT_BACK || axis != Direction.Axis.X)
			? BlockRotation.NONE
			: BlockRotation.CLOCKWISE_180;
	}

	public Direction apply(Direction direction) {
		if (this == FRONT_BACK && direction.getAxis() == Direction.Axis.X) {
			return direction.getOpposite();
		} else {
			return this == LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z ? direction.getOpposite() : direction;
		}
	}

	public DirectionTransformation getDirectionTransformation() {
		return this.directionTransformation;
	}

	public Text getName() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
