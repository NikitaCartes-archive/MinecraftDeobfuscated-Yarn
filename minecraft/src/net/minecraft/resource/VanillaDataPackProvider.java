package net.minecraft.resource;

import java.util.function.Consumer;
import net.minecraft.class_5352;

public class VanillaDataPackProvider implements ResourcePackProvider {
	private final DefaultResourcePack pack = new DefaultResourcePack("minecraft");

	@Override
	public <T extends ResourcePackProfile> void register(Consumer<T> consumer, ResourcePackProfile.class_5351<T> factory) {
		T resourcePackProfile = ResourcePackProfile.of(
			"vanilla", false, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.BOTTOM, class_5352.field_25348
		);
		if (resourcePackProfile != null) {
			consumer.accept(resourcePackProfile);
		}
	}
}
