package net.minecraft.client.render.model;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.client.util.math.Rotation3Helper;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3i;

@Environment(EnvType.CLIENT)
public class BakedQuadFactory {
	private static final float field_4260 = 1.0F / (float)Math.cos((float) (Math.PI / 8)) - 1.0F;
	private static final float field_4259 = 1.0F / (float)Math.cos((float) (Math.PI / 4)) - 1.0F;

	public BakedQuad bake(
		Vector3f from,
		Vector3f to,
		ModelElementFace face,
		Sprite texture,
		Direction side,
		ModelBakeSettings settings,
		@Nullable net.minecraft.client.render.model.json.ModelRotation rotation,
		boolean shade,
		Identifier identifier
	) {
		ModelElementTexture modelElementTexture = face.textureData;
		if (settings.isUvLocked()) {
			modelElementTexture = uvLock(face.textureData, side, settings.getRotation(), identifier);
		}

		float[] fs = new float[modelElementTexture.uvs.length];
		System.arraycopy(modelElementTexture.uvs, 0, fs, 0, fs.length);
		float f = (float)texture.getWidth() / (texture.getMaxU() - texture.getMinU());
		float g = (float)texture.getHeight() / (texture.getMaxV() - texture.getMinV());
		float h = 4.0F / Math.max(g, f);
		float i = (modelElementTexture.uvs[0] + modelElementTexture.uvs[0] + modelElementTexture.uvs[2] + modelElementTexture.uvs[2]) / 4.0F;
		float j = (modelElementTexture.uvs[1] + modelElementTexture.uvs[1] + modelElementTexture.uvs[3] + modelElementTexture.uvs[3]) / 4.0F;
		modelElementTexture.uvs[0] = MathHelper.lerp(h, modelElementTexture.uvs[0], i);
		modelElementTexture.uvs[2] = MathHelper.lerp(h, modelElementTexture.uvs[2], i);
		modelElementTexture.uvs[1] = MathHelper.lerp(h, modelElementTexture.uvs[1], j);
		modelElementTexture.uvs[3] = MathHelper.lerp(h, modelElementTexture.uvs[3], j);
		int[] is = this.method_3458(modelElementTexture, texture, side, this.method_3459(from, to), settings.getRotation(), rotation, shade);
		Direction direction = method_3467(is);
		System.arraycopy(fs, 0, modelElementTexture.uvs, 0, fs.length);
		if (rotation == null) {
			this.method_3462(is, direction);
		}

		return new BakedQuad(is, face.tintIndex, direction, texture);
	}

