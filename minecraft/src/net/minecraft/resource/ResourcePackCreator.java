package net.minecraft.resource;

import java.util.Map;

public interface ResourcePackCreator {
	<T extends ResourcePackContainer> void registerContainer(Map<String, T> map, ResourcePackContainer.Factory<T> factory);
}
