package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.SmokerRecipeBookScreen;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SmokerScreen extends AbstractFurnaceScreen<SmokerContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/smoker.png");

	public SmokerScreen(SmokerContainer container, PlayerInventory inventory, Text title) {
		super(container, new SmokerRecipeBookScreen(), inventory, title, BG_TEX);
	}
}
