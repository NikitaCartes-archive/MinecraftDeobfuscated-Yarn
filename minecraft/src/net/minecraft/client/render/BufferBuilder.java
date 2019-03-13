package net.minecraft.client.render;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BufferBuilder {
	private static final Logger LOGGER = LogManager.getLogger();
	private ByteBuffer bufByte;
	private IntBuffer bufInt;
	private ShortBuffer bufShort;
	private FloatBuffer bufFloat;
	private int vertexCount;
	private VertexFormatElement field_1558;
	private int currentElementId;
	private boolean colorDisabled;
	private int drawMode;
	private double offsetX;
	private double offsetY;
	private double offsetZ;
	private VertexFormat field_1565;
	private boolean building;

	public BufferBuilder(int i) {
		this.bufByte = GlAllocationUtils.allocateByteBuffer(i * 4);
		this.bufInt = this.bufByte.asIntBuffer();
		this.bufShort = this.bufByte.asShortBuffer();
		this.bufFloat = this.bufByte.asFloatBuffer();
	}

	private void grow(int i) {
		if (this.vertexCount * this.field_1565.getVertexSize() + i > this.bufByte.capacity()) {
			int j = this.bufByte.capacity();
			int k = j + roundBufferSize(i);
			LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", j, k);
			int l = this.bufInt.position();
			ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(k);
			this.bufByte.position(0);
			byteBuffer.put(this.bufByte);
			byteBuffer.rewind();
			this.bufByte = byteBuffer;
			this.bufFloat = this.bufByte.asFloatBuffer().asReadOnlyBuffer();
			this.bufInt = this.bufByte.asIntBuffer();
			this.bufInt.position(l);
			this.bufShort = this.bufByte.asShortBuffer();
			this.bufShort.position(l << 1);
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
				this.field_1565.getVertexSizeInteger(),
				j * this.field_1565.getVertexSize()
			);
		}

		Integer[] integers = new Integer[i];

		for (int k = 0; k < integers.length; k++) {
			integers[k] = k;
		}

		Arrays.sort(integers, (integer, integer2) -> Floats.compare(fs[integer2], fs[integer]));
		BitSet bitSet = new BitSet();
		int l = this.field_1565.getVertexSize();
		int[] is = new int[l];

		for (int m = bitSet.nextClearBit(0); m < integers.length; m = bitSet.nextClearBit(m + 1)) {
			int n = integers[m];
			if (n != m) {
				this.bufInt.limit(n * l + l);
				this.bufInt.position(n * l);
				this.bufInt.get(is);
				int o = n;

				for (int p = integers[n]; o != m; p = integers[p]) {
					this.bufInt.limit(p * l + l);
					this.bufInt.position(p * l);
					IntBuffer intBuffer = this.bufInt.slice();
					this.bufInt.limit(o * l + l);
					this.bufInt.position(o * l);
					this.bufInt.put(intBuffer);
					bitSet.set(o);
					o = p;
				}

				this.bufInt.limit(m * l + l);
				this.bufInt.position(m * l);
				this.bufInt.put(is);
			}

			bitSet.set(m);
		}
	}

	public BufferBuilder.State toBufferState() {
		this.bufInt.rewind();
		int i = this.getCurrentSize();
		this.bufInt.limit(i);
		int[] is = new int[i];
		this.bufInt.get(is);
		this.bufInt.limit(this.bufInt.capacity());
		this.bufInt.position(i);
		return new BufferBuilder.State(is, new VertexFormat(this.field_1565));
	}

	private int getCurrentSize() {
		return this.vertexCount * this.field_1565.getVertexSizeInteger();
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
		this.bufInt.clear();
		this.grow(state.getRawBuffer().length * 4);
		this.bufInt.put(state.getRawBuffer());
		this.vertexCount = state.getVertexCount();
		this.field_1565 = new VertexFormat(state.method_1345());
	}

	public void clear() {
		this.vertexCount = 0;
		this.field_1558 = null;
		this.currentElementId = 0;
	}

	public void method_1328(int i, VertexFormat vertexFormat) {
		if (this.building) {
			throw new IllegalStateException("Already building!");
		} else {
			this.building = true;
			this.clear();
			this.drawMode = i;
			this.field_1565 = vertexFormat;
			this.field_1558 = vertexFormat.method_1364(this.currentElementId);
			this.colorDisabled = false;
			this.bufByte.limit(this.bufByte.capacity());
		}
	}

	public BufferBuilder texture(double d, double e) {
		int i = this.vertexCount * this.field_1565.getVertexSize() + this.field_1565.getElementOffset(this.currentElementId);
		switch (this.field_1558.getFormat()) {
			case FLOAT:
				this.bufByte.putFloat(i, (float)d);
				this.bufByte.putFloat(i + 4, (float)e);
				break;
			case UNSIGNED_INT:
			case INT:
				this.bufByte.putInt(i, (int)d);
				this.bufByte.putInt(i + 4, (int)e);
				break;
			case UNSIGNED_SHORT:
			case SHORT:
				this.bufByte.putShort(i, (short)((int)e));
				this.bufByte.putShort(i + 2, (short)((int)d));
				break;
			case UNSIGNED_BYTE:
			case BYTE:
				this.bufByte.put(i, (byte)((int)e));
				this.bufByte.put(i + 1, (byte)((int)d));
		}

		this.nextElement();
		return this;
	}

	public BufferBuilder texture(int i, int j) {
		int k = this.vertexCount * this.field_1565.getVertexSize() + this.field_1565.getElementOffset(this.currentElementId);
		switch (this.field_1558.getFormat()) {
			case FLOAT:
				this.bufByte.putFloat(k, (float)i);
				this.bufByte.putFloat(k + 4, (float)j);
				break;
			case UNSIGNED_INT:
			case INT:
				this.bufByte.putInt(k, i);
				this.bufByte.putInt(k + 4, j);
				break;
			case UNSIGNED_SHORT:
			case SHORT:
				this.bufByte.putShort(k, (short)j);
				this.bufByte.putShort(k + 2, (short)i);
				break;
			case UNSIGNED_BYTE:
			case BYTE:
				this.bufByte.put(k, (byte)j);
				this.bufByte.put(k + 1, (byte)i);
		}

		this.nextElement();
		return this;
	}

	public void brightness(int i, int j, int k, int l) {
		int m = (this.vertexCount - 4) * this.field_1565.getVertexSizeInteger() + this.field_1565.getUvOffset(1) / 4;
		int n = this.field_1565.getVertexSize() >> 2;
		this.bufInt.put(m, i);
		this.bufInt.put(m + n, j);
		this.bufInt.put(m + n * 2, k);
		this.bufInt.put(m + n * 3, l);
	}

	public void postPosition(double d, double e, double f) {
		int i = this.field_1565.getVertexSizeInteger();
		int j = (this.vertexCount - 4) * i;

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
		return ((this.vertexCount - i) * this.field_1565.getVertexSize() + this.field_1565.getColorOffset()) / 4;
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
			return this;
		} else {
			int m = this.vertexCount * this.field_1565.getVertexSize() + this.field_1565.getElementOffset(this.currentElementId);
			switch (this.field_1558.getFormat()) {
				case FLOAT:
					this.bufByte.putFloat(m, (float)i / 255.0F);
					this.bufByte.putFloat(m + 4, (float)j / 255.0F);
					this.bufByte.putFloat(m + 8, (float)k / 255.0F);
					this.bufByte.putFloat(m + 12, (float)l / 255.0F);
					break;
				case UNSIGNED_INT:
				case INT:
					this.bufByte.putFloat(m, (float)i);
					this.bufByte.putFloat(m + 4, (float)j);
					this.bufByte.putFloat(m + 8, (float)k);
					this.bufByte.putFloat(m + 12, (float)l);
					break;
				case UNSIGNED_SHORT:
				case SHORT:
					this.bufByte.putShort(m, (short)i);
					this.bufByte.putShort(m + 2, (short)j);
					this.bufByte.putShort(m + 4, (short)k);
					this.bufByte.putShort(m + 6, (short)l);
					break;
				case UNSIGNED_BYTE:
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

	public void putVertexData(int[] is) {
		this.grow(is.length * 4 + this.field_1565.getVertexSize());
		this.bufInt.position(this.getCurrentSize());
		this.bufInt.put(is);
		this.vertexCount = this.vertexCount + is.length / this.field_1565.getVertexSizeInteger();
	}

	public void next() {
		this.vertexCount++;
		this.grow(this.field_1565.getVertexSize());
	}

	public BufferBuilder vertex(double d, double e, double f) {
		int i = this.vertexCount * this.field_1565.getVertexSize() + this.field_1565.getElementOffset(this.currentElementId);
		switch (this.field_1558.getFormat()) {
			case FLOAT:
				this.bufByte.putFloat(i, (float)(d + this.offsetX));
				this.bufByte.putFloat(i + 4, (float)(e + this.offsetY));
				this.bufByte.putFloat(i + 8, (float)(f + this.offsetZ));
				break;
			case UNSIGNED_INT:
			case INT:
				this.bufByte.putInt(i, Float.floatToRawIntBits((float)(d + this.offsetX)));
				this.bufByte.putInt(i + 4, Float.floatToRawIntBits((float)(e + this.offsetY)));
				this.bufByte.putInt(i + 8, Float.floatToRawIntBits((float)(f + this.offsetZ)));
				break;
			case UNSIGNED_SHORT:
			case SHORT:
				this.bufByte.putShort(i, (short)((int)(d + this.offsetX)));
				this.bufByte.putShort(i + 2, (short)((int)(e + this.offsetY)));
				this.bufByte.putShort(i + 4, (short)((int)(f + this.offsetZ)));
				break;
			case UNSIGNED_BYTE:
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
		int m = this.field_1565.getVertexSize() >> 2;
		int n = (this.vertexCount - 4) * m + this.field_1565.getNormalOffset() / 4;
		this.bufInt.put(n, l);
		this.bufInt.put(n + m, l);
		this.bufInt.put(n + m * 2, l);
		this.bufInt.put(n + m * 3, l);
	}

	private void nextElement() {
		this.currentElementId++;
		this.currentElementId = this.currentElementId % this.field_1565.getElementCount();
		this.field_1558 = this.field_1565.method_1364(this.currentElementId);
		if (this.field_1558.getType() == VertexFormatElement.Type.PADDING) {
			this.nextElement();
		}
	}

	public BufferBuilder normal(float f, float g, float h) {
		int i = this.vertexCount * this.field_1565.getVertexSize() + this.field_1565.getElementOffset(this.currentElementId);
		switch (this.field_1558.getFormat()) {
			case FLOAT:
				this.bufByte.putFloat(i, f);
				this.bufByte.putFloat(i + 4, g);
				this.bufByte.putFloat(i + 8, h);
				break;
			case UNSIGNED_INT:
			case INT:
				this.bufByte.putInt(i, (int)f);
				this.bufByte.putInt(i + 4, (int)g);
				this.bufByte.putInt(i + 8, (int)h);
				break;
			case UNSIGNED_SHORT:
			case SHORT:
				this.bufByte.putShort(i, (short)((int)f * 32767 & 65535));
				this.bufByte.putShort(i + 2, (short)((int)g * 32767 & 65535));
				this.bufByte.putShort(i + 4, (short)((int)h * 32767 & 65535));
				break;
			case UNSIGNED_BYTE:
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

	public void end() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		} else {
			this.building = false;
			this.bufByte.position(0);
			this.bufByte.limit(this.getCurrentSize() * 4);
		}
	}

	public ByteBuffer getByteBuffer() {
		return this.bufByte;
	}

	public VertexFormat method_1311() {
		return this.field_1565;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}

	public int getDrawMode() {
		return this.drawMode;
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

	@Environment(EnvType.CLIENT)
	public class State {
		private final int[] rawBuffer;
		private final VertexFormat field_1570;

		public State(int[] is, VertexFormat vertexFormat) {
			this.rawBuffer = is;
			this.field_1570 = vertexFormat;
		}

		public int[] getRawBuffer() {
			return this.rawBuffer;
		}

		public int getVertexCount() {
			return this.rawBuffer.length / this.field_1570.getVertexSizeInteger();
		}

		public VertexFormat method_1345() {
			return this.field_1570;
		}
	}
}
