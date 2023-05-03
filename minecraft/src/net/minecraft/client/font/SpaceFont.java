package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public class SpaceFont implements Font {
	private final Int2ObjectMap<Glyph.EmptyGlyph> codePointsToGlyphs;

	public SpaceFont(Map<Integer, Float> codePointsToAdvances) {
		this.codePointsToGlyphs = new Int2ObjectOpenHashMap<>(codePointsToAdvances.size());
		codePointsToAdvances.forEach((codePoint, glyph) -> this.codePointsToGlyphs.put(codePoint.intValue(), () -> glyph));
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

	@Environment(EnvType.CLIENT)
	public static record Loader(Map<Integer, Float> advances) implements FontLoader {
		public static final MapCodec<SpaceFont.Loader> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codec.unboundedMap(Codecs.CODEPOINT, Codec.FLOAT).fieldOf("advances").forGetter(SpaceFont.Loader::advances))
					.apply(instance, SpaceFont.Loader::new)
		);

		@Override
		public FontType getType() {
			return FontType.SPACE;
		}

		@Override
		public Either<FontLoader.Loadable, FontLoader.Reference> build() {
			FontLoader.Loadable loadable = resourceManager -> new SpaceFont(this.advances);
			return Either.left(loadable);
		}
	}
}
