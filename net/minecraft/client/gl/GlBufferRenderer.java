/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;

@Environment(value=EnvType.CLIENT)
public class GlBufferRenderer
extends BufferRenderer {
    private GlBuffer glBuffer;

    @Override
    public void draw(BufferBuilder bufferBuilder) {
        bufferBuilder.clear();
        this.glBuffer.set(bufferBuilder.getByteBuffer());
    }

    public void setGlBuffer(GlBuffer glBuffer) {
        this.glBuffer = glBuffer;
    }
}

