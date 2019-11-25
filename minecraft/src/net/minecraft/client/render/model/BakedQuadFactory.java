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
	private static final float MIN_SCALE = 1.0F / (float)Math.cos((float) (Math.PI / 8)) - 1.0F;
	private static final float MAX_SCALE = 1.0F / (float)Math.cos((float) (Math.PI / 4)) - 1.0F;

	public BakedQuad bake(
		Vector3f from,
		Vector3f to,
		ModelElementFace face,
		Sprite texture,
		Direction side,
		ModelBakeSettings settings,
		@Nullable net.minecraft.client.render.model.json.ModelRotation rotation,
		boolean shade,
		Identifier modelId
	) {
		ModelElementTexture modelElementTexture = face.textureData;
		if (settings.isShaded()) {
			modelElementTexture = uvLock(face.textureData, side, settings.getRotation(), modelId);
		}

		float[] fs = new float[modelElementTexture.uvs.length];
		System.arraycopy(modelElementTexture.uvs, 0, fs, 0, fs.length);
		float f = texture.getAnimationFrameDelta();
		float g = (modelElementTexture.uvs[0] + modelElementTexture.uvs[0] + modelElementTexture.uvs[2] + modelElementTexture.uvs[2]) / 4.0F;
		float h = (modelElementTexture.uvs[1] + modelElementTexture.uvs[1] + modelElementTexture.uvs[3] + modelElementTexture.uvs[3]) / 4.0F;
		modelElementTexture.uvs[0] = MathHelper.lerp(f, modelElementTexture.uvs[0], g);
		modelElementTexture.uvs[2] = MathHelper.lerp(f, modelElementTexture.uvs[2], g);
		modelElementTexture.uvs[1] = MathHelper.lerp(f, modelElementTexture.uvs[1], h);
		modelElementTexture.uvs[3] = MathHelper.lerp(f, modelElementTexture.uvs[3], h);
		int[] is = this.packVertexData(modelElementTexture, texture, side, this.getPositionMatrix(from, to), settings.getRotation(), rotation, shade);
		Direction direction = decodeDirection(is);
		System.arraycopy(fs, 0, modelElementTexture.uvs, 0, fs.length);
		if (rotation == null) {
			this.encodeDirection(is, direction);
		}

		return new BakedQuad(is, face.tintIndex, direction, texture);
	}

	public static ModelElementTexture uvLock(ModelElementTexture texture, Direction orientation, Rotation3 rotation, Identifier modelId) {
		Matrix4f matrix4f = Rotation3Helper.uvLock(rotation, orientation, () -> "Unable to resolve UVLock for model: " + modelId).getMatrix();
		float f = texture.getU(texture.getDirectionIndex(0));
		float g = texture.getV(texture.getDirectionIndex(0));
		Vector4f vector4f = new Vector4f(f / 16.0F, g / 16.0F, 0.0F, 1.0F);
		vector4f.transform(matrix4f);
		float h = 16.0F * vector4f.getX();
		float i = 16.0F * vector4f.getY();
		float j = texture.getU(texture.getDirectionIndex(2));
		float k = texture.getV(texture.getDirectionIndex(2));
		Vector4f vector4f2 = new Vector4f(j / 16.0F, k / 16.0F, 0.0F, 1.0F);
		vector4f2.transform(matrix4f);
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

		float r = (float)Math.toRadians((double)texture.rotation);
		Vector3f vector3f = new Vector3f(MathHelper.cos(r), MathHelper.sin(r), 0.0F);
		Matrix3f matrix3f = new Matrix3f(matrix4f);
		vector3f.transform(matrix3f);
		int s = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2((double)vector3f.getY(), (double)vector3f.getX())) / 90.0)) * 90, 360);
		return new ModelElementTexture(new float[]{n, p, o, q}, s);
	}

	private int[] packVertexData(
		ModelElementTexture texture,
		Sprite sprite,
		Direction direction,
		float[] positionMatrix,
		Rotation3 orientation,
		@Nullable net.minecraft.client.render.model.json.ModelRotation rotation,
		boolean shaded
	) {
		int[] is = new int[32];

		for (int i = 0; i < 4; i++) {
			this.packVertexData(is, i, direction, texture, positionMatrix, sprite, orientation, rotation, shaded);
		}

		return is;
	}

	private int getLightmapCoordinate(Direction direction) {
		float f = this.getRelativeDirectionalBrightness(direction);
		int i = MathHelper.clamp((int)(f * 255.0F), 0, 255);
		return 0xFF000000 | i << 16 | i << 8 | i;
	}

	private float getRelativeDirectionalBrightness(Direction direction) {
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

	private float[] getPositionMatrix(Vector3f from, Vector3f to) {
		float[] fs = new float[Direction.values().length];
		fs[CubeFace.DirectionIds.WEST] = from.getX() / 16.0F;
		fs[CubeFace.DirectionIds.DOWN] = from.getY() / 16.0F;
		fs[CubeFace.DirectionIds.NORTH] = from.getZ() / 16.0F;
		fs[CubeFace.DirectionIds.EAST] = to.getX() / 16.0F;
		fs[CubeFace.DirectionIds.UP] = to.getY() / 16.0F;
		fs[CubeFace.DirectionIds.SOUTH] = to.getZ() / 16.0F;
		return fs;
	}

	private void packVertexData(
		int[] vertices,
		int cornerIndex,
		Direction direction,
		ModelElementTexture texture,
		float[] positionMatrix,
		Sprite sprite,
		Rotation3 orientation,
		@Nullable net.minecraft.client.render.model.json.ModelRotation rotation,
		boolean shaded
	) {
		Direction direction2 = Direction.transform(orientation.getMatrix(), direction);
		int i = shaded ? this.getLightmapCoordinate(direction2) : -1;
		CubeFace.Corner corner = CubeFace.getFace(direction).getCorner(cornerIndex);
		Vector3f vector3f = new Vector3f(positionMatrix[corner.xSide], positionMatrix[corner.ySide], positionMatrix[corner.zSide]);
		this.rotateVertex(vector3f, rotation);
		this.transformVertex(vector3f, orientation);
		this.packVertexData(vertices, cornerIndex, vector3f, i, sprite, texture);
	}

	private void packVertexData(int[] vertices, int cornerIndex, Vector3f position, int brightness, Sprite sprite, ModelElementTexture texture) {
		int i = cornerIndex * 8;
		vertices[i] = Float.floatToRawIntBits(position.getX());
		vertices[i + 1] = Float.floatToRawIntBits(position.getY());
		vertices[i + 2] = Float.floatToRawIntBits(position.getZ());
		vertices[i + 3] = brightness;
		vertices[i + 4] = Float.floatToRawIntBits(sprite.getFrameU((double)texture.getU(cornerIndex)));
		vertices[i + 4 + 1] = Float.floatToRawIntBits(sprite.getFrameV((double)texture.getV(cornerIndex)));
	}

	private void rotateVertex(Vector3f vector, @Nullable net.minecraft.client.render.model.json.ModelRotation rotation) {
		if (rotation != null) {
			Vector3f vector3f;
			Vector3f vector3f2;
			switch (rotation.axis) {
				case X:
					vector3f = new Vector3f(1.0F, 0.0F, 0.0F);
					vector3f2 = new Vector3f(0.0F, 1.0F, 1.0F);
					break;
				case Y:
					vector3f = new Vector3f(0.0F, 1.0F, 0.0F);
					vector3f2 = new Vector3f(1.0F, 0.0F, 1.0F);
					break;
				case Z:
					vector3f = new Vector3f(0.0F, 0.0F, 1.0F);
					vector3f2 = new Vector3f(1.0F, 1.0F, 0.0F);
					break;
				default:
					throw new IllegalArgumentException("There are only 3 axes");
			}

			Quaternion quaternion = new Quaternion(vector3f, rotation.angle, true);
			if (rotation.rescale) {
				if (Math.abs(rotation.angle) == 22.5F) {
					vector3f2.scale(MIN_SCALE);
				} else {
					vector3f2.scale(MAX_SCALE);
				}

				vector3f2.add(1.0F, 1.0F, 1.0F);
			} else {
				vector3f2.set(1.0F, 1.0F, 1.0F);
			}

			this.transformVertex(vector, rotation.origin.copy(), new Matrix4f(quaternion), vector3f2);
		}
	}

	public void transformVertex(Vector3f vertex, Rotation3 transformation) {
		if (transformation != Rotation3.identity()) {
			this.transformVertex(vertex, new Vector3f(0.5F, 0.5F, 0.5F), transformation.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
		}
	}

	private void transformVertex(Vector3f vertex, Vector3f origin, Matrix4f transformationMatrix, Vector3f scale) {
		Vector4f vector4f = new Vector4f(vertex.getX() - origin.getX(), vertex.getY() - origin.getY(), vertex.getZ() - origin.getZ(), 1.0F);
		vector4f.transform(transformationMatrix);
		vector4f.multiplyComponentwise(scale);
		vertex.set(vector4f.getX() + origin.getX(), vector4f.getY() + origin.getY(), vector4f.getZ() + origin.getZ());
	}

	public static Direction decodeDirection(int[] rotationMatrix) {
		Vector3f vector3f = new Vector3f(Float.intBitsToFloat(rotationMatrix[0]), Float.intBitsToFloat(rotationMatrix[1]), Float.intBitsToFloat(rotationMatrix[2]));
		Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(rotationMatrix[8]), Float.intBitsToFloat(rotationMatrix[9]), Float.intBitsToFloat(rotationMatrix[10]));
		Vector3f vector3f3 = new Vector3f(
			Float.intBitsToFloat(rotationMatrix[16]), Float.intBitsToFloat(rotationMatrix[17]), Float.intBitsToFloat(rotationMatrix[18])
		);
		Vector3f vector3f4 = vector3f.copy();
		vector3f4.subtract(vector3f2);
		Vector3f vector3f5 = vector3f3.copy();
		vector3f5.subtract(vector3f2);
		Vector3f vector3f6 = vector3f5.copy();
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

	private void encodeDirection(int[] rotationMatrix, Direction direction) {
		int[] is = new int[rotationMatrix.length];
		System.arraycopy(rotationMatrix, 0, is, 0, rotationMatrix.length);
		float[] fs = new float[Direction.values().length];
		fs[CubeFace.DirectionIds.WEST] = 999.0F;
		fs[CubeFace.DirectionIds.DOWN] = 999.0F;
		fs[CubeFace.DirectionIds.NORTH] = 999.0F;
		fs[CubeFace.DirectionIds.EAST] = -999.0F;
		fs[CubeFace.DirectionIds.UP] = -999.0F;
		fs[CubeFace.DirectionIds.SOUTH] = -999.0F;

		for (int i = 0; i < 4; i++) {
			int j = 8 * i;
			float f = Float.intBitsToFloat(is[j]);
			float g = Float.intBitsToFloat(is[j + 1]);
			float h = Float.intBitsToFloat(is[j + 2]);
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

		CubeFace cubeFace = CubeFace.getFace(direction);

		for (int jx = 0; jx < 4; jx++) {
			int k = 8 * jx;
			CubeFace.Corner corner = cubeFace.getCorner(jx);
			float hx = fs[corner.xSide];
			float l = fs[corner.ySide];
			float m = fs[corner.zSide];
			rotationMatrix[k] = Float.floatToRawIntBits(hx);
			rotationMatrix[k + 1] = Float.floatToRawIntBits(l);
			rotationMatrix[k + 2] = Float.floatToRawIntBits(m);

			for (int n = 0; n < 4; n++) {
				int o = 8 * n;
				float p = Float.intBitsToFloat(is[o]);
				float q = Float.intBitsToFloat(is[o + 1]);
				float r = Float.intBitsToFloat(is[o + 2]);
				if (MathHelper.approximatelyEquals(hx, p) && MathHelper.approximatelyEquals(l, q) && MathHelper.approximatelyEquals(m, r)) {
					rotationMatrix[k + 4] = is[o + 4];
					rotationMatrix[k + 4 + 1] = is[o + 4 + 1];
				}
			}
		}
	}
}
