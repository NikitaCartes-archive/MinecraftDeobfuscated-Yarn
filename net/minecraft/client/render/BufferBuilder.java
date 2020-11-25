/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferVertexConsumer;
import net.minecraft.client.render.FixedColorVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BufferBuilder
extends FixedColorVertexConsumer
implements BufferVertexConsumer {
    private static final Logger LOGGER = LogManager.getLogger();
    private ByteBuffer buffer;
    private final List<DrawArrayParameters> parameters = Lists.newArrayList();
    private int lastParameterIndex;
    private int buildStart;
    private int elementOffset;
    private int nextDrawStart;
    private int vertexCount;
    @Nullable
    private VertexFormatElement currentElement;
    private int currentElementId;
    private VertexFormat format;
    private VertexFormat.DrawMode drawMode;
    private boolean field_21594;
    private boolean field_21595;
    private boolean building;
    @Nullable
    private Vec3f[] field_27348;
    private float field_27349 = Float.NaN;
    private float field_27350 = Float.NaN;
    private float field_27351 = Float.NaN;
    private boolean field_27352;

    public BufferBuilder(int initialCapacity) {
        this.buffer = GlAllocationUtils.allocateByteBuffer(initialCapacity * 6);
    }

    private void grow() {
        this.grow(this.format.getVertexSize());
    }

    private void grow(int size) {
        if (this.elementOffset + size <= this.buffer.capacity()) {
            return;
        }
        int i = this.buffer.capacity();
        int j = i + BufferBuilder.roundBufferSize(size);
        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", (Object)i, (Object)j);
        ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(j);
        this.buffer.position(0);
        byteBuffer.put(this.buffer);
        byteBuffer.rewind();
        this.buffer = byteBuffer;
    }

    private static int roundBufferSize(int amount) {
        int j;
        int i = 0x200000;
        if (amount == 0) {
            return i;
        }
        if (amount < 0) {
            i *= -1;
        }
        if ((j = amount % i) == 0) {
            return amount;
        }
        return amount + i - j;
    }

    public void method_31948(float f, float g, float h) {
        if (this.drawMode != VertexFormat.DrawMode.QUADS) {
            return;
        }
        if (this.field_27349 != f || this.field_27350 != g || this.field_27351 != h) {
            this.field_27349 = f;
            this.field_27350 = g;
            this.field_27351 = h;
            if (this.field_27348 == null) {
                this.field_27348 = this.method_31954();
            }
        }
    }

    public class_5594 popState() {
        return new class_5594(this.drawMode, this.vertexCount, this.field_27348, this.field_27349, this.field_27350, this.field_27351);
    }

    public void restoreState(class_5594 arg) {
        this.buffer.clear();
        this.drawMode = arg.field_27358;
        this.vertexCount = arg.field_27359;
        this.elementOffset = this.buildStart;
        this.field_27348 = arg.field_27360;
        this.field_27349 = arg.field_27361;
        this.field_27350 = arg.field_27362;
        this.field_27351 = arg.field_27363;
        this.field_27352 = true;
    }

    public void begin(VertexFormat.DrawMode drawMode, VertexFormat format) {
        if (this.building) {
            throw new IllegalStateException("Already building!");
        }
        this.building = true;
        this.drawMode = drawMode;
        this.setFormat(format);
        this.currentElement = (VertexFormatElement)format.getElements().get(0);
        this.currentElementId = 0;
        this.buffer.clear();
    }

    private void setFormat(VertexFormat format) {
        if (this.format == format) {
            return;
        }
        this.format = format;
        boolean bl = format == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
        boolean bl2 = format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
        this.field_21594 = bl || bl2;
        this.field_21595 = bl;
    }

    private IntConsumer method_31949(VertexFormat.IntType intType) {
        switch (intType) {
            case BYTE: {
                return i -> this.buffer.put((byte)i);
            }
            case SHORT: {
                return i -> this.buffer.putShort((short)i);
            }
        }
        return i -> this.buffer.putInt(i);
    }

    private Vec3f[] method_31954() {
        FloatBuffer floatBuffer = this.buffer.asFloatBuffer();
        int i = this.buildStart / 4;
        int j = this.format.getVertexSizeInteger();
        int k = j * this.drawMode.field_27385;
        int l = this.vertexCount / this.drawMode.field_27385;
        Vec3f[] vec3fs = new Vec3f[l];
        for (int m = 0; m < l; ++m) {
            float f = floatBuffer.get(i + m * k + 0);
            float g = floatBuffer.get(i + m * k + 1);
            float h = floatBuffer.get(i + m * k + 2);
            float n = floatBuffer.get(i + m * k + j * 2 + 0);
            float o = floatBuffer.get(i + m * k + j * 2 + 1);
            float p = floatBuffer.get(i + m * k + j * 2 + 2);
            float q = (f + n) / 2.0f;
            float r = (g + o) / 2.0f;
            float s = (h + p) / 2.0f;
            vec3fs[m] = new Vec3f(q, r, s);
        }
        return vec3fs;
    }

    private void method_31950(VertexFormat.IntType intType) {
        float[] fs = new float[this.field_27348.length];
        int[] is = new int[this.field_27348.length];
        for (int i2 = 0; i2 < this.field_27348.length; ++i2) {
            float f = this.field_27348[i2].getX() - this.field_27349;
            float g = this.field_27348[i2].getY() - this.field_27350;
            float h = this.field_27348[i2].getZ() - this.field_27351;
            fs[i2] = f * f + g * g + h * h;
            is[i2] = i2;
        }
        IntArrays.mergeSort(is, (i, j) -> Floats.compare(fs[j], fs[i]));
        IntConsumer intConsumer = this.method_31949(intType);
        this.buffer.position(this.elementOffset);
        for (int j2 : is) {
            intConsumer.accept(j2 * this.drawMode.field_27385 + 0);
            intConsumer.accept(j2 * this.drawMode.field_27385 + 1);
            intConsumer.accept(j2 * this.drawMode.field_27385 + 2);
            intConsumer.accept(j2 * this.drawMode.field_27385 + 2);
            intConsumer.accept(j2 * this.drawMode.field_27385 + 3);
            intConsumer.accept(j2 * this.drawMode.field_27385 + 0);
        }
    }

    public void end() {
        boolean bl;
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
        int i = this.drawMode.method_31973(this.vertexCount);
        VertexFormat.IntType intType = VertexFormat.IntType.getSmallestTypeFor(i);
        if (this.field_27348 != null) {
            int j = MathHelper.roundUpToMultiple(i * intType.size, 4);
            this.grow(j);
            this.method_31950(intType);
            bl = false;
            this.elementOffset += j;
            this.buildStart += this.vertexCount * this.format.getVertexSize() + j;
        } else {
            bl = true;
            this.buildStart += this.vertexCount * this.format.getVertexSize();
        }
        this.building = false;
        this.parameters.add(new DrawArrayParameters(this.format, this.vertexCount, i, this.drawMode, intType, this.field_27352, bl));
        this.vertexCount = 0;
        this.currentElement = null;
        this.currentElementId = 0;
        this.field_27348 = null;
        this.field_27349 = Float.NaN;
        this.field_27350 = Float.NaN;
        this.field_27351 = Float.NaN;
        this.field_27352 = false;
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
        }
        ++this.vertexCount;
        this.grow();
    }

    @Override
    public void nextElement() {
        VertexFormatElement vertexFormatElement;
        ImmutableList<VertexFormatElement> immutableList = this.format.getElements();
        this.currentElementId = (this.currentElementId + 1) % immutableList.size();
        this.elementOffset += this.currentElement.getSize();
        this.currentElement = vertexFormatElement = (VertexFormatElement)immutableList.get(this.currentElementId);
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
        }
        return BufferVertexConsumer.super.color(red, green, blue, alpha);
    }

    @Override
    public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        if (this.colorFixed) {
            throw new IllegalStateException();
        }
        if (this.field_21594) {
            int i;
            this.putFloat(0, x);
            this.putFloat(4, y);
            this.putFloat(8, z);
            this.putByte(12, (byte)(red * 255.0f));
            this.putByte(13, (byte)(green * 255.0f));
            this.putByte(14, (byte)(blue * 255.0f));
            this.putByte(15, (byte)(alpha * 255.0f));
            this.putFloat(16, u);
            this.putFloat(20, v);
            if (this.field_21595) {
                this.putShort(24, (short)(overlay & 0xFFFF));
                this.putShort(26, (short)(overlay >> 16 & 0xFFFF));
                i = 28;
            } else {
                i = 24;
            }
            this.putShort(i + 0, (short)(light & 0xFFFF));
            this.putShort(i + 2, (short)(light >> 16 & 0xFFFF));
            this.putByte(i + 4, BufferVertexConsumer.method_24212(normalX));
            this.putByte(i + 5, BufferVertexConsumer.method_24212(normalY));
            this.putByte(i + 6, BufferVertexConsumer.method_24212(normalZ));
            this.elementOffset += i + 8;
            this.next();
            return;
        }
        super.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);
    }

    public Pair<DrawArrayParameters, ByteBuffer> popData() {
        DrawArrayParameters drawArrayParameters = this.parameters.get(this.lastParameterIndex++);
        this.buffer.position(this.nextDrawStart);
        this.nextDrawStart += MathHelper.roundUpToMultiple(drawArrayParameters.method_31958(), 4);
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
            LOGGER.warn("Bytes mismatch {} {}", (Object)this.buildStart, (Object)this.nextDrawStart);
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
        }
        return this.currentElement;
    }

    public boolean isBuilding() {
        return this.building;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DrawArrayParameters {
        private final VertexFormat vertexFormat;
        private final int count;
        private final int field_27354;
        private final VertexFormat.DrawMode mode;
        private final VertexFormat.IntType field_27355;
        private final boolean field_27356;
        private final boolean field_27357;

        private DrawArrayParameters(VertexFormat vertexFormat, int count, int mode, VertexFormat.DrawMode drawMode, VertexFormat.IntType intType, boolean bl, boolean bl2) {
            this.vertexFormat = vertexFormat;
            this.count = count;
            this.field_27354 = mode;
            this.mode = drawMode;
            this.field_27355 = intType;
            this.field_27356 = bl;
            this.field_27357 = bl2;
        }

        public VertexFormat getVertexFormat() {
            return this.vertexFormat;
        }

        public int getCount() {
            return this.count;
        }

        public int method_31955() {
            return this.field_27354;
        }

        public VertexFormat.DrawMode getMode() {
            return this.mode;
        }

        public VertexFormat.IntType method_31956() {
            return this.field_27355;
        }

        public int method_31957() {
            return this.count * this.vertexFormat.getVertexSize();
        }

        private int method_31961() {
            return this.field_27357 ? 0 : this.field_27354 * this.field_27355.size;
        }

        public int method_31958() {
            return this.method_31957() + this.method_31961();
        }

        public boolean method_31959() {
            return this.field_27356;
        }

        public boolean method_31960() {
            return this.field_27357;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5594 {
        private final VertexFormat.DrawMode field_27358;
        private final int field_27359;
        @Nullable
        private final Vec3f[] field_27360;
        private final float field_27361;
        private final float field_27362;
        private final float field_27363;

        private class_5594(VertexFormat.DrawMode drawMode, int i, @Nullable Vec3f[] vec3fs, float f, float g, float h) {
            this.field_27358 = drawMode;
            this.field_27359 = i;
            this.field_27360 = vec3fs;
            this.field_27361 = f;
            this.field_27362 = g;
            this.field_27363 = h;
        }
    }
}

