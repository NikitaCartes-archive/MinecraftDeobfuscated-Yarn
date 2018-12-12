package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FurnaceGui extends AbstractFurnaceGui {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/furnace.png");

	public FurnaceGui(PlayerInventory playerInventory, Inventory inventory) {
		super(new FurnaceContainer(playerInventory, inventory), new FurnaceRecipeBookGui(), playerInventory, inventory);
	}

	@Override
	protected Identifier getBackgroundTexture() {
		return BG_TEX;
	}
}
