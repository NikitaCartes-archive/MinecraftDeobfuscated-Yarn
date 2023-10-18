package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

/**
 * Builds a buffer of primitives and optionally sorts them by the distance
 * from the camera.
 * 
 * <p>This builder can sort quad primitives. It sorts them by the distance
 * between the camera position and the center of the quad. Sorting is
 * required when drawing translucent objects because they have to be drawn
 * in back-to-front order. See
 * <a href="https://www.khronos.org/opengl/wiki/Transparency_Sorting">
 * Transparency Sorting - OpenGL Wiki</a>.
 * 
 * <p>For {@link VertexFormat.DrawMode#LINES LINES} and {@link
 * VertexFormat.DrawMode#LINE_STRIP LINE_STRIP} draw modes, this builder
 * duplicates every vertex in a line to produce a quad with zero area. See
 * {@link GameRenderer#getRenderTypeLinesProgram}.
 */
@Environment(EnvType.CLIENT)
public class BufferBuilder extends FixedColorVertexConsumer implements BufferVertexConsumer {
	private static final int ROUND_SIZE_CHANGE = 2097152;
	private static final Logger LOGGER = LogUtils.getLogger();
	private ByteBuffer buffer;
	private boolean closed;
	private int builtBufferCount;
	private int batchOffset;
	private int elementOffset;
	private int vertexCount;
	@Nullable
	private VertexFormatElement currentElement;
	private int currentElementId;
	private VertexFormat format;
	private VertexFormat.DrawMode drawMode;
	/**
	 * Whether this builder is aware of the vertex format and can skip checks
	 * for the current target element while building a vertex in {@link
	 * #vertex(float, float, float, float, float, float, float, float, float, int, int, float, float, float)}.
	 */
	private boolean canSkipElementChecks;
	private boolean hasOverlay;
	private boolean building;
	@Nullable
	private Vector3f[] sortingPrimitiveCenters;
	@Nullable
	private VertexSorter sorter;
	private boolean hasNoVertexBuffer;

	public BufferBuilder(int initialCapacity) {
		this.buffer = GlAllocationUtils.allocateByteBuffer(initialCapacity);
	}

	private void grow() {
		this.grow(this.format.getVertexSizeByte());
	}

