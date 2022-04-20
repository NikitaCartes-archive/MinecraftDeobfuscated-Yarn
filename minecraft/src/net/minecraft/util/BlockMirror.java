package net.minecraft.util;

import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

public enum BlockMirror {
	NONE(Text.translatable("mirror.none"), DirectionTransformation.IDENTITY),
	LEFT_RIGHT(Text.translatable("mirror.left_right"), DirectionTransformation.INVERT_Z),
	FRONT_BACK(Text.translatable("mirror.front_back"), DirectionTransformation.INVERT_X);

	private final Text name;
	private final DirectionTransformation directionTransformation;

	private BlockMirror(Text name, DirectionTransformation directionTransformation) {
		this.name = name;
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
}
