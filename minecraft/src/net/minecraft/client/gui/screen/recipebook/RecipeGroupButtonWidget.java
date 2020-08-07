package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.AbstractRecipeScreenHandler;

@Environment(EnvType.CLIENT)
public class RecipeGroupButtonWidget extends ToggleButtonWidget {
	private final RecipeBookGroup category;
	private float bounce;

	public RecipeGroupButtonWidget(RecipeBookGroup category) {
		super(0, 0, 35, 27, false);
		this.category = category;
		this.setTextureUV(153, 2, 35, 0, RecipeBookWidget.TEXTURE);
	}

	public void checkForNewRecipes(MinecraftClient client) {
		ClientRecipeBook clientRecipeBook = client.player.getRecipeBook();
		List<RecipeResultCollection> list = clientRecipeBook.getResultsForGroup(this.category);
		if (client.player.currentScreenHandler instanceof AbstractRecipeScreenHandler) {
			for (RecipeResultCollection recipeResultCollection : list) {
				for (Recipe<?> recipe : recipeResultCollection.getResults(
					clientRecipeBook.isFilteringCraftable((AbstractRecipeScreenHandler<?>)client.player.currentScreenHandler)
				)) {
					if (clientRecipeBook.shouldDisplay(recipe)) {
						this.bounce = 15.0F;
						return;
					}
				}
			}
		}
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.bounce > 0.0F) {
			float f = 1.0F + 0.1F * (float)Math.sin((double)(this.bounce / 15.0F * (float) Math.PI));
			RenderSystem.pushMatrix();
			RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
			RenderSystem.scalef(1.0F, f, 1.0F);
			RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
		}

		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.getTextureManager().bindTexture(this.texture);
		RenderSystem.disableDepthTest();
		int i = this.u;
		int j = this.v;
		if (this.toggled) {
			i += this.pressedUOffset;
		}

		if (this.isHovered()) {
			j += this.hoverVOffset;
		}

		int k = this.x;
		if (this.toggled) {
			k -= 2;
		}

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexture(matrices, k, this.y, i, j, this.width, this.height);
		RenderSystem.enableDepthTest();
		this.renderIcons(minecraftClient.getItemRenderer());
		if (this.bounce > 0.0F) {
			RenderSystem.popMatrix();
			this.bounce -= delta;
		}
	}

	private void renderIcons(ItemRenderer itemRenderer) {
		List<ItemStack> list = this.category.getIcons();
		int i = this.toggled ? -2 : 0;
		if (list.size() == 1) {
			itemRenderer.renderInGui((ItemStack)list.get(0), this.x + 9 + i, this.y + 5);
		} else if (list.size() == 2) {
			itemRenderer.renderInGui((ItemStack)list.get(0), this.x + 3 + i, this.y + 5);
			itemRenderer.renderInGui((ItemStack)list.get(1), this.x + 14 + i, this.y + 5);
		}
	}

	public RecipeBookGroup getCategory() {
		return this.category;
	}

	public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
		List<RecipeResultCollection> list = recipeBook.getResultsForGroup(this.category);
		this.visible = false;
		if (list != null) {
			for (RecipeResultCollection recipeResultCollection : list) {
				if (recipeResultCollection.isInitialized() && recipeResultCollection.hasFittingRecipes()) {
					this.visible = true;
					break;
				}
			}
		}

		return this.visible;
	}
}