	private void grow(int size) {
		if (this.elementOffset + size > this.buffer.capacity()) {
			int i = this.buffer.capacity();
			int j = Math.min(i, 2097152);
			int k = i + size;
			int l = Math.max(i + j, k);
			LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, l);
			ByteBuffer byteBuffer = GlAllocationUtils.resizeByteBuffer(this.buffer, l);
			byteBuffer.rewind();
			this.buffer = byteBuffer;
		}
	}

	public void setSorter(VertexSorter sorter) {
		if (this.drawMode == VertexFormat.DrawMode.QUADS) {
			this.sorter = sorter;
			if (this.sortingPrimitiveCenters == null) {
				this.sortingPrimitiveCenters = this.buildPrimitiveCenters();
			}
		}
	}

	public BufferBuilder.TransparentSortingData getSortingData() {
		return new BufferBuilder.TransparentSortingData(this.drawMode, this.vertexCount, this.sortingPrimitiveCenters, this.sorter);
	}

	private void ensureNotClosed() {
		if (this.closed) {
			throw new IllegalStateException("This BufferBuilder has been closed");
		}
	}

	public void beginSortedIndexBuffer(BufferBuilder.TransparentSortingData state) {
		this.ensureNotClosed();
		this.buffer.rewind();
		this.drawMode = state.drawMode;
		this.vertexCount = state.vertexCount;
		this.elementOffset = this.batchOffset;
		this.sortingPrimitiveCenters = state.primitiveCenters;
		this.sorter = state.sorter;
		this.hasNoVertexBuffer = true;
	}

	public void begin(VertexFormat.DrawMode drawMode, VertexFormat format) {
		if (this.building) {
			throw new IllegalStateException("Already building!");
		} else {
			this.ensureNotClosed();
			this.building = true;
			this.drawMode = drawMode;
			this.setFormat(format);
			this.currentElement = (VertexFormatElement)format.getElements().get(0);
			this.currentElementId = 0;
			this.buffer.rewind();
		}
	}

	private void setFormat(VertexFormat format) {
		if (this.format != format) {
			this.format = format;
			boolean bl = format == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
			boolean bl2 = format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
			this.canSkipElementChecks = bl || bl2;
			this.hasOverlay = bl;
		}
	}

	private IntConsumer getIndexConsumer(int offset, VertexFormat.IndexType indexType) {
		MutableInt mutableInt = new MutableInt(offset);

		return switch (indexType) {
			case SHORT -> index -> this.buffer.putShort(mutableInt.getAndAdd(2), (short)index);
			case INT -> index -> this.buffer.putInt(mutableInt.getAndAdd(4), index);
		};
	}

	private Vector3f[] buildPrimitiveCenters() {
		FloatBuffer floatBuffer = this.buffer.asFloatBuffer();
		int i = this.batchOffset / 4;
		int j = this.format.getVertexSizeInteger();
		int k = j * this.drawMode.additionalVertexCount;
		int l = this.vertexCount / this.drawMode.additionalVertexCount;
		Vector3f[] vector3fs = new Vector3f[l];

		for (int m = 0; m < l; m++) {
			float f = floatBuffer.get(i + m * k + 0);
			float g = floatBuffer.get(i + m * k + 1);
			float h = floatBuffer.get(i + m * k + 2);
			float n = floatBuffer.get(i + m * k + j * 2 + 0);
			float o = floatBuffer.get(i + m * k + j * 2 + 1);
			float p = floatBuffer.get(i + m * k + j * 2 + 2);
			float q = (f + n) / 2.0F;
			float r = (g + o) / 2.0F;
			float s = (h + p) / 2.0F;
			vector3fs[m] = new Vector3f(q, r, s);
		}

		return vector3fs;
	}

	private void writeSortedIndices(VertexFormat.IndexType indexType) {
		if (this.sortingPrimitiveCenters != null && this.sorter != null) {
			int[] is = this.sorter.sort(this.sortingPrimitiveCenters);
			IntConsumer intConsumer = this.getIndexConsumer(this.elementOffset, indexType);

			for (int i : is) {
				intConsumer.accept(i * this.drawMode.additionalVertexCount + 0);
				intConsumer.accept(i * this.drawMode.additionalVertexCount + 1);
				intConsumer.accept(i * this.drawMode.additionalVertexCount + 2);
				intConsumer.accept(i * this.drawMode.additionalVertexCount + 2);
				intConsumer.accept(i * this.drawMode.additionalVertexCount + 3);
				intConsumer.accept(i * this.drawMode.additionalVertexCount + 0);
			}
		} else {
			throw new IllegalStateException("Sorting state uninitialized");
		}
	}

	public boolean isBatchEmpty() {
		return this.vertexCount == 0;
	}

	/**
	 * Builds a buffer if there are vertices in the current batch and resets
	 * the building state.
	 * 
	 * @throws IllegalStateException if this builder has not begun building
	 * 
	 * @return the built buffer if there are vertices, otherwise {@code null}
	 * 
	 * @see #end()
	 */
	@Nullable
	public BufferBuilder.BuiltBuffer endNullable() {
		this.ensureBuilding();
		if (this.isBatchEmpty()) {
			this.resetBuilding();
			return null;
		} else {
			BufferBuilder.BuiltBuffer builtBuffer = this.build();
			this.resetBuilding();
			return builtBuffer;
		}
	}

	/**
	 * Builds a buffer from the current batch and resets the building state.
	 * 
	 * <p>Unlike {@link #endNullable()}, this always builds a buffer even if
	 * there are no vertices in the current batch.
	 * 
	 * @throws IllegalStateException if this builder has not begun building
	 * 
	 * @return the buffer built from the current batch
	 */
	public BufferBuilder.BuiltBuffer end() {
		this.ensureBuilding();
		BufferBuilder.BuiltBuffer builtBuffer = this.build();
		this.resetBuilding();
		return builtBuffer;
	}

	private void ensureBuilding() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		}
	}

	private BufferBuilder.BuiltBuffer build() {
		int i = this.drawMode.getIndexCount(this.vertexCount);
		int j = !this.hasNoVertexBuffer ? this.vertexCount * this.format.getVertexSizeByte() : 0;
		VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(this.vertexCount);
		boolean bl;
		int l;
		if (this.sortingPrimitiveCenters != null) {
			int k = MathHelper.roundUpToMultiple(i * indexType.size, 4);
			this.grow(k);
			this.writeSortedIndices(indexType);
			bl = false;
			this.elementOffset += k;
			l = j + k;
		} else {
			bl = true;
			l = j;
		}

		int k = this.batchOffset;
		this.batchOffset += l;
		this.builtBufferCount++;
		BufferBuilder.DrawParameters drawParameters = new BufferBuilder.DrawParameters(
			this.format, this.vertexCount, i, this.drawMode, indexType, this.hasNoVertexBuffer, bl
		);
		return new BufferBuilder.BuiltBuffer(k, drawParameters);
	}

	private void resetBuilding() {
		this.building = false;
		this.vertexCount = 0;
		this.currentElement = null;
		this.currentElementId = 0;
		this.sortingPrimitiveCenters = null;
		this.sorter = null;
		this.hasNoVertexBuffer = false;
	}

	@Override
	public void putByte(int index, byte value) {
		this.buffer.put(this.elementOffset + index, value);
	}

	@Override
	public void putShort(int index, short value) {
		this.buffer.putShort(this.elementOffset + index, value);
	}

	@Override
	public void putFloat(int index, float value) {
		this.buffer.putFloat(this.elementOffset + index, value);
	}

	@Override
	public void next() {
		if (this.currentElementId != 0) {
			throw new IllegalStateException("Not filled all elements of the vertex");
		} else {
			this.vertexCount++;
			this.grow();
			if (this.drawMode == VertexFormat.DrawMode.LINES || this.drawMode == VertexFormat.DrawMode.LINE_STRIP) {
				int i = this.format.getVertexSizeByte();
				this.buffer.put(this.elementOffset, this.buffer, this.elementOffset - i, i);
				this.elementOffset += i;
				this.vertexCount++;
				this.grow();
			}
		}
	}

	@Override
	public void nextElement() {
		ImmutableList<VertexFormatElement> immutableList = this.format.getElements();
		this.currentElementId = (this.currentElementId + 1) % immutableList.size();
		this.elementOffset = this.elementOffset + this.currentElement.getByteLength();
		VertexFormatElement vertexFormatElement = (VertexFormatElement)immutableList.get(this.currentElementId);
		this.currentElement = vertexFormatElement;
		if (vertexFormatElement.getType() == VertexFormatElement.Type.PADDING) {
			this.nextElement();
		}

		if (this.colorFixed && this.currentElement.getType() == VertexFormatElement.Type.COLOR) {
			BufferVertexConsumer.super.color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha);
		}
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		if (this.colorFixed) {
			throw new IllegalStateException();
		} else {
			return BufferVertexConsumer.super.color(red, green, blue, alpha);
		}
	}

	@Override
	public void vertex(
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
		if (this.colorFixed) {
			throw new IllegalStateException();
		} else if (this.canSkipElementChecks) {
			this.putFloat(0, x);
			this.putFloat(4, y);
			this.putFloat(8, z);
			this.putByte(12, (byte)((int)(red * 255.0F)));
			this.putByte(13, (byte)((int)(green * 255.0F)));
			this.putByte(14, (byte)((int)(blue * 255.0F)));
			this.putByte(15, (byte)((int)(alpha * 255.0F)));
			this.putFloat(16, u);
			this.putFloat(20, v);
			int i;
			if (this.hasOverlay) {
				this.putShort(24, (short)(overlay & 65535));
				this.putShort(26, (short)(overlay >> 16 & 65535));
				i = 28;
			} else {
				i = 24;
			}

			this.putShort(i + 0, (short)(light & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 65295)));
			this.putShort(i + 2, (short)(light >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 65295)));
			this.putByte(i + 4, BufferVertexConsumer.packByte(normalX));
			this.putByte(i + 5, BufferVertexConsumer.packByte(normalY));
			this.putByte(i + 6, BufferVertexConsumer.packByte(normalZ));
			this.elementOffset += i + 8;
			this.next();
		} else {
			super.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);
		}
	}

	void releaseBuiltBuffer() {
		if (this.builtBufferCount > 0 && --this.builtBufferCount == 0) {
			this.clear();
		}
	}

	public void clear() {
		if (this.builtBufferCount > 0) {
			LOGGER.warn("Clearing BufferBuilder with unused batches");
		}

		this.reset();
	}

	public void reset() {
		this.builtBufferCount = 0;
		this.batchOffset = 0;
		this.elementOffset = 0;
	}

	public void close() {
		if (this.builtBufferCount > 0) {
			throw new IllegalStateException("BufferBuilder closed with unused batches");
		} else if (this.building) {
			throw new IllegalStateException("Cannot close BufferBuilder while it is building");
		} else if (!this.closed) {
			this.closed = true;
			GlAllocationUtils.free(this.buffer);
		}
	}

	@Override
	public VertexFormatElement getCurrentElement() {
		if (this.currentElement == null) {
			throw new IllegalStateException("BufferBuilder not started");
		} else {
			return this.currentElement;
		}
	}

	public boolean isBuilding() {
		return this.building;
	}

	ByteBuffer slice(int start, int end) {
		return MemoryUtil.memSlice(this.buffer, start, end - start);
	}

	/**
	 * An output of {@link BufferBuilder}. It contains a {@link ByteBuffer} of
	 * vertices, a {@code ByteBuffer} of indices if sorting has been done, and
	 * some information necessary to draw these data.
	 */
	@Environment(EnvType.CLIENT)
	public class BuiltBuffer {
		private final int batchOffset;
		private final BufferBuilder.DrawParameters parameters;
		private boolean released;

		BuiltBuffer(int batchOffset, BufferBuilder.DrawParameters parameters) {
			this.batchOffset = batchOffset;
			this.parameters = parameters;
		}

		public ByteBuffer getVertexBuffer() {
			int i = this.batchOffset + this.parameters.getVertexBufferStart();
			int j = this.batchOffset + this.parameters.getVertexBufferEnd();
			return BufferBuilder.this.slice(i, j);
		}

		public ByteBuffer getIndexBuffer() {
			int i = this.batchOffset + this.parameters.getIndexBufferStart();
			int j = this.batchOffset + this.parameters.getIndexBufferEnd();
			return BufferBuilder.this.slice(i, j);
		}

		public BufferBuilder.DrawParameters getParameters() {
			return this.parameters;
		}

		public boolean isEmpty() {
			return this.parameters.vertexCount == 0;
		}

		public void release() {
			if (this.released) {
				throw new IllegalStateException("Buffer has already been released!");
			} else {
				BufferBuilder.this.releaseBuiltBuffer();
				this.released = true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static record DrawParameters(
		VertexFormat format,
		int vertexCount,
		int indexCount,
		VertexFormat.DrawMode mode,
		VertexFormat.IndexType indexType,
		boolean indexOnly,
		boolean sequentialIndex
	) {

		public int getVertexBufferSize() {
			return this.vertexCount * this.format.getVertexSizeByte();
		}

		public int getVertexBufferStart() {
			return 0;
		}

		public int getVertexBufferEnd() {
			return this.getVertexBufferSize();
		}

		public int getIndexBufferStart() {
			return this.indexOnly ? 0 : this.getVertexBufferEnd();
		}

		public int getIndexBufferEnd() {
			return this.getIndexBufferStart() + this.getIndexBufferSize();
		}

		private int getIndexBufferSize() {
			return this.sequentialIndex ? 0 : this.indexCount * this.indexType.size;
		}

		/**
		 * {@return the total size of the index buffer and the vertex buffer
		 * combined}
		 */
		public int getBufferSize() {
			return this.getIndexBufferEnd();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class TransparentSortingData {
		final VertexFormat.DrawMode drawMode;
		final int vertexCount;
		@Nullable
		final Vector3f[] primitiveCenters;
		@Nullable
		final VertexSorter sorter;

		TransparentSortingData(VertexFormat.DrawMode drawMode, int vertexCount, @Nullable Vector3f[] primitiveCenters, @Nullable VertexSorter sorter) {
			this.drawMode = drawMode;
			this.vertexCount = vertexCount;
			this.primitiveCenters = primitiveCenters;
			this.sorter = sorter;
		}
	}
}
