package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexBufferRenderer;

@Environment(EnvType.CLIENT)
public class class_294 extends VertexBufferRenderer {
	private GlBuffer field_1604;

	@Override
	public void draw(VertexBuffer vertexBuffer) {
		vertexBuffer.clear();
		this.field_1604.set(vertexBuffer.getByteBuffer());
	}

	public void method_1372(GlBuffer glBuffer) {
		this.field_1604 = glBuffer;
	}
}
