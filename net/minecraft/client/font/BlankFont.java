/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.Glyph;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlankFont
implements Font {
    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        return BuiltinEmptyGlyph.MISSING;
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return IntSets.EMPTY_SET;
    }
}

