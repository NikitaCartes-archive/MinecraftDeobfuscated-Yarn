package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Vec3i;
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

	default VertexConsumer color(float red, float green, float blue, float alpha) {
		return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
	}

	default VertexConsumer light(int uv) {
		return this.light(uv & 65535, uv >> 16 & 65535);
	}

	default VertexConsumer overlay(int uv) {
		return this.overlay(uv & 65535, uv >> 16 & 65535);
	}

	default void quad(Matrix4f modelMatrix, Matrix3f normalMatrix, BakedQuad quad, float red, float green, float blue, int light, int overlay) {
		this.quad(modelMatrix, normalMatrix, quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, new int[]{light, light, light, light}, overlay, false);
	}

	default void quad(
		Matrix4f modelMatrix,
		Matrix3f normalMatrix,
		BakedQuad quad,
		float[] brightness,
		float red,
		float green,
		float blue,
		int[] light,
		int overlay,
		boolean hasColorData
	) {
		int[] is = quad.getVertexData();
		Vec3i vec3i = quad.getFace().getVector();
		Vector3f vector3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
		vector3f.multiply(normalMatrix);
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
				byte b;
				byte c;
				byte d;
				if (hasColorData) {
					int l = byteBuffer.get(12) & 255;
					int m = byteBuffer.get(13) & 255;
					int n = byteBuffer.get(14) & 255;
					b = (byte)((int)((float)l * brightness[k] * red));
					c = (byte)((int)((float)m * brightness[k] * green));
					d = (byte)((int)((float)n * brightness[k] * blue));
				} else {
					b = (byte)((int)(255.0F * brightness[k] * red));
					c = (byte)((int)(255.0F * brightness[k] * green));
					d = (byte)((int)(255.0F * brightness[k] * blue));
				}

				int l = light[k];
				float o = byteBuffer.getFloat(16);
				float p = byteBuffer.getFloat(20);
				this.vertex(modelMatrix, f, g, h);
				this.color(b, c, d, 255);
				this.texture(o, p);
				this.overlay(overlay);
				this.light(l);
				this.normal(vector3f.getX(), vector3f.getY(), vector3f.getZ());
				this.next();
			}
		}
	}

	default VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
		Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
		vector4f.multiply(matrix);
		return this.vertex((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ());
	}
}
