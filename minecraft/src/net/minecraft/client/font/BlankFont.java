package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlankFont implements Font {
	@Nullable
	@Override
	public RenderableGlyph getGlyph(char character) {
		return BlankGlyph.INSTANCE;
	}
}
