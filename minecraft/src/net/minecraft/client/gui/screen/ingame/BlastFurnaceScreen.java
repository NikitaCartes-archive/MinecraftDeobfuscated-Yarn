package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.BlastFurnaceRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlastFurnaceScreen extends AbstractFurnaceScreen<BlastFurnaceScreenHandler> {
	private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.method_60656("container/blast_furnace/lit_progress");
	private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.method_60656("container/blast_furnace/burn_progress");
	private static final Identifier TEXTURE = Identifier.method_60656("textures/gui/container/blast_furnace.png");

	public BlastFurnaceScreen(BlastFurnaceScreenHandler container, PlayerInventory inventory, Text title) {
		super(container, new BlastFurnaceRecipeBookScreen(), inventory, title, TEXTURE, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE);
	}
}
