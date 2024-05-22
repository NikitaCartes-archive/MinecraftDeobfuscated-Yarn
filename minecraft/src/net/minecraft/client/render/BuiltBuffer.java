package net.minecraft.client.render;

import com.mojang.blaze3d.systems.VertexSorter;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.BufferAllocator;
import org.apache.commons.lang3.mutable.MutableLong;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class BuiltBuffer implements AutoCloseable {
	private final BufferAllocator.CloseableBuffer buffer;
	@Nullable
	private BufferAllocator.CloseableBuffer sortedBuffer;
	private final BuiltBuffer.DrawParameters drawParameters;

	public BuiltBuffer(BufferAllocator.CloseableBuffer buffer, BuiltBuffer.DrawParameters drawParameters) {
		this.buffer = buffer;
		this.drawParameters = drawParameters;
	}

	private static Vector3f[] collectCentroids(ByteBuffer buf, int vertexCount, VertexFormat format) {
		int i = format.getOffset(VertexFormatElement.POSITION);
		if (i == -1) {
			throw new IllegalArgumentException("Cannot identify quad centers with no position element");
		} else {
			FloatBuffer floatBuffer = buf.asFloatBuffer();
			int j = format.getVertexSizeByte() / 4;
			int k = j * 4;
			int l = vertexCount / 4;
			Vector3f[] vector3fs = new Vector3f[l];

			for (int m = 0; m < l; m++) {
				int n = m * k + i;
				int o = n + j * 2;
				float f = floatBuffer.get(n + 0);
				float g = floatBuffer.get(n + 1);
				float h = floatBuffer.get(n + 2);
				float p = floatBuffer.get(o + 0);
				float q = floatBuffer.get(o + 1);
				float r = floatBuffer.get(o + 2);
				vector3fs[m] = new Vector3f((f + p) / 2.0F, (g + q) / 2.0F, (h + r) / 2.0F);
			}

			return vector3fs;
		}
	}

	public ByteBuffer getBuffer() {
		return this.buffer.getBuffer();
	}

	@Nullable
	public ByteBuffer getSortedBuffer() {
		return this.sortedBuffer != null ? this.sortedBuffer.getBuffer() : null;
	}

	public BuiltBuffer.DrawParameters getDrawParameters() {
		return this.drawParameters;
	}

	@Nullable
	public BuiltBuffer.SortState sortQuads(BufferAllocator allocator, VertexSorter sorter) {
		if (this.drawParameters.mode() != VertexFormat.DrawMode.QUADS) {
			return null;
		} else {
			Vector3f[] vector3fs = collectCentroids(this.buffer.getBuffer(), this.drawParameters.vertexCount(), this.drawParameters.format());
			BuiltBuffer.SortState sortState = new BuiltBuffer.SortState(vector3fs, this.drawParameters.indexType());
			this.sortedBuffer = sortState.sortAndStore(allocator, sorter);
			return sortState;
		}
	}

	public void close() {
		this.buffer.close();
		if (this.sortedBuffer != null) {
			this.sortedBuffer.close();
		}
	}

	@Environment(EnvType.CLIENT)
	public static record DrawParameters(VertexFormat format, int vertexCount, int indexCount, VertexFormat.DrawMode mode, VertexFormat.IndexType indexType) {
	}

	@Environment(EnvType.CLIENT)
	public static record SortState(Vector3f[] centroids, VertexFormat.IndexType indexType) {
		@Nullable
		public BufferAllocator.CloseableBuffer sortAndStore(BufferAllocator allocator, VertexSorter sorter) {
			int[] is = sorter.sort(this.centroids);
			long l = allocator.allocate(is.length * 6 * this.indexType.size);
			IntConsumer intConsumer = this.getStorer(l, this.indexType);

			for (int i : is) {
				intConsumer.accept(i * 4 + 0);
				intConsumer.accept(i * 4 + 1);
				intConsumer.accept(i * 4 + 2);
				intConsumer.accept(i * 4 + 2);
				intConsumer.accept(i * 4 + 3);
				intConsumer.accept(i * 4 + 0);
			}

			return allocator.getAllocated();
		}

		private IntConsumer getStorer(long pointer, VertexFormat.IndexType indexTyp) {
			MutableLong mutableLong = new MutableLong(pointer);

			return switch (indexTyp) {
				case SHORT -> i -> MemoryUtil.memPutShort(mutableLong.getAndAdd(2L), (short)i);
				case INT -> i -> MemoryUtil.memPutInt(mutableLong.getAndAdd(4L), i);
			};
		}
	}
}
