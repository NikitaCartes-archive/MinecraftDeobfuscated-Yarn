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
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

@Environment(EnvType.CLIENT)
public record TrueTypeFontLoader(Identifier location, float size, float oversample, TrueTypeFontLoader.Shift shift, String skip) implements FontLoader {
	private static final Codec<String> SKIP_CODEC = Codec.withAlternative(Codec.STRING, Codec.STRING.listOf(), chars -> String.join("", chars));
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
		FT_Face fT_Face = null;
		ByteBuffer byteBuffer = null;

		try {
			InputStream inputStream = resourceManager.open(this.location.withPrefixedPath("font/"));

			TrueTypeFont var20;
			try {
				byteBuffer = TextureUtil.readResource(inputStream);
				byteBuffer.flip();
				synchronized (FreeTypeUtil.LOCK) {
					try (MemoryStack memoryStack = MemoryStack.stackPush()) {
						PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
						FreeTypeUtil.checkFatalError(FreeType.FT_New_Memory_Face(FreeTypeUtil.initialize(), byteBuffer, 0L, pointerBuffer), "Initializing font face");
						fT_Face = FT_Face.create(pointerBuffer.get());
					}

					String string = FreeType.FT_Get_Font_Format(fT_Face);
					if (!"TrueType".equals(string)) {
						throw new IOException("Font is not in TTF format, was " + string);
					}

					FreeTypeUtil.checkFatalError(FreeType.FT_Select_Charmap(fT_Face, FreeType.FT_ENCODING_UNICODE), "Find unicode charmap");
					var20 = new TrueTypeFont(byteBuffer, fT_Face, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
				}
			} catch (Throwable var16) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var11) {
						var16.addSuppressed(var11);
					}
				}

				throw var16;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var20;
		} catch (Exception var17) {
			synchronized (FreeTypeUtil.LOCK) {
				if (fT_Face != null) {
					FreeType.FT_Done_Face(fT_Face);
				}
			}

			MemoryUtil.memFree(byteBuffer);
			throw var17;
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Shift(float x, float y) {
		public static final TrueTypeFontLoader.Shift NONE = new TrueTypeFontLoader.Shift(0.0F, 0.0F);
		public static final Codec<TrueTypeFontLoader.Shift> CODEC = Codec.floatRange(-512.0F, 512.0F)
			.listOf()
			.comapFlatMap(
				floatList -> Util.decodeFixedLengthList(floatList, 2).map(floatListx -> new TrueTypeFontLoader.Shift((Float)floatListx.get(0), (Float)floatListx.get(1))),
				shift -> List.of(shift.x, shift.y)
			);
	}
}
