package net.minecraft.resource;

import java.util.function.Consumer;

public interface ResourcePackProvider {
	void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory);
}
