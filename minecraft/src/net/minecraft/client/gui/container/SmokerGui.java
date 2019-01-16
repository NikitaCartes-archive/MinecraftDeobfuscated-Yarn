package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SmokerGui extends AbstractFurnaceGui<SmokerContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/smoker.png");

	public SmokerGui(SmokerContainer smokerContainer, PlayerInventory playerInventory, TextComponent textComponent) {
		super(smokerContainer, new SmokerRecipeBookGui(), playerInventory, textComponent);
	}

	@Override
	protected Identifier getBackgroundTexture() {
		return BG_TEX;
	}
}
