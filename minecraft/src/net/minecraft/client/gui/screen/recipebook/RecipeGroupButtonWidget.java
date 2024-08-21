package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RecipeGroupButtonWidget extends ToggleButtonWidget {
	private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/tab"), Identifier.ofVanilla("recipe_book/tab_selected"));
	private final RecipeBookGroup category;
	private static final float field_32412 = 15.0F;
	private float bounce;

	public RecipeGroupButtonWidget(RecipeBookGroup category) {
		super(0, 0, 35, 27, false);
		this.category = category;
		this.setTextures(TEXTURES);
	}

	public void checkForNewRecipes(ClientRecipeBook recipeBook, boolean filteringCraftable) {
		RecipeResultCollection.RecipeFilterMode recipeFilterMode = filteringCraftable
			? RecipeResultCollection.RecipeFilterMode.CRAFTABLE
			: RecipeResultCollection.RecipeFilterMode.ANY;

		for (RecipeResultCollection recipeResultCollection : recipeBook.getResultsForGroup(this.category)) {
			for (RecipeEntry<?> recipeEntry : recipeResultCollection.filter(recipeFilterMode)) {
				if (recipeBook.shouldDisplay(recipeEntry)) {
					this.bounce = 15.0F;
					return;
				}
			}
		}
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.textures != null) {
			if (this.bounce > 0.0F) {
				float f = 1.0F + 0.1F * (float)Math.sin((double)(this.bounce / 15.0F * (float) Math.PI));
				context.getMatrices().push();
				context.getMatrices().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
				context.getMatrices().scale(1.0F, f, 1.0F);
				context.getMatrices().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
			}

			Identifier identifier = this.textures.get(true, this.toggled);
			int i = this.getX();
			if (this.toggled) {
				i -= 2;
			}

			context.drawGuiTexture(RenderLayer::getGuiTextured, identifier, i, this.getY(), this.width, this.height);
			this.renderIcons(context);
			if (this.bounce > 0.0F) {
				context.getMatrices().pop();
				this.bounce -= delta;
			}
		}
	}

	private void renderIcons(DrawContext context) {
		List<ItemStack> list = this.category.getIcons();
		int i = this.toggled ? -2 : 0;
		if (list.size() == 1) {
			context.drawItemWithoutEntity((ItemStack)list.get(0), this.getX() + 9 + i, this.getY() + 5);
		} else if (list.size() == 2) {
			context.drawItemWithoutEntity((ItemStack)list.get(0), this.getX() + 3 + i, this.getY() + 5);
			context.drawItemWithoutEntity((ItemStack)list.get(1), this.getX() + 14 + i, this.getY() + 5);
		}
	}

	public RecipeBookGroup getCategory() {
		return this.category;
	}

	public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
		List<RecipeResultCollection> list = recipeBook.getResultsForGroup(this.category);
		this.visible = false;

		for (RecipeResultCollection recipeResultCollection : list) {
			if (recipeResultCollection.isInitialized() && recipeResultCollection.hasFittingRecipes()) {
				this.visible = true;
				break;
			}
		}

		return this.visible;
	}
}
