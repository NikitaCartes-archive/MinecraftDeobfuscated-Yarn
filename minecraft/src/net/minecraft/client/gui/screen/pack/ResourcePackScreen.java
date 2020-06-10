package net.minecraft.client.gui.screen.pack;

import java.io.File;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ResourcePackScreen extends AbstractPackScreen {
	public ResourcePackScreen(
		Screen parent,
		ResourcePackManager<ClientResourcePackProfile> resourcePackManager,
		Consumer<ResourcePackManager<ClientResourcePackProfile>> consumer,
		File file
	) {
		super(
			parent,
			new TranslatableText("resourcePack.title"),
			runnable -> new ResourcePackOrganizer<>(runnable, ClientResourcePackProfile::drawIcon, resourcePackManager, consumer),
			file
		);
	}
}
