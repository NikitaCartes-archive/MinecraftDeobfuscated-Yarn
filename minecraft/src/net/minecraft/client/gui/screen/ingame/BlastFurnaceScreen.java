package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.BlastFurnaceRecipeBookScreen;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlastFurnaceScreen extends AbstractFurnaceScreen<BlastFurnaceContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/blast_furnace.png");

	public BlastFurnaceScreen(BlastFurnaceContainer blastFurnaceContainer, PlayerInventory playerInventory, Text text) {
		super(blastFurnaceContainer, new BlastFurnaceRecipeBookScreen(), playerInventory, text, BG_TEX);
	}
}
