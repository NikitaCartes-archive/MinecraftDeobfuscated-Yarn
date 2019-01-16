package net.minecraft.resource;

import java.util.List;

public interface ReloadableResourceManager extends ResourceManager {
	void reload(List<ResourcePack> list);

	void addListener(ResourceReloadListener resourceReloadListener);
}
