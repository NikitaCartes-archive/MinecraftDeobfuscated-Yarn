/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import org.jetbrains.annotations.Nullable;

/**
 * Containing methods for immediately drawing a buffer built with {@link
 * BufferBuilder}.
 */
@Environment(value=EnvType.CLIENT)
public class BufferRenderer {
    @Nullable
    private static VertexBuffer currentVertexBuffer;

    public static void reset() {
        if (currentVertexBuffer != null) {
            BufferRenderer.resetCurrentVertexBuffer();
            VertexBuffer.unbind();
        }
    }

    public static void resetCurrentVertexBuffer() {
        currentVertexBuffer = null;
    }

    /**
     * Draws {@code buffer} using the shader specified with {@link
     * com.mojang.blaze3d.systems.RenderSystem#setShader
     * RenderSystem#setShader}
     */
    public static void drawWithShader(BufferBuilder.BuiltBuffer buffer) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> BufferRenderer.drawWithShaderInternal(buffer));
        } else {
            BufferRenderer.drawWithShaderInternal(buffer);
        }
    }

    private static void drawWithShaderInternal(BufferBuilder.BuiltBuffer buffer) {
        VertexBuffer vertexBuffer = BufferRenderer.upload(buffer);
        if (vertexBuffer != null) {
            vertexBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        }
    }

    /**
     * Draws {@code buffer}.
     * 
     * <p>Unlike {@link #drawWithShader}, the shader cannot be specified with
     * {@link com.mojang.blaze3d.systems.RenderSystem#setShader
     * RenderSystem#setShader}. The caller of this method must manually bind a
     * shader before calling this method.
     */
    public static void draw(BufferBuilder.BuiltBuffer buffer) {
        VertexBuffer vertexBuffer = BufferRenderer.upload(buffer);
        if (vertexBuffer != null) {
            vertexBuffer.draw();
        }
    }

    @Nullable
    private static VertexBuffer upload(BufferBuilder.BuiltBuffer buffer) {
        RenderSystem.assertOnRenderThread();
        if (buffer.isEmpty()) {
            buffer.release();
            return null;
        }
        VertexBuffer vertexBuffer = BufferRenderer.bind(buffer.getParameters().format());
        vertexBuffer.upload(buffer);
        return vertexBuffer;
    }

    private static VertexBuffer bind(VertexFormat vertexFormat) {
        VertexBuffer vertexBuffer = vertexFormat.getBuffer();
        BufferRenderer.bind(vertexBuffer);
        return vertexBuffer;
    }

    private static void bind(VertexBuffer vertexBuffer) {
        if (vertexBuffer != currentVertexBuffer) {
            vertexBuffer.bind();
            currentVertexBuffer = vertexBuffer;
        }
    }
}

