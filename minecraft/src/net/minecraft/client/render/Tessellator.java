package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Tessellator {
	private final VertexBuffer buffer;
	private final VertexBufferRenderer renderer = new VertexBufferRenderer();
	private static final Tessellator INSTANCE = new Tessellator(2097152);

	public static Tessellator getInstance() {
		return INSTANCE;
	}

	public Tessellator(int i) {
		this.buffer = new VertexBuffer(i);
	}

	public void draw() {
		this.buffer.end();
		this.renderer.draw(this.buffer);
	}

	public VertexBuffer getVertexBuffer() {
		return this.buffer;
	}
}
