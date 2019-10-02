/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormatElement;

@Environment(value=EnvType.CLIENT)
public interface BufferVertexConsumer
extends VertexConsumer {
    public VertexFormatElement getCurrentElement();

    public void nextElement();

    public void putByte(int var1, byte var2);

    public void putShort(int var1, short var2);

    public void putFloat(int var1, float var2);

    @Override
    default public VertexConsumer vertex(double d, double e, double f) {
        if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.FLOAT) {
            throw new IllegalStateException();
        }
        this.putFloat(0, (float)d);
        this.putFloat(4, (float)e);
        this.putFloat(8, (float)f);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer color(int i, int j, int k, int l) {
        if (this.getCurrentElement().getType() != VertexFormatElement.Type.COLOR) {
            return this;
        }
        if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.UBYTE) {
            throw new IllegalStateException();
        }
        this.putByte(0, (byte)i);
        this.putByte(1, (byte)j);
        this.putByte(2, (byte)k);
        this.putByte(3, (byte)l);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer texture(float f, float g) {
        if (this.getCurrentElement().getType() != VertexFormatElement.Type.UV || this.getCurrentElement().getIndex() != 0) {
            return this;
        }
        if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.FLOAT) {
            throw new IllegalStateException();
        }
        this.putFloat(0, f);
        this.putFloat(4, g);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer overlay(int i, int j) {
        return this.texture((short)i, (short)j, 1);
    }

    @Override
    default public VertexConsumer light(int i, int j) {
        return this.texture((short)i, (short)j, 2);
    }

    default public VertexConsumer texture(short s, short t, int i) {
        if (this.getCurrentElement().getType() != VertexFormatElement.Type.UV || this.getCurrentElement().getIndex() != i) {
            return this;
        }
        if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.SHORT) {
            throw new IllegalStateException();
        }
        this.putShort(0, s);
        this.putShort(2, t);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer normal(float f, float g, float h) {
        if (this.getCurrentElement().getType() != VertexFormatElement.Type.NORMAL) {
            return this;
        }
        if (this.getCurrentElement().getFormat() != VertexFormatElement.Format.BYTE) {
            throw new IllegalStateException();
        }
        this.putByte(0, (byte)((int)(f * 127.0f) & 0xFF));
        this.putByte(1, (byte)((int)(g * 127.0f) & 0xFF));
        this.putByte(2, (byte)((int)(h * 127.0f) & 0xFF));
        this.nextElement();
        return this;
    }
}

