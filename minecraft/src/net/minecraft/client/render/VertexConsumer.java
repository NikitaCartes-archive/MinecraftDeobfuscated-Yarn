package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vector4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

@Environment(EnvType.CLIENT)
public interface VertexConsumer {
	Logger LOGGER = LogManager.getLogger();

	VertexConsumer vertex(double x, double y, double z);

	VertexConsumer color(int red, int green, int blue, int alpha);

	VertexConsumer texture(float u, float v);

	VertexConsumer overlay(int u, int v);

	VertexConsumer light(int u, int v);

	VertexConsumer normal(float x, float y, float z);

	void next();

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

	default VertexConsumer color(float red, float green, float blue, float alpha) {
		return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
	}

	default VertexConsumer light(int uv) {
		return this.light(uv & 65535, uv >> 16 & 65535);
	}

	default VertexConsumer overlay(int uv) {
		return this.overlay(uv & 65535, uv >> 16 & 65535);
	}

	default void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, int light, int overlay) {
		this.quad(matrixEntry, quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, new int[]{light, light, light, light}, overlay, false);
	}

	default void quad(
		MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, int[] lights, int overlay, boolean useQuadColorData
	) {
		int[] is = quad.getVertexData();
		Vec3i vec3i = quad.getFace().getVector();
		Vec3f vec3f = new Vec3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
		Matrix4f matrix4f = matrixEntry.getModel();
		vec3f.transform(matrixEntry.getNormal());
		int i = 8;
		int j = is.length / 8;

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for (int k = 0; k < j; k++) {
				intBuffer.clear();
				intBuffer.put(is, k * 8, 8);
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
					o = l * brightnesses[k] * red;
					p = m * brightnesses[k] * green;
					q = n * brightnesses[k] * blue;
				} else {
					o = brightnesses[k] * red;
					p = brightnesses[k] * green;
					q = brightnesses[k] * blue;
				}

				int r = lights[k];
				float m = byteBuffer.getFloat(16);
				float n = byteBuffer.getFloat(20);
				Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
				vector4f.transform(matrix4f);
				this.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), o, p, q, 1.0F, m, n, overlay, r, vec3f.getX(), vec3f.getY(), vec3f.getZ());
			}
		}
	}

	default VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
		Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
		vector4f.transform(matrix);
		return this.vertex((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ());
	}

	default VertexConsumer normal(Matrix3f matrix, float x, float y, float z) {
		Vec3f vec3f = new Vec3f(x, y, z);
		vec3f.transform(matrix);
		return this.normal(vec3f.getX(), vec3f.getY(), vec3f.getZ());
	}
}
