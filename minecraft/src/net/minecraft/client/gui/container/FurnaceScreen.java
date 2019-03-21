package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FurnaceScreen extends AbstractFurnaceScreen<FurnaceContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/furnace.png");

	public FurnaceScreen(FurnaceContainer furnaceContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(furnaceContainer, new FurnaceRecipeBookScreen(), playerInventory, textComponent, BG_TEX);
	}
}
