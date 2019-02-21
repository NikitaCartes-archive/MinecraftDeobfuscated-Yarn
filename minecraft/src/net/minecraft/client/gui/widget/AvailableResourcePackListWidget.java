package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class AvailableResourcePackListWidget extends ResourcePackListWidget {
	public AvailableResourcePackListWidget(MinecraftClient minecraftClient, int i, int j) {
		super(minecraftClient, i, j);
	}

	@Override
	protected String getTitle() {
		return I18n.translate("resourcePack.available.title");
	}
}
