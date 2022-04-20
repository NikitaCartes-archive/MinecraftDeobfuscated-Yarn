package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Tessellator {
	private static final int field_32051 = 8388608;
	private static final int DEFAULT_BUFFER_CAPACITY = 2097152;
	private final BufferBuilder buffer;
	private static final Tessellator INSTANCE = new Tessellator();

	public static Tessellator getInstance() {
		RenderSystem.assertOnGameThreadOrInit();
		return INSTANCE;
	}

	public Tessellator(int bufferCapacity) {
		this.buffer = new BufferBuilder(bufferCapacity);
	}

	public Tessellator() {
		this(2097152);
	}

	public void draw() {
		this.buffer.end();
		BufferRenderer.method_43433(this.buffer);
	}

	public BufferBuilder getBuffer() {
		return this.buffer;
	}
}
