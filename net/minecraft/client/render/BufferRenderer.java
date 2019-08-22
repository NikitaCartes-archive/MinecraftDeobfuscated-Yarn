/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

@Environment(value=EnvType.CLIENT)
public class BufferRenderer {
    public void draw(BufferBuilder bufferBuilder) {
        if (bufferBuilder.getVertexCount() > 0) {
            int l;
            int j;
            VertexFormat vertexFormat = bufferBuilder.getVertexFormat();
            int i = vertexFormat.getVertexSize();
            ByteBuffer byteBuffer = bufferBuilder.getByteBuffer();
            List<VertexFormatElement> list = vertexFormat.getElements();
            block12: for (j = 0; j < list.size(); ++j) {
                VertexFormatElement vertexFormatElement = list.get(j);
                VertexFormatElement.Type type = vertexFormatElement.getType();
                int k = vertexFormatElement.getFormat().getGlId();
                l = vertexFormatElement.getIndex();
                byteBuffer.position(vertexFormat.getElementOffset(j));
                switch (type) {
                    case POSITION: {
                        RenderSystem.vertexPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
                        RenderSystem.enableClientState(32884);
                        continue block12;
                    }
                    case UV: {
                        RenderSystem.glClientActiveTexture(33984 + l);
                        RenderSystem.texCoordPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
                        RenderSystem.enableClientState(32888);
                        RenderSystem.glClientActiveTexture(33984);
                        continue block12;
                    }
                    case COLOR: {
                        RenderSystem.colorPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
                        RenderSystem.enableClientState(32886);
                        continue block12;
                    }
                    case NORMAL: {
                        RenderSystem.normalPointer(k, i, byteBuffer);
                        RenderSystem.enableClientState(32885);
                    }
                }
            }
            RenderSystem.drawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
            int m = list.size();
            block13: for (j = 0; j < m; ++j) {
                VertexFormatElement vertexFormatElement2 = list.get(j);
                VertexFormatElement.Type type2 = vertexFormatElement2.getType();
                l = vertexFormatElement2.getIndex();
                switch (type2) {
                    case POSITION: {
                        RenderSystem.disableClientState(32884);
                        continue block13;
                    }
                    case UV: {
                        RenderSystem.glClientActiveTexture(33984 + l);
                        RenderSystem.disableClientState(32888);
                        RenderSystem.glClientActiveTexture(33984);
                        continue block13;
                    }
                    case COLOR: {
                        RenderSystem.disableClientState(32886);
                        RenderSystem.clearCurrentColor();
                        continue block13;
                    }
                    case NORMAL: {
                        RenderSystem.disableClientState(32885);
                    }
                }
            }
        }
        bufferBuilder.clear();
    }
}

