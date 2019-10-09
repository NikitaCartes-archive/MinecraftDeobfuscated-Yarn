package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

@Environment(EnvType.CLIENT)
public interface VertexConsumer {
	Logger LOGGER = LogManager.getLogger();

	VertexConsumer vertex(double d, double e, double f);

	VertexConsumer color(int i, int j, int k, int l);

	VertexConsumer texture(float f, float g);

	VertexConsumer overlay(int i, int j);

	VertexConsumer light(int i, int j);

	VertexConsumer normal(float f, float g, float h);

	void next();

	default VertexConsumer color(float f, float g, float h, float i) {
		return this.color((int)(f * 255.0F), (int)(g * 255.0F), (int)(h * 255.0F), (int)(i * 255.0F));
	}

	default VertexConsumer light(int i) {
		return this.light(i & 65535, i >> 16 & 65535);
	}

	default VertexConsumer defaultOverlay(int i) {
		return this.overlay(i & 65535, i >> 16 & 65535);
	}

	default void quad(Matrix4f matrix4f, Matrix3f matrix3f, BakedQuad bakedQuad, float f, float g, float h, int i, int j) {
		this.quad(matrix4f, matrix3f, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, f, g, h, new int[]{i, i, i, i}, j, false);
	}

	default void quad(Matrix4f matrix4f, Matrix3f matrix3f, BakedQuad bakedQuad, float[] fs, float f, float g, float h, int[] is, int i, boolean bl) {
		int[] js = bakedQuad.getVertexData();
		Vec3i vec3i = bakedQuad.getFace().getVector();
		Vector3f vector3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
		vector3f.multiply(matrix3f);
		int j = 8;
		int k = js.length / 8;

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_UV_NORMAL.getVertexSize());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for (int l = 0; l < k; l++) {
				intBuffer.clear();
				intBuffer.put(js, l * 8, 8);
				float m = byteBuffer.getFloat(0);
				float n = byteBuffer.getFloat(4);
				float o = byteBuffer.getFloat(8);
				byte b;
				byte c;
				byte d;
				if (bl) {
					int p = byteBuffer.get(12) & 255;
					int q = byteBuffer.get(13) & 255;
					int r = byteBuffer.get(14) & 255;
					b = (byte)((int)((float)p * fs[l] * f));
					c = (byte)((int)((float)q * fs[l] * g));
					d = (byte)((int)((float)r * fs[l] * h));
				} else {
					b = (byte)((int)(255.0F * fs[l] * f));
					c = (byte)((int)(255.0F * fs[l] * g));
					d = (byte)((int)(255.0F * fs[l] * h));
				}

				int p = is[l];
				float s = byteBuffer.getFloat(16);
				float t = byteBuffer.getFloat(20);
				this.vertex(matrix4f, m, n, o);
				this.color(b, c, d, 255);
				this.texture(s, t);
				this.defaultOverlay(i);
				this.light(p);
				this.normal(vector3f.getX(), vector3f.getY(), vector3f.getZ());
				this.next();
			}
		}
	}

	default VertexConsumer vertex(Matrix4f matrix4f, float f, float g, float h) {
		Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
		vector4f.multiply(matrix4f);
		return this.vertex((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ());
	}
}
