package net.minecraft.resource;

import java.util.Map;

public interface ResourcePackProvider {
	<T extends ResourcePackProfile> void register(Map<String, T> map, ResourcePackProfile.Factory<T> factory);
}
