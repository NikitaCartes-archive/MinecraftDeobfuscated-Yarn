package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.FurnaceRecipeBookScreen;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FurnaceScreen extends AbstractFurnaceScreen<FurnaceContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/furnace.png");

	public FurnaceScreen(FurnaceContainer container, PlayerInventory inventory, Text title) {
		super(container, new FurnaceRecipeBookScreen(), inventory, title, BG_TEX);
	}
}
