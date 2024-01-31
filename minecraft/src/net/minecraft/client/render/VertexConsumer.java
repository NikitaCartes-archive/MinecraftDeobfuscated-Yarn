package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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
	VertexConsumer vertex(double x, double y, double z);

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
	 * Starts consuming the next vertex.
	 * 
	 * <p>This method must be called after specifying all elements in a vertex.
	 */
	void next();

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
	default void vertex(
		float x,
		float y,
		float z,
		float red,
		float green,
		float blue,
		float alpha,
		float u,
		float v,
		int overlay,
		int light,
		float normalX,
		float normalY,
		float normalZ
	) {
		this.vertex((double)x, (double)y, (double)z);
		this.color(red, green, blue, alpha);
		this.texture(u, v);
		this.overlay(overlay);
		this.light(light);
		this.normal(normalX, normalY, normalZ);
		this.next();
	}

	/**
	 * Makes this consumer always use the same color for subsequent vertices
	 * until {@link #unfixColor} is called.
	 * 
	 * <p>The color will be automatically supplied when the color element is
	 * requested. Make sure not to specify the color yourself when using this
	 * method.
	 */
	void fixedColor(int red, int green, int blue, int alpha);

	/**
	 * Makes this consumer no longer use the color set in {@link #fixedColor}.
	 */
	void unfixColor();

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
		return this.color(ColorHelper.Argb.getRed(argb), ColorHelper.Argb.getGreen(argb), ColorHelper.Argb.getBlue(argb), ColorHelper.Argb.getAlpha(argb));
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
	default void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, int light, int overlay) {
		this.quad(matrixEntry, quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, new int[]{light, light, light, light}, overlay, false);
	}

	/**
	 * Specifies the vertex elements from {@code quad} and starts consuming
	 * the next vertex.
	 * 
	 * @throws IllegalStateException if a color has been set in {@link
	 * #fixedColor}.
	 */
	default void quad(
		MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, int[] lights, int overlay, boolean useQuadColorData
	) {
		float[] fs = new float[]{brightnesses[0], brightnesses[1], brightnesses[2], brightnesses[3]};
		int[] is = new int[]{lights[0], lights[1], lights[2], lights[3]};
		int[] js = quad.getVertexData();
		Vec3i vec3i = quad.getFace().getVector();
		Matrix4f matrix4f = matrixEntry.getPositionMatrix();
		Vector3f vector3f = matrixEntry.method_56820((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ(), new Vector3f());
		int i = 8;
		int j = js.length / 8;

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeByte());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for (int k = 0; k < j; k++) {
				intBuffer.clear();
				intBuffer.put(js, k * 8, 8);
				float f = byteBuffer.getFloat(0);
				float g = byteBuffer.getFloat(4);
				float h = byteBuffer.getFloat(8);
				float o;
				float p;
				float q;
				if (useQuadColorData) {
					float l = (float)(byteBuffer.get(12) & 255) / 255.0F;
					float m = (float)(byteBuffer.get(13) & 255) / 255.0F;
					float n = (float)(byteBuffer.get(14) & 255) / 255.0F;
					o = l * fs[k] * red;
					p = m * fs[k] * green;
					q = n * fs[k] * blue;
				} else {
					o = fs[k] * red;
					p = fs[k] * green;
					q = fs[k] * blue;
				}

				int r = is[k];
				float m = byteBuffer.getFloat(16);
				float n = byteBuffer.getFloat(20);
				Vector4f vector4f = matrix4f.transform(new Vector4f(f, g, h, 1.0F));
				this.vertex(vector4f.x(), vector4f.y(), vector4f.z(), o, p, q, 1.0F, m, n, overlay, r, vector3f.x(), vector3f.y(), vector3f.z());
			}
		}
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
		return this.vertex((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z());
	}

	/**
	 * Specifies the {@linkplain VertexFormats#NORMAL_ELEMENT
	 * normal element} of the current vertex.
	 * 
	 * @throws IllegalStateException if this consumer is not currently
	 * accepting a normal element.
	 * 
	 * @return this consumer, for chaining
	 */
	default VertexConsumer normal(MatrixStack.Entry matrix, float x, float y, float z) {
		Vector3f vector3f = matrix.method_56820(x, y, z, new Vector3f());
		return this.normal(vector3f.x(), vector3f.y(), vector3f.z());
	}
}
