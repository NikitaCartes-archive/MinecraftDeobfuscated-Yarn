package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlankFont implements Font {
	@Nullable
	@Override
	public Glyph getGlyph(int codePoint) {
		return BuiltinEmptyGlyph.MISSING;
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return IntSets.EMPTY_SET;
	}
}
