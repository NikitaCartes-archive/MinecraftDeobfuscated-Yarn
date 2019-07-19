package net.minecraft.client.render.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public enum ModelRotation implements ModelBakeSettings {
	X0_Y0(0, 0),
	X0_Y90(0, 90),
	X0_Y180(0, 180),
	X0_Y270(0, 270),
	X90_Y0(90, 0),
	X90_Y90(90, 90),
	X90_Y180(90, 180),
	X90_Y270(90, 270),
	X180_Y0(180, 0),
	X180_Y90(180, 90),
	X180_Y180(180, 180),
	X180_Y270(180, 270),
	X270_Y0(270, 0),
	X270_Y90(270, 90),
	X270_Y180(270, 180),
	X270_Y270(270, 270);

	private static final Map<Integer, ModelRotation> BY_INDEX = (Map<Integer, ModelRotation>)Arrays.stream(values())
		.sorted(Comparator.comparingInt(modelRotation -> modelRotation.index))
		.collect(Collectors.toMap(modelRotation -> modelRotation.index, modelRotation -> modelRotation));
	private final int index;
	private final Quaternion quaternion;
	private final int xRotations;
	private final int yRotations;

	private static int getIndex(int x, int y) {
		return x * 360 + y;
	}

	private ModelRotation(int x, int y) {
		this.index = getIndex(x, y);
		Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-y), true);
		quaternion.hamiltonProduct(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-x), true));
		this.quaternion = quaternion;
		this.xRotations = MathHelper.abs(x / 90);
		this.yRotations = MathHelper.abs(y / 90);
	}

	@Override
	public ModelRotation getRotation() {
		return this;
	}

	public Quaternion getQuaternion() {
		return this.quaternion;
	}

	public Direction apply(Direction direction) {
		Direction direction2 = direction;

		for (int i = 0; i < this.xRotations; i++) {
			direction2 = direction2.rotateClockwise(Direction.Axis.X);
		}

		if (direction2.getAxis() != Direction.Axis.Y) {
			for (int i = 0; i < this.yRotations; i++) {
				direction2 = direction2.rotateClockwise(Direction.Axis.Y);
			}
		}

		return direction2;
	}

	public int method_4706(Direction direction, int i) {
		int j = i;
		if (direction.getAxis() == Direction.Axis.X) {
			j = (i + this.xRotations) % 4;
		}

		Direction direction2 = direction;

		for (int k = 0; k < this.xRotations; k++) {
			direction2 = direction2.rotateClockwise(Direction.Axis.X);
		}

		if (direction2.getAxis() == Direction.Axis.Y) {
			j = (j + this.yRotations) % 4;
		}

		return j;
	}

	public static ModelRotation get(int x, int y) {
		return (ModelRotation)BY_INDEX.get(getIndex(MathHelper.floorMod(x, 360), MathHelper.floorMod(y, 360)));
	}
}
