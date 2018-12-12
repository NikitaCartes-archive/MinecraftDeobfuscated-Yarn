package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;

@Environment(EnvType.CLIENT)
public class class_294 extends BufferRenderer {
	private GlBuffer field_1604;

	@Override
	public void draw(BufferBuilder bufferBuilder) {
		bufferBuilder.clear();
		this.field_1604.set(bufferBuilder.getByteBuffer());
	}

	public void method_1372(GlBuffer glBuffer) {
		this.field_1604 = glBuffer;
	}
}
