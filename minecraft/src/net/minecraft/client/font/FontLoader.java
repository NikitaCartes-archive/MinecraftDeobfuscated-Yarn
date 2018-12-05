package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_390;
import net.minecraft.resource.ResourceManager;

@Environment(EnvType.CLIENT)
public interface FontLoader {
	@Nullable
	class_390 load(ResourceManager resourceManager);
}
