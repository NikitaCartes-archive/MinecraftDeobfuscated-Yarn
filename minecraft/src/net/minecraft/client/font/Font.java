package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Font extends AutoCloseable {
	default void close() {
	}

	@Nullable
	default Glyph getGlyph(int codePoint) {
		return null;
	}

	/**
	 * {@return the set of code points for which this font can provide glyphs}
	 */
	IntSet getProvidedGlyphs();
}
