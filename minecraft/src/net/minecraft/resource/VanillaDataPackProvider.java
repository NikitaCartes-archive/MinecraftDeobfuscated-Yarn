package net.minecraft.resource;

import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.TranslatableText;

public class VanillaDataPackProvider implements ResourcePackProvider {
	public static final PackResourceMetadata DEFAULT_PACK_METADATA = new PackResourceMetadata(
		new TranslatableText("dataPack.vanilla.description"), ResourceType.SERVER_DATA.getPackVersion(SharedConstants.getGameVersion())
	);
	public static final String NAME = "vanilla";
	private final DefaultResourcePack pack = new DefaultResourcePack(DEFAULT_PACK_METADATA, "minecraft");

	@Override
	public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
		ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(
			"vanilla", false, () -> this.pack, factory, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.PACK_SOURCE_BUILTIN
		);
		if (resourcePackProfile != null) {
			profileAdder.accept(resourcePackProfile);
		}
	}
}
