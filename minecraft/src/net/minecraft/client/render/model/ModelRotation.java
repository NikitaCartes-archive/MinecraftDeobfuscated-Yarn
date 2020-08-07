package net.minecraft.client.render.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

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
		.collect(Collectors.toMap(modelRotation -> modelRotation.index, modelRotation -> modelRotation));
	private final AffineTransformation rotation;
	private final DirectionTransformation directionTransformation;
	private final int index;

	private static int getIndex(int x, int y) {
		return x * 360 + y;
	}

	private ModelRotation(int x, int y) {
		this.index = getIndex(x, y);
		Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-y), true);
		quaternion.hamiltonProduct(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-x), true));
		DirectionTransformation directionTransformation = DirectionTransformation.field_23292;

		for (int j = 0; j < y; j += 90) {
			directionTransformation = directionTransformation.prepend(DirectionTransformation.field_23318);
		}

		for (int j = 0; j < x; j += 90) {
			directionTransformation = directionTransformation.prepend(DirectionTransformation.field_23316);
		}

		this.rotation = new AffineTransformation(null, quaternion, null, null);
		this.directionTransformation = directionTransformation;
	}

	@Override
	public AffineTransformation getRotation() {
		return this.rotation;
	}

	public static ModelRotation get(int x, int y) {
		return (ModelRotation)BY_INDEX.get(getIndex(MathHelper.floorMod(x, 360), MathHelper.floorMod(y, 360)));
	}
}
