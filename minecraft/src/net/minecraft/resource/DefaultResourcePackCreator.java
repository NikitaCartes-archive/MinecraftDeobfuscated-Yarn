package net.minecraft.resource;

import java.util.Map;

public class DefaultResourcePackCreator implements ResourcePackCreator {
	private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

	@Override
	public <T extends ResourcePackContainer> void registerContainer(Map<String, T> map, ResourcePackContainer.Factory<T> factory) {
		T resourcePackContainer = ResourcePackContainer.of("vanilla", false, () -> this.pack, factory, ResourcePackContainer.InsertionPosition.field_14281);
		if (resourcePackContainer != null) {
			map.put("vanilla", resourcePackContainer);
		}
	}
}
