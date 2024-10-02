package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SmokerScreen extends AbstractFurnaceScreen<SmokerScreenHandler> {
	private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.ofVanilla("container/smoker/lit_progress");
	private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.ofVanilla("container/smoker/burn_progress");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/smoker.png");
	private static final Text TOGGLE_SMOKABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.smokable");
	private static final List<RecipeBookWidget.Tab> TABS = List.of(
		new RecipeBookWidget.Tab(RecipeBookType.SMOKER), new RecipeBookWidget.Tab(Items.PORKCHOP, RecipeBookGroup.SMOKER_FOOD)
	);

	public SmokerScreen(SmokerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title, TOGGLE_SMOKABLE_TEXT, TEXTURE, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE, TABS);
	}
}
