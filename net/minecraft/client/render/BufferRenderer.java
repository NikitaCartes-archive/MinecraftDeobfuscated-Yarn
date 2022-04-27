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

    public static void drawWithShader(BufferBuilder.class_7433 arg) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> BufferRenderer.drawWithShaderInternal(arg));
        } else {
            BufferRenderer.drawWithShaderInternal(arg);
        }
    }

    private static void drawWithShaderInternal(BufferBuilder.class_7433 arg) {
        VertexBuffer vertexBuffer = BufferRenderer.getVertexBuffer(arg);
        if (vertexBuffer != null) {
            vertexBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        }
    }

    public static void drawWithoutShader(BufferBuilder.class_7433 arg) {
        VertexBuffer vertexBuffer = BufferRenderer.getVertexBuffer(arg);
        if (vertexBuffer != null) {
            vertexBuffer.drawElements();
        }
    }

    @Nullable
    private static VertexBuffer getVertexBuffer(BufferBuilder.class_7433 arg) {
        RenderSystem.assertOnRenderThread();
        if (arg.method_43584()) {
            arg.method_43585();
            return null;
        }
        VertexBuffer vertexBuffer = BufferRenderer.bindAndSet(arg.method_43583().format());
        vertexBuffer.upload(arg);
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

