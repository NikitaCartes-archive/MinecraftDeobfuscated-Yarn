package net.minecraft.client.font;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Glyph {
	float getAdvance();

	default float getAdvance(boolean bold) {
		return this.getAdvance() + (bold ? this.getBoldOffset() : 0.0F);
	}

	default float getBoldOffset() {
		return 1.0F;
	}

	default float getShadowOffset() {
		return 1.0F;
	}

	GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> glyphRendererGetter);

	@Environment(EnvType.CLIENT)
	public interface EmptyGlyph extends Glyph {
		@Override
		default GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
			return EmptyGlyphRenderer.INSTANCE;
		}
	}
}
