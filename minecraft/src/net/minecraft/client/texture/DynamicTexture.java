package net.minecraft.client.texture;

import java.io.IOException;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface DynamicTexture {
	void save(Identifier id, Path path) throws IOException;
}
