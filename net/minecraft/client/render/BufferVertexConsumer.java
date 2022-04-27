/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public interface BufferVertexConsumer
extends VertexConsumer {
    public VertexFormatElement getCurrentElement();

    public void nextElement();

    public void putByte(int var1, byte var2);

    public void putShort(int var1, short var2);

    public void putFloat(int var1, float var2);

    @Override
    default public VertexConsumer vertex(double x, double y, double z) {
        if (this.getCurrentElement().getType() != VertexFormatElement.Type.POSITION) {
            return this;
        }
        if (this.getCurrentElement().getComponentType() != VertexFormatElement.ComponentType.FLOAT || this.getCurrentElement().getComponentCount() != 3) {
            throw new IllegalStateException();
        }
        this.putFloat(0, (float)x);
        this.putFloat(4, (float)y);
        this.putFloat(8, (float)z);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer color(int red, int green, int blue, int alpha) {
        VertexFormatElement vertexFormatElement = this.getCurrentElement();
        if (vertexFormatElement.getType() != VertexFormatElement.Type.COLOR) {
            return this;
        }
        if (vertexFormatElement.getComponentType() != VertexFormatElement.ComponentType.UBYTE || vertexFormatElement.getComponentCount() != 4) {
            throw new IllegalStateException();
        }
        this.putByte(0, (byte)red);
        this.putByte(1, (byte)green);
        this.putByte(2, (byte)blue);
        this.putByte(3, (byte)alpha);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer texture(float u, float v) {
        VertexFormatElement vertexFormatElement = this.getCurrentElement();
        if (vertexFormatElement.getType() != VertexFormatElement.Type.UV || vertexFormatElement.getUvIndex() != 0) {
            return this;
        }
        if (vertexFormatElement.getComponentType() != VertexFormatElement.ComponentType.FLOAT || vertexFormatElement.getComponentCount() != 2) {
            throw new IllegalStateException();
        }
        this.putFloat(0, u);
        this.putFloat(4, v);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer overlay(int u, int v) {
        return this.texture((short)u, (short)v, 1);
    }

    @Override
    default public VertexConsumer light(int u, int v) {
        return this.texture((short)u, (short)v, 2);
    }

    default public VertexConsumer texture(short u, short v, int index) {
        VertexFormatElement vertexFormatElement = this.getCurrentElement();
        if (vertexFormatElement.getType() != VertexFormatElement.Type.UV || vertexFormatElement.getUvIndex() != index) {
            return this;
        }
        if (vertexFormatElement.getComponentType() != VertexFormatElement.ComponentType.SHORT || vertexFormatElement.getComponentCount() != 2) {
            throw new IllegalStateException();
        }
        this.putShort(0, u);
        this.putShort(2, v);
        this.nextElement();
        return this;
    }

    @Override
    default public VertexConsumer normal(float x, float y, float z) {
        VertexFormatElement vertexFormatElement = this.getCurrentElement();
        if (vertexFormatElement.getType() != VertexFormatElement.Type.NORMAL) {
            return this;
        }
        if (vertexFormatElement.getComponentType() != VertexFormatElement.ComponentType.BYTE || vertexFormatElement.getComponentCount() != 3) {
            throw new IllegalStateException();
        }
        this.putByte(0, BufferVertexConsumer.packByte(x));
        this.putByte(1, BufferVertexConsumer.packByte(y));
        this.putByte(2, BufferVertexConsumer.packByte(z));
        this.nextElement();
        return this;
    }

    public static byte packByte(float f) {
        return (byte)((int)(MathHelper.clamp(f, -1.0f, 1.0f) * 127.0f) & 0xFF);
    }
}

