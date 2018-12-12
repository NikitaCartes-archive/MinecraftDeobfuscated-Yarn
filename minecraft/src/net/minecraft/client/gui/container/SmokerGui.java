package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SmokerGui extends AbstractFurnaceGui {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/smoker.png");

	public SmokerGui(PlayerInventory playerInventory, Inventory inventory) {
		super(new SmokerContainer(playerInventory, inventory), new SmokerRecipeBookGui(), playerInventory, inventory);
	}

	@Override
	protected Identifier getBackgroundTexture() {
		return BG_TEX;
	}
}
