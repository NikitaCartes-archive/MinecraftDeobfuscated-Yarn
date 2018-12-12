package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlastFurnaceGui extends AbstractFurnaceGui {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/blast_furnace.png");

	public BlastFurnaceGui(PlayerInventory playerInventory, Inventory inventory) {
		super(new BlastFurnaceContainer(playerInventory, inventory), new BlastFurnaceRecipeBookGui(), playerInventory, inventory);
	}

	@Override
	protected Identifier getBackgroundTexture() {
		return BG_TEX;
	}
}