	public static ModelElementTexture uvLock(ModelElementTexture modelElementTexture, Direction direction, Rotation3 rotation3, Identifier identifier) {
		Matrix4f matrix4f = Rotation3Helper.uvLock(rotation3, direction, () -> "Unable to resolve UVLock for model: " + identifier).getMatrix();
		float f = modelElementTexture.getU(modelElementTexture.method_3414(0));
		float g = modelElementTexture.getV(modelElementTexture.method_3414(0));
		Vector4f vector4f = new Vector4f(f / 16.0F, g / 16.0F, 0.0F, 1.0F);
		vector4f.multiply(matrix4f);
		float h = 16.0F * vector4f.getX();
		float i = 16.0F * vector4f.getY();
		float j = modelElementTexture.getU(modelElementTexture.method_3414(2));
		float k = modelElementTexture.getV(modelElementTexture.method_3414(2));
		Vector4f vector4f2 = new Vector4f(j / 16.0F, k / 16.0F, 0.0F, 1.0F);
		vector4f2.multiply(matrix4f);
		float l = 16.0F * vector4f2.getX();
		float m = 16.0F * vector4f2.getY();
		float n;
		float o;
		if (Math.signum(j - f) == Math.signum(l - h)) {
			n = h;
			o = l;
		} else {
			n = l;
			o = h;
		}

		float p;
		float q;
		if (Math.signum(k - g) == Math.signum(m - i)) {
			p = i;
			q = m;
		} else {
			p = m;
			q = i;
		}

		float r = (float)Math.toRadians((double)modelElementTexture.rotation);
		Vector3f vector3f = new Vector3f(MathHelper.cos(r), MathHelper.sin(r), 0.0F);
		Matrix3f matrix3f = new Matrix3f(matrix4f);
		vector3f.multiply(matrix3f);
		int s = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2((double)vector3f.getY(), (double)vector3f.getX())) / 90.0)) * 90, 360);
		return new ModelElementTexture(new float[]{n, p, o, q}, s);
	}

	private int[] method_3458(
		ModelElementTexture modelElementTexture,
		Sprite sprite,
		Direction direction,
		float[] fs,
		Rotation3 rotation3,
		@Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation,
		boolean bl
	) {
		int[] is = new int[32];

		for (int i = 0; i < 4; i++) {
			this.method_3461(is, i, direction, modelElementTexture, fs, sprite, rotation3, modelRotation, bl);
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
			case DOWN:
				return 0.5F;
			case UP:
				return 1.0F;
			case NORTH:
			case SOUTH:
				return 0.8F;
			case WEST:
			case EAST:
				return 0.6F;
			default:
				return 1.0F;
		}
	}

	private float[] method_3459(Vector3f vector3f, Vector3f vector3f2) {
		float[] fs = new float[Direction.values().length];
		fs[CubeFace.DirectionIds.WEST] = vector3f.getX() / 16.0F;
		fs[CubeFace.DirectionIds.DOWN] = vector3f.getY() / 16.0F;
		fs[CubeFace.DirectionIds.NORTH] = vector3f.getZ() / 16.0F;
		fs[CubeFace.DirectionIds.EAST] = vector3f2.getX() / 16.0F;
		fs[CubeFace.DirectionIds.UP] = vector3f2.getY() / 16.0F;
		fs[CubeFace.DirectionIds.SOUTH] = vector3f2.getZ() / 16.0F;
		return fs;
	}

	private void method_3461(
		int[] is,
		int i,
		Direction direction,
		ModelElementTexture modelElementTexture,
		float[] fs,
		Sprite sprite,
		Rotation3 rotation3,
		@Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation,
		boolean bl
	) {
		Direction direction2 = Direction.transform(rotation3.getMatrix(), direction);
		int j = bl ? this.method_3457(direction2) : -1;
		CubeFace.Corner corner = CubeFace.method_3163(direction).getCorner(i);
		Vector3f vector3f = new Vector3f(fs[corner.xSide], fs[corner.ySide], fs[corner.zSide]);
		this.method_3463(vector3f, modelRotation);
		this.method_3455(vector3f, rotation3);
		this.method_3460(is, i, vector3f, j, sprite, modelElementTexture);
	}

	private void method_3460(int[] is, int i, Vector3f vector3f, int j, Sprite sprite, ModelElementTexture modelElementTexture) {
		int k = i * 8;
		is[k] = Float.floatToRawIntBits(vector3f.getX());
		is[k + 1] = Float.floatToRawIntBits(vector3f.getY());
		is[k + 2] = Float.floatToRawIntBits(vector3f.getZ());
		is[k + 3] = j;
		is[k + 4] = Float.floatToRawIntBits(sprite.getU((double)modelElementTexture.getU(i)));
		is[k + 4 + 1] = Float.floatToRawIntBits(sprite.getV((double)modelElementTexture.getV(i)));
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

			this.method_3464(vector3f, new Vector3f(modelRotation.origin), new Matrix4f(quaternion), vector3f3);
		}
	}

	public void method_3455(Vector3f vector3f, Rotation3 rotation3) {
		if (rotation3 != Rotation3.identity()) {
			this.method_3464(vector3f, new Vector3f(0.5F, 0.5F, 0.5F), rotation3.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
		}
	}

	private void method_3464(Vector3f vector3f, Vector3f vector3f2, Matrix4f matrix4f, Vector3f vector3f3) {
		Vector4f vector4f = new Vector4f(vector3f.getX() - vector3f2.getX(), vector3f.getY() - vector3f2.getY(), vector3f.getZ() - vector3f2.getZ(), 1.0F);
		vector4f.multiply(matrix4f);
		vector4f.multiplyXyz(vector3f3);
		vector3f.set(vector4f.getX() + vector3f2.getX(), vector4f.getY() + vector3f2.getY(), vector4f.getZ() + vector3f2.getZ());
	}

	public static Direction method_3467(int[] is) {
		Vector3f vector3f = new Vector3f(Float.intBitsToFloat(is[0]), Float.intBitsToFloat(is[1]), Float.intBitsToFloat(is[2]));
		Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(is[8]), Float.intBitsToFloat(is[9]), Float.intBitsToFloat(is[10]));
		Vector3f vector3f3 = new Vector3f(Float.intBitsToFloat(is[16]), Float.intBitsToFloat(is[17]), Float.intBitsToFloat(is[18]));
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

		return direction == null ? Direction.UP : direction;
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
			int j = 8 * i;
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
			int k = 8 * jx;
			CubeFace.Corner corner = cubeFace.getCorner(jx);
			float hx = fs[corner.xSide];
			float l = fs[corner.ySide];
			float m = fs[corner.zSide];
			is[k] = Float.floatToRawIntBits(hx);
			is[k + 1] = Float.floatToRawIntBits(l);
			is[k + 2] = Float.floatToRawIntBits(m);

			for (int n = 0; n < 4; n++) {
				int o = 8 * n;
				float p = Float.intBitsToFloat(js[o]);
				float q = Float.intBitsToFloat(js[o + 1]);
				float r = Float.intBitsToFloat(js[o + 2]);
				if (MathHelper.approximatelyEquals(hx, p) && MathHelper.approximatelyEquals(l, q) && MathHelper.approximatelyEquals(m, r)) {
					is[k + 4] = js[o + 4];
					is[k + 4 + 1] = js[o + 4 + 1];
				}
			}
		}
	}
}
