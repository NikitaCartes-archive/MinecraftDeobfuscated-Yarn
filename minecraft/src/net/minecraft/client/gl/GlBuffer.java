package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.client.render.VertexFormat;

@Environment(EnvType.CLIENT)
public class GlBuffer {
	private int id;
	private final VertexFormat format;
	private int vertexCount;

	public GlBuffer(VertexFormat vertexFormat) {
		this.format = vertexFormat;
		this.id = class_4493.method_22065();
	}

	public void bind() {
		class_4493.method_22036(34962, this.id);
	}

	public void set(ByteBuffer byteBuffer) {
		this.bind();
		class_4493.method_21962(34962, byteBuffer, 35044);
		unbind();
		this.vertexCount = byteBuffer.limit() / this.format.getVertexSize();
	}

	public void draw(int i) {
		RenderSystem.drawArrays(i, 0, this.vertexCount);
	}

	public static void unbind() {
		class_4493.method_22036(34962, 0);
	}

	public void delete() {
		if (this.id >= 0) {
			class_4493.method_22054(this.id);
			this.id = -1;
		}
	}
}
