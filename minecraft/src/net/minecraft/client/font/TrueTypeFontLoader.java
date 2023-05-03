package net.minecraft.client.font;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public record TrueTypeFontLoader(Identifier location, float size, float oversample, TrueTypeFontLoader.Shift shift, String skip) implements FontLoader {
	private static final Codec<String> SKIP_CODEC = Codec.either(Codec.STRING, Codec.STRING.listOf())
		.xmap(either -> either.map(string -> string, list -> String.join("", list)), Either::left);
	public static final MapCodec<TrueTypeFontLoader> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("file").forGetter(TrueTypeFontLoader::location),
					Codec.FLOAT.optionalFieldOf("size", Float.valueOf(11.0F)).forGetter(TrueTypeFontLoader::size),
					Codec.FLOAT.optionalFieldOf("oversample", Float.valueOf(1.0F)).forGetter(TrueTypeFontLoader::oversample),
					TrueTypeFontLoader.Shift.CODEC.optionalFieldOf("shift", TrueTypeFontLoader.Shift.NONE).forGetter(TrueTypeFontLoader::shift),
					SKIP_CODEC.optionalFieldOf("skip", "").forGetter(TrueTypeFontLoader::skip)
				)
				.apply(instance, TrueTypeFontLoader::new)
	);

	@Override
	public FontType getType() {
		return FontType.TTF;
	}

	@Override
	public Either<FontLoader.Loadable, FontLoader.Reference> build() {
		return Either.left(this::load);
	}

	private Font load(ResourceManager resourceManager) throws IOException {
		STBTTFontinfo sTBTTFontinfo = null;
		ByteBuffer byteBuffer = null;

		try {
			InputStream inputStream = resourceManager.open(this.location.withPrefixedPath("font/"));

			TrueTypeFont var5;
			try {
				sTBTTFontinfo = STBTTFontinfo.malloc();
				byteBuffer = TextureUtil.readResource(inputStream);
				byteBuffer.flip();
				if (!STBTruetype.stbtt_InitFont(sTBTTFontinfo, byteBuffer)) {
					throw new IOException("Invalid ttf");
				}

				var5 = new TrueTypeFont(byteBuffer, sTBTTFontinfo, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
			} catch (Throwable var8) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}
				}

				throw var8;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var5;
		} catch (Exception var9) {
			if (sTBTTFontinfo != null) {
				sTBTTFontinfo.free();
			}

			MemoryUtil.memFree(byteBuffer);
			throw var9;
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Shift(float x, float y) {
		public static final TrueTypeFontLoader.Shift NONE = new TrueTypeFontLoader.Shift(0.0F, 0.0F);
		public static final Codec<TrueTypeFontLoader.Shift> CODEC = Codec.FLOAT
			.listOf()
			.comapFlatMap(
				floatList -> Util.decodeFixedLengthList(floatList, 2).map(floatListx -> new TrueTypeFontLoader.Shift((Float)floatListx.get(0), (Float)floatListx.get(1))),
				shift -> List.of(shift.x, shift.y)
			);
	}
}
