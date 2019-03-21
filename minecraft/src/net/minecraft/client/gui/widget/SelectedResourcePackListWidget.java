package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class SelectedResourcePackListWidget extends ResourcePackListWidget {
	public SelectedResourcePackListWidget(MinecraftClient minecraftClient, int i, int j) {
		super(minecraftClient, i, j, new TranslatableTextComponent("resourcePack.selected.title"));
	}
}
