/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;

/**
 * Holding a single instance of {@link BufferBuilder}.
 * 
 * <p>This class reuses the buffer builder so a buffer doesn't have to be
 * allocated every time.
 */
@Environment(value=EnvType.CLIENT)
public class Tessellator {
    private static final int field_32051 = 0x800000;
    private static final int DEFAULT_BUFFER_CAPACITY = 0x200000;
    private final BufferBuilder buffer;
    private static final Tessellator INSTANCE = new Tessellator();

    public static Tessellator getInstance() {
        RenderSystem.assertOnGameThreadOrInit();
        return INSTANCE;
    }

    public Tessellator(int bufferCapacity) {
        this.buffer = new BufferBuilder(bufferCapacity);
    }

    public Tessellator() {
        this(0x200000);
    }

    /**
     * Draws the contents of the buffer builder using the shader specified with
     * {@link com.mojang.blaze3d.systems.RenderSystem#setShader
     * RenderSystem#setShader}.
     */
    public void draw() {
        BufferRenderer.drawWithShader(this.buffer.end());
    }

    public BufferBuilder getBuffer() {
        return this.buffer;
    }
}

