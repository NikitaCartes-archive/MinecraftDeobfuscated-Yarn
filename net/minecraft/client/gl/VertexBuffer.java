/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class VertexBuffer
implements AutoCloseable {
    private int vertexBufferId;
    private int indexBufferId;
    private int vertexArrayId;
    @Nullable
    private VertexFormat vertexFormat;
    @Nullable
    private RenderSystem.IndexBuffer indexBuffer;
    private VertexFormat.IntType elementFormat;
    private int vertexCount;
    private VertexFormat.DrawMode drawMode;

    public VertexBuffer() {
        RenderSystem.assertOnRenderThread();
        this.vertexBufferId = GlStateManager._glGenBuffers();
        this.indexBufferId = GlStateManager._glGenBuffers();
        this.vertexArrayId = GlStateManager._glGenVertexArrays();
    }

    public CompletableFuture<Void> submitUpload(BufferBuilder buffer) {
        if (!RenderSystem.isOnRenderThread()) {
            return CompletableFuture.runAsync(() -> this.uploadInternal(buffer), action -> RenderSystem.recordRenderCall(action::run));
        }
        this.uploadInternal(buffer);
        return CompletableFuture.completedFuture(null);
    }

    private void uploadInternal(BufferBuilder builder) {
        if (!this.isClosed()) {
            this.bind();
            this.upload(builder);
            VertexBuffer.unbind();
        }
    }

    public void upload(BufferBuilder buffer) {
        RenderSystem.assertOnRenderThread();
        Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = buffer.popData();
        this.setFromParameters(pair.getFirst(), pair.getSecond());
    }

    public void setFromParameters(BufferBuilder.DrawArrayParameters parameters, ByteBuffer data) {
        if (this.isClosed()) {
            return;
        }
        this.vertexFormat = this.configureVertexFormat(parameters, data);
        this.indexBuffer = this.configureIndexBuffer(parameters, data);
        data.limit(parameters.getIndexBufferEnd());
        data.position(0);
        this.vertexCount = parameters.getVertexCount();
        this.elementFormat = parameters.getElementFormat();
        this.drawMode = parameters.getMode();
    }

    private VertexFormat configureVertexFormat(BufferBuilder.DrawArrayParameters parameters, ByteBuffer data) {
        boolean bl = false;
        if (!parameters.getVertexFormat().equals(this.vertexFormat)) {
            if (this.vertexFormat != null) {
                this.vertexFormat.endDrawing();
            }
            GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, this.vertexBufferId);
            parameters.getVertexFormat().startDrawing();
            bl = true;
        }
        if (!parameters.hasNoVertexBuffer()) {
            if (!bl) {
                GlStateManager._glBindBuffer(GlConst.GL_ARRAY_BUFFER, this.vertexBufferId);
            }
            data.position(parameters.getVertexBufferPosition());
            data.limit(parameters.getVertexBufferLimit());
            RenderSystem.glBufferData(GlConst.GL_ARRAY_BUFFER, data, GlConst.GL_STATIC_DRAW);
        }
        return parameters.getVertexFormat();
    }

    @Nullable
    private RenderSystem.IndexBuffer configureIndexBuffer(BufferBuilder.DrawArrayParameters parameters, ByteBuffer data) {
        if (parameters.hasNoIndexBuffer()) {
            RenderSystem.IndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(parameters.getMode());
            if (indexBuffer != this.indexBuffer || !indexBuffer.isSizeLessThanOrEqual(parameters.getVertexCount())) {
                indexBuffer.bindAndGrow(parameters.getVertexCount());
            }
            return indexBuffer;
        }
        GlStateManager._glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
        data.position(parameters.getIndexBufferPosition());
        data.limit(parameters.getIndexBufferLimit());
        RenderSystem.glBufferData(GlConst.GL_ELEMENT_ARRAY_BUFFER, data, GlConst.GL_STATIC_DRAW);
        return null;
    }

    public void bind() {
        BufferRenderer.resetCurrentVertexBuffer();
        GlStateManager._glBindVertexArray(this.vertexArrayId);
    }

    public static void unbind() {
        BufferRenderer.resetCurrentVertexBuffer();
        GlStateManager._glBindVertexArray(0);
    }

    public void drawElements() {
        RenderSystem.drawElements(this.drawMode.mode, this.vertexCount, this.getElementFormat().type);
    }

    private VertexFormat.IntType getElementFormat() {
        RenderSystem.IndexBuffer indexBuffer = this.indexBuffer;
        return indexBuffer != null ? indexBuffer.getElementFormat() : this.elementFormat;
    }

    public void draw(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.drawInternal(viewMatrix.copy(), projectionMatrix.copy(), shader));
        } else {
            this.drawInternal(viewMatrix, projectionMatrix, shader);
        }
    }

    private void drawInternal(Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
        for (int i = 0; i < 12; ++i) {
            int j = RenderSystem.getShaderTexture(i);
            shader.addSampler("Sampler" + i, j);
        }
        if (shader.modelViewMat != null) {
            shader.modelViewMat.set(viewMatrix);
        }
        if (shader.projectionMat != null) {
            shader.projectionMat.set(projectionMatrix);
        }
        if (shader.viewRotationMat != null) {
            shader.viewRotationMat.set(RenderSystem.getInverseViewRotationMatrix());
        }
        if (shader.colorModulator != null) {
            shader.colorModulator.set(RenderSystem.getShaderColor());
        }
        if (shader.fogStart != null) {
            shader.fogStart.set(RenderSystem.getShaderFogStart());
        }
        if (shader.fogEnd != null) {
            shader.fogEnd.set(RenderSystem.getShaderFogEnd());
        }
        if (shader.fogColor != null) {
            shader.fogColor.set(RenderSystem.getShaderFogColor());
        }
        if (shader.fogShape != null) {
            shader.fogShape.set(RenderSystem.getShaderFogShape().getId());
        }
        if (shader.textureMat != null) {
            shader.textureMat.set(RenderSystem.getTextureMatrix());
        }
        if (shader.gameTime != null) {
            shader.gameTime.set(RenderSystem.getShaderGameTime());
        }
        if (shader.screenSize != null) {
            Window window = MinecraftClient.getInstance().getWindow();
            shader.screenSize.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
        }
        if (shader.lineWidth != null && (this.drawMode == VertexFormat.DrawMode.LINES || this.drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
            shader.lineWidth.set(RenderSystem.getShaderLineWidth());
        }
        RenderSystem.setupShaderLights(shader);
        shader.bind();
        this.drawElements();
        shader.unbind();
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

