package net.minecraft.client.gui.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SmokerScreen extends AbstractFurnaceScreen<SmokerContainer> {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/smoker.png");

	public SmokerScreen(SmokerContainer smokerContainer, PlayerInventory playerInventory, Component component) {
		super(smokerContainer, new SmokerRecipeBookScreen(), playerInventory, component, BG_TEX);
	}
}
