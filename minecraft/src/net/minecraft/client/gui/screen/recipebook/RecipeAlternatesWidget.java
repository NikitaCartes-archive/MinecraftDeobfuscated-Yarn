package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RecipeAlternatesWidget extends DrawableHelper implements Drawable, Element {
	private static final Identifier BG_TEX = new Identifier("textures/gui/recipe_book.png");
	private final List<RecipeAlternatesWidget.AlternateButtonWidget> alternateButtons = Lists.<RecipeAlternatesWidget.AlternateButtonWidget>newArrayList();
	private boolean visible;
	private int buttonX;
	private int buttonY;
	private MinecraftClient client;
	private RecipeResultCollection resultCollection;
	private Recipe<?> lastClickedRecipe;
	private float time;
	private boolean furnace;

	public void showAlternatesForResult(MinecraftClient minecraftClient, RecipeResultCollection recipeResultCollection, int i, int j, int k, int l, float f) {
		this.client = minecraftClient;
		this.resultCollection = recipeResultCollection;
		if (minecraftClient.player.container instanceof AbstractFurnaceContainer) {
			this.furnace = true;
		}

		boolean bl = minecraftClient.player.getRecipeBook().isFilteringCraftable((CraftingContainer<?>)minecraftClient.player.container);
		List<Recipe<?>> list = recipeResultCollection.getResultsExclusive(true);
		List<Recipe<?>> list2 = bl ? Collections.emptyList() : recipeResultCollection.getResultsExclusive(false);
		int m = list.size();
		int n = m + list2.size();
		int o = n <= 16 ? 4 : 5;
		int p = (int)Math.ceil((double)((float)n / (float)o));
		this.buttonX = i;
		this.buttonY = j;
		int q = 25;
		float g = (float)(this.buttonX + Math.min(n, o) * 25);
		float h = (float)(k + 50);
		if (g > h) {
			this.buttonX = (int)((float)this.buttonX - f * (float)((int)((g - h) / f)));
		}

		float r = (float)(this.buttonY + p * 25);
		float s = (float)(l + 50);
		if (r > s) {
			this.buttonY = (int)((float)this.buttonY - f * (float)MathHelper.ceil((r - s) / f));
		}

		float t = (float)this.buttonY;
		float u = (float)(l - 100);
		if (t < u) {
			this.buttonY = (int)((float)this.buttonY - f * (float)MathHelper.ceil((t - u) / f));
		}

		this.visible = true;
		this.alternateButtons.clear();

		for (int v = 0; v < n; v++) {
			boolean bl2 = v < m;
			Recipe<?> recipe = bl2 ? (Recipe)list.get(v) : (Recipe)list2.get(v - m);
			int w = this.buttonX + 4 + 25 * (v % o);
			int x = this.buttonY + 5 + 25 * (v / o);
			if (this.furnace) {
				this.alternateButtons.add(new RecipeAlternatesWidget.class_511(w, x, recipe, bl2));
			} else {
				this.alternateButtons.add(new RecipeAlternatesWidget.AlternateButtonWidget(w, x, recipe, bl2));
			}
		}

		this.lastClickedRecipe = null;
	}

	@Override
	public boolean changeFocus(boolean bl) {
		return false;
	}

	public RecipeResultCollection getResults() {
		return this.resultCollection;
	}

	public Recipe<?> getLastClickedRecipe() {
		return this.lastClickedRecipe;
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i != 0) {
			return false;
		} else {
			for (RecipeAlternatesWidget.AlternateButtonWidget alternateButtonWidget : this.alternateButtons) {
				if (alternateButtonWidget.mouseClicked(d, e, i)) {
					this.lastClickedRecipe = alternateButtonWidget.recipe;
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return false;
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.visible) {
			this.time += f;
			GuiLighting.enableForItems();
			RenderSystem.enableBlend();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(BG_TEX);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, 0.0F, 170.0F);
			int k = this.alternateButtons.size() <= 16 ? 4 : 5;
			int l = Math.min(this.alternateButtons.size(), k);
			int m = MathHelper.ceil((float)this.alternateButtons.size() / (float)k);
			int n = 24;
			int o = 4;
			int p = 82;
			int q = 208;
			this.method_2618(l, m, 24, 4, 82, 208);
			RenderSystem.disableBlend();
			GuiLighting.disable();

			for (RecipeAlternatesWidget.AlternateButtonWidget alternateButtonWidget : this.alternateButtons) {
				alternateButtonWidget.render(i, j, f);
			}

			RenderSystem.popMatrix();
		}
	}

	private void method_2618(int i, int j, int k, int l, int m, int n) {
		this.blit(this.buttonX, this.buttonY, m, n, l, l);
		this.blit(this.buttonX + l * 2 + i * k, this.buttonY, m + k + l, n, l, l);
		this.blit(this.buttonX, this.buttonY + l * 2 + j * k, m, n + k + l, l, l);
		this.blit(this.buttonX + l * 2 + i * k, this.buttonY + l * 2 + j * k, m + k + l, n + k + l, l, l);

		for (int o = 0; o < i; o++) {
			this.blit(this.buttonX + l + o * k, this.buttonY, m + l, n, k, l);
			this.blit(this.buttonX + l + (o + 1) * k, this.buttonY, m + l, n, l, l);

			for (int p = 0; p < j; p++) {
				if (o == 0) {
					this.blit(this.buttonX, this.buttonY + l + p * k, m, n + l, l, k);
					this.blit(this.buttonX, this.buttonY + l + (p + 1) * k, m, n + l, l, l);
				}

				this.blit(this.buttonX + l + o * k, this.buttonY + l + p * k, m + l, n + l, k, k);
				this.blit(this.buttonX + l + (o + 1) * k, this.buttonY + l + p * k, m + l, n + l, l, k);
				this.blit(this.buttonX + l + o * k, this.buttonY + l + (p + 1) * k, m + l, n + l, k, l);
				this.blit(this.buttonX + l + (o + 1) * k - 1, this.buttonY + l + (p + 1) * k - 1, m + l, n + l, l + 1, l + 1);
				if (o == i - 1) {
					this.blit(this.buttonX + l * 2 + i * k, this.buttonY + l + p * k, m + k + l, n + l, l, k);
					this.blit(this.buttonX + l * 2 + i * k, this.buttonY + l + (p + 1) * k, m + k + l, n + l, l, l);
				}
			}

			this.blit(this.buttonX + l + o * k, this.buttonY + l * 2 + j * k, m + l, n + k + l, k, l);
			this.blit(this.buttonX + l + (o + 1) * k, this.buttonY + l * 2 + j * k, m + l, n + k + l, l, l);
		}
	}

	public void setVisible(boolean bl) {
		this.visible = bl;
	}

	public boolean isVisible() {
		return this.visible;
	}

	@Environment(EnvType.CLIENT)
	class AlternateButtonWidget extends AbstractButtonWidget implements RecipeGridAligner<Ingredient> {
		private final Recipe<?> recipe;
		private final boolean isCraftable;
		protected final List<RecipeAlternatesWidget.AlternateButtonWidget.InputSlot> slots = Lists.<RecipeAlternatesWidget.AlternateButtonWidget.InputSlot>newArrayList();

		public AlternateButtonWidget(int i, int j, Recipe<?> recipe, boolean bl) {
			super(i, j, 200, 20, "");
			this.width = 24;
			this.height = 24;
			this.recipe = recipe;
			this.isCraftable = bl;
			this.alignRecipe(recipe);
		}

		protected void alignRecipe(Recipe<?> recipe) {
			this.alignRecipeToGrid(3, 3, -1, recipe, recipe.getPreviewInputs().iterator(), 0);
		}

		@Override
		public void acceptAlignedInput(Iterator<Ingredient> iterator, int i, int j, int k, int l) {
			ItemStack[] itemStacks = ((Ingredient)iterator.next()).getMatchingStacksClient();
			if (itemStacks.length != 0) {
				this.slots.add(new RecipeAlternatesWidget.AlternateButtonWidget.InputSlot(3 + l * 7, 3 + k * 7, itemStacks));
			}
		}

		@Override
		public void renderButton(int i, int j, float f) {
			GuiLighting.enableForItems();
			RenderSystem.enableAlphaTest();
			RecipeAlternatesWidget.this.client.getTextureManager().bindTexture(RecipeAlternatesWidget.BG_TEX);
			int k = 152;
			if (!this.isCraftable) {
				k += 26;
			}

			int l = RecipeAlternatesWidget.this.furnace ? 130 : 78;
			if (this.isHovered()) {
				l += 26;
			}

			this.blit(this.x, this.y, k, l, this.width, this.height);

			for (RecipeAlternatesWidget.AlternateButtonWidget.InputSlot inputSlot : this.slots) {
				RenderSystem.pushMatrix();
				float g = 0.42F;
				int m = (int)((float)(this.x + inputSlot.field_3119) / 0.42F - 3.0F);
				int n = (int)((float)(this.y + inputSlot.field_3118) / 0.42F - 3.0F);
				RenderSystem.scalef(0.42F, 0.42F, 1.0F);
				RenderSystem.enableLighting();
				RecipeAlternatesWidget.this.client
					.getItemRenderer()
					.renderGuiItem(inputSlot.field_3120[MathHelper.floor(RecipeAlternatesWidget.this.time / 30.0F) % inputSlot.field_3120.length], m, n);
				RenderSystem.disableLighting();
				RenderSystem.popMatrix();
			}

			RenderSystem.disableAlphaTest();
			GuiLighting.disable();
		}

		@Environment(EnvType.CLIENT)
		public class InputSlot {
			public final ItemStack[] field_3120;
			public final int field_3119;
			public final int field_3118;

			public InputSlot(int i, int j, ItemStack[] itemStacks) {
				this.field_3119 = i;
				this.field_3118 = j;
				this.field_3120 = itemStacks;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_511 extends RecipeAlternatesWidget.AlternateButtonWidget {
		public class_511(int i, int j, Recipe<?> recipe, boolean bl) {
			super(i, j, recipe, bl);
		}

		@Override
		protected void alignRecipe(Recipe<?> recipe) {
			ItemStack[] itemStacks = recipe.getPreviewInputs().get(0).getMatchingStacksClient();
			this.slots.add(new RecipeAlternatesWidget.AlternateButtonWidget.InputSlot(10, 10, itemStacks));
		}
	}
}
