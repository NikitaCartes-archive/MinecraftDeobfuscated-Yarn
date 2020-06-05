package net.minecraft.resource;

import java.util.function.Consumer;

public class VanillaDataPackProvider implements ResourcePackProvider {
	private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

	@Override
	public <T extends ResourcePackProfile> void register(Consumer<T> consumer, ResourcePackProfile.Factory<T> factory) {
		T resourcePackProfile = ResourcePackProfile.of(
			"vanilla", false, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.PACK_SOURCE_BUILTIN
		);
		if (resourcePackProfile != null) {
			consumer.accept(resourcePackProfile);
		}
	}
}
