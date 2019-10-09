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
public class BufferBuilder extends AbstractVertexConsumer implements BufferVertexConsumer {
	private static final Logger LOGGER = LogManager.getLogger();
	private ByteBuffer buffer;
	private final List<BufferBuilder.class_4574> field_20774 = Lists.<BufferBuilder.class_4574>newArrayList();
	private int field_20775 = 0;
	private int field_20776 = 0;
	private int field_20884 = 0;
	private int field_20777 = 0;
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

	protected void method_22892() {
		this.grow(this.format.getVertexSize());
	}

	private void grow(int i) {
		if (this.field_20884 + i > this.buffer.capacity()) {
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
			fs[j] = getDistanceSq(floatBuffer, f, g, h, this.format.getVertexSizeInteger(), this.field_20776 / 4 + j * this.format.getVertexSize());
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
		floatBuffer.limit(this.field_20776 / 4 + (i + 1) * j);
		floatBuffer.position(this.field_20776 / 4 + i * j);
	}

	public BufferBuilder.State toBufferState() {
		this.buffer.limit(this.field_20884);
		this.buffer.position(this.field_20776);
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
		state.field_20885.clear();
		int i = state.field_20885.capacity();
		this.grow(i);
		this.buffer.limit(this.buffer.capacity());
		this.buffer.position(this.field_20776);
		this.buffer.put(state.field_20885);
		this.buffer.clear();
		this.format = state.format;
		this.vertexCount = i / this.format.getVertexSize();
		this.field_20884 = this.field_20776 + this.vertexCount * this.format.getVertexSize();
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
			this.field_20774.add(new BufferBuilder.class_4574(this.format, this.vertexCount, this.drawMode));
			this.field_20776 = this.field_20776 + this.vertexCount * this.format.getVertexSize();
			this.vertexCount = 0;
			this.currentElement = null;
			this.currentElementId = 0;
		}
	}

	@Override
	public void putByte(int i, byte b) {
		this.buffer.put(this.field_20884 + i, b);
	}

	@Override
	public void putShort(int i, short s) {
		this.buffer.putShort(this.field_20884 + i, s);
	}

	@Override
	public void putFloat(int i, float f) {
		this.buffer.putFloat(this.field_20884 + i, f);
	}

	@Override
	public void next() {
		if (this.currentElementId != 0) {
			throw new IllegalStateException("Not filled all elements of the vertex");
		} else {
			this.vertexCount++;
			this.method_22892();
		}
	}

	@Override
	public void nextElement() {
		ImmutableList<VertexFormatElement> immutableList = this.format.getElements();
		this.currentElementId = (this.currentElementId + 1) % immutableList.size();
		this.field_20884 = this.field_20884 + this.currentElement.getSize();
		VertexFormatElement vertexFormatElement = (VertexFormatElement)immutableList.get(this.currentElementId);
		this.currentElement = vertexFormatElement;
		if (vertexFormatElement.getType() == VertexFormatElement.Type.PADDING) {
			this.nextElement();
		}

		if (this.field_20889 && this.currentElement.getType() == VertexFormatElement.Type.COLOR) {
			BufferVertexConsumer.super.color(this.field_20890, this.field_20891, this.field_20892, this.field_20893);
		}
	}

	@Override
	public VertexConsumer color(int i, int j, int k, int l) {
		if (this.field_20889) {
			throw new IllegalStateException();
		} else {
			return BufferVertexConsumer.super.color(i, j, k, l);
		}
	}

	public Pair<BufferBuilder.class_4574, ByteBuffer> method_22632() {
		BufferBuilder.class_4574 lv = (BufferBuilder.class_4574)this.field_20774.get(this.field_20775++);
		this.buffer.position(this.field_20777);
		this.field_20777 = this.field_20777 + lv.method_22635() * lv.method_22634().getVertexSize();
		this.buffer.limit(this.field_20777);
		if (this.field_20775 == this.field_20774.size() && this.vertexCount == 0) {
			this.clear();
		}

		ByteBuffer byteBuffer = this.buffer.slice();
		this.buffer.clear();
		return Pair.of(lv, byteBuffer);
	}

	public void clear() {
		if (this.field_20776 != this.field_20777) {
			LOGGER.warn("Bytes mismatch " + this.field_20776 + " " + this.field_20777);
		}

		this.method_23477();
	}

	public void method_23477() {
		this.field_20776 = 0;
		this.field_20777 = 0;
		this.field_20884 = 0;
		this.field_20774.clear();
		this.field_20775 = 0;
	}

	@Override
	public VertexFormatElement getCurrentElement() {
		if (this.currentElement == null) {
			throw new IllegalStateException("BufferBuilder not started");
		} else {
			return this.currentElement;
		}
	}

	public boolean method_22893() {
		return this.building;
	}

	@Environment(EnvType.CLIENT)
	public static class State {
		private final ByteBuffer field_20885;
		private final VertexFormat format;

		private State(ByteBuffer byteBuffer, VertexFormat vertexFormat) {
			this.field_20885 = byteBuffer;
			this.format = vertexFormat;
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class class_4574 {
		private final VertexFormat field_20779;
		private final int field_20780;
		private final int field_20781;

		private class_4574(VertexFormat vertexFormat, int i, int j) {
			this.field_20779 = vertexFormat;
			this.field_20780 = i;
			this.field_20781 = j;
		}

		public VertexFormat method_22634() {
			return this.field_20779;
		}

		public int method_22635() {
			return this.field_20780;
		}

		public int method_22636() {
			return this.field_20781;
		}
	}
}
