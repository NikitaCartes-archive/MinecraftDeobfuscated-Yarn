/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;

@Environment(value=EnvType.CLIENT)
public interface Glyph {
    public float getAdvance();

    default public float getAdvance(boolean bold) {
        return this.getAdvance() + (bold ? this.getBoldOffset() : 0.0f);
    }

    default public float getBoldOffset() {
        return 1.0f;
    }

    default public float getShadowOffset() {
        return 1.0f;
    }

    public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> var1);

    @Environment(value=EnvType.CLIENT)
    public static interface EmptyGlyph
    extends Glyph {
        @Override
        default public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
            return EmptyGlyphRenderer.field_38311;
        }
    }
}

