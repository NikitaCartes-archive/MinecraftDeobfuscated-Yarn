package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;

@Environment(EnvType.CLIENT)
public class VertexBuffer {
	private int id;
	private final VertexFormat format;
	private int vertexCount;

	public VertexBuffer(VertexFormat format) {
		this.format = format;
		this.id = GLX.glGenBuffers();
	}

	public void bind() {
		GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, this.id);
	}

	public void set(ByteBuffer buffer) {
		this.bind();
		GLX.glBufferData(GLX.GL_ARRAY_BUFFER, buffer, 35044);
		unbind();
		this.vertexCount = buffer.limit() / this.format.getVertexSize();
	}

	public void draw(int mode) {
		GlStateManager.drawArrays(mode, 0, this.vertexCount);
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
