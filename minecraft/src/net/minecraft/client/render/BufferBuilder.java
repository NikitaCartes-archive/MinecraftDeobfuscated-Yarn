package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Floats;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.util.Deque;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BufferBuilder {
	private static final Logger LOGGER = LogManager.getLogger();
	private ByteBuffer bufByte;
	private IntBuffer bufInt;
	private FloatBuffer bufFloat;
	private final List<BufferBuilder.class_4574> field_20774 = Lists.<BufferBuilder.class_4574>newArrayList();
	private int field_20775 = 0;
	private int field_20776 = 0;
	private int field_20777 = 0;
	private int vertexCount;
	private VertexFormatElement currentElement;
	private int currentElementId;
	private boolean colorDisabled;
	private int drawMode;
	private double offsetX;
	private double offsetY;
	private double offsetZ;
	private final Deque<Matrix4f> field_20778 = SystemUtil.consume(Queues.<Matrix4f>newArrayDeque(), arrayDeque -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_22668();
		arrayDeque.add(matrix4f);
	});
	private VertexFormat format;
	private boolean building;

	public BufferBuilder(int i) {
		this.bufByte = GlAllocationUtils.allocateByteBuffer(i * 4);
		this.bufInt = this.bufByte.asIntBuffer();
		this.bufFloat = this.bufByte.asFloatBuffer().asReadOnlyBuffer();
	}

	private void grow(int i) {
		if (this.field_20776 + this.vertexCount * this.format.getVertexSize() + i > this.bufByte.capacity()) {
			int j = this.bufByte.capacity();
			int k = j + roundBufferSize(i);
			LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", j, k);
			ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(k);
			this.bufByte.position(0);
			byteBuffer.put(this.bufByte);
			byteBuffer.rewind();
			this.bufByte = byteBuffer;
			this.bufFloat = this.bufByte.asFloatBuffer().asReadOnlyBuffer();
			this.bufInt = this.bufByte.asIntBuffer();
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
		int i = this.vertexCount / 4;
		float[] fs = new float[i];

		for (int j = 0; j < i; j++) {
			fs[j] = getDistanceSq(
				this.bufFloat,
				(float)((double)f + this.offsetX),
				(float)((double)g + this.offsetY),
				(float)((double)h + this.offsetZ),
				this.format.getVertexSizeInteger(),
				this.field_20776 / 4 + j * this.format.getVertexSize()
			);
		}

		int[] is = new int[i];
		int k = 0;

		while (k < is.length) {
			is[k] = k++;
		}

		IntArrays.quickSort(is, (ix, j) -> Floats.compare(fs[j], fs[ix]));
		BitSet bitSet = new BitSet();
		int[] js = new int[this.format.getVertexSize()];

		for (int l = bitSet.nextClearBit(0); l < is.length; l = bitSet.nextClearBit(l + 1)) {
			int m = is[l];
			if (m != l) {
				this.method_22628(m);
				this.bufInt.get(js);
				int n = m;

				for (int o = is[m]; n != l; o = is[o]) {
					this.method_22628(o);
					IntBuffer intBuffer = this.bufInt.slice();
					this.method_22628(n);
					this.bufInt.put(intBuffer);
					bitSet.set(n);
					n = o;
				}

				this.method_22628(l);
				this.bufInt.put(js);
			}

			bitSet.set(l);
		}
	}

	private void method_22628(int i) {
		int j = this.format.getVertexSizeInteger() * 4;
		this.bufInt.limit(this.field_20776 / 4 + (i + 1) * j);
		this.bufInt.position(this.field_20776 / 4 + i * j);
	}

	public BufferBuilder.State toBufferState() {
		this.bufInt.position(this.field_20776 / 4);
		int i = this.getCurrentSize();
		this.bufInt.limit(i);
		int[] is = new int[this.vertexCount * this.format.getVertexSizeInteger()];
		this.bufInt.get(is);
		return new BufferBuilder.State(is, new VertexFormat(this.format));
	}

	private int getCurrentSize() {
		return this.field_20776 / 4 + this.vertexCount * this.format.getVertexSizeInteger();
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
		this.vertexCount = 0;
		this.grow(state.getRawBuffer().length * 4);
		this.bufInt.limit(this.bufInt.capacity());
		this.bufInt.position(this.field_20776 / 4);
		this.bufInt.put(state.getRawBuffer());
		this.vertexCount = state.getVertexCount();
		this.format = new VertexFormat(state.getFormat());
	}

	public void begin(int i, VertexFormat vertexFormat) {
		if (this.building) {
			throw new IllegalStateException("Already building!");
		} else {
			this.building = true;
			this.drawMode = i;
			this.format = vertexFormat;
			this.currentElement = vertexFormat.getElement(this.currentElementId);
			this.colorDisabled = false;
			this.bufByte.limit(this.bufByte.capacity());
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

	public BufferBuilder texture(double d, double e) {
		int i = this.method_22633();
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.bufByte.putFloat(i, (float)d);
				this.bufByte.putFloat(i + 4, (float)e);
				break;
			case UINT:
			case INT:
				this.bufByte.putInt(i, (int)d);
				this.bufByte.putInt(i + 4, (int)e);
				break;
			case USHORT:
			case SHORT:
				this.bufByte.putShort(i, (short)((int)e));
				this.bufByte.putShort(i + 2, (short)((int)d));
				break;
			case UBYTE:
			case BYTE:
				this.bufByte.put(i, (byte)((int)e));
				this.bufByte.put(i + 1, (byte)((int)d));
		}

		this.nextElement();
		return this;
	}

	public BufferBuilder texture(int i, int j) {
		int k = this.method_22633();
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.bufByte.putFloat(k, (float)i);
				this.bufByte.putFloat(k + 4, (float)j);
				break;
			case UINT:
			case INT:
				this.bufByte.putInt(k, i);
				this.bufByte.putInt(k + 4, j);
				break;
			case USHORT:
			case SHORT:
				this.bufByte.putShort(k, (short)j);
				this.bufByte.putShort(k + 2, (short)i);
				break;
			case UBYTE:
			case BYTE:
				this.bufByte.put(k, (byte)j);
				this.bufByte.put(k + 1, (byte)i);
		}

		this.nextElement();
		return this;
	}

	public void brightness(int i, int j, int k, int l) {
		int m = this.field_20776 / 4 + (this.vertexCount - 4) * this.format.getVertexSizeInteger() + this.format.getUvOffset(1) / 4;
		int n = this.format.getVertexSize() >> 2;
		this.bufInt.put(m, i);
		this.bufInt.put(m + n, j);
		this.bufInt.put(m + n * 2, k);
		this.bufInt.put(m + n * 3, l);
	}

	public void postPosition(double d, double e, double f) {
		int i = this.format.getVertexSizeInteger();
		int j = this.field_20776 / 4 + (this.vertexCount - 4) * i;

		for (int k = 0; k < 4; k++) {
			int l = j + k * i;
			int m = l + 1;
			int n = m + 1;
			this.bufInt.put(l, Float.floatToRawIntBits((float)(d + this.offsetX) + Float.intBitsToFloat(this.bufInt.get(l))));
			this.bufInt.put(m, Float.floatToRawIntBits((float)(e + this.offsetY) + Float.intBitsToFloat(this.bufInt.get(m))));
			this.bufInt.put(n, Float.floatToRawIntBits((float)(f + this.offsetZ) + Float.intBitsToFloat(this.bufInt.get(n))));
		}
	}

	private int getColorIndex(int i) {
		return (this.field_20776 + (this.vertexCount - i) * this.format.getVertexSize() + this.format.getColorOffset()) / 4;
	}

	public void multiplyColor(float f, float g, float h, int i) {
		int j = this.getColorIndex(i);
		int k = -1;
		if (!this.colorDisabled) {
			k = this.bufInt.get(j);
			if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
				int l = (int)((float)(k & 0xFF) * f);
				int m = (int)((float)(k >> 8 & 0xFF) * g);
				int n = (int)((float)(k >> 16 & 0xFF) * h);
				k &= -16777216;
				k |= n << 16 | m << 8 | l;
			} else {
				int l = (int)((float)(k >> 24 & 0xFF) * f);
				int m = (int)((float)(k >> 16 & 0xFF) * g);
				int n = (int)((float)(k >> 8 & 0xFF) * h);
				k &= 255;
				k |= l << 24 | m << 16 | n << 8;
			}
		}

		this.bufInt.put(j, k);
	}

	private void setColor(int i, int j) {
		int k = this.getColorIndex(j);
		int l = i >> 16 & 0xFF;
		int m = i >> 8 & 0xFF;
		int n = i & 0xFF;
		this.setColor(k, l, m, n);
	}

	public void setColor(float f, float g, float h, int i) {
		int j = this.getColorIndex(i);
		int k = clamp((int)(f * 255.0F), 0, 255);
		int l = clamp((int)(g * 255.0F), 0, 255);
		int m = clamp((int)(h * 255.0F), 0, 255);
		this.setColor(j, k, l, m);
	}

	private static int clamp(int i, int j, int k) {
		if (i < j) {
			return j;
		} else {
			return i > k ? k : i;
		}
	}

	private void setColor(int i, int j, int k, int l) {
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
			this.bufInt.put(i, 0xFF000000 | l << 16 | k << 8 | j);
		} else {
			this.bufInt.put(i, j << 24 | k << 16 | l << 8 | 0xFF);
		}
	}

	public void disableColor() {
		this.colorDisabled = true;
	}

	public BufferBuilder color(float f, float g, float h, float i) {
		return this.color((int)(f * 255.0F), (int)(g * 255.0F), (int)(h * 255.0F), (int)(i * 255.0F));
	}

	public BufferBuilder color(int i, int j, int k, int l) {
		if (this.colorDisabled) {
			this.nextElement();
			return this;
		} else {
			int m = this.method_22633();
			switch (this.currentElement.getFormat()) {
				case FLOAT:
					this.bufByte.putFloat(m, (float)i / 255.0F);
					this.bufByte.putFloat(m + 4, (float)j / 255.0F);
					this.bufByte.putFloat(m + 8, (float)k / 255.0F);
					this.bufByte.putFloat(m + 12, (float)l / 255.0F);
					break;
				case UINT:
				case INT:
					this.bufByte.putFloat(m, (float)i);
					this.bufByte.putFloat(m + 4, (float)j);
					this.bufByte.putFloat(m + 8, (float)k);
					this.bufByte.putFloat(m + 12, (float)l);
					break;
				case USHORT:
				case SHORT:
					this.bufByte.putShort(m, (short)i);
					this.bufByte.putShort(m + 2, (short)j);
					this.bufByte.putShort(m + 4, (short)k);
					this.bufByte.putShort(m + 6, (short)l);
					break;
				case UBYTE:
				case BYTE:
					if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
						this.bufByte.put(m, (byte)i);
						this.bufByte.put(m + 1, (byte)j);
						this.bufByte.put(m + 2, (byte)k);
						this.bufByte.put(m + 3, (byte)l);
					} else {
						this.bufByte.put(m, (byte)l);
						this.bufByte.put(m + 1, (byte)k);
						this.bufByte.put(m + 2, (byte)j);
						this.bufByte.put(m + 3, (byte)i);
					}
			}

			this.nextElement();
			return this;
		}
	}

	private int method_22633() {
		return this.field_20776 + this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
	}

	public void putVertexData(int[] is) {
		this.grow(is.length * 4 + this.format.getVertexSize());
		this.bufInt.limit(this.bufInt.capacity());
		this.bufInt.position(this.getCurrentSize());
		this.bufInt.put(is);
		this.vertexCount = this.vertexCount + is.length / this.format.getVertexSizeInteger();
	}

	public void next() {
		this.vertexCount++;
		this.grow(this.format.getVertexSize());
	}

	public BufferBuilder vertex(double d, double e, double f) {
		int i = this.method_22633();
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.bufByte.putFloat(i, (float)(d + this.offsetX));
				this.bufByte.putFloat(i + 4, (float)(e + this.offsetY));
				this.bufByte.putFloat(i + 8, (float)(f + this.offsetZ));
				break;
			case UINT:
			case INT:
				this.bufByte.putInt(i, Float.floatToRawIntBits((float)(d + this.offsetX)));
				this.bufByte.putInt(i + 4, Float.floatToRawIntBits((float)(e + this.offsetY)));
				this.bufByte.putInt(i + 8, Float.floatToRawIntBits((float)(f + this.offsetZ)));
				break;
			case USHORT:
			case SHORT:
				this.bufByte.putShort(i, (short)((int)(d + this.offsetX)));
				this.bufByte.putShort(i + 2, (short)((int)(e + this.offsetY)));
				this.bufByte.putShort(i + 4, (short)((int)(f + this.offsetZ)));
				break;
			case UBYTE:
			case BYTE:
				this.bufByte.put(i, (byte)((int)(d + this.offsetX)));
				this.bufByte.put(i + 1, (byte)((int)(e + this.offsetY)));
				this.bufByte.put(i + 2, (byte)((int)(f + this.offsetZ)));
		}

		this.nextElement();
		return this;
	}

	public void postNormal(float f, float g, float h) {
		int i = (byte)((int)(f * 127.0F)) & 255;
		int j = (byte)((int)(g * 127.0F)) & 255;
		int k = (byte)((int)(h * 127.0F)) & 255;
		int l = i | j << 8 | k << 16;
		int m = this.format.getVertexSize() >> 2;
		int n = this.field_20776 / 4 + (this.vertexCount - 4) * m + this.format.getNormalOffset() / 4;
		this.bufInt.put(n, l);
		this.bufInt.put(n + m, l);
		this.bufInt.put(n + m * 2, l);
		this.bufInt.put(n + m * 3, l);
	}

	private void nextElement() {
		this.currentElementId++;
		this.currentElementId = this.currentElementId % this.format.getElementCount();
		this.currentElement = this.format.getElement(this.currentElementId);
		if (this.currentElement.getType() == VertexFormatElement.Type.PADDING) {
			this.nextElement();
		}
	}

	public BufferBuilder normal(float f, float g, float h) {
		int i = this.method_22633();
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.bufByte.putFloat(i, f);
				this.bufByte.putFloat(i + 4, g);
				this.bufByte.putFloat(i + 8, h);
				break;
			case UINT:
			case INT:
				this.bufByte.putInt(i, (int)f);
				this.bufByte.putInt(i + 4, (int)g);
				this.bufByte.putInt(i + 8, (int)h);
				break;
			case USHORT:
			case SHORT:
				this.bufByte.putShort(i, (short)((int)f * 32767 & 65535));
				this.bufByte.putShort(i + 2, (short)((int)g * 32767 & 65535));
				this.bufByte.putShort(i + 4, (short)((int)h * 32767 & 65535));
				break;
			case UBYTE:
			case BYTE:
				this.bufByte.put(i, (byte)((int)f * 127 & 0xFF));
				this.bufByte.put(i + 1, (byte)((int)g * 127 & 0xFF));
				this.bufByte.put(i + 2, (byte)((int)h * 127 & 0xFF));
		}

		this.nextElement();
		return this;
	}

	public void setOffset(double d, double e, double f) {
		this.offsetX = d;
		this.offsetY = e;
		this.offsetZ = f;
	}

	public void method_22626(double d, double e, double f) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_22668();
		matrix4f.method_22671(new Vector3f((float)d, (float)e, (float)f));
		this.method_22623(matrix4f);
	}

	public void method_22627(float f, float g, float h) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_22668();
		matrix4f.set(0, 0, f);
		matrix4f.set(1, 1, g);
		matrix4f.set(2, 2, h);
		this.method_22623(matrix4f);
	}

	public void method_22623(Matrix4f matrix4f) {
		Matrix4f matrix4f2 = (Matrix4f)this.field_20778.getLast();
		matrix4f2.method_22672(matrix4f);
	}

	public void method_22622(Quaternion quaternion) {
		Matrix4f matrix4f = (Matrix4f)this.field_20778.getLast();
		matrix4f.method_22670(quaternion);
	}

	public void method_22629() {
		this.field_20778.addLast(((Matrix4f)this.field_20778.getLast()).method_22673());
	}

	public void method_22630() {
		this.field_20778.removeLast();
	}

	public Matrix4f method_22631() {
		return (Matrix4f)this.field_20778.getLast();
	}

	public VertexFormat getVertexFormat() {
		return this.format;
	}

	public void setQuadColor(int i) {
		for (int j = 0; j < 4; j++) {
			this.setColor(i, j + 1);
		}
	}

	public void setQuadColor(float f, float g, float h) {
		for (int i = 0; i < 4; i++) {
			this.setColor(f, g, h, i + 1);
		}
	}

	public Pair<BufferBuilder.class_4574, ByteBuffer> method_22632() {
		BufferBuilder.class_4574 lv = (BufferBuilder.class_4574)this.field_20774.get(this.field_20775++);
		this.bufByte.position(this.field_20777);
		this.field_20777 = this.field_20777 + lv.method_22635() * lv.method_22634().getVertexSize();
		this.bufByte.limit(this.field_20777);
		if (this.field_20775 == this.field_20774.size() && this.vertexCount == 0) {
			this.clear();
		}

		ByteBuffer byteBuffer = this.bufByte.slice();
		this.bufByte.position(0);
		this.bufByte.limit(this.bufByte.capacity());
		return Pair.of(lv, byteBuffer);
	}

	public void clear() {
		if (this.field_20776 != this.field_20777) {
			LOGGER.warn("Bytes mismatch " + this.field_20776 + " " + this.field_20777);
		}

		this.field_20776 = 0;
		this.field_20777 = 0;
		this.field_20774.clear();
		this.field_20775 = 0;
	}

	@Environment(EnvType.CLIENT)
	public class State {
		private final int[] rawBuffer;
		private final VertexFormat format;

		public State(int[] is, VertexFormat vertexFormat) {
			this.rawBuffer = is;
			this.format = vertexFormat;
		}

		public int[] getRawBuffer() {
			return this.rawBuffer;
		}

		public int getVertexCount() {
			return this.rawBuffer.length / this.format.getVertexSizeInteger();
		}

		public VertexFormat getFormat() {
			return this.format;
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
