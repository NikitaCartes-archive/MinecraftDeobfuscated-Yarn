/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * Stores vertex data on GPU.
 * 
 * <p>If you don't need to change the geometry, you can upload data once
 * and reuse it every frame. For example, {@linkplain
 * net.minecraft.client.render.WorldRenderer#renderStars star rendering}
 * uses this technique to save bandwidth.
 * 
 * @implNote This is mostly a wrapper around vertex buffer object (VBO),
 * element buffer object (EBO), and vertex array object (VAO).
 */
@Environment(value=EnvType.CLIENT)
public class VertexBuffer
implements AutoCloseable {
    private int vertexBufferId;
    private int indexBufferId;
    private int vertexArrayId;
    @Nullable
    private VertexFormat vertexFormat;
    @Nullable
    private RenderSystem.ShapeIndexBuffer sharedSequentialIndexBuffer;
    private VertexFormat.IndexType indexType;
    private int indexCount;
    private VertexFormat.DrawMode drawMode;

    public VertexBuffer() {
        RenderSystem.assertOnRenderThread();
        this.vertexBufferId = GlStateManager._glGenBuffers();
        this.indexBufferId = GlStateManager._glGenBuffers();
        this.vertexArrayId = GlStateManager._glGenVertexArrays();
    }

    /**
     * Uploads the contents of {@code buffer} to GPU, discarding previously
     * uploaded data.
     * 
     * <p>The caller of this method must {@linkplain #bind bind} this vertex
     * buffer before calling this method.
     */
    public void upload(BufferBuilder.BuiltBuffer buffer) {
        if (this.isClosed()) {
            return;
        }
        RenderSystem.assertOnRenderThread();
        try {
            BufferBuilder.DrawParameters drawParameters = buffer.getParameters();
            this.vertexFormat = this.uploadVertexBuffer(drawParameters, buffer.getVertexBuffer());
            this.sharedSequentialIndexBuffer = this.uploadIndexBuffer(drawParameters, buffer.getIndexBuffer());
            this.indexCount = drawParameters.indexCount();
            this.indexType = drawParameters.indexType();
            this.drawMode = drawParameters.mode();
        } finally {
            buffer.release();
        }
    }

    private VertexFormat uploadVertexBuffer(BufferBuilder.DrawParameters parameters, ByteBuffer vertexBuffer) {
        boolean bl = false;
        if (!parameters.format().equals(this.vertexFormat)) {
            if (this.vertexFormat != null) {
                this.vertexFormat.clearState();
            }
            GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, this.vertexBufferId);
            parameters.format().setupState();
            bl = true;
        }
        if (!parameters.indexOnly()) {
            if (!bl) {
                GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, this.vertexBufferId);
            }
            RenderSystem.glBufferData(GlConst.GL_ARRAY_BUFFER, vertexBuffer, GlConst.GL_STATIC_DRAW);
        }
        return parameters.format();
    }

    @Nullable
    private RenderSystem.ShapeIndexBuffer uploadIndexBuffer(BufferBuilder.DrawParameters parameters, ByteBuffer indexBuffer) {
        if (parameters.sequentialIndex()) {
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(parameters.mode());
            if (shapeIndexBuffer != this.sharedSequentialIndexBuffer || !shapeIndexBuffer.isLargeEnough(parameters.indexCount())) {
                shapeIndexBuffer.bindAndGrow(parameters.indexCount());
            }
            return shapeIndexBuffer;
        }
        GlStateManager._glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
        RenderSystem.glBufferData(GlConst.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GlConst.GL_STATIC_DRAW);
        return null;
    }

    /**
     * Sets this vertex buffer as the current one.
     * 
     * <p>This method must be called before uploading or drawing data.
     */
    public void bind() {
        BufferRenderer.resetCurrentVertexBuffer();
        GlStateManager._glBindVertexArray(this.vertexArrayId);
    }

    public static void unbind() {
        BufferRenderer.resetCurrentVertexBuffer();
        GlStateManager._glBindVertexArray(0);
    }

    /**
     * Draws the contents in this vertex buffer.
     * 
     * <p>The caller of this method must {@linkplain #bind bind} this vertex
     * buffer before calling this method.
     * 
     * <p>Unlike {@link #draw(Matrix4f, Matrix4f, ShaderProgram)}, the caller
     * of this method must manually bind a shader program before calling this
     * method.
     */
    public void draw() {
        RenderSystem.drawElements(this.drawMode.glMode, this.indexCount, this.getIndexType().glType);
    }

    private VertexFormat.IndexType getIndexType() {
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = this.sharedSequentialIndexBuffer;
        return shapeIndexBuffer != null ? shapeIndexBuffer.getIndexType() : this.indexType;
    }

    /**
     * Draws the contents in this vertex buffer with {@code program}.
     * 
     * <p>The caller of this method must {@linkplain #bind bind} this vertex
     * buffer before calling this method.
     */
    public void draw(Matrix4f viewMatrix, Matrix4f projectionMatrix, ShaderProgram program) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.drawInternal(new Matrix4f(viewMatrix), new Matrix4f(projectionMatrix), program));
        } else {
            this.drawInternal(viewMatrix, projectionMatrix, program);
        }
    }

    private void drawInternal(Matrix4f viewMatrix, Matrix4f projectionMatrix, ShaderProgram program) {
        for (int i = 0; i < 12; ++i) {
            int j = RenderSystem.getShaderTexture(i);
            program.addSampler("Sampler" + i, j);
        }
        if (program.modelViewMat != null) {
            program.modelViewMat.set(viewMatrix);
        }
        if (program.projectionMat != null) {
            program.projectionMat.set(projectionMatrix);
        }
        if (program.viewRotationMat != null) {
            program.viewRotationMat.set(RenderSystem.getInverseViewRotationMatrix());
        }
        if (program.colorModulator != null) {
            program.colorModulator.set(RenderSystem.getShaderColor());
        }
        if (program.glintAlpha != null) {
            program.glintAlpha.set(RenderSystem.getShaderGlintAlpha());
        }
        if (program.fogStart != null) {
            program.fogStart.set(RenderSystem.getShaderFogStart());
        }
        if (program.fogEnd != null) {
            program.fogEnd.set(RenderSystem.getShaderFogEnd());
        }
        if (program.fogColor != null) {
            program.fogColor.set(RenderSystem.getShaderFogColor());
        }
        if (program.fogShape != null) {
            program.fogShape.set(RenderSystem.getShaderFogShape().getId());
        }
        if (program.textureMat != null) {
            program.textureMat.set(RenderSystem.getTextureMatrix());
        }
        if (program.gameTime != null) {
            program.gameTime.set(RenderSystem.getShaderGameTime());
        }
        if (program.screenSize != null) {
            Window window = MinecraftClient.getInstance().getWindow();
            program.screenSize.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
        }
        if (program.lineWidth != null && (this.drawMode == VertexFormat.DrawMode.LINES || this.drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
            program.lineWidth.set(RenderSystem.getShaderLineWidth());
        }
        RenderSystem.setupShaderLights(program);
        program.bind();
        this.draw();
        program.unbind();
    }

    @Override
    public void close() {
        if (this.vertexBufferId >= 0) {
            RenderSystem.glDeleteBuffers(this.vertexBufferId);
            this.vertexBufferId = -1;
        }
        if (this.indexBufferId >= 0) {
            RenderSystem.glDeleteBuffers(this.indexBufferId);
            this.indexBufferId = -1;
        }
        if (this.vertexArrayId >= 0) {
            RenderSystem.glDeleteVertexArrays(this.vertexArrayId);
            this.vertexArrayId = -1;
        }
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public boolean isClosed() {
        return this.vertexArrayId == -1;
    }
}

