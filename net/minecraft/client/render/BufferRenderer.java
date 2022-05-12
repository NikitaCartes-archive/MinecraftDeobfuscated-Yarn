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

@Environment(value=EnvType.CLIENT)
public class BufferRenderer {
    @Nullable
    private static VertexBuffer currentVertexBuffer;

    public static void unbindAll() {
        if (currentVertexBuffer != null) {
            BufferRenderer.resetCurrentVertexBuffer();
            VertexBuffer.unbind();
        }
    }

    public static void resetCurrentVertexBuffer() {
        currentVertexBuffer = null;
    }

    public static void drawWithShader(BufferBuilder.BuiltBuffer buffer) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> BufferRenderer.drawWithShaderInternal(buffer));
        } else {
            BufferRenderer.drawWithShaderInternal(buffer);
        }
    }

    private static void drawWithShaderInternal(BufferBuilder.BuiltBuffer buffer) {
        VertexBuffer vertexBuffer = BufferRenderer.getVertexBuffer(buffer);
        if (vertexBuffer != null) {
            vertexBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        }
    }

    public static void drawWithoutShader(BufferBuilder.BuiltBuffer buffer) {
        VertexBuffer vertexBuffer = BufferRenderer.getVertexBuffer(buffer);
        if (vertexBuffer != null) {
            vertexBuffer.drawElements();
        }
    }

    @Nullable
    private static VertexBuffer getVertexBuffer(BufferBuilder.BuiltBuffer buffer) {
        RenderSystem.assertOnRenderThread();
        if (buffer.isEmpty()) {
            buffer.release();
            return null;
        }
        VertexBuffer vertexBuffer = BufferRenderer.bindAndSet(buffer.getParameters().format());
        vertexBuffer.upload(buffer);
        return vertexBuffer;
    }

    private static VertexBuffer bindAndSet(VertexFormat vertexFormat) {
        VertexBuffer vertexBuffer = vertexFormat.getBuffer();
        BufferRenderer.bindAndSet(vertexBuffer);
        return vertexBuffer;
    }

    private static void bindAndSet(VertexBuffer vertexBuffer) {
        if (vertexBuffer != currentVertexBuffer) {
            vertexBuffer.bind();
            currentVertexBuffer = vertexBuffer;
        }
    }
}

