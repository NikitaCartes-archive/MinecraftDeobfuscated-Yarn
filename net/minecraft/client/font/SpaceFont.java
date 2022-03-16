/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMaps;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpaceFont
implements Font {
    static final EmptyGlyphRenderer EMPTY_GLYPH_RENDERER = new EmptyGlyphRenderer();
    private final Int2ObjectMap<EmptyGlyph> codePointsToGlyphs;

    public SpaceFont(Int2FloatMap codePointsToAdvances) {
        this.codePointsToGlyphs = new Int2ObjectOpenHashMap<EmptyGlyph>(codePointsToAdvances.size());
        Int2FloatMaps.fastForEach(codePointsToAdvances, entry -> {
            float f = entry.getFloatValue();
            this.codePointsToGlyphs.put(entry.getIntKey(), () -> f);
        });
    }

    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        return (Glyph)this.codePointsToGlyphs.get(codePoint);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return IntSets.unmodifiable(this.codePointsToGlyphs.keySet());
    }

    public static FontLoader fromJson(JsonObject json) {
        Int2FloatOpenHashMap int2FloatMap = new Int2FloatOpenHashMap();
        JsonObject jsonObject = JsonHelper.getObject(json, "advances");
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            int[] is = entry.getKey().codePoints().toArray();
            if (is.length != 1) {
                throw new JsonParseException("Expected single codepoint, got " + Arrays.toString(is));
            }
            float f = JsonHelper.asFloat(entry.getValue(), "advance");
            int2FloatMap.put(is[0], f);
        }
        return resourceManager -> new SpaceFont(int2FloatMap);
    }

    @Environment(value=EnvType.CLIENT)
    static interface EmptyGlyph
    extends Glyph {
        @Override
        default public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
            return EMPTY_GLYPH_RENDERER;
        }
    }
}

