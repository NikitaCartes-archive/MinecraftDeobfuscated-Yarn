package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Tessellator {
	private final BufferBuilder buffer;
	private final BufferRenderer renderer = new BufferRenderer();
	private static final Tessellator INSTANCE = new Tessellator(2097152);

	public static Tessellator getInstance() {
		return INSTANCE;
	}

	public Tessellator(int bufferCapacity) {
		this.buffer = new BufferBuilder(bufferCapacity);
	}

	public void draw() {
		this.buffer.end();
		this.renderer.draw(this.buffer);
	}

	public BufferBuilder getBuffer() {
		return this.buffer;
	}
}
