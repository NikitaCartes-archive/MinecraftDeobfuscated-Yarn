package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

/**
 * An interface that consumes vertices in a certain {@linkplain
 * VertexFormat vertex format}.
 * 
 * <p>The vertex elements must be specified in the same order as defined in
 * the format the vertices being consumed are in.
 */
@Environment(EnvType.CLIENT)
public interface VertexConsumer {
	/**
	 * Specifies the {@linkplain VertexFormats#POSITION_ELEMENT
	 * position element} of the current vertex.
	 * 
	 * <p>This is typically the first element in a vertex, hence the name.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a position element.
	 * 
	 * @return this consumer, for chaining
	 */
	VertexConsumer vertex(float x, float y, float z);

	/**
	 * Specifies the {@linkplain VertexFormats#COLOR_ELEMENT
	 * color element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a color element or if a color has been set in {@link
	 * #fixedColor}.
	 * 
	 * @return this consumer, for chaining
	 */
	VertexConsumer color(int red, int green, int blue, int alpha);

	/**
	 * Specifies the {@linkplain VertexFormats#TEXTURE_ELEMENT
	 * texture element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a texture element.
	 * 
	 * @return this consumer, for chaining
	 */
	VertexConsumer texture(float u, float v);

	/**
	 * Specifies the {@linkplain VertexFormats#OVERLAY_ELEMENT
	 * overlay element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting an overlay element.
	 * 
	 * @return this consumer, for chaining
	 */
	VertexConsumer overlay(int u, int v);

	/**
	 * Specifies the {@linkplain VertexFormats#LIGHT_ELEMENT
	 * light element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a light element.
	 * 
	 * @return this consumer, for chaining
	 */
	VertexConsumer light(int u, int v);

	/**
	 * Specifies the {@linkplain VertexFormats#NORMAL_ELEMENT
	 * normal element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a normal element.
	 * 
	 * @return this consumer, for chaining
	 */
	VertexConsumer normal(float x, float y, float z);

