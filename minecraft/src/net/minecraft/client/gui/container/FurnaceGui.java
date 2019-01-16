package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FurnaceGui extends AbstractFurnaceGui<FurnaceContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/furnace.png");

	public FurnaceGui(FurnaceContainer furnaceContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(furnaceContainer, new FurnaceRecipeBookGui(), playerInventory, textComponent);
	}

	@Override
	protected Identifier getBackgroundTexture() {
		return BG_TEX;
	}
}
