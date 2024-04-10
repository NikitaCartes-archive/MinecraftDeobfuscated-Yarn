package net.minecraft.client.font;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public enum FontType implements StringIdentifiable {
	BITMAP("bitmap", BitmapFont.Loader.CODEC),
	TTF("ttf", TrueTypeFontLoader.CODEC),
	SPACE("space", SpaceFont.Loader.CODEC),
	UNIHEX("unihex", UnihexFont.Loader.CODEC),
	REFERENCE("reference", ReferenceFont.CODEC);

	public static final Codec<FontType> CODEC = StringIdentifiable.createCodec(FontType::values);
	private final String id;
	private final MapCodec<? extends FontLoader> loaderCodec;

	private FontType(final String id, final MapCodec<? extends FontLoader> loaderCodec) {
		this.id = id;
		this.loaderCodec = loaderCodec;
	}

	@Override
	public String asString() {
		return this.id;
	}

	public MapCodec<? extends FontLoader> getLoaderCodec() {
		return this.loaderCodec;
	}
}
