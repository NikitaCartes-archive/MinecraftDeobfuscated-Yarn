/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class VertexBuffer {
    private int id;
    private final VertexFormat format;
    private int vertexCount;

    public VertexBuffer(VertexFormat vertexFormat) {
        this.format = vertexFormat;
        RenderSystem.glGenBuffers(integer -> {
            this.id = integer;
        });
    }

    public void bind() {
        RenderSystem.glBindBuffer(34962, () -> this.id);
    }

    public void upload(BufferBuilder bufferBuilder) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.uploadInternal(bufferBuilder));
        } else {
            this.uploadInternal(bufferBuilder);
        }
    }

    public CompletableFuture<Void> submitUpload(BufferBuilder bufferBuilder) {
        if (!RenderSystem.isOnRenderThread()) {
            return CompletableFuture.runAsync(() -> this.uploadInternal(bufferBuilder), runnable -> RenderSystem.recordRenderCall(runnable::run));
        }
        this.uploadInternal(bufferBuilder);
        return CompletableFuture.completedFuture(null);
    }

    private void uploadInternal(BufferBuilder bufferBuilder) {
        Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
        ByteBuffer byteBuffer = pair.getSecond();
        this.vertexCount = byteBuffer.remaining() / this.format.getVertexSize();
        this.bind();
        RenderSystem.glBufferData(34962, byteBuffer, 35044);
        VertexBuffer.unbind();
    }

    public void draw(Matrix4f matrix4f, int i) {
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrix4f);
        RenderSystem.drawArrays(i, 0, this.vertexCount);
        RenderSystem.popMatrix();
    }

    public static void unbind() {
        RenderSystem.glBindBuffer(34962, () -> 0);
    }

    public void delete() {
        if (this.id >= 0) {
            RenderSystem.glDeleteBuffers(this.id);
            this.id = -1;
        }
    }
}

