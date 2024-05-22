package net.minecraft;

import com.mojang.blaze3d.systems.VertexSorter;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import org.apache.commons.lang3.mutable.MutableLong;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class class_9801 implements AutoCloseable {
	private final class_9799.class_9800 field_52093;
	@Nullable
	private class_9799.class_9800 field_52094;
	private final class_9801.DrawParameters field_52095;

	public class_9801(class_9799.class_9800 arg, class_9801.DrawParameters drawParameters) {
		this.field_52093 = arg;
		this.field_52095 = drawParameters;
	}

	private static Vector3f[] method_60820(ByteBuffer byteBuffer, int i, VertexFormat vertexFormat) {
		int j = vertexFormat.method_60835(VertexFormatElement.field_52107);
		if (j == -1) {
			throw new IllegalArgumentException("Cannot identify quad centers with no position element");
		} else {
			FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
			int k = vertexFormat.getVertexSizeByte() / 4;
			int l = k * 4;
			int m = i / 4;
			Vector3f[] vector3fs = new Vector3f[m];

			for (int n = 0; n < m; n++) {
				int o = n * l + j;
				int p = o + k * 2;
				float f = floatBuffer.get(o + 0);
				float g = floatBuffer.get(o + 1);
				float h = floatBuffer.get(o + 2);
				float q = floatBuffer.get(p + 0);
				float r = floatBuffer.get(p + 1);
				float s = floatBuffer.get(p + 2);
				vector3fs[n] = new Vector3f((f + q) / 2.0F, (g + r) / 2.0F, (h + s) / 2.0F);
			}

			return vector3fs;
		}
	}

	public ByteBuffer method_60818() {
		return this.field_52093.method_60817();
	}

	@Nullable
	public ByteBuffer method_60821() {
		return this.field_52094 != null ? this.field_52094.method_60817() : null;
	}

	public class_9801.DrawParameters method_60822() {
		return this.field_52095;
	}

	@Nullable
	public class_9801.class_9802 method_60819(class_9799 arg, VertexSorter vertexSorter) {
		if (this.field_52095.mode() != VertexFormat.DrawMode.QUADS) {
			return null;
		} else {
			Vector3f[] vector3fs = method_60820(this.field_52093.method_60817(), this.field_52095.vertexCount(), this.field_52095.format());
			class_9801.class_9802 lv = new class_9801.class_9802(vector3fs, this.field_52095.indexType());
			this.field_52094 = lv.method_60824(arg, vertexSorter);
			return lv;
		}
	}

	public void close() {
		this.field_52093.close();
		if (this.field_52094 != null) {
			this.field_52094.close();
		}
	}

	@Environment(EnvType.CLIENT)
	public static record DrawParameters(VertexFormat format, int vertexCount, int indexCount, VertexFormat.DrawMode mode, VertexFormat.IndexType indexType) {
	}

	@Environment(EnvType.CLIENT)
	public static record class_9802(Vector3f[] centroids, VertexFormat.IndexType indexType) {
		@Nullable
		public class_9799.class_9800 method_60824(class_9799 arg, VertexSorter vertexSorter) {
			int[] is = vertexSorter.sort(this.centroids);
			long l = arg.method_60808(is.length * 6 * this.indexType.size);
			IntConsumer intConsumer = this.method_60823(l, this.indexType);

			for (int i : is) {
				intConsumer.accept(i * 4 + 0);
				intConsumer.accept(i * 4 + 1);
				intConsumer.accept(i * 4 + 2);
				intConsumer.accept(i * 4 + 2);
				intConsumer.accept(i * 4 + 3);
				intConsumer.accept(i * 4 + 0);
			}

			return arg.method_60807();
		}

		private IntConsumer method_60823(long l, VertexFormat.IndexType indexType) {
			MutableLong mutableLong = new MutableLong(l);

			return switch (indexType) {
				case SHORT -> i -> MemoryUtil.memPutShort(mutableLong.getAndAdd(2L), (short)i);
				case INT -> i -> MemoryUtil.memPutInt(mutableLong.getAndAdd(4L), i);
			};
		}
	}
}
