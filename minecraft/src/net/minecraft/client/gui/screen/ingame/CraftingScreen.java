package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.recipebook.AbstractCraftingRecipeBookWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CraftingScreen extends RecipeBookScreen<CraftingScreenHandler> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/crafting_table.png");

	public CraftingScreen(CraftingScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, new AbstractCraftingRecipeBookWidget(handler), inventory, title);
	}

	@Override
	protected void init() {
		super.init();
		this.titleX = 29;
	}

	@Override
	protected ScreenPos getRecipeBookButtonPos() {
		return new ScreenPos(this.x + 5, this.height / 2 - 49);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = (this.height - this.backgroundHeight) / 2;
		context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
	}
}
