package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface FontLoader {
	Codec<FontLoader> CODEC = FontType.CODEC.dispatch(FontLoader::getType, fontType -> fontType.getLoaderCodec().codec());

	FontType getType();

	Either<FontLoader.Loadable, FontLoader.Reference> build();

	@Environment(EnvType.CLIENT)
	public interface Loadable {
		Font load(ResourceManager resourceManager) throws IOException;
	}

	@Environment(EnvType.CLIENT)
	public static record Reference(Identifier id) {
	}
}
