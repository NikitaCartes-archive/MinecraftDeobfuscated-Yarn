package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlastFurnaceScreen extends AbstractFurnaceScreen<BlastFurnaceContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/blast_furnace.png");

	public BlastFurnaceScreen(BlastFurnaceContainer blastFurnaceContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(blastFurnaceContainer, new BlastFurnaceRecipeBookScreen(), playerInventory, textComponent);
	}

	@Override
	protected Identifier getBackgroundTexture() {
		return BG_TEX;
	}
}
