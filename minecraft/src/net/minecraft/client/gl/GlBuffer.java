package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;

@Environment(EnvType.CLIENT)
public class GlBuffer {
	private int id;
	private final VertexFormat format;
	private int vertexCount;

	public GlBuffer(VertexFormat vertexFormat) {
		this.format = vertexFormat;
		this.id = GlStateManager.genBuffers();
	}

	public void bind() {
		GlStateManager.bindBuffers(34962, this.id);
	}

	public void set(ByteBuffer byteBuffer) {
		this.bind();
		GlStateManager.bufferData(34962, byteBuffer, 35044);
		unbind();
		this.vertexCount = byteBuffer.limit() / this.format.getVertexSize();
	}

	public void draw(int i) {
		RenderSystem.drawArrays(i, 0, this.vertexCount);
	}

	public static void unbind() {
		GlStateManager.bindBuffers(34962, 0);
	}

	public void delete() {
		if (this.id >= 0) {
			GlStateManager.deleteBuffers(this.id);
			this.id = -1;
		}
	}
}
