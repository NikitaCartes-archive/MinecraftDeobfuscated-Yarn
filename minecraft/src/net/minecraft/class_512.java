package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.container.RecipeBookGui;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.recipe.book.RecipeBookGroup;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

@Environment(EnvType.CLIENT)
public class class_512 extends class_361 {
	private final RecipeBookGroup category;
	private float field_3122;

	public class_512(int i, RecipeBookGroup recipeBookGroup) {
		super(i, 0, 0, 35, 27, false);
		this.category = recipeBookGroup;
		this.method_1962(153, 2, 35, 0, RecipeBookGui.TEXTURE);
	}

	public void method_2622(MinecraftClient minecraftClient) {
		ClientRecipeBook clientRecipeBook = minecraftClient.player.getRecipeBook();
		List<class_516> list = clientRecipeBook.method_1396(this.category);
		if (minecraftClient.player.container instanceof CraftingContainer) {
			for (class_516 lv : list) {
				for (Recipe recipe : lv.method_2651(clientRecipeBook.isFilteringCraftable((CraftingContainer)minecraftClient.player.container))) {
					if (clientRecipeBook.method_14883(recipe)) {
						this.field_3122 = 15.0F;
						return;
					}
				}
			}
		}
	}

	@Override
	public void draw(int i, int j, float f) {
		if (this.visible) {
			if (this.field_3122 > 0.0F) {
				float g = 1.0F + 0.1F * (float)Math.sin((double)(this.field_3122 / 15.0F * (float) Math.PI));
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
				GlStateManager.scalef(1.0F, g, 1.0F);
				GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
			}

			this.hovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			minecraftClient.getTextureManager().bindTexture(this.field_2193);
			GlStateManager.disableDepthTest();
			int k = this.field_2192;
			int l = this.field_2191;
			if (this.field_2194) {
				k += this.field_2190;
			}

			if (this.hovered) {
				l += this.field_2189;
			}

			int m = this.x;
			if (this.field_2194) {
				m -= 2;
			}

			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedRect(m, this.y, k, l, this.width, this.height);
			GlStateManager.enableDepthTest();
			class_308.method_1453();
			GlStateManager.disableLighting();
			this.method_2621(minecraftClient.getItemRenderer());
			GlStateManager.enableLighting();
			class_308.method_1450();
			if (this.field_3122 > 0.0F) {
				GlStateManager.popMatrix();
				this.field_3122 -= f;
			}
		}
	}

	private void method_2621(ItemRenderer itemRenderer) {
		List<ItemStack> list = this.category.getIcons();
		int i = this.field_2194 ? -2 : 0;
		if (list.size() == 1) {
			itemRenderer.renderItemAndGlowInGui((ItemStack)list.get(0), this.x + 9 + i, this.y + 5);
		} else if (list.size() == 2) {
			itemRenderer.renderItemAndGlowInGui((ItemStack)list.get(0), this.x + 3 + i, this.y + 5);
			itemRenderer.renderItemAndGlowInGui((ItemStack)list.get(1), this.x + 14 + i, this.y + 5);
		}
	}

	public RecipeBookGroup getCategory() {
		return this.category;
	}

	public boolean method_2624(ClientRecipeBook clientRecipeBook) {
		List<class_516> list = clientRecipeBook.method_1396(this.category);
		this.visible = false;
		if (list != null) {
			for (class_516 lv : list) {
				if (lv.method_2652() && lv.method_2657()) {
					this.visible = true;
					break;
				}
			}
		}

		return this.visible;
	}
}
