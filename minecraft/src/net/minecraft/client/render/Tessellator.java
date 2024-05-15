package net.minecraft.client.render;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Holding a single instance of {@link BufferBuilder}.
 * 
 * <p>This class reuses the buffer builder so a buffer doesn't have to be
 * allocated every time.
 */
@Environment(EnvType.CLIENT)
public class Tessellator {
	private static final int field_46841 = 786432;
	private final BufferBuilder buffer;
	@Nullable
	private static Tessellator INSTANCE;

	public static void initialize() {
		if (INSTANCE != null) {
			throw new IllegalStateException("Tesselator has already been initialized");
		} else {
			INSTANCE = new Tessellator();
		}
	}

	public static Tessellator getInstance() {
		if (INSTANCE == null) {
			throw new IllegalStateException("Tesselator has not been initialized");
		} else {
			return INSTANCE;
		}
	}

	public Tessellator(int bufferCapacity) {
		this.buffer = new BufferBuilder(bufferCapacity);
	}

	public Tessellator() {
		this(786432);
	}

	/**
	 * Draws the contents of the buffer builder using the shader program
	 * specified with {@link com.mojang.blaze3d.systems.RenderSystem#setShader
	 * RenderSystem#setShader}.
	 */
	public void draw() {
		BufferRenderer.drawWithGlobalProgram(this.buffer.end());
	}

	public BufferBuilder getBuffer() {
		return this.buffer;
	}
}
