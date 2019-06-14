package net.minecraft.client.render.model;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Quaternion;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

@Environment(EnvType.CLIENT)
public class BakedQuadFactory {
	private static final float field_4260 = 1.0F / (float)Math.cos((float) (Math.PI / 8)) - 1.0F;
	private static final float field_4259 = 1.0F / (float)Math.cos((float) (Math.PI / 4)) - 1.0F;
	private static final BakedQuadFactory.class_797[] field_4264 = new BakedQuadFactory.class_797[ModelRotation.values().length * Direction.values().length];
	private static final BakedQuadFactory.class_797 field_4258 = new BakedQuadFactory.class_797() {
		@Override
		ModelElementTexture method_3470(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{f, g, h, i}, 0);
		}
	};
	private static final BakedQuadFactory.class_797 field_4261 = new BakedQuadFactory.class_797() {
		@Override
		ModelElementTexture method_3470(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{i, 16.0F - f, g, 16.0F - h}, 270);
		}
	};
	private static final BakedQuadFactory.class_797 field_4262 = new BakedQuadFactory.class_797() {
		@Override
		ModelElementTexture method_3470(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{16.0F - f, 16.0F - g, 16.0F - h, 16.0F - i}, 0);
		}
	};
	private static final BakedQuadFactory.class_797 field_4263 = new BakedQuadFactory.class_797() {
		@Override
		ModelElementTexture method_3470(float f, float g, float h, float i) {
			return new ModelElementTexture(new float[]{16.0F - g, h, 16.0F - i, f}, 90);
		}
	};

	public BakedQuad method_3468(
		Vector3f vector3f,
		Vector3f vector3f2,
		ModelElementFace modelElementFace,
		Sprite sprite,
		Direction direction,
		ModelBakeSettings modelBakeSettings,
		@Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation,
		boolean bl
	) {
		ModelElementTexture modelElementTexture = modelElementFace.field_4227;
		if (modelBakeSettings.isUvLocked()) {
			modelElementTexture = this.method_3454(modelElementFace.field_4227, direction, modelBakeSettings.getRotation());
		}

		float[] fs = new float[modelElementTexture.uvs.length];
		System.arraycopy(modelElementTexture.uvs, 0, fs, 0, fs.length);
		float f = (float)sprite.getWidth() / (sprite.getMaxU() - sprite.getMinU());
		float g = (float)sprite.getHeight() / (sprite.getMaxV() - sprite.getMinV());
		float h = 4.0F / Math.max(g, f);
		float i = (modelElementTexture.uvs[0] + modelElementTexture.uvs[0] + modelElementTexture.uvs[2] + modelElementTexture.uvs[2]) / 4.0F;
		float j = (modelElementTexture.uvs[1] + modelElementTexture.uvs[1] + modelElementTexture.uvs[3] + modelElementTexture.uvs[3]) / 4.0F;
		modelElementTexture.uvs[0] = MathHelper.lerp(h, modelElementTexture.uvs[0], i);
		modelElementTexture.uvs[2] = MathHelper.lerp(h, modelElementTexture.uvs[2], i);
		modelElementTexture.uvs[1] = MathHelper.lerp(h, modelElementTexture.uvs[1], j);
		modelElementTexture.uvs[3] = MathHelper.lerp(h, modelElementTexture.uvs[3], j);
		int[] is = this.method_3458(modelElementTexture, sprite, direction, this.method_3459(vector3f, vector3f2), modelBakeSettings.getRotation(), modelRotation, bl);
		Direction direction2 = method_3467(is);
		System.arraycopy(fs, 0, modelElementTexture.uvs, 0, fs.length);
		if (modelRotation == null) {
			this.method_3462(is, direction2);
		}

		return new BakedQuad(is, modelElementFace.tintIndex, direction2, sprite);
	}

	private ModelElementTexture method_3454(ModelElementTexture modelElementTexture, Direction direction, ModelRotation modelRotation) {
		return field_4264[method_3465(modelRotation, direction)].method_3469(modelElementTexture);
	}

	private int[] method_3458(
		ModelElementTexture modelElementTexture,
		Sprite sprite,
		Direction direction,
		float[] fs,
		ModelRotation modelRotation,
		@Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation2,
		boolean bl
	) {
		int[] is = new int[28];

		for (int i = 0; i < 4; i++) {
			this.method_3461(is, i, direction, modelElementTexture, fs, sprite, modelRotation, modelRotation2, bl);
		}

		return is;
	}

	private int method_3457(Direction direction) {
		float f = this.method_3456(direction);
		int i = MathHelper.clamp((int)(f * 255.0F), 0, 255);
		return 0xFF000000 | i << 16 | i << 8 | i;
	}

	private float method_3456(Direction direction) {
		switch (direction) {
			case field_11033:
				return 0.5F;
			case field_11036:
				return 1.0F;
			case field_11043:
			case field_11035:
				return 0.8F;
			case field_11039:
			case field_11034:
				return 0.6F;
			default:
				return 1.0F;
		}
	}

	private float[] method_3459(Vector3f vector3f, Vector3f vector3f2) {
		float[] fs = new float[Direction.values().length];
		fs[CubeFace.DirectionIds.WEST] = vector3f.x() / 16.0F;
		fs[CubeFace.DirectionIds.DOWN] = vector3f.y() / 16.0F;
		fs[CubeFace.DirectionIds.NORTH] = vector3f.z() / 16.0F;
		fs[CubeFace.DirectionIds.EAST] = vector3f2.x() / 16.0F;
		fs[CubeFace.DirectionIds.UP] = vector3f2.y() / 16.0F;
		fs[CubeFace.DirectionIds.SOUTH] = vector3f2.z() / 16.0F;
		return fs;
	}

	private void method_3461(
		int[] is,
		int i,
		Direction direction,
		ModelElementTexture modelElementTexture,
		float[] fs,
		Sprite sprite,
		ModelRotation modelRotation,
		@Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation2,
		boolean bl
	) {
		Direction direction2 = modelRotation.apply(direction);
		int j = bl ? this.method_3457(direction2) : -1;
		CubeFace.Corner corner = CubeFace.method_3163(direction).getCorner(i);
		Vector3f vector3f = new Vector3f(fs[corner.xSide], fs[corner.ySide], fs[corner.zSide]);
		this.method_3463(vector3f, modelRotation2);
		int k = this.method_3455(vector3f, direction, i, modelRotation);
		this.method_3460(is, k, i, vector3f, j, sprite, modelElementTexture);
	}

	private void method_3460(int[] is, int i, int j, Vector3f vector3f, int k, Sprite sprite, ModelElementTexture modelElementTexture) {
		int l = i * 7;
		is[l] = Float.floatToRawIntBits(vector3f.x());
		is[l + 1] = Float.floatToRawIntBits(vector3f.y());
		is[l + 2] = Float.floatToRawIntBits(vector3f.z());
		is[l + 3] = k;
		is[l + 4] = Float.floatToRawIntBits(sprite.getU((double)modelElementTexture.getU(j)));
		is[l + 4 + 1] = Float.floatToRawIntBits(sprite.getV((double)modelElementTexture.getV(j)));
	}

	private void method_3463(Vector3f vector3f, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation) {
		if (modelRotation != null) {
			Vector3f vector3f2;
			Vector3f vector3f3;
			switch (modelRotation.axis) {
				case X:
					vector3f2 = new Vector3f(1.0F, 0.0F, 0.0F);
					vector3f3 = new Vector3f(0.0F, 1.0F, 1.0F);
					break;
				case Y:
					vector3f2 = new Vector3f(0.0F, 1.0F, 0.0F);
					vector3f3 = new Vector3f(1.0F, 0.0F, 1.0F);
					break;
				case Z:
					vector3f2 = new Vector3f(0.0F, 0.0F, 1.0F);
					vector3f3 = new Vector3f(1.0F, 1.0F, 0.0F);
					break;
				default:
					throw new IllegalArgumentException("There are only 3 axes");
			}

			Quaternion quaternion = new Quaternion(vector3f2, modelRotation.angle, true);
			if (modelRotation.rescale) {
				if (Math.abs(modelRotation.angle) == 22.5F) {
					vector3f3.scale(field_4260);
				} else {
					vector3f3.scale(field_4259);
				}

				vector3f3.add(1.0F, 1.0F, 1.0F);
			} else {
				vector3f3.set(1.0F, 1.0F, 1.0F);
			}

			this.method_3464(vector3f, new Vector3f(modelRotation.origin), quaternion, vector3f3);
		}
	}

	public int method_3455(Vector3f vector3f, Direction direction, int i, ModelRotation modelRotation) {
		if (modelRotation == ModelRotation.field_5350) {
			return i;
		} else {
			this.method_3464(vector3f, new Vector3f(0.5F, 0.5F, 0.5F), modelRotation.getQuaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
			return modelRotation.method_4706(direction, i);
		}
	}

	private void method_3464(Vector3f vector3f, Vector3f vector3f2, Quaternion quaternion, Vector3f vector3f3) {
		Vector4f vector4f = new Vector4f(vector3f.x() - vector3f2.x(), vector3f.y() - vector3f2.y(), vector3f.z() - vector3f2.z(), 1.0F);
		vector4f.method_4959(quaternion);
		vector4f.multiply(vector3f3);
		vector3f.set(vector4f.x() + vector3f2.x(), vector4f.y() + vector3f2.y(), vector4f.z() + vector3f2.z());
	}

	public static Direction method_3467(int[] is) {
		Vector3f vector3f = new Vector3f(Float.intBitsToFloat(is[0]), Float.intBitsToFloat(is[1]), Float.intBitsToFloat(is[2]));
		Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(is[7]), Float.intBitsToFloat(is[8]), Float.intBitsToFloat(is[9]));
		Vector3f vector3f3 = new Vector3f(Float.intBitsToFloat(is[14]), Float.intBitsToFloat(is[15]), Float.intBitsToFloat(is[16]));
		Vector3f vector3f4 = new Vector3f(vector3f);
		vector3f4.subtract(vector3f2);
		Vector3f vector3f5 = new Vector3f(vector3f3);
		vector3f5.subtract(vector3f2);
		Vector3f vector3f6 = new Vector3f(vector3f5);
		vector3f6.cross(vector3f4);
		vector3f6.reciprocal();
		Direction direction = null;
		float f = 0.0F;

		for (Direction direction2 : Direction.values()) {
			Vec3i vec3i = direction2.getVector();
			Vector3f vector3f7 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
			float g = vector3f6.dot(vector3f7);
			if (g >= 0.0F && g > f) {
				f = g;
				direction = direction2;
			}
		}

		return direction == null ? Direction.field_11036 : direction;
	}

	private void method_3462(int[] is, Direction direction) {
		int[] js = new int[is.length];
		System.arraycopy(is, 0, js, 0, is.length);
		float[] fs = new float[Direction.values().length];
		fs[CubeFace.DirectionIds.WEST] = 999.0F;
		fs[CubeFace.DirectionIds.DOWN] = 999.0F;
		fs[CubeFace.DirectionIds.NORTH] = 999.0F;
		fs[CubeFace.DirectionIds.EAST] = -999.0F;
		fs[CubeFace.DirectionIds.UP] = -999.0F;
		fs[CubeFace.DirectionIds.SOUTH] = -999.0F;

		for (int i = 0; i < 4; i++) {
			int j = 7 * i;
			float f = Float.intBitsToFloat(js[j]);
			float g = Float.intBitsToFloat(js[j + 1]);
			float h = Float.intBitsToFloat(js[j + 2]);
			if (f < fs[CubeFace.DirectionIds.WEST]) {
				fs[CubeFace.DirectionIds.WEST] = f;
			}

			if (g < fs[CubeFace.DirectionIds.DOWN]) {
				fs[CubeFace.DirectionIds.DOWN] = g;
			}

			if (h < fs[CubeFace.DirectionIds.NORTH]) {
				fs[CubeFace.DirectionIds.NORTH] = h;
			}

			if (f > fs[CubeFace.DirectionIds.EAST]) {
				fs[CubeFace.DirectionIds.EAST] = f;
			}

			if (g > fs[CubeFace.DirectionIds.UP]) {
				fs[CubeFace.DirectionIds.UP] = g;
			}

			if (h > fs[CubeFace.DirectionIds.SOUTH]) {
				fs[CubeFace.DirectionIds.SOUTH] = h;
			}
		}

		CubeFace cubeFace = CubeFace.method_3163(direction);

		for (int jx = 0; jx < 4; jx++) {
			int k = 7 * jx;
			CubeFace.Corner corner = cubeFace.getCorner(jx);
			float hx = fs[corner.xSide];
			float l = fs[corner.ySide];
			float m = fs[corner.zSide];
			is[k] = Float.floatToRawIntBits(hx);
			is[k + 1] = Float.floatToRawIntBits(l);
			is[k + 2] = Float.floatToRawIntBits(m);

			for (int n = 0; n < 4; n++) {
				int o = 7 * n;
				float p = Float.intBitsToFloat(js[o]);
				float q = Float.intBitsToFloat(js[o + 1]);
				float r = Float.intBitsToFloat(js[o + 2]);
				if (MathHelper.equalsApproximate(hx, p) && MathHelper.equalsApproximate(l, q) && MathHelper.equalsApproximate(m, r)) {
					is[k + 4] = js[o + 4];
					is[k + 4 + 1] = js[o + 4 + 1];
				}
			}
		}
	}

	private static void method_3466(ModelRotation modelRotation, Direction direction, BakedQuadFactory.class_797 arg) {
		field_4264[method_3465(modelRotation, direction)] = arg;
	}

	private static int method_3465(ModelRotation modelRotation, Direction direction) {
		return ModelRotation.values().length * direction.ordinal() + modelRotation.ordinal();
	}

	static {
		method_3466(ModelRotation.field_5350, Direction.field_11033, field_4258);
		method_3466(ModelRotation.field_5350, Direction.field_11034, field_4258);
		method_3466(ModelRotation.field_5350, Direction.field_11043, field_4258);
		method_3466(ModelRotation.field_5350, Direction.field_11035, field_4258);
		method_3466(ModelRotation.field_5350, Direction.field_11036, field_4258);
		method_3466(ModelRotation.field_5350, Direction.field_11039, field_4258);
		method_3466(ModelRotation.field_5366, Direction.field_11034, field_4258);
		method_3466(ModelRotation.field_5366, Direction.field_11043, field_4258);
		method_3466(ModelRotation.field_5366, Direction.field_11035, field_4258);
		method_3466(ModelRotation.field_5366, Direction.field_11039, field_4258);
		method_3466(ModelRotation.field_5355, Direction.field_11034, field_4258);
		method_3466(ModelRotation.field_5355, Direction.field_11043, field_4258);
		method_3466(ModelRotation.field_5355, Direction.field_11035, field_4258);
		method_3466(ModelRotation.field_5355, Direction.field_11039, field_4258);
		method_3466(ModelRotation.field_5347, Direction.field_11034, field_4258);
		method_3466(ModelRotation.field_5347, Direction.field_11043, field_4258);
		method_3466(ModelRotation.field_5347, Direction.field_11035, field_4258);
		method_3466(ModelRotation.field_5347, Direction.field_11039, field_4258);
		method_3466(ModelRotation.field_5351, Direction.field_11033, field_4258);
		method_3466(ModelRotation.field_5351, Direction.field_11035, field_4258);
		method_3466(ModelRotation.field_5360, Direction.field_11033, field_4258);
		method_3466(ModelRotation.field_5367, Direction.field_11033, field_4258);
		method_3466(ModelRotation.field_5367, Direction.field_11043, field_4258);
		method_3466(ModelRotation.field_5354, Direction.field_11033, field_4258);
		method_3466(ModelRotation.field_5358, Direction.field_11033, field_4258);
		method_3466(ModelRotation.field_5358, Direction.field_11036, field_4258);
		method_3466(ModelRotation.field_5353, Direction.field_11035, field_4258);
		method_3466(ModelRotation.field_5353, Direction.field_11036, field_4258);
		method_3466(ModelRotation.field_5349, Direction.field_11036, field_4258);
		method_3466(ModelRotation.field_5361, Direction.field_11043, field_4258);
		method_3466(ModelRotation.field_5361, Direction.field_11036, field_4258);
		method_3466(ModelRotation.field_5352, Direction.field_11036, field_4258);
		method_3466(ModelRotation.field_5347, Direction.field_11036, field_4261);
		method_3466(ModelRotation.field_5366, Direction.field_11033, field_4261);
		method_3466(ModelRotation.field_5351, Direction.field_11039, field_4261);
		method_3466(ModelRotation.field_5360, Direction.field_11039, field_4261);
		method_3466(ModelRotation.field_5367, Direction.field_11039, field_4261);
		method_3466(ModelRotation.field_5354, Direction.field_11043, field_4261);
		method_3466(ModelRotation.field_5354, Direction.field_11035, field_4261);
		method_3466(ModelRotation.field_5354, Direction.field_11039, field_4261);
		method_3466(ModelRotation.field_5348, Direction.field_11036, field_4261);
		method_3466(ModelRotation.field_5359, Direction.field_11033, field_4261);
		method_3466(ModelRotation.field_5353, Direction.field_11034, field_4261);
		method_3466(ModelRotation.field_5349, Direction.field_11034, field_4261);
		method_3466(ModelRotation.field_5349, Direction.field_11043, field_4261);
		method_3466(ModelRotation.field_5349, Direction.field_11035, field_4261);
		method_3466(ModelRotation.field_5361, Direction.field_11034, field_4261);
		method_3466(ModelRotation.field_5352, Direction.field_11034, field_4261);
		method_3466(ModelRotation.field_5355, Direction.field_11033, field_4262);
		method_3466(ModelRotation.field_5355, Direction.field_11036, field_4262);
		method_3466(ModelRotation.field_5351, Direction.field_11043, field_4262);
		method_3466(ModelRotation.field_5351, Direction.field_11036, field_4262);
		method_3466(ModelRotation.field_5360, Direction.field_11036, field_4262);
		method_3466(ModelRotation.field_5367, Direction.field_11035, field_4262);
		method_3466(ModelRotation.field_5367, Direction.field_11036, field_4262);
		method_3466(ModelRotation.field_5354, Direction.field_11036, field_4262);
		method_3466(ModelRotation.field_5358, Direction.field_11034, field_4262);
		method_3466(ModelRotation.field_5358, Direction.field_11043, field_4262);
		method_3466(ModelRotation.field_5358, Direction.field_11035, field_4262);
		method_3466(ModelRotation.field_5358, Direction.field_11039, field_4262);
		method_3466(ModelRotation.field_5348, Direction.field_11034, field_4262);
		method_3466(ModelRotation.field_5348, Direction.field_11043, field_4262);
		method_3466(ModelRotation.field_5348, Direction.field_11035, field_4262);
		method_3466(ModelRotation.field_5348, Direction.field_11039, field_4262);
		method_3466(ModelRotation.field_5356, Direction.field_11033, field_4262);
		method_3466(ModelRotation.field_5356, Direction.field_11034, field_4262);
		method_3466(ModelRotation.field_5356, Direction.field_11043, field_4262);
		method_3466(ModelRotation.field_5356, Direction.field_11035, field_4262);
		method_3466(ModelRotation.field_5356, Direction.field_11036, field_4262);
		method_3466(ModelRotation.field_5356, Direction.field_11039, field_4262);
		method_3466(ModelRotation.field_5359, Direction.field_11034, field_4262);
		method_3466(ModelRotation.field_5359, Direction.field_11043, field_4262);
		method_3466(ModelRotation.field_5359, Direction.field_11035, field_4262);
		method_3466(ModelRotation.field_5359, Direction.field_11039, field_4262);
		method_3466(ModelRotation.field_5353, Direction.field_11033, field_4262);
		method_3466(ModelRotation.field_5353, Direction.field_11043, field_4262);
		method_3466(ModelRotation.field_5349, Direction.field_11033, field_4262);
		method_3466(ModelRotation.field_5361, Direction.field_11033, field_4262);
		method_3466(ModelRotation.field_5361, Direction.field_11035, field_4262);
		method_3466(ModelRotation.field_5352, Direction.field_11033, field_4262);
		method_3466(ModelRotation.field_5366, Direction.field_11036, field_4263);
		method_3466(ModelRotation.field_5347, Direction.field_11033, field_4263);
		method_3466(ModelRotation.field_5351, Direction.field_11034, field_4263);
		method_3466(ModelRotation.field_5360, Direction.field_11034, field_4263);
		method_3466(ModelRotation.field_5360, Direction.field_11043, field_4263);
		method_3466(ModelRotation.field_5360, Direction.field_11035, field_4263);
		method_3466(ModelRotation.field_5367, Direction.field_11034, field_4263);
		method_3466(ModelRotation.field_5354, Direction.field_11034, field_4263);
		method_3466(ModelRotation.field_5353, Direction.field_11039, field_4263);
		method_3466(ModelRotation.field_5348, Direction.field_11033, field_4263);
		method_3466(ModelRotation.field_5359, Direction.field_11036, field_4263);
		method_3466(ModelRotation.field_5349, Direction.field_11039, field_4263);
		method_3466(ModelRotation.field_5361, Direction.field_11039, field_4263);
		method_3466(ModelRotation.field_5352, Direction.field_11043, field_4263);
		method_3466(ModelRotation.field_5352, Direction.field_11035, field_4263);
		method_3466(ModelRotation.field_5352, Direction.field_11039, field_4263);
	}

	@Environment(EnvType.CLIENT)
	abstract static class class_797 {
		private class_797() {
		}

		public ModelElementTexture method_3469(ModelElementTexture modelElementTexture) {
			float f = modelElementTexture.getU(modelElementTexture.method_3414(0));
			float g = modelElementTexture.getV(modelElementTexture.method_3414(0));
			float h = modelElementTexture.getU(modelElementTexture.method_3414(2));
			float i = modelElementTexture.getV(modelElementTexture.method_3414(2));
			return this.method_3470(f, g, h, i);
		}

		abstract ModelElementTexture method_3470(float f, float g, float h, float i);
	}
}
