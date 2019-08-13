package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
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
		this.id = GLX.glGenBuffers();
	}

	public void bind() {
		GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, this.id);
	}

	public void set(ByteBuffer byteBuffer) {
		this.bind();
		GLX.glBufferData(GLX.GL_ARRAY_BUFFER, byteBuffer, 35044);
		unbind();
		this.vertexCount = byteBuffer.limit() / this.format.getVertexSize();
	}

	public void draw(int i) {
		GlStateManager.drawArrays(i, 0, this.vertexCount);
	}

	public static void unbind() {
		GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, 0);
	}

	public void delete() {
		if (this.id >= 0) {
			GLX.glDeleteBuffers(this.id);
			this.id = -1;
		}
	}
}
