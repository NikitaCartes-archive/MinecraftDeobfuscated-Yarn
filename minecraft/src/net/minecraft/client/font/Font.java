package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Closeable;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Font extends Closeable {
	default void close() {
	}

	@Nullable
	default RenderableGlyph getGlyph(int codePoint) {
		return null;
	}

	/**
	 * Returns the set of code points for which this font can provide glyphs.
	 * 
	 * @return a set of integer code points.
	 */
	IntSet getProvidedGlyphs();
}
