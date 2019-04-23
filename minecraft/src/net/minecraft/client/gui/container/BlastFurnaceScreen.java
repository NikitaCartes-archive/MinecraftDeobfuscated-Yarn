package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlastFurnaceScreen extends AbstractFurnaceScreen<BlastFurnaceContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/blast_furnace.png");

	public BlastFurnaceScreen(BlastFurnaceContainer blastFurnaceContainer, PlayerInventory playerInventory, Component component) {
		super(blastFurnaceContainer, new BlastFurnaceRecipeBookScreen(), playerInventory, component, BG_TEX);
	}
}
