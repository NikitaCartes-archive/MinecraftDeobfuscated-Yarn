package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Floats;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BufferBuilder extends FixedColorVertexConsumer implements BufferVertexConsumer {
	private static final int MAX_BUFFER_SIZE = 2097152;
	private static final Logger LOGGER = LogUtils.getLogger();
	private ByteBuffer buffer;
	private int field_39061;
	private int field_39062;
	private int elementOffset;
	private int vertexCount;
	@Nullable
	private VertexFormatElement currentElement;
	private int currentElementId;
	private VertexFormat format;
	private VertexFormat.DrawMode drawMode;
	private boolean textured;
	private boolean hasOverlay;
	private boolean building;
	@Nullable
	private Vec3f[] sortingPrimitiveCenters;
	private float sortingCameraX = Float.NaN;
	private float sortingCameraY = Float.NaN;
	private float sortingCameraZ = Float.NaN;
	private boolean hasNoVertexBuffer;

	public BufferBuilder(int initialCapacity) {
		this.buffer = GlAllocationUtils.allocateByteBuffer(initialCapacity * 6);
	}

	private void grow() {
		this.grow(this.format.getVertexSizeByte());
	}

	private void grow(int size) {
		if (this.elementOffset + size > this.buffer.capacity()) {
			int i = this.buffer.capacity();
			int j = i + roundBufferSize(size);
			LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
			ByteBuffer byteBuffer = GlAllocationUtils.resizeByteBuffer(this.buffer, j);
			byteBuffer.rewind();
			this.buffer = byteBuffer;
		}
	}

	private static int roundBufferSize(int amount) {
		int i = 2097152;
		if (amount == 0) {
			return i;
		} else {
			if (amount < 0) {
				i *= -1;
			}

			int j = amount % i;
			return j == 0 ? amount : amount + i - j;
		}
	}

	public void sortFrom(float cameraX, float cameraY, float cameraZ) {
		if (this.drawMode == VertexFormat.DrawMode.QUADS) {
			if (this.sortingCameraX != cameraX || this.sortingCameraY != cameraY || this.sortingCameraZ != cameraZ) {
				this.sortingCameraX = cameraX;
				this.sortingCameraY = cameraY;
				this.sortingCameraZ = cameraZ;
				if (this.sortingPrimitiveCenters == null) {
					this.sortingPrimitiveCenters = this.buildPrimitiveCenters();
				}
			}
		}
	}

	public BufferBuilder.State popState() {
		return new BufferBuilder.State(this.drawMode, this.vertexCount, this.sortingPrimitiveCenters, this.sortingCameraX, this.sortingCameraY, this.sortingCameraZ);
	}

	public void restoreState(BufferBuilder.State state) {
		this.buffer.rewind();
		this.drawMode = state.drawMode;
		this.vertexCount = state.vertexCount;
		this.elementOffset = this.field_39062;
		this.sortingPrimitiveCenters = state.sortingPrimitiveCenters;
		this.sortingCameraX = state.sortingCameraX;
		this.sortingCameraY = state.sortingCameraY;
		this.sortingCameraZ = state.sortingCameraZ;
		this.hasNoVertexBuffer = true;
	}

	public void begin(VertexFormat.DrawMode drawMode, VertexFormat format) {
		if (this.building) {
			throw new IllegalStateException("Already building!");
		} else {
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
			this.textured = bl || bl2;
			this.hasOverlay = bl;
		}
	}

	private IntConsumer createIndexWriter(int i, VertexFormat.IndexType indexType) {
		MutableInt mutableInt = new MutableInt(i);

		return switch (indexType) {
			case BYTE -> ix -> this.buffer.put(mutableInt.getAndIncrement(), (byte)ix);
			case SHORT -> ix -> this.buffer.putShort(mutableInt.getAndAdd(2), (short)ix);
			case INT -> ix -> this.buffer.putInt(mutableInt.getAndAdd(4), ix);
		};
	}

	private Vec3f[] buildPrimitiveCenters() {
		FloatBuffer floatBuffer = this.buffer.asFloatBuffer();
		int i = this.field_39062 / 4;
		int j = this.format.getVertexSizeInteger();
		int k = j * this.drawMode.additionalVertexCount;
		int l = this.vertexCount / this.drawMode.additionalVertexCount;
		Vec3f[] vec3fs = new Vec3f[l];

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
			vec3fs[m] = new Vec3f(q, r, s);
		}

		return vec3fs;
	}

	private void writeSortedIndices(VertexFormat.IndexType indexType) {
		float[] fs = new float[this.sortingPrimitiveCenters.length];
		int[] is = new int[this.sortingPrimitiveCenters.length];

		for (int i = 0; i < this.sortingPrimitiveCenters.length; is[i] = i++) {
			float f = this.sortingPrimitiveCenters[i].getX() - this.sortingCameraX;
			float g = this.sortingPrimitiveCenters[i].getY() - this.sortingCameraY;
			float h = this.sortingPrimitiveCenters[i].getZ() - this.sortingCameraZ;
			fs[i] = f * f + g * g + h * h;
		}

		IntArrays.mergeSort(is, (a, b) -> Floats.compare(fs[b], fs[a]));
		IntConsumer intConsumer = this.createIndexWriter(this.elementOffset, indexType);

		for (int j : is) {
			intConsumer.accept(j * this.drawMode.additionalVertexCount + 0);
			intConsumer.accept(j * this.drawMode.additionalVertexCount + 1);
			intConsumer.accept(j * this.drawMode.additionalVertexCount + 2);
			intConsumer.accept(j * this.drawMode.additionalVertexCount + 2);
			intConsumer.accept(j * this.drawMode.additionalVertexCount + 3);
			intConsumer.accept(j * this.drawMode.additionalVertexCount + 0);
		}
	}

	public boolean method_43574() {
		return this.vertexCount == 0;
	}

	@Nullable
	public BufferBuilder.class_7433 method_43575() {
		this.method_43577();
		if (this.method_43574()) {
			this.method_43579();
			return null;
		} else {
			BufferBuilder.class_7433 lv = this.method_43578();
			this.method_43579();
			return lv;
		}
	}

	public BufferBuilder.class_7433 end() {
		this.method_43577();
		BufferBuilder.class_7433 lv = this.method_43578();
		this.method_43579();
		return lv;
	}

	private void method_43577() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		}
	}

	private BufferBuilder.class_7433 method_43578() {
		int i = this.drawMode.getIndexCount(this.vertexCount);
		int j = !this.hasNoVertexBuffer ? this.vertexCount * this.format.getVertexSizeByte() : 0;
		VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(i);
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

		int k = this.field_39062;
		this.field_39062 += l;
		this.field_39061++;
		BufferBuilder.DrawArrayParameters drawArrayParameters = new BufferBuilder.DrawArrayParameters(
			this.format, this.vertexCount, i, this.drawMode, indexType, this.hasNoVertexBuffer, bl
		);
		return new BufferBuilder.class_7433(k, drawArrayParameters);
	}

	private void method_43579() {
		this.building = false;
		this.vertexCount = 0;
		this.currentElement = null;
		this.currentElementId = 0;
		this.sortingPrimitiveCenters = null;
		this.sortingCameraX = Float.NaN;
		this.sortingCameraY = Float.NaN;
		this.sortingCameraZ = Float.NaN;
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
		} else if (this.textured) {
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

	void method_43580() {
		if (this.field_39061 > 0 && --this.field_39061 == 0) {
			this.clear();
		}
	}

	public void clear() {
		if (this.field_39061 > 0) {
			LOGGER.warn("Clearing BufferBuilder with unused batches");
		}

		this.reset();
	}

	public void reset() {
		this.field_39061 = 0;
		this.field_39062 = 0;
		this.elementOffset = 0;
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

	ByteBuffer method_43576(int i, int j) {
		return MemoryUtil.memSlice(this.buffer, i, j - i);
	}

	@Environment(EnvType.CLIENT)
	public static record DrawArrayParameters(
		VertexFormat format,
		int vertexCount,
		int indexCount,
		VertexFormat.DrawMode mode,
		VertexFormat.IndexType indexType,
		boolean indexOnly,
		boolean sequentialIndex
	) {

		public int getIndexBufferStart() {
			return this.vertexCount * this.format.getVertexSizeByte();
		}

		public int getVertexBufferPosition() {
			return 0;
		}

		public int getVertexBufferLimit() {
			return this.getIndexBufferStart();
		}

		public int getIndexBufferPosition() {
			return this.indexOnly ? 0 : this.getVertexBufferLimit();
		}

		public int getIndexBufferLimit() {
			return this.getIndexBufferPosition() + this.getIndexBufferLength();
		}

		private int getIndexBufferLength() {
			return this.sequentialIndex ? 0 : this.indexCount * this.indexType.size;
		}

		public int getIndexBufferEnd() {
			return this.getIndexBufferLimit();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class State {
		final VertexFormat.DrawMode drawMode;
		final int vertexCount;
		@Nullable
		final Vec3f[] sortingPrimitiveCenters;
		final float sortingCameraX;
		final float sortingCameraY;
		final float sortingCameraZ;

		State(VertexFormat.DrawMode drawMode, int vertexCount, @Nullable Vec3f[] currentParameters, float cameraX, float cameraY, float cameraZ) {
			this.drawMode = drawMode;
			this.vertexCount = vertexCount;
			this.sortingPrimitiveCenters = currentParameters;
			this.sortingCameraX = cameraX;
			this.sortingCameraY = cameraY;
			this.sortingCameraZ = cameraZ;
		}
	}

	@Environment(EnvType.CLIENT)
	public class class_7433 {
		private final int field_39064;
		private final BufferBuilder.DrawArrayParameters field_39065;
		private boolean field_39066;

		class_7433(int i, BufferBuilder.DrawArrayParameters drawArrayParameters) {
			this.field_39064 = i;
			this.field_39065 = drawArrayParameters;
		}

		public ByteBuffer method_43581() {
			int i = this.field_39064 + this.field_39065.getVertexBufferPosition();
			int j = this.field_39064 + this.field_39065.getVertexBufferLimit();
			return BufferBuilder.this.method_43576(i, j);
		}

		public ByteBuffer method_43582() {
			int i = this.field_39064 + this.field_39065.getIndexBufferPosition();
			int j = this.field_39064 + this.field_39065.getIndexBufferLimit();
			return BufferBuilder.this.method_43576(i, j);
		}

		public BufferBuilder.DrawArrayParameters method_43583() {
			return this.field_39065;
		}

		public boolean method_43584() {
			return this.field_39065.vertexCount == 0;
		}

		public void method_43585() {
			if (this.field_39066) {
				throw new IllegalStateException("Buffer has already been released!");
			} else {
				BufferBuilder.this.method_43580();
				this.field_39066 = true;
			}
		}
	}
}
