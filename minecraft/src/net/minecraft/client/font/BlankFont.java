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
	public RenderableGlyph getGlyph(int i) {
		return BlankGlyph.INSTANCE;
	}

	@Override
	public IntSet method_27442() {
		return IntSets.EMPTY_SET;
	}
}
