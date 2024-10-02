package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BlastFurnaceScreen extends AbstractFurnaceScreen<BlastFurnaceScreenHandler> {
	private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.ofVanilla("container/blast_furnace/lit_progress");
	private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.ofVanilla("container/blast_furnace/burn_progress");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/blast_furnace.png");
	private static final Text TOGGLE_BLASTABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.blastable");
	private static final List<RecipeBookWidget.Tab> TABS = List.of(
		new RecipeBookWidget.Tab(RecipeBookType.BLAST_FURNACE),
		new RecipeBookWidget.Tab(Items.REDSTONE_ORE, RecipeBookGroup.BLAST_FURNACE_BLOCKS),
		new RecipeBookWidget.Tab(Items.IRON_SHOVEL, Items.GOLDEN_LEGGINGS, RecipeBookGroup.BLAST_FURNACE_MISC)
	);

	public BlastFurnaceScreen(BlastFurnaceScreenHandler container, PlayerInventory inventory, Text title) {
		super(container, inventory, title, TOGGLE_BLASTABLE_TEXT, TEXTURE, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE, TABS);
	}
}
