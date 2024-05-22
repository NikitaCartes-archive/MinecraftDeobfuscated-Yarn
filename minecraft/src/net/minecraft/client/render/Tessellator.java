package net.minecraft.client.render;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9799;

/**
 * Holding a single instance of {@link BufferBuilder}.
 * 
 * <p>This class reuses the buffer builder so a buffer doesn't have to be
 * allocated every time.
 */
@Environment(EnvType.CLIENT)
public class Tessellator {
	private static final int field_46841 = 786432;
	private final class_9799 field_52098;
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
		this.field_52098 = new class_9799(bufferCapacity);
	}

	public Tessellator() {
		this(786432);
	}

	public BufferBuilder method_60827(VertexFormat.DrawMode drawMode, VertexFormat vertexFormat) {
		return new BufferBuilder(this.field_52098, drawMode, vertexFormat);
	}

	public void method_60828() {
		this.field_52098.method_60809();
	}
}
