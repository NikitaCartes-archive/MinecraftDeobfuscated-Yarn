package net.minecraft.client.render;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.BufferAllocator;

/**
 * Holding a single instance of {@link BufferBuilder}.
 * 
 * <p>This class reuses the buffer builder so a buffer doesn't have to be
 * allocated every time.
 */
@Environment(EnvType.CLIENT)
public class Tessellator {
	private static final int field_46841 = 786432;
	private final BufferAllocator allocator;
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
		this.allocator = new BufferAllocator(bufferCapacity);
	}

	public Tessellator() {
		this(786432);
	}

	public BufferBuilder begin(VertexFormat.DrawMode drawMode, VertexFormat format) {
		return new BufferBuilder(this.allocator, drawMode, format);
	}

	public void clear() {
		this.allocator.clear();
	}
}
