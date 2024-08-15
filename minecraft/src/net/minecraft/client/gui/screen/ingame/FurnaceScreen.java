package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FurnaceScreen extends AbstractFurnaceScreen<FurnaceScreenHandler> {
	private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.ofVanilla("container/furnace/lit_progress");
	private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.ofVanilla("container/furnace/burn_progress");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/furnace.png");
	private static final Text TOGGLE_SMELTABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.smeltable");

	public FurnaceScreen(FurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title, TOGGLE_SMELTABLE_TEXT, TEXTURE, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE);
	}
}
