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
	private ByteBuffer buffer;
	private IntBuffer bufInt;
	private ShortBuffer bufShort;
	private FloatBuffer bufFloat;
	private int vertexCount;
	private VertexFormatElement currentElement;
	private int currentElementId;
	private boolean colorDisabled;
	private int drawMode;
	private double offsetX;
	private double offsetY;
	private double offsetZ;
	private VertexFormat format;
	private boolean building;

	public BufferBuilder(int initialCapacity) {
		this.buffer = GlAllocationUtils.allocateByteBuffer(initialCapacity * 4);
		this.bufInt = this.buffer.asIntBuffer();
		this.bufShort = this.buffer.asShortBuffer();
		this.bufFloat = this.buffer.asFloatBuffer();
	}

	private void grow(int size) {
		if (this.vertexCount * this.format.getVertexSize() + size > this.buffer.capacity()) {
			int i = this.buffer.capacity();
			int j = i + roundBufferSize(size);
			LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", i, j);
			int k = this.bufInt.position();
			ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(j);
			this.buffer.position(0);
			byteBuffer.put(this.buffer);
			byteBuffer.rewind();
			this.buffer = byteBuffer;
			this.bufFloat = this.buffer.asFloatBuffer().asReadOnlyBuffer();
			this.bufInt = this.buffer.asIntBuffer();
			this.bufInt.position(k);
			this.bufShort = this.buffer.asShortBuffer();
			this.bufShort.position(k << 1);
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
		int i = this.vertexCount / 4;
		float[] fs = new float[i];

		for (int j = 0; j < i; j++) {
			fs[j] = getDistanceSq(
				this.bufFloat,
				(float)((double)cameraX + this.offsetX),
				(float)((double)cameraY + this.offsetY),
				(float)((double)cameraZ + this.offsetZ),
				this.format.getVertexSizeInteger(),
				j * this.format.getVertexSize()
			);
		}

		Integer[] integers = new Integer[i];

		for (int k = 0; k < integers.length; k++) {
			integers[k] = k;
		}

		Arrays.sort(integers, (integer, integer2) -> Floats.compare(fs[integer2], fs[integer]));
		BitSet bitSet = new BitSet();
		int l = this.format.getVertexSize();
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

	public BufferBuilder.State popState() {
		this.bufInt.rewind();
		int i = this.getCurrentSize();
		this.bufInt.limit(i);
		int[] is = new int[i];
		this.bufInt.get(is);
		this.bufInt.limit(this.bufInt.capacity());
		this.bufInt.position(i);
		return new BufferBuilder.State(is, new VertexFormat(this.format));
	}

	private int getCurrentSize() {
		return this.vertexCount * this.format.getVertexSizeInteger();
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
		this.bufInt.clear();
		this.grow(state.getRawBuffer().length * 4);
		this.bufInt.put(state.getRawBuffer());
		this.vertexCount = state.getVertexCount();
		this.format = new VertexFormat(state.getFormat());
	}

	public void clear() {
		this.vertexCount = 0;
		this.currentElement = null;
		this.currentElementId = 0;
	}

	public void begin(int drawMode, VertexFormat format) {
		if (this.building) {
			throw new IllegalStateException("Already building!");
		} else {
			this.building = true;
			this.clear();
			this.drawMode = drawMode;
			this.format = format;
			this.currentElement = format.getElement(this.currentElementId);
			this.colorDisabled = false;
			this.buffer.limit(this.buffer.capacity());
		}
	}

	public BufferBuilder texture(double u, double v) {
		int i = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.buffer.putFloat(i, (float)u);
				this.buffer.putFloat(i + 4, (float)v);
				break;
			case UINT:
			case INT:
				this.buffer.putInt(i, (int)u);
				this.buffer.putInt(i + 4, (int)v);
				break;
			case USHORT:
			case SHORT:
				this.buffer.putShort(i, (short)((int)v));
				this.buffer.putShort(i + 2, (short)((int)u));
				break;
			case UBYTE:
			case BYTE:
				this.buffer.put(i, (byte)((int)v));
				this.buffer.put(i + 1, (byte)((int)u));
		}

		this.nextElement();
		return this;
	}

	public BufferBuilder texture(int u, int v) {
		int i = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.buffer.putFloat(i, (float)u);
				this.buffer.putFloat(i + 4, (float)v);
				break;
			case UINT:
			case INT:
				this.buffer.putInt(i, u);
				this.buffer.putInt(i + 4, v);
				break;
			case USHORT:
			case SHORT:
				this.buffer.putShort(i, (short)v);
				this.buffer.putShort(i + 2, (short)u);
				break;
			case UBYTE:
			case BYTE:
				this.buffer.put(i, (byte)v);
				this.buffer.put(i + 1, (byte)u);
		}

		this.nextElement();
		return this;
	}

	public void brightness(int i, int j, int k, int l) {
		int m = (this.vertexCount - 4) * this.format.getVertexSizeInteger() + this.format.getUvOffset(1) / 4;
		int n = this.format.getVertexSize() >> 2;
		this.bufInt.put(m, i);
		this.bufInt.put(m + n, j);
		this.bufInt.put(m + n * 2, k);
		this.bufInt.put(m + n * 3, l);
	}

	public void postPosition(double x, double y, double z) {
		int i = this.format.getVertexSizeInteger();
		int j = (this.vertexCount - 4) * i;

		for (int k = 0; k < 4; k++) {
			int l = j + k * i;
			int m = l + 1;
			int n = m + 1;
			this.bufInt.put(l, Float.floatToRawIntBits((float)(x + this.offsetX) + Float.intBitsToFloat(this.bufInt.get(l))));
			this.bufInt.put(m, Float.floatToRawIntBits((float)(y + this.offsetY) + Float.intBitsToFloat(this.bufInt.get(m))));
			this.bufInt.put(n, Float.floatToRawIntBits((float)(z + this.offsetZ) + Float.intBitsToFloat(this.bufInt.get(n))));
		}
	}

	private int getColorIndex(int i) {
		return ((this.vertexCount - i) * this.format.getVertexSize() + this.format.getColorOffset()) / 4;
	}

	public void multiplyColor(float red, float green, float blue, int index) {
		int i = this.getColorIndex(index);
		int j = -1;
		if (!this.colorDisabled) {
			j = this.bufInt.get(i);
			if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
				int k = (int)((float)(j & 0xFF) * red);
				int l = (int)((float)(j >> 8 & 0xFF) * green);
				int m = (int)((float)(j >> 16 & 0xFF) * blue);
				j &= -16777216;
				j |= m << 16 | l << 8 | k;
			} else {
				int k = (int)((float)(j >> 24 & 0xFF) * red);
				int l = (int)((float)(j >> 16 & 0xFF) * green);
				int m = (int)((float)(j >> 8 & 0xFF) * blue);
				j &= 255;
				j |= k << 24 | l << 16 | m << 8;
			}
		}

		this.bufInt.put(i, j);
	}

	private void setColor(int color, int index) {
		int i = this.getColorIndex(index);
		int j = color >> 16 & 0xFF;
		int k = color >> 8 & 0xFF;
		int l = color & 0xFF;
		this.setColor(i, j, k, l);
	}

	public void setColor(float red, float green, float blue, int index) {
		int i = this.getColorIndex(index);
		int j = clamp((int)(red * 255.0F), 0, 255);
		int k = clamp((int)(green * 255.0F), 0, 255);
		int l = clamp((int)(blue * 255.0F), 0, 255);
		this.setColor(i, j, k, l);
	}

	private static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	private void setColor(int colorIndex, int red, int blue, int index) {
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
			this.bufInt.put(colorIndex, 0xFF000000 | index << 16 | blue << 8 | red);
		} else {
			this.bufInt.put(colorIndex, red << 24 | blue << 16 | index << 8 | 0xFF);
		}
	}

	public void disableColor() {
		this.colorDisabled = true;
	}

	public BufferBuilder color(float red, float green, float blue, float f) {
		return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(f * 255.0F));
	}

	public BufferBuilder color(int red, int green, int blue, int i) {
		if (this.colorDisabled) {
			return this;
		} else {
			int j = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
			switch (this.currentElement.getFormat()) {
				case FLOAT:
					this.buffer.putFloat(j, (float)red / 255.0F);
					this.buffer.putFloat(j + 4, (float)green / 255.0F);
					this.buffer.putFloat(j + 8, (float)blue / 255.0F);
					this.buffer.putFloat(j + 12, (float)i / 255.0F);
					break;
				case UINT:
				case INT:
					this.buffer.putFloat(j, (float)red);
					this.buffer.putFloat(j + 4, (float)green);
					this.buffer.putFloat(j + 8, (float)blue);
					this.buffer.putFloat(j + 12, (float)i);
					break;
				case USHORT:
				case SHORT:
					this.buffer.putShort(j, (short)red);
					this.buffer.putShort(j + 2, (short)green);
					this.buffer.putShort(j + 4, (short)blue);
					this.buffer.putShort(j + 6, (short)i);
					break;
				case UBYTE:
				case BYTE:
					if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
						this.buffer.put(j, (byte)red);
						this.buffer.put(j + 1, (byte)green);
						this.buffer.put(j + 2, (byte)blue);
						this.buffer.put(j + 3, (byte)i);
					} else {
						this.buffer.put(j, (byte)i);
						this.buffer.put(j + 1, (byte)blue);
						this.buffer.put(j + 2, (byte)green);
						this.buffer.put(j + 3, (byte)red);
					}
			}

			this.nextElement();
			return this;
		}
	}

	public void putVertexData(int[] is) {
		this.grow(is.length * 4 + this.format.getVertexSize());
		this.bufInt.position(this.getCurrentSize());
		this.bufInt.put(is);
		this.vertexCount = this.vertexCount + is.length / this.format.getVertexSizeInteger();
	}

	public void next() {
		this.vertexCount++;
		this.grow(this.format.getVertexSize());
	}

	public BufferBuilder vertex(double x, double y, double z) {
		int i = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.buffer.putFloat(i, (float)(x + this.offsetX));
				this.buffer.putFloat(i + 4, (float)(y + this.offsetY));
				this.buffer.putFloat(i + 8, (float)(z + this.offsetZ));
				break;
			case UINT:
			case INT:
				this.buffer.putInt(i, Float.floatToRawIntBits((float)(x + this.offsetX)));
				this.buffer.putInt(i + 4, Float.floatToRawIntBits((float)(y + this.offsetY)));
				this.buffer.putInt(i + 8, Float.floatToRawIntBits((float)(z + this.offsetZ)));
				break;
			case USHORT:
			case SHORT:
				this.buffer.putShort(i, (short)((int)(x + this.offsetX)));
				this.buffer.putShort(i + 2, (short)((int)(y + this.offsetY)));
				this.buffer.putShort(i + 4, (short)((int)(z + this.offsetZ)));
				break;
			case UBYTE:
			case BYTE:
				this.buffer.put(i, (byte)((int)(x + this.offsetX)));
				this.buffer.put(i + 1, (byte)((int)(y + this.offsetY)));
				this.buffer.put(i + 2, (byte)((int)(z + this.offsetZ)));
		}

		this.nextElement();
		return this;
	}

	public void postNormal(float x, float y, float z) {
		int i = (byte)((int)(x * 127.0F)) & 255;
		int j = (byte)((int)(y * 127.0F)) & 255;
		int k = (byte)((int)(z * 127.0F)) & 255;
		int l = i | j << 8 | k << 16;
		int m = this.format.getVertexSize() >> 2;
		int n = (this.vertexCount - 4) * m + this.format.getNormalOffset() / 4;
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

	public BufferBuilder normal(float x, float y, float z) {
		int i = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
		switch (this.currentElement.getFormat()) {
			case FLOAT:
				this.buffer.putFloat(i, x);
				this.buffer.putFloat(i + 4, y);
				this.buffer.putFloat(i + 8, z);
				break;
			case UINT:
			case INT:
				this.buffer.putInt(i, (int)x);
				this.buffer.putInt(i + 4, (int)y);
				this.buffer.putInt(i + 8, (int)z);
				break;
			case USHORT:
			case SHORT:
				this.buffer.putShort(i, (short)((int)x * 32767 & 65535));
				this.buffer.putShort(i + 2, (short)((int)y * 32767 & 65535));
				this.buffer.putShort(i + 4, (short)((int)z * 32767 & 65535));
				break;
			case UBYTE:
			case BYTE:
				this.buffer.put(i, (byte)((int)x * 127 & 0xFF));
				this.buffer.put(i + 1, (byte)((int)y * 127 & 0xFF));
				this.buffer.put(i + 2, (byte)((int)z * 127 & 0xFF));
		}

		this.nextElement();
		return this;
	}

	public void setOffset(double x, double y, double z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
	}

	public void end() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		} else {
			this.building = false;
			this.buffer.position(0);
			this.buffer.limit(this.getCurrentSize() * 4);
		}
	}

	public ByteBuffer getByteBuffer() {
		return this.buffer;
	}

	public VertexFormat getVertexFormat() {
		return this.format;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}

	public int getDrawMode() {
		return this.drawMode;
	}

	public void setQuadColor(int color) {
		for (int i = 0; i < 4; i++) {
			this.setColor(color, i + 1);
		}
	}

	public void setQuadColor(float red, float green, float blue) {
		for (int i = 0; i < 4; i++) {
			this.setColor(red, green, blue, i + 1);
		}
	}

	@Environment(EnvType.CLIENT)
	public class State {
		private final int[] rawBuffer;
		private final VertexFormat format;

		public State(int[] rawBuffer, VertexFormat format) {
			this.rawBuffer = rawBuffer;
			this.format = format;
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
}
