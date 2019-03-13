package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlastFurnaceScreen extends AbstractFurnaceScreen<BlastFurnaceContainer> {
	private static final Identifier field_17115 = new Identifier("textures/gui/container/blast_furnace.png");

	public BlastFurnaceScreen(BlastFurnaceContainer blastFurnaceContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(blastFurnaceContainer, new BlastFurnaceRecipeBookScreen(), playerInventory, textComponent);
	}

	@Override
	protected Identifier method_17045() {
		return field_17115;
	}
}
