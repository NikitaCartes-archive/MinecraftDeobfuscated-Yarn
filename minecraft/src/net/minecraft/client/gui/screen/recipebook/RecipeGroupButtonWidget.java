package net.minecraft.client.gui.screen.recipebook;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.recipe.book.RecipeBookGroup;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

@Environment(EnvType.CLIENT)
public class RecipeGroupButtonWidget extends ToggleButtonWidget {
	private final RecipeBookGroup category;
	private float bounce;

	public RecipeGroupButtonWidget(RecipeBookGroup recipeBookGroup) {
		super(0, 0, 35, 27, false);
		this.category = recipeBookGroup;
		this.setTextureUV(153, 2, 35, 0, RecipeBookWidget.TEXTURE);
	}

	public void checkForNewRecipes(MinecraftClient minecraftClient) {
		ClientRecipeBook clientRecipeBook = minecraftClient.field_1724.getRecipeBook();
		List<RecipeResultCollection> list = clientRecipeBook.method_1396(this.category);
		if (minecraftClient.field_1724.container instanceof CraftingContainer) {
			for (RecipeResultCollection recipeResultCollection : list) {
				for (Recipe<?> recipe : recipeResultCollection.getResults(clientRecipeBook.isFilteringCraftable((CraftingContainer<?>)minecraftClient.field_1724.container))) {
					if (clientRecipeBook.shouldDisplay(recipe)) {
						this.bounce = 15.0F;
						return;
					}
				}
			}
		}
	}

	@Override
	public void renderButton(int i, int j, float f) {
		if (this.bounce > 0.0F) {
			float g = 1.0F + 0.1F * (float)Math.sin((double)(this.bounce / 15.0F * (float) Math.PI));
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
			GlStateManager.scalef(1.0F, g, 1.0F);
			GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
		}

		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		minecraftClient.method_1531().bindTexture(this.texture);
		GlStateManager.disableDepthTest();
		int k = this.u;
		int l = this.v;
		if (this.toggled) {
			k += this.pressedUOffset;
		}

		if (this.isHovered()) {
			l += this.hoverVOffset;
		}

		int m = this.x;
		if (this.toggled) {
			m -= 2;
		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.blit(m, this.y, k, l, this.width, this.height);
		GlStateManager.enableDepthTest();
		GuiLighting.enableForItems();
		GlStateManager.disableLighting();
		this.method_2621(minecraftClient.method_1480());
		GlStateManager.enableLighting();
		GuiLighting.disable();
		if (this.bounce > 0.0F) {
			GlStateManager.popMatrix();
			this.bounce -= f;
		}
	}

	private void method_2621(ItemRenderer itemRenderer) {
		List<ItemStack> list = this.category.getIcons();
		int i = this.toggled ? -2 : 0;
		if (list.size() == 1) {
			itemRenderer.renderGuiItem((ItemStack)list.get(0), this.x + 9 + i, this.y + 5);
		} else if (list.size() == 2) {
			itemRenderer.renderGuiItem((ItemStack)list.get(0), this.x + 3 + i, this.y + 5);
			itemRenderer.renderGuiItem((ItemStack)list.get(1), this.x + 14 + i, this.y + 5);
		}
	}

	public RecipeBookGroup getCategory() {
		return this.category;
	}

	public boolean hasKnownRecipes(ClientRecipeBook clientRecipeBook) {
		List<RecipeResultCollection> list = clientRecipeBook.method_1396(this.category);
		this.visible = false;
		if (list != null) {
			for (RecipeResultCollection recipeResultCollection : list) {
				if (recipeResultCollection.isInitialized() && recipeResultCollection.hasFittableResults()) {
					this.visible = true;
					break;
				}
			}
		}

		return this.visible;
	}
}
