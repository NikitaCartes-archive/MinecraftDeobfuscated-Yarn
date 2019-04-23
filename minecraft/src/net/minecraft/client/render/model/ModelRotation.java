package net.minecraft.client.render.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Quaternion;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum ModelRotation implements ModelBakeSettings {
	field_5350(0, 0),
	field_5366(0, 90),
	field_5355(0, 180),
	field_5347(0, 270),
	field_5351(90, 0),
	field_5360(90, 90),
	field_5367(90, 180),
	field_5354(90, 270),
	field_5358(180, 0),
	field_5348(180, 90),
	field_5356(180, 180),
	field_5359(180, 270),
	field_5353(270, 0),
	field_5349(270, 90),
	field_5361(270, 180),
	field_5352(270, 270);

	private static final Map<Integer, ModelRotation> BY_INDEX = (Map<Integer, ModelRotation>)Arrays.stream(values())
		.sorted(Comparator.comparingInt(modelRotation -> modelRotation.index))
		.collect(Collectors.toMap(modelRotation -> modelRotation.index, modelRotation -> modelRotation));
	private final int index;
	private final Quaternion quaternion;
	private final int xRotations;
	private final int yRotations;

	private static int getIndex(int i, int j) {
		return i * 360 + j;
	}

	private ModelRotation(int j, int k) {
		this.index = getIndex(j, k);
		Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-k), true);
		quaternion.method_4925(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-j), true));
		this.quaternion = quaternion;
		this.xRotations = MathHelper.abs(j / 90);
		this.yRotations = MathHelper.abs(k / 90);
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

	public static ModelRotation get(int i, int j) {
		return (ModelRotation)BY_INDEX.get(getIndex(MathHelper.floorMod(i, 360), MathHelper.floorMod(j, 360)));
	}
}
