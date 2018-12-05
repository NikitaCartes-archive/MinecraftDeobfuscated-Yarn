package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexBufferRenderer {
	public void draw(VertexBuffer vertexBuffer) {
		if (vertexBuffer.getVertexCount() > 0) {
			VertexFormat vertexFormat = vertexBuffer.getVertexFormat();
			int i = vertexFormat.getVertexSize();
			ByteBuffer byteBuffer = vertexBuffer.getByteBuffer();
			List<VertexFormatElement> list = vertexFormat.getElements();

			for (int j = 0; j < list.size(); j++) {
				VertexFormatElement vertexFormatElement = (VertexFormatElement)list.get(j);
				VertexFormatElement.Type type = vertexFormatElement.getType();
				int k = vertexFormatElement.getFormat().getGlId();
				int l = vertexFormatElement.getIndex();
				byteBuffer.position(vertexFormat.getElementOffset(j));
				switch (type) {
					case POSITION:
						GlStateManager.vertexPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						GlStateManager.enableClientState(32884);
						break;
					case UV:
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + l);
						GlStateManager.texCoordPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						GlStateManager.enableClientState(32888);
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
						break;
					case COLOR:
						GlStateManager.colorPointer(vertexFormatElement.getCount(), k, i, byteBuffer);
						GlStateManager.enableClientState(32886);
						break;
					case NORMAL:
						GlStateManager.normalPointer(k, i, byteBuffer);
						GlStateManager.enableClientState(32885);
				}
			}

			GlStateManager.drawArrays(vertexBuffer.getDrawMode(), 0, vertexBuffer.getVertexCount());
			int j = 0;

			for (int m = list.size(); j < m; j++) {
				VertexFormatElement vertexFormatElement2 = (VertexFormatElement)list.get(j);
				VertexFormatElement.Type type2 = vertexFormatElement2.getType();
				int l = vertexFormatElement2.getIndex();
				switch (type2) {
					case POSITION:
						GlStateManager.disableClientState(32884);
						break;
					case UV:
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + l);
						GlStateManager.disableClientState(32888);
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
						break;
					case COLOR:
						GlStateManager.disableClientState(32886);
						GlStateManager.clearCurrentColor();
						break;
					case NORMAL:
						GlStateManager.disableClientState(32885);
				}
			}
		}

		vertexBuffer.clear();
	}
}
