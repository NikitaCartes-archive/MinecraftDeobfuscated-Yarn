package net.minecraft.resource;

import java.util.Map;

public interface ResourcePackProvider {
	<T extends ResourcePackProfile> void register(Map<String, T> registry, ResourcePackProfile.Factory<T> factory);
}
