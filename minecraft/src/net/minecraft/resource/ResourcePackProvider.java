package net.minecraft.resource;

import java.util.function.Consumer;

public interface ResourcePackProvider {
	<T extends ResourcePackProfile> void register(Consumer<T> consumer, ResourcePackProfile.Factory<T> factory);
}
