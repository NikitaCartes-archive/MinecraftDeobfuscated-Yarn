/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Glyph;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface Font
extends AutoCloseable {
    @Override
    default public void close() {
    }

    @Nullable
    default public Glyph getGlyph(int codePoint) {
        return null;
    }

    /**
     * {@return the set of code points for which this font can provide glyphs}
     */
    public IntSet getProvidedGlyphs();
}

