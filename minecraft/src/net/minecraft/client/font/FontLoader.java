package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;

@Environment(EnvType.CLIENT)
public interface FontLoader {
	@Nullable
	Font method_2039(ResourceManager resourceManager);
}
