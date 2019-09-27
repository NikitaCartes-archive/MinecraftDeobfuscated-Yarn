package net.minecraft;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.system.MemoryStack;

@Environment(EnvType.CLIENT)
public interface class_4588 {
	class_4588 vertex(double d, double e, double f);

	class_4588 color(int i, int j, int k, int l);

	class_4588 texture(float f, float g);

	class_4588 method_22917(int i, int j);

	class_4588 method_22921(int i, int j);

	class_4588 method_22914(float f, float g, float h);

	void next();

	void method_22922(int i, int j);

	void method_22923();

	default class_4588 method_22915(float f, float g, float h, float i) {
		return this.color((int)(f * 255.0F), (int)(g * 255.0F), (int)(h * 255.0F), (int)(i * 255.0F));
	}

	default class_4588 method_22916(int i) {
		return this.method_22921(i & 65535, i >> 16 & 65535);
	}

	default void method_22919(Matrix4f matrix4f, BakedQuad bakedQuad, float f, float g, float h, int i) {
		this.method_22920(matrix4f, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, f, g, h, new int[]{i, i, i, i}, false);
	}

	default void method_22920(Matrix4f matrix4f, BakedQuad bakedQuad, float[] fs, float f, float g, float h, int[] is, boolean bl) {
		int[] js = bakedQuad.getVertexData();
		Vec3i vec3i = bakedQuad.getFace().getVector();
		int i = 8;
		int j = js.length / 8;

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_UV_NORMAL.getVertexSize());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();

			for (int k = 0; k < j; k++) {
				intBuffer.clear();
				intBuffer.put(js, k * 8, 8);
				float l = byteBuffer.getFloat(0);
				float m = byteBuffer.getFloat(4);
				float n = byteBuffer.getFloat(8);
				byte b;
				byte c;
				byte d;
				if (bl) {
					int o = byteBuffer.get(12) & 255;
					int p = byteBuffer.get(13) & 255;
					int q = byteBuffer.get(14) & 255;
					b = (byte)((int)((float)o * fs[k] * f));
					c = (byte)((int)((float)p * fs[k] * g));
					d = (byte)((int)((float)q * fs[k] * h));
				} else {
					b = (byte)((int)(255.0F * fs[k] * f));
					c = (byte)((int)(255.0F * fs[k] * g));
					d = (byte)((int)(255.0F * fs[k] * h));
				}

				int o = is[k];
				float r = byteBuffer.getFloat(16);
				float s = byteBuffer.getFloat(20);
				this.method_22918(matrix4f, l, m, n);
				this.color(b, c, d, 255);
				this.texture(r, s);
				this.method_22916(o);
				this.method_22914((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
				this.next();
			}
		}
	}

	default class_4588 method_22918(Matrix4f matrix4f, float f, float g, float h) {
		Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
		vector4f.method_22674(matrix4f);
		return this.vertex((double)vector4f.getX(), (double)vector4f.getY(), (double)vector4f.getZ());
	}
}
