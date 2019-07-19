package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;

@Environment(EnvType.CLIENT)
public class GlBufferRenderer extends BufferRenderer {
	private VertexBuffer glBuffer;

	@Override
	public void draw(BufferBuilder bufferBuilder) {
		bufferBuilder.clear();
		this.glBuffer.set(bufferBuilder.getByteBuffer());
	}

	public void setGlBuffer(VertexBuffer glBuffer) {
		this.glBuffer = glBuffer;
	}
}
