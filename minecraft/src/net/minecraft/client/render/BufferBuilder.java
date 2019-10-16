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
	private boolean building;

	public BufferBuilder(int i) {
		this.buffer = GlAllocationUtils.allocateByteBuffer(i * 4);
	}

	protected void grow() {
		this.grow(this.format.getVertexSize());
	}

	private void grow(int i) {
		if (this.elementOffset + i > this.buffer.capacity()) {
			int j = this.buffer.capacity();
			int k = j + roundBufferSize(i);
			LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", j, k);
			ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(k);
			this.buffer.position(0);
			byteBuffer.put(this.buffer);
			byteBuffer.rewind();
			this.buffer = byteBuffer;
		}
	}

	private static int roundBufferSize(int i) {
		int j = 2097152;
		if (i == 0) {
			return j;
		} else {
			if (i < 0) {
				j *= -1;
			}

			int k = i % j;
			return k == 0 ? i : i + j - k;
		}
	}

	public void sortQuads(float f, float g, float h) {
		this.buffer.clear();
		FloatBuffer floatBuffer = this.buffer.asFloatBuffer();
		int i = this.vertexCount / 4;
		float[] fs = new float[i];

		for (int j = 0; j < i; j++) {
			fs[j] = getDistanceSq(floatBuffer, f, g, h, this.format.getVertexSizeInteger(), this.buildStart / 4 + j * this.format.getVertexSize());
		}

		int[] is = new int[i];
		int k = 0;

		while (k < is.length) {
			is[k] = k++;
		}

		IntArrays.quickSort(is, (ix, j) -> Floats.compare(fs[j], fs[ix]));
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

	public BufferBuilder.State toBufferState() {
		this.buffer.limit(this.elementOffset);
		this.buffer.position(this.buildStart);
		ByteBuffer byteBuffer = ByteBuffer.allocate(this.vertexCount * this.format.getVertexSize());
		byteBuffer.put(this.buffer);
		this.buffer.clear();
		return new BufferBuilder.State(byteBuffer, this.format);
	}

	private static float getDistanceSq(FloatBuffer floatBuffer, float f, float g, float h, int i, int j) {
		float k = floatBuffer.get(j + i * 0 + 0);
		float l = floatBuffer.get(j + i * 0 + 1);
		float m = floatBuffer.get(j + i * 0 + 2);
		float n = floatBuffer.get(j + i * 1 + 0);
		float o = floatBuffer.get(j + i * 1 + 1);
		float p = floatBuffer.get(j + i * 1 + 2);
		float q = floatBuffer.get(j + i * 2 + 0);
		float r = floatBuffer.get(j + i * 2 + 1);
		float s = floatBuffer.get(j + i * 2 + 2);
		float t = floatBuffer.get(j + i * 3 + 0);
		float u = floatBuffer.get(j + i * 3 + 1);
		float v = floatBuffer.get(j + i * 3 + 2);
		float w = (k + n + q + t) * 0.25F - f;
		float x = (l + o + r + u) * 0.25F - g;
		float y = (m + p + s + v) * 0.25F - h;
		return w * w + x * x + y * y;
	}

	public void restoreState(BufferBuilder.State state) {
		state.buffer.clear();
		int i = state.buffer.capacity();
		this.grow(i);
		this.buffer.limit(this.buffer.capacity());
		this.buffer.position(this.buildStart);
		this.buffer.put(state.buffer);
		this.buffer.clear();
		this.format = state.format;
		this.vertexCount = i / this.format.getVertexSize();
		this.elementOffset = this.buildStart + this.vertexCount * this.format.getVertexSize();
	}

	public void begin(int i, VertexFormat vertexFormat) {
		if (this.building) {
			throw new IllegalStateException("Already building!");
		} else {
			this.building = true;
			this.drawMode = i;
			this.format = vertexFormat;
			this.currentElement = (VertexFormatElement)vertexFormat.getElements().get(0);
			this.currentElementId = 0;
			this.buffer.clear();
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
	public void putByte(int i, byte b) {
		this.buffer.put(this.elementOffset + i, b);
	}

	@Override
	public void putShort(int i, short s) {
		this.buffer.putShort(this.elementOffset + i, s);
	}

	@Override
	public void putFloat(int i, float f) {
		this.buffer.putFloat(this.elementOffset + i, f);
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
		if (vertexFormatElement.getType() == VertexFormatElement.Type.PADDING) {
			this.nextElement();
		}

		if (this.colorFixed && this.currentElement.getType() == VertexFormatElement.Type.COLOR) {
			BufferVertexConsumer.super.color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha);
		}
	}

	@Override
	public VertexConsumer color(int i, int j, int k, int l) {
		if (this.colorFixed) {
			throw new IllegalStateException();
		} else {
			return BufferVertexConsumer.super.color(i, j, k, l);
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

		private DrawArrayParameters(VertexFormat vertexFormat, int i, int j) {
			this.vertexFormat = vertexFormat;
			this.count = i;
			this.mode = j;
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

		private State(ByteBuffer byteBuffer, VertexFormat vertexFormat) {
			this.buffer = byteBuffer;
			this.format = vertexFormat;
		}
	}
}
