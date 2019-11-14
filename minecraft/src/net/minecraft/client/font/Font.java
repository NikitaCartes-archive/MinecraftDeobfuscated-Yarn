package net.minecraft.client.font;

import java.io.Closeable;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Font extends Closeable {
	default void close() {
	}

	@Nullable
	default RenderableGlyph getGlyph(char character) {
		return null;
	}
}
