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
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class SpaceFont implements Font {
	private final Int2ObjectMap<Glyph.EmptyGlyph> codePointsToGlyphs;

	public SpaceFont(Int2FloatMap codePointsToAdvances) {
		this.codePointsToGlyphs = new Int2ObjectOpenHashMap<>(codePointsToAdvances.size());
		Int2FloatMaps.fastForEach(codePointsToAdvances, entry -> {
			float f = entry.getFloatValue();
			this.codePointsToGlyphs.put(entry.getIntKey(), () -> f);
		});
	}

	@Nullable
	@Override
	public Glyph getGlyph(int codePoint) {
		return this.codePointsToGlyphs.get(codePoint);
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return IntSets.unmodifiable(this.codePointsToGlyphs.keySet());
	}

	public static FontLoader fromJson(JsonObject json) {
		Int2FloatMap int2FloatMap = new Int2FloatOpenHashMap();
		JsonObject jsonObject = JsonHelper.getObject(json, "advances");

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			int[] is = ((String)entry.getKey()).codePoints().toArray();
			if (is.length != 1) {
				throw new JsonParseException("Expected single codepoint, got " + Arrays.toString(is));
			}

			float f = JsonHelper.asFloat((JsonElement)entry.getValue(), "advance");
			int2FloatMap.put(is[0], f);
		}

		return resourceManager -> new SpaceFont(int2FloatMap);
	}
}
