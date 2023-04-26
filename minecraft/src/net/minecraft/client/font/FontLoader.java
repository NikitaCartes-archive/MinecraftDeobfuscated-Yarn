package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface FontLoader {
	Either<FontLoader.Loadable, FontLoader.Reference> build();

	@Environment(EnvType.CLIENT)
	public interface Loadable {
		Font load(ResourceManager resourceManager) throws IOException;
	}

	@Environment(EnvType.CLIENT)
	public static record Reference(Identifier id) {
	}
}
