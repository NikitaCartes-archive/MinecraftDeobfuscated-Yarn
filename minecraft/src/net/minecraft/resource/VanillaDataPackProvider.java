package net.minecraft.resource;

import java.util.function.Consumer;

public class VanillaDataPackProvider implements ResourcePackProvider {
	private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

	@Override
	public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
		ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(
			"vanilla", false, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.field_14281, ResourcePackSource.field_25348
		);
		if (resourcePackProfile != null) {
			consumer.accept(resourcePackProfile);
		}
	}
}
