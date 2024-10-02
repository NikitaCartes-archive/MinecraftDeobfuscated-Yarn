package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceScreenHandler> extends RecipeBookScreen<T> {
	private final Identifier background;
	private final Identifier litProgressTexture;
	private final Identifier burnProgressTexture;

	public AbstractFurnaceScreen(
		T handler,
		PlayerInventory playerInventory,
		Text title,
		Text toggleCraftableButtonText,
		Identifier background,
		Identifier litProgressTexture,
		Identifier burnProgressTexture,
		List<RecipeBookWidget.Tab> recipeBookTabs
	) {
		super(handler, new AbstractFurnaceRecipeBookWidget(handler, toggleCraftableButtonText, recipeBookTabs), playerInventory, title);
		this.background = background;
		this.litProgressTexture = litProgressTexture;
		this.burnProgressTexture = burnProgressTexture;
	}

	@Override
	public void init() {
		super.init();
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
	}

	@Override
	protected ScreenPos getRecipeBookButtonPos() {
		return new ScreenPos(this.x + 20, this.height / 2 - 49);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		context.drawTexture(RenderLayer::getGuiTextured, this.background, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
		if (this.handler.isBurning()) {
			int k = 14;
			int l = MathHelper.ceil(this.handler.getFuelProgress() * 13.0F) + 1;
			context.drawGuiTexture(RenderLayer::getGuiTextured, this.litProgressTexture, 14, 14, 0, 14 - l, i + 56, j + 36 + 14 - l, 14, l);
		}

		int k = 24;
		int l = MathHelper.ceil(this.handler.getCookProgress() * 24.0F);
		context.drawGuiTexture(RenderLayer::getGuiTextured, this.burnProgressTexture, 24, 16, 0, 0, i + 79, j + 34, l, 16);
	}
}
