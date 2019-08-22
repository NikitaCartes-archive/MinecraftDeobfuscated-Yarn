package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BufferRenderer {
	public void draw(BufferBuilder bufferBuilder) {
		if (bufferBuilder.getVertexCount() > 0) {
			VertexFormat vertexFormat = bufferBuilder.getVertexFormat();
			int i = vertexFormat.getVertexSize();
			ByteBuffer byteBuffer = bufferBuilder.getByteBuffer();
			List<VertexFormatElement> list = vertexFormat.getElements();

			for (int j = 0; j < list.size(); j++) {
				VertexFormatElement vertexFormatElement = (VertexFormatElement)list.get(j);
				VertexFormatElement.Type type = vertexFormatElement.getType();
				int k = vertexFormatElement.getFormat().getGlId();
				int l = vertexFormatElement.getIndex();
				byteBuffer.position(vertexFormat.getElementOffset(j));
				switch (type) {
					case POSITION:
						RenderSystem.vertexPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						RenderSystem.enableClientState(32884);
						break;
					case UV:
						RenderSystem.glClientActiveTexture(33984 + l);
						RenderSystem.texCoordPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						RenderSystem.enableClientState(32888);
						RenderSystem.glClientActiveTexture(33984);
						break;
					case COLOR:
						RenderSystem.colorPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						RenderSystem.enableClientState(32886);
						break;
					case NORMAL:
						RenderSystem.normalPointer(k, i, byteBuffer);
						RenderSystem.enableClientState(32885);
				}
			}

			RenderSystem.drawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
			int j = 0;

			for (int m = list.size(); j < m; j++) {
				VertexFormatElement vertexFormatElement2 = (VertexFormatElement)list.get(j);
				VertexFormatElement.Type type2 = vertexFormatElement2.getType();
				int l = vertexFormatElement2.getIndex();
				switch (type2) {
					case POSITION:
						RenderSystem.disableClientState(32884);
						break;
					case UV:
						RenderSystem.glClientActiveTexture(33984 + l);
						RenderSystem.disableClientState(32888);
						RenderSystem.glClientActiveTexture(33984);
						break;
					case COLOR:
						RenderSystem.disableClientState(32886);
						RenderSystem.clearCurrentColor();
						break;
					case NORMAL:
						RenderSystem.disableClientState(32885);
				}
			}
		}

		bufferBuilder.clear();
	}
}
