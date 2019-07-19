/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.util.GlAllocationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
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

    public BufferBuilder(int i) {
        this.buffer = GlAllocationUtils.allocateByteBuffer(i * 4);
        this.bufInt = this.buffer.asIntBuffer();
        this.bufShort = this.buffer.asShortBuffer();
        this.bufFloat = this.buffer.asFloatBuffer();
    }

    private void grow(int i) {
        if (this.vertexCount * this.format.getVertexSize() + i <= this.buffer.capacity()) {
            return;
        }
        int j = this.buffer.capacity();
        int k = j + BufferBuilder.roundBufferSize(i);
        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", (Object)j, (Object)k);
        int l = this.bufInt.position();
        ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(k);
        this.buffer.position(0);
        byteBuffer.put(this.buffer);
        byteBuffer.rewind();
        this.buffer = byteBuffer;
        this.bufFloat = this.buffer.asFloatBuffer().asReadOnlyBuffer();
        this.bufInt = this.buffer.asIntBuffer();
        this.bufInt.position(l);
        this.bufShort = this.buffer.asShortBuffer();
        this.bufShort.position(l << 1);
    }

    private static int roundBufferSize(int i) {
        int k;
        int j = 0x200000;
        if (i == 0) {
            return j;
        }
        if (i < 0) {
            j *= -1;
        }
        if ((k = i % j) == 0) {
            return i;
        }
        return i + j - k;
    }

    public void sortQuads(float f, float g, float h) {
        int i = this.vertexCount / 4;
        float[] fs = new float[i];
        for (int j = 0; j < i; ++j) {
            fs[j] = BufferBuilder.getDistanceSq(this.bufFloat, (float)((double)f + this.offsetX), (float)((double)g + this.offsetY), (float)((double)h + this.offsetZ), this.format.getVertexSizeInteger(), j * this.format.getVertexSize());
        }
        Integer[] integers = new Integer[i];
        for (int k = 0; k < integers.length; ++k) {
            integers[k] = k;
        }
        Arrays.sort(integers, (integer, integer2) -> Floats.compare(fs[integer2], fs[integer]));
        BitSet bitSet = new BitSet();
        int l = this.format.getVertexSize();
        int[] is = new int[l];
        int m = bitSet.nextClearBit(0);
        while (m < integers.length) {
            int n = integers[m];
            if (n != m) {
                this.bufInt.limit(n * l + l);
                this.bufInt.position(n * l);
                this.bufInt.get(is);
                int o = n;
                int p = integers[o];
                while (o != m) {
                    this.bufInt.limit(p * l + l);
                    this.bufInt.position(p * l);
                    IntBuffer intBuffer = this.bufInt.slice();
                    this.bufInt.limit(o * l + l);
                    this.bufInt.position(o * l);
                    this.bufInt.put(intBuffer);
                    bitSet.set(o);
                    o = p;
                    p = integers[o];
                }
                this.bufInt.limit(m * l + l);
                this.bufInt.position(m * l);
                this.bufInt.put(is);
            }
            bitSet.set(m);
            m = bitSet.nextClearBit(m + 1);
        }
    }

    public State popState() {
        this.bufInt.rewind();
        int i = this.getCurrentSize();
        this.bufInt.limit(i);
        int[] is = new int[i];
        this.bufInt.get(is);
        this.bufInt.limit(this.bufInt.capacity());
        this.bufInt.position(i);
        return new State(is, new VertexFormat(this.format));
    }

    private int getCurrentSize() {
        return this.vertexCount * this.format.getVertexSizeInteger();
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
        float w = (k + n + q + t) * 0.25f - f;
        float x = (l + o + r + u) * 0.25f - g;
        float y = (m + p + s + v) * 0.25f - h;
        return w * w + x * x + y * y;
    }

    public void restoreState(State state) {
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

    public void begin(int i, VertexFormat vertexFormat) {
        if (this.building) {
            throw new IllegalStateException("Already building!");
        }
        this.building = true;
        this.clear();
        this.drawMode = i;
        this.format = vertexFormat;
        this.currentElement = vertexFormat.getElement(this.currentElementId);
        this.colorDisabled = false;
        this.buffer.limit(this.buffer.capacity());
    }

    public BufferBuilder texture(double d, double e) {
        int i = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.buffer.putFloat(i, (float)d);
                this.buffer.putFloat(i + 4, (float)e);
                break;
            }
            case UINT: 
            case INT: {
                this.buffer.putInt(i, (int)d);
                this.buffer.putInt(i + 4, (int)e);
                break;
            }
            case USHORT: 
            case SHORT: {
                this.buffer.putShort(i, (short)e);
                this.buffer.putShort(i + 2, (short)d);
                break;
            }
            case UBYTE: 
            case BYTE: {
                this.buffer.put(i, (byte)e);
                this.buffer.put(i + 1, (byte)d);
            }
        }
        this.nextElement();
        return this;
    }

    public BufferBuilder texture(int i, int j) {
        int k = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.buffer.putFloat(k, i);
                this.buffer.putFloat(k + 4, j);
                break;
            }
            case UINT: 
            case INT: {
                this.buffer.putInt(k, i);
                this.buffer.putInt(k + 4, j);
                break;
            }
            case USHORT: 
            case SHORT: {
                this.buffer.putShort(k, (short)j);
                this.buffer.putShort(k + 2, (short)i);
                break;
            }
            case UBYTE: 
            case BYTE: {
                this.buffer.put(k, (byte)j);
                this.buffer.put(k + 1, (byte)i);
            }
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

    public void postPosition(double d, double e, double f) {
        int i = this.format.getVertexSizeInteger();
        int j = (this.vertexCount - 4) * i;
        for (int k = 0; k < 4; ++k) {
            int l = j + k * i;
            int m = l + 1;
            int n = m + 1;
            this.bufInt.put(l, Float.floatToRawIntBits((float)(d + this.offsetX) + Float.intBitsToFloat(this.bufInt.get(l))));
            this.bufInt.put(m, Float.floatToRawIntBits((float)(e + this.offsetY) + Float.intBitsToFloat(this.bufInt.get(m))));
            this.bufInt.put(n, Float.floatToRawIntBits((float)(f + this.offsetZ) + Float.intBitsToFloat(this.bufInt.get(n))));
        }
    }

    private int getColorIndex(int i) {
        return ((this.vertexCount - i) * this.format.getVertexSize() + this.format.getColorOffset()) / 4;
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
                k &= 0xFF000000;
                k |= n << 16 | m << 8 | l;
            } else {
                int l = (int)((float)(k >> 24 & 0xFF) * f);
                int m = (int)((float)(k >> 16 & 0xFF) * g);
                int n = (int)((float)(k >> 8 & 0xFF) * h);
                k &= 0xFF;
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
        int k = BufferBuilder.clamp((int)(f * 255.0f), 0, 255);
        int l = BufferBuilder.clamp((int)(g * 255.0f), 0, 255);
        int m = BufferBuilder.clamp((int)(h * 255.0f), 0, 255);
        this.setColor(j, k, l, m);
    }

    private static int clamp(int i, int j, int k) {
        if (i < j) {
            return j;
        }
        if (i > k) {
            return k;
        }
        return i;
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
        return this.color((int)(f * 255.0f), (int)(g * 255.0f), (int)(h * 255.0f), (int)(i * 255.0f));
    }

    public BufferBuilder color(int i, int j, int k, int l) {
        if (this.colorDisabled) {
            return this;
        }
        int m = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.buffer.putFloat(m, (float)i / 255.0f);
                this.buffer.putFloat(m + 4, (float)j / 255.0f);
                this.buffer.putFloat(m + 8, (float)k / 255.0f);
                this.buffer.putFloat(m + 12, (float)l / 255.0f);
                break;
            }
            case UINT: 
            case INT: {
                this.buffer.putFloat(m, i);
                this.buffer.putFloat(m + 4, j);
                this.buffer.putFloat(m + 8, k);
                this.buffer.putFloat(m + 12, l);
                break;
            }
            case USHORT: 
            case SHORT: {
                this.buffer.putShort(m, (short)i);
                this.buffer.putShort(m + 2, (short)j);
                this.buffer.putShort(m + 4, (short)k);
                this.buffer.putShort(m + 6, (short)l);
                break;
            }
            case UBYTE: 
            case BYTE: {
                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    this.buffer.put(m, (byte)i);
                    this.buffer.put(m + 1, (byte)j);
                    this.buffer.put(m + 2, (byte)k);
                    this.buffer.put(m + 3, (byte)l);
                    break;
                }
                this.buffer.put(m, (byte)l);
                this.buffer.put(m + 1, (byte)k);
                this.buffer.put(m + 2, (byte)j);
                this.buffer.put(m + 3, (byte)i);
            }
        }
        this.nextElement();
        return this;
    }

    public void putVertexData(int[] is) {
        this.grow(is.length * 4 + this.format.getVertexSize());
        this.bufInt.position(this.getCurrentSize());
        this.bufInt.put(is);
        this.vertexCount += is.length / this.format.getVertexSizeInteger();
    }

    public void next() {
        ++this.vertexCount;
        this.grow(this.format.getVertexSize());
    }

    public BufferBuilder vertex(double d, double e, double f) {
        int i = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.buffer.putFloat(i, (float)(d + this.offsetX));
                this.buffer.putFloat(i + 4, (float)(e + this.offsetY));
                this.buffer.putFloat(i + 8, (float)(f + this.offsetZ));
                break;
            }
            case UINT: 
            case INT: {
                this.buffer.putInt(i, Float.floatToRawIntBits((float)(d + this.offsetX)));
                this.buffer.putInt(i + 4, Float.floatToRawIntBits((float)(e + this.offsetY)));
                this.buffer.putInt(i + 8, Float.floatToRawIntBits((float)(f + this.offsetZ)));
                break;
            }
            case USHORT: 
            case SHORT: {
                this.buffer.putShort(i, (short)(d + this.offsetX));
                this.buffer.putShort(i + 2, (short)(e + this.offsetY));
                this.buffer.putShort(i + 4, (short)(f + this.offsetZ));
                break;
            }
            case UBYTE: 
            case BYTE: {
                this.buffer.put(i, (byte)(d + this.offsetX));
                this.buffer.put(i + 1, (byte)(e + this.offsetY));
                this.buffer.put(i + 2, (byte)(f + this.offsetZ));
            }
        }
        this.nextElement();
        return this;
    }

    public void postNormal(float f, float g, float h) {
        int i = (byte)(f * 127.0f) & 0xFF;
        int j = (byte)(g * 127.0f) & 0xFF;
        int k = (byte)(h * 127.0f) & 0xFF;
        int l = i | j << 8 | k << 16;
        int m = this.format.getVertexSize() >> 2;
        int n = (this.vertexCount - 4) * m + this.format.getNormalOffset() / 4;
        this.bufInt.put(n, l);
        this.bufInt.put(n + m, l);
        this.bufInt.put(n + m * 2, l);
        this.bufInt.put(n + m * 3, l);
    }

    private void nextElement() {
        ++this.currentElementId;
        this.currentElementId %= this.format.getElementCount();
        this.currentElement = this.format.getElement(this.currentElementId);
        if (this.currentElement.getType() == VertexFormatElement.Type.PADDING) {
            this.nextElement();
        }
    }

    public BufferBuilder normal(float f, float g, float h) {
        int i = this.vertexCount * this.format.getVertexSize() + this.format.getElementOffset(this.currentElementId);
        switch (this.currentElement.getFormat()) {
            case FLOAT: {
                this.buffer.putFloat(i, f);
                this.buffer.putFloat(i + 4, g);
                this.buffer.putFloat(i + 8, h);
                break;
            }
            case UINT: 
            case INT: {
                this.buffer.putInt(i, (int)f);
                this.buffer.putInt(i + 4, (int)g);
                this.buffer.putInt(i + 8, (int)h);
                break;
            }
            case USHORT: 
            case SHORT: {
                this.buffer.putShort(i, (short)((int)f * Short.MAX_VALUE & 0xFFFF));
                this.buffer.putShort(i + 2, (short)((int)g * Short.MAX_VALUE & 0xFFFF));
                this.buffer.putShort(i + 4, (short)((int)h * Short.MAX_VALUE & 0xFFFF));
                break;
            }
            case UBYTE: 
            case BYTE: {
                this.buffer.put(i, (byte)((int)f * 127 & 0xFF));
                this.buffer.put(i + 1, (byte)((int)g * 127 & 0xFF));
                this.buffer.put(i + 2, (byte)((int)h * 127 & 0xFF));
            }
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
        }
        this.building = false;
        this.buffer.position(0);
        this.buffer.limit(this.getCurrentSize() * 4);
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

    public void setQuadColor(int i) {
        for (int j = 0; j < 4; ++j) {
            this.setColor(i, j + 1);
        }
    }

    public void setQuadColor(float f, float g, float h) {
        for (int i = 0; i < 4; ++i) {
            this.setColor(f, g, h, i + 1);
        }
    }

    @Environment(value=EnvType.CLIENT)
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
}

