package net.minecraft.client.gui.screen.pack;

import java.io.File;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DataPackScreen extends AbstractPackScreen {
	private static final Identifier UNKNOWN_PACK_TEXTURE = new Identifier("textures/misc/unknown_pack.png");

	public DataPackScreen(
		Screen parent, ResourcePackManager<ResourcePackProfile> resourcePackManager, Consumer<ResourcePackManager<ResourcePackProfile>> consumer, File file
	) {
		super(
			parent,
			new TranslatableText("dataPack.title"),
			runnable -> new ResourcePackOrganizer<>(
					runnable, (resourcePackProfile, textureManager) -> textureManager.bindTexture(UNKNOWN_PACK_TEXTURE), resourcePackManager, consumer
				),
			file
		);
	}
}