	/**
	 * Specifies the
	 * {@linkplain VertexFormats#POSITION_ELEMENT position},
	 * {@linkplain VertexFormats#COLOR_ELEMENT color},
	 * {@linkplain VertexFormats#TEXTURE_ELEMENT texture},
	 * {@linkplain VertexFormats#OVERLAY_ELEMENT overlay},
	 * {@linkplain VertexFormats#LIGHT_ELEMENT light}, and
	 * {@linkplain VertexFormats#NORMAL_ELEMENT normal} elements of the
	 * current vertex and starts consuming the next vertex.
	 * 
	 * @throws IllegalStateException if a color has been set in {@link
	 * #fixedColor}.
	 */
	default void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
		this.vertex(x, y, z);
		this.color(color);
		this.texture(u, v);
		this.overlay(overlay);
		this.light(light);
		this.normal(normalX, normalY, normalZ);
	}

	/**
	 * Specifies the {@linkplain VertexFormats#COLOR_ELEMENT
	 * color element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a color element or if a color has been set in {@link
	 * #fixedColor}.
	 * 
	 * @return this consumer, for chaining
	 */
	default VertexConsumer color(float red, float green, float blue, float alpha) {
		return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
	}

	/**
	 * Specifies the {@linkplain VertexFormats#COLOR_ELEMENT
	 * color element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a color element or if a color has been set in {@link
	 * #fixedColor}.
	 * 
	 * @return this consumer, for chaining
	 */
	default VertexConsumer color(int argb) {
		return this.color(ColorHelper.getRed(argb), ColorHelper.getGreen(argb), ColorHelper.getBlue(argb), ColorHelper.getAlpha(argb));
	}

	/**
	 * Specifies the {@linkplain VertexFormats#COLOR_ELEMENT
	 * color element} of the current vertex in rgb format.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a color element or if a color has been set in {@link
	 * #fixedColor}.
	 * 
	 * @return this consumer, for chaining
	 */
	default VertexConsumer colorRgb(int rgb) {
		return this.color(ColorHelper.withAlpha(rgb, Colors.WHITE));
	}

	/**
	 * Specifies the {@linkplain VertexFormats#LIGHT_ELEMENT
	 * light element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a light element.
	 * 
	 * @return this consumer, for chaining
	 */
	default VertexConsumer light(int uv) {
		return this.light(uv & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 65295), uv >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 65295));
	}

	/**
	 * Specifies the {@linkplain VertexFormats#OVERLAY_ELEMENT
	 * overlay element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting an overlay element.
	 * 
	 * @return this consumer, for chaining
	 */
	default VertexConsumer overlay(int uv) {
		return this.overlay(uv & 65535, uv >> 16 & 65535);
	}

	/**
	 * Specifies the vertex elements from {@code quad} and starts consuming
	 * the next vertex.
	 * 
	 * @throws IllegalStateException if a color has been set in {@link
	 * #fixedColor}.
	 */
	default void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, float f, int i, int j) {
		this.quad(matrixEntry, quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, f, new int[]{i, i, i, i}, j, false);
	}

	/**
	 * Specifies the vertex elements from {@code quad} and starts consuming
	 * the next vertex.
	 * 
	 * @throws IllegalStateException if a color has been set in {@link
	 * #fixedColor}.
	 */
	default void quad(
		MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float f, int[] is, int i, boolean bl
	) {
		int[] js = quad.getVertexData();
		Vec3i vec3i = quad.getFace().getVector();
		Matrix4f matrix4f = matrixEntry.getPositionMatrix();
		Vector3f vector3f = matrixEntry.transformNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ(), new Vector3f());
		int j = 8;
		int k = js.length / 8;
		int l = (int)(f * 255.0F);
		int m = quad.getLightEmission();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeByte());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for (int n = 0; n < k; n++) {
				intBuffer.clear();
				intBuffer.put(js, n * 8, 8);
				float g = byteBuffer.getFloat(0);
				float h = byteBuffer.getFloat(4);
				float o = byteBuffer.getFloat(8);
				float s;
				float t;
				float u;
				if (bl) {
					float p = (float)(byteBuffer.get(12) & 255);
					float q = (float)(byteBuffer.get(13) & 255);
					float r = (float)(byteBuffer.get(14) & 255);
					s = p * brightnesses[n] * red;
					t = q * brightnesses[n] * green;
					u = r * brightnesses[n] * blue;
				} else {
					s = brightnesses[n] * red * 255.0F;
					t = brightnesses[n] * green * 255.0F;
					u = brightnesses[n] * blue * 255.0F;
				}

				int v = ColorHelper.getArgb(l, (int)s, (int)t, (int)u);
				int w = LightmapTextureManager.applyEmission(is[n], m);
				float r = byteBuffer.getFloat(16);
				float x = byteBuffer.getFloat(20);
				Vector3f vector3f2 = matrix4f.transformPosition(g, h, o, new Vector3f());
				this.vertex(vector3f2.x(), vector3f2.y(), vector3f2.z(), v, r, x, i, w, vector3f.x(), vector3f.y(), vector3f.z());
			}
		}
	}

	default VertexConsumer vertex(Vector3f vec) {
		return this.vertex(vec.x(), vec.y(), vec.z());
	}

	default VertexConsumer vertex(MatrixStack.Entry matrix, Vector3f vec) {
		return this.vertex(matrix, vec.x(), vec.y(), vec.z());
	}

	default VertexConsumer vertex(MatrixStack.Entry matrix, float x, float y, float z) {
		return this.vertex(matrix.getPositionMatrix(), x, y, z);
	}

	/**
	 * Specifies the {@linkplain VertexFormats#POSITION_ELEMENT
	 * position element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a position element.
	 * 
	 * @return this consumer, for chaining
	 * 
	 * @param matrix the matrix that will be applied to the vertex position, typically {@link
	 * net.minecraft.client.util.math.MatrixStack.Entry#getPositionMatrix
	 * MatrixStack.Entry#getPositionMatrix}
	 */
	default VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
		Vector3f vector3f = matrix.transformPosition(x, y, z, new Vector3f());
		return this.vertex(vector3f.x(), vector3f.y(), vector3f.z());
	}

	default VertexConsumer normal(MatrixStack.Entry matrix, float x, float y, float z) {
		Vector3f vector3f = matrix.transformNormal(x, y, z, new Vector3f());
		return this.normal(vector3f.x(), vector3f.y(), vector3f.z());
	}

	default VertexConsumer normal(MatrixStack.Entry matrix, Vector3f vec) {
		return this.normal(matrix, vec.x(), vec.y(), vec.z());
	}
}
