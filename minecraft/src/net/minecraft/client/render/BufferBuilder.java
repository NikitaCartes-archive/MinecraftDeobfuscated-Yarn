package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BufferBuilder extends FixedColorVertexConsumer implements BufferVertexConsumer {
	private static final Logger LOGGER = LogManager.getLogger();
	private ByteBuffer buffer;
	private final List<BufferBuilder.DrawArrayParameters> parameters = Lists.<BufferBuilder.DrawArrayParameters>newArrayList();
	private int lastParameterIndex = 0;
	private int buildStart = 0;
	private int elementOffset = 0;
	private int nextDrawStart = 0;
	private int vertexCount;
	@Nullable
	private VertexFormatElement currentElement;
	private int currentElementId;
	private int drawMode;
	private VertexFormat format;
	private boolean field_21594;
	private boolean field_21595;
	private boolean building;

	public BufferBuilder(int initialCapacity) {
		this.buffer = GlAllocationUtils.allocateByteBuffer(initialCapacity * 4);
	}

	protected void grow() {
		this.grow(this.format.getVertexSize());
	}

	private void grow(int size) {
		if (this.elementOffset + size > this.buffer.capacity()) {
			int i = this.buffer.capacity();
			int j = i + roundBufferSize(size);
			LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
			ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(j);
			this.buffer.position(0);
			byteBuffer.put(this.buffer);
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

	public void sortQuads(float cameraX, float cameraY, float cameraZ) {
		this.buffer.clear();
		FloatBuffer floatBuffer = this.buffer.asFloatBuffer();
		int i = this.vertexCount / 4;
		float[] fs = new float[i];

		for (int j = 0; j < i; j++) {
			fs[j] = getDistanceSq(floatBuffer, cameraX, cameraY, cameraZ, this.format.getVertexSizeInteger(), this.buildStart / 4 + j * this.format.getVertexSize());
		}

		int[] is = new int[i];
		int k = 0;

		while (k < is.length) {
			is[k] = k++;
		}

		IntArrays.mergeSort(is, (ix, j) -> Floats.compare(fs[j], fs[ix]));
		BitSet bitSet = new BitSet();
		FloatBuffer floatBuffer2 = GlAllocationUtils.allocateFloatBuffer(this.format.getVertexSizeInteger() * 4);

		for (int l = bitSet.nextClearBit(0); l < is.length; l = bitSet.nextClearBit(l + 1)) {
			int m = is[l];
			if (m != l) {
				this.method_22628(floatBuffer, m);
				floatBuffer2.clear();
				floatBuffer2.put(floatBuffer);
				int n = m;

				for (int o = is[m]; n != l; o = is[o]) {
					this.method_22628(floatBuffer, o);
					FloatBuffer floatBuffer3 = floatBuffer.slice();
					this.method_22628(floatBuffer, n);
					floatBuffer.put(floatBuffer3);
					bitSet.set(n);
					n = o;
				}

				this.method_22628(floatBuffer, l);
				floatBuffer2.flip();
				floatBuffer.put(floatBuffer2);
			}

			bitSet.set(l);
		}
	}

	private void method_22628(FloatBuffer floatBuffer, int i) {
		int j = this.format.getVertexSizeInteger() * 4;
		floatBuffer.limit(this.buildStart / 4 + (i + 1) * j);
		floatBuffer.position(this.buildStart / 4 + i * j);
	}

	public BufferBuilder.State popState() {
		this.buffer.limit(this.elementOffset);
		this.buffer.position(this.buildStart);
		ByteBuffer byteBuffer = ByteBuffer.allocate(this.vertexCount * this.format.getVertexSize());
		byteBuffer.put(this.buffer);
		this.buffer.clear();
		return new BufferBuilder.State(byteBuffer, this.format);
	}

	private static float getDistanceSq(FloatBuffer buffer, float x, float y, float z, int i, int j) {
		float f = buffer.get(j + i * 0 + 0);
		float g = buffer.get(j + i * 0 + 1);
		float h = buffer.get(j + i * 0 + 2);
		float k = buffer.get(j + i * 1 + 0);
		float l = buffer.get(j + i * 1 + 1);
		float m = buffer.get(j + i * 1 + 2);
		float n = buffer.get(j + i * 2 + 0);
		float o = buffer.get(j + i * 2 + 1);
		float p = buffer.get(j + i * 2 + 2);
		float q = buffer.get(j + i * 3 + 0);
		float r = buffer.get(j + i * 3 + 1);
		float s = buffer.get(j + i * 3 + 2);
		float t = (f + k + n + q) * 0.25F - x;
		float u = (g + l + o + r) * 0.25F - y;
		float v = (h + m + p + s) * 0.25F - z;
		return t * t + u * u + v * v;
	}

	public void restoreState(BufferBuilder.State state) {
		state.buffer.clear();
		int i = state.buffer.capacity();
		this.grow(i);
		this.buffer.limit(this.buffer.capacity());
		this.buffer.position(this.buildStart);
		this.buffer.put(state.buffer);
		this.buffer.clear();
		VertexFormat vertexFormat = state.format;
		this.method_23918(vertexFormat);
		this.vertexCount = i / vertexFormat.getVertexSize();
		this.elementOffset = this.buildStart + this.vertexCount * vertexFormat.getVertexSize();
	}

	public void begin(int drawMode, VertexFormat format) {
		if (this.building) {
			throw new IllegalStateException("Already building!");
		} else {
			this.building = true;
			this.drawMode = drawMode;
			this.method_23918(format);
			this.currentElement = (VertexFormatElement)format.getElements().get(0);
			this.currentElementId = 0;
			this.buffer.clear();
		}
	}

	private void method_23918(VertexFormat vertexFormat) {
		if (this.format != vertexFormat) {
			this.format = vertexFormat;
			boolean bl = vertexFormat == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
			boolean bl2 = vertexFormat == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
			this.field_21594 = bl || bl2;
			this.field_21595 = bl;
		}
	}

	public void end() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		} else {
			this.building = false;
			this.parameters.add(new BufferBuilder.DrawArrayParameters(this.format, this.vertexCount, this.drawMode));
			this.buildStart = this.buildStart + this.vertexCount * this.format.getVertexSize();
			this.vertexCount = 0;
			this.currentElement = null;
			this.currentElementId = 0;
		}
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
		}
	}

	@Override
	public void nextElement() {
		ImmutableList<VertexFormatElement> immutableList = this.format.getElements();
		this.currentElementId = (this.currentElementId + 1) % immutableList.size();
		this.elementOffset = this.elementOffset + this.currentElement.getSize();
		VertexFormatElement vertexFormatElement = (VertexFormatElement)immutableList.get(this.currentElementId);
		this.currentElement = vertexFormatElement;
		if (vertexFormatElement.getType() == VertexFormatElement.Type.field_1629) {
			this.nextElement();
		}

		if (this.colorFixed && this.currentElement.getType() == VertexFormatElement.Type.field_1632) {
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
		} else if (this.field_21594) {
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
			if (this.field_21595) {
				this.putShort(24, (short)(overlay & 65535));
				this.putShort(26, (short)(overlay >> 16 & 65535));
				i = 28;
			} else {
				i = 24;
			}

			this.putShort(i + 0, (short)(light & 65535));
			this.putShort(i + 2, (short)(light >> 16 & 65535));
			this.putByte(i + 4, BufferVertexConsumer.method_24212(normalX));
			this.putByte(i + 5, BufferVertexConsumer.method_24212(normalY));
			this.putByte(i + 6, BufferVertexConsumer.method_24212(normalZ));
			this.elementOffset += i + 8;
			this.next();
		} else {
			super.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);
		}
	}

	public Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> popData() {
		BufferBuilder.DrawArrayParameters drawArrayParameters = (BufferBuilder.DrawArrayParameters)this.parameters.get(this.lastParameterIndex++);
		this.buffer.position(this.nextDrawStart);
		this.nextDrawStart = this.nextDrawStart + drawArrayParameters.getCount() * drawArrayParameters.getVertexFormat().getVertexSize();
		this.buffer.limit(this.nextDrawStart);
		if (this.lastParameterIndex == this.parameters.size() && this.vertexCount == 0) {
			this.clear();
		}

		ByteBuffer byteBuffer = this.buffer.slice();
		this.buffer.clear();
		return Pair.of(drawArrayParameters, byteBuffer);
	}

	public void clear() {
		if (this.buildStart != this.nextDrawStart) {
			LOGGER.warn("Bytes mismatch " + this.buildStart + " " + this.nextDrawStart);
		}

		this.reset();
	}

	public void reset() {
		this.buildStart = 0;
		this.nextDrawStart = 0;
		this.elementOffset = 0;
		this.parameters.clear();
		this.lastParameterIndex = 0;
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

	@Environment(EnvType.CLIENT)
	public static final class DrawArrayParameters {
		private final VertexFormat vertexFormat;
		private final int count;
		private final int mode;

		private DrawArrayParameters(VertexFormat vertexFormat, int count, int mode) {
			this.vertexFormat = vertexFormat;
			this.count = count;
			this.mode = mode;
		}

		public VertexFormat getVertexFormat() {
			return this.vertexFormat;
		}

		public int getCount() {
			return this.count;
		}

		public int getMode() {
			return this.mode;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class State {
		private final ByteBuffer buffer;
		private final VertexFormat format;

		private State(ByteBuffer buffer, VertexFormat format) {
			this.buffer = buffer;
			this.format = format;
		}
	}
}
