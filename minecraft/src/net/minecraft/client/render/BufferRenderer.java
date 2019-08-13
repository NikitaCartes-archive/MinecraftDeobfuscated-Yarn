package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
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
					case field_1633:
						GlStateManager.vertexPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						GlStateManager.enableClientState(32884);
						break;
					case field_1636:
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + l);
						GlStateManager.texCoordPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						GlStateManager.enableClientState(32888);
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
						break;
					case COLOR:
						GlStateManager.colorPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						GlStateManager.enableClientState(32886);
						break;
					case field_1635:
						GlStateManager.normalPointer(k, i, byteBuffer);
						GlStateManager.enableClientState(32885);
				}
			}

			GlStateManager.drawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
			int j = 0;

			for (int m = list.size(); j < m; j++) {
				VertexFormatElement vertexFormatElement2 = (VertexFormatElement)list.get(j);
				VertexFormatElement.Type type2 = vertexFormatElement2.getType();
				int l = vertexFormatElement2.getIndex();
				switch (type2) {
					case field_1633:
						GlStateManager.disableClientState(32884);
						break;
					case field_1636:
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + l);
						GlStateManager.disableClientState(32888);
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
						break;
					case COLOR:
						GlStateManager.disableClientState(32886);
						GlStateManager.clearCurrentColor();
						break;
					case field_1635:
						GlStateManager.disableClientState(32885);
				}
			}
		}

		bufferBuilder.clear();
	}
}
