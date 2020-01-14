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
import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RecipeAlternativesWidget extends DrawableHelper implements Drawable, Element {
	private static final Identifier BG_TEX = new Identifier("textures/gui/recipe_book.png");
	private final List<RecipeAlternativesWidget.AlternativeButtonWidget> alternativeButtons = Lists.<RecipeAlternativesWidget.AlternativeButtonWidget>newArrayList();
	private boolean visible;
	private int buttonX;
	private int buttonY;
	private MinecraftClient client;
	private RecipeResultCollection resultCollection;
	private Recipe<?> lastClickedRecipe;
	private float time;
	private boolean furnace;

	public void showAlternativesForResult(
		MinecraftClient client, RecipeResultCollection results, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta
	) {
		this.client = client;
		this.resultCollection = results;
		if (client.player.container instanceof AbstractFurnaceContainer) {
			this.furnace = true;
		}

		boolean bl = client.player.getRecipeBook().isFilteringCraftable((CraftingContainer<?>)client.player.container);
		List<Recipe<?>> list = results.getRecipes(true);
		List<Recipe<?>> list2 = bl ? Collections.emptyList() : results.getRecipes(false);
		int i = list.size();
		int j = i + list2.size();
		int k = j <= 16 ? 4 : 5;
		int l = (int)Math.ceil((double)((float)j / (float)k));
		this.buttonX = buttonX;
		this.buttonY = buttonY;
		int m = 25;
		float f = (float)(this.buttonX + Math.min(j, k) * 25);
		float g = (float)(areaCenterX + 50);
		if (f > g) {
			this.buttonX = (int)((float)this.buttonX - delta * (float)((int)((f - g) / delta)));
		}

		float h = (float)(this.buttonY + l * 25);
		float n = (float)(areaCenterY + 50);
		if (h > n) {
			this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((h - n) / delta));
		}

		float o = (float)this.buttonY;
		float p = (float)(areaCenterY - 100);
		if (o < p) {
			this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((o - p) / delta));
		}

		this.visible = true;
		this.alternativeButtons.clear();

		for (int q = 0; q < j; q++) {
			boolean bl2 = q < i;
			Recipe<?> recipe = bl2 ? (Recipe)list.get(q) : (Recipe)list2.get(q - i);
			int r = this.buttonX + 4 + 25 * (q % k);
			int s = this.buttonY + 5 + 25 * (q / k);
			if (this.furnace) {
				this.alternativeButtons.add(new RecipeAlternativesWidget.FurnaceAlternativeButtonWidget(r, s, recipe, bl2));
			} else {
				this.alternativeButtons.add(new RecipeAlternativesWidget.AlternativeButtonWidget(r, s, recipe, bl2));
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0) {
			return false;
		} else {
			for (RecipeAlternativesWidget.AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
				if (alternativeButtonWidget.mouseClicked(mouseX, mouseY, button)) {
					this.lastClickedRecipe = alternativeButtonWidget.recipe;
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.time += delta;
			RenderSystem.enableBlend();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(BG_TEX);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, 0.0F, 170.0F);
			int i = this.alternativeButtons.size() <= 16 ? 4 : 5;
			int j = Math.min(this.alternativeButtons.size(), i);
			int k = MathHelper.ceil((float)this.alternativeButtons.size() / (float)i);
			int l = 24;
			int m = 4;
			int n = 82;
			int o = 208;
			this.renderGrid(j, k, 24, 4, 82, 208);
			RenderSystem.disableBlend();

			for (RecipeAlternativesWidget.AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
				alternativeButtonWidget.render(mouseX, mouseY, delta);
			}

			RenderSystem.popMatrix();
		}
	}

	private void renderGrid(int columns, int rows, int squareSize, int borderSize, int u, int v) {
		this.blit(this.buttonX, this.buttonY, u, v, borderSize, borderSize);
		this.blit(this.buttonX + borderSize * 2 + columns * squareSize, this.buttonY, u + squareSize + borderSize, v, borderSize, borderSize);
		this.blit(this.buttonX, this.buttonY + borderSize * 2 + rows * squareSize, u, v + squareSize + borderSize, borderSize, borderSize);
		this.blit(
			this.buttonX + borderSize * 2 + columns * squareSize,
			this.buttonY + borderSize * 2 + rows * squareSize,
			u + squareSize + borderSize,
			v + squareSize + borderSize,
			borderSize,
			borderSize
		);

		for (int i = 0; i < columns; i++) {
			this.blit(this.buttonX + borderSize + i * squareSize, this.buttonY, u + borderSize, v, squareSize, borderSize);
			this.blit(this.buttonX + borderSize + (i + 1) * squareSize, this.buttonY, u + borderSize, v, borderSize, borderSize);

			for (int j = 0; j < rows; j++) {
				if (i == 0) {
					this.blit(this.buttonX, this.buttonY + borderSize + j * squareSize, u, v + borderSize, borderSize, squareSize);
					this.blit(this.buttonX, this.buttonY + borderSize + (j + 1) * squareSize, u, v + borderSize, borderSize, borderSize);
				}

				this.blit(this.buttonX + borderSize + i * squareSize, this.buttonY + borderSize + j * squareSize, u + borderSize, v + borderSize, squareSize, squareSize);
				this.blit(
					this.buttonX + borderSize + (i + 1) * squareSize, this.buttonY + borderSize + j * squareSize, u + borderSize, v + borderSize, borderSize, squareSize
				);
				this.blit(
					this.buttonX + borderSize + i * squareSize, this.buttonY + borderSize + (j + 1) * squareSize, u + borderSize, v + borderSize, squareSize, borderSize
				);
				this.blit(
					this.buttonX + borderSize + (i + 1) * squareSize - 1,
					this.buttonY + borderSize + (j + 1) * squareSize - 1,
					u + borderSize,
					v + borderSize,
					borderSize + 1,
					borderSize + 1
				);
				if (i == columns - 1) {
					this.blit(
						this.buttonX + borderSize * 2 + columns * squareSize,
						this.buttonY + borderSize + j * squareSize,
						u + squareSize + borderSize,
						v + borderSize,
						borderSize,
						squareSize
					);
					this.blit(
						this.buttonX + borderSize * 2 + columns * squareSize,
						this.buttonY + borderSize + (j + 1) * squareSize,
						u + squareSize + borderSize,
						v + borderSize,
						borderSize,
						borderSize
					);
				}
			}

			this.blit(
				this.buttonX + borderSize + i * squareSize,
				this.buttonY + borderSize * 2 + rows * squareSize,
				u + borderSize,
				v + squareSize + borderSize,
				squareSize,
				borderSize
			);
			this.blit(
				this.buttonX + borderSize + (i + 1) * squareSize,
				this.buttonY + borderSize * 2 + rows * squareSize,
				u + borderSize,
				v + squareSize + borderSize,
				borderSize,
				borderSize
			);
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return this.visible;
	}

	@Environment(EnvType.CLIENT)
	class AlternativeButtonWidget extends AbstractButtonWidget implements RecipeGridAligner<Ingredient> {
		private final Recipe<?> recipe;
		private final boolean isCraftable;
		protected final List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> slots = Lists.<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot>newArrayList();

		public AlternativeButtonWidget(int x, int y, Recipe<?> recipe, boolean isCraftable) {
			super(x, y, 200, 20, "");
			this.width = 24;
			this.height = 24;
			this.recipe = recipe;
			this.isCraftable = isCraftable;
			this.alignRecipe(recipe);
		}

		protected void alignRecipe(Recipe<?> recipe) {
			this.alignRecipeToGrid(3, 3, -1, recipe, recipe.getPreviewInputs().iterator(), 0);
		}

		@Override
		public void acceptAlignedInput(Iterator<Ingredient> inputs, int slot, int amount, int gridX, int gridY) {
			ItemStack[] itemStacks = ((Ingredient)inputs.next()).getMatchingStacksClient();
			if (itemStacks.length != 0) {
				this.slots.add(new RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot(3 + gridY * 7, 3 + gridX * 7, itemStacks));
			}
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float delta) {
			RenderSystem.enableAlphaTest();
			RecipeAlternativesWidget.this.client.getTextureManager().bindTexture(RecipeAlternativesWidget.BG_TEX);
			int i = 152;
			if (!this.isCraftable) {
				i += 26;
			}

			int j = RecipeAlternativesWidget.this.furnace ? 130 : 78;
			if (this.isHovered()) {
				j += 26;
			}

			this.blit(this.x, this.y, i, j, this.width, this.height);

			for (RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot inputSlot : this.slots) {
				RenderSystem.pushMatrix();
				float f = 0.42F;
				int k = (int)((float)(this.x + inputSlot.y) / 0.42F - 3.0F);
				int l = (int)((float)(this.y + inputSlot.x) / 0.42F - 3.0F);
				RenderSystem.scalef(0.42F, 0.42F, 1.0F);
				RecipeAlternativesWidget.this.client
					.getItemRenderer()
					.renderGuiItem(inputSlot.stacks[MathHelper.floor(RecipeAlternativesWidget.this.time / 30.0F) % inputSlot.stacks.length], k, l);
				RenderSystem.popMatrix();
			}

			RenderSystem.disableAlphaTest();
		}

		@Environment(EnvType.CLIENT)
		public class InputSlot {
			public final ItemStack[] stacks;
			public final int y;
			public final int x;

			public InputSlot(int y, int x, ItemStack[] stacks) {
				this.y = y;
				this.x = x;
				this.stacks = stacks;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class FurnaceAlternativeButtonWidget extends RecipeAlternativesWidget.AlternativeButtonWidget {
		public FurnaceAlternativeButtonWidget(int x, int y, Recipe<?> recipe, boolean isCraftable) {
			super(x, y, recipe, isCraftable);
		}

		@Override
		protected void alignRecipe(Recipe<?> recipe) {
			ItemStack[] itemStacks = recipe.getPreviewInputs().get(0).getMatchingStacksClient();
			this.slots.add(new RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot(10, 10, itemStacks));
		}
	}
}