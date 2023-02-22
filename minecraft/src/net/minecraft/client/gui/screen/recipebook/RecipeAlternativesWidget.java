package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RecipeAlternativesWidget extends DrawableHelper implements Drawable, Element {
	static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/recipe_book.png");
	private static final int field_32406 = 4;
	private static final int field_32407 = 5;
	private static final float field_33739 = 0.375F;
	public static final int field_42162 = 25;
	private final List<RecipeAlternativesWidget.AlternativeButtonWidget> alternativeButtons = Lists.<RecipeAlternativesWidget.AlternativeButtonWidget>newArrayList();
	private boolean visible;
	private int buttonX;
	private int buttonY;
	MinecraftClient client;
	private RecipeResultCollection resultCollection;
	@Nullable
	private Recipe<?> lastClickedRecipe;
	float time;
	boolean furnace;

	public void showAlternativesForResult(
		MinecraftClient client, RecipeResultCollection results, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta
	) {
		this.client = client;
		this.resultCollection = results;
		if (client.player.currentScreenHandler instanceof AbstractFurnaceScreenHandler) {
			this.furnace = true;
		}

		boolean bl = client.player.getRecipeBook().isFilteringCraftable((AbstractRecipeScreenHandler<?>)client.player.currentScreenHandler);
		List<Recipe<?>> list = results.getRecipes(true);
		List<Recipe<?>> list2 = bl ? Collections.emptyList() : results.getRecipes(false);
		int i = list.size();
		int j = i + list2.size();
		int k = j <= 16 ? 4 : 5;
		int l = (int)Math.ceil((double)((float)j / (float)k));
		this.buttonX = buttonX;
		this.buttonY = buttonY;
		float f = (float)(this.buttonX + Math.min(j, k) * 25);
		float g = (float)(areaCenterX + 50);
		if (f > g) {
			this.buttonX = (int)((float)this.buttonX - delta * (float)((int)((f - g) / delta)));
		}

		float h = (float)(this.buttonY + l * 25);
		float m = (float)(areaCenterY + 50);
		if (h > m) {
			this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((h - m) / delta));
		}

		float n = (float)this.buttonY;
		float o = (float)(areaCenterY - 100);
		if (n < o) {
			this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((n - o) / delta));
		}

		this.visible = true;
		this.alternativeButtons.clear();

		for (int p = 0; p < j; p++) {
			boolean bl2 = p < i;
			Recipe<?> recipe = bl2 ? (Recipe)list.get(p) : (Recipe)list2.get(p - i);
			int q = this.buttonX + 4 + 25 * (p % k);
			int r = this.buttonY + 5 + 25 * (p / k);
			if (this.furnace) {
				this.alternativeButtons.add(new RecipeAlternativesWidget.FurnaceAlternativeButtonWidget(q, r, recipe, bl2));
			} else {
				this.alternativeButtons.add(new RecipeAlternativesWidget.AlternativeButtonWidget(q, r, recipe, bl2));
			}
		}

		this.lastClickedRecipe = null;
	}

	public RecipeResultCollection getResults() {
		return this.resultCollection;
	}

	@Nullable
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.time += delta;
			RenderSystem.enableBlend();
			RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
			matrices.push();
			matrices.translate(0.0F, 0.0F, 170.0F);
			int i = this.alternativeButtons.size() <= 16 ? 4 : 5;
			int j = Math.min(this.alternativeButtons.size(), i);
			int k = MathHelper.ceil((float)this.alternativeButtons.size() / (float)i);
			int l = 4;
			drawNineSlicedTexture(matrices, this.buttonX, this.buttonY, j * 25 + 8, k * 25 + 8, 4, 32, 32, 82, 208);
			RenderSystem.disableBlend();

			for (RecipeAlternativesWidget.AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
				alternativeButtonWidget.render(matrices, mouseX, mouseY, delta);
			}

			matrices.pop();
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setFocused(boolean focused) {
	}

	@Override
	public boolean isFocused() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	class AlternativeButtonWidget extends ClickableWidget implements RecipeGridAligner<Ingredient> {
		final Recipe<?> recipe;
		private final boolean craftable;
		protected final List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> slots = Lists.<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot>newArrayList();

		public AlternativeButtonWidget(int x, int y, Recipe<?> recipe, boolean craftable) {
			super(x, y, 200, 20, ScreenTexts.EMPTY);
			this.width = 24;
			this.height = 24;
			this.recipe = recipe;
			this.craftable = craftable;
			this.alignRecipe(recipe);
		}

		protected void alignRecipe(Recipe<?> recipe) {
			this.alignRecipeToGrid(3, 3, -1, recipe, recipe.getIngredients().iterator(), 0);
		}

		@Override
		public void appendClickableNarrations(NarrationMessageBuilder builder) {
			this.appendDefaultNarrations(builder);
		}

		@Override
		public void acceptAlignedInput(Iterator<Ingredient> inputs, int slot, int amount, int gridX, int gridY) {
			ItemStack[] itemStacks = ((Ingredient)inputs.next()).getMatchingStacks();
			if (itemStacks.length != 0) {
				this.slots.add(new RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot(3 + gridY * 7, 3 + gridX * 7, itemStacks));
			}
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RenderSystem.setShaderTexture(0, RecipeAlternativesWidget.BACKGROUND_TEXTURE);
			int i = 152;
			if (!this.craftable) {
				i += 26;
			}

			int j = RecipeAlternativesWidget.this.furnace ? 130 : 78;
			if (this.isSelected()) {
				j += 26;
			}

			drawTexture(matrices, this.getX(), this.getY(), i, j, this.width, this.height);
			matrices.push();
			matrices.translate((double)(this.getX() + 2), (double)(this.getY() + 2), 150.0);

			for (RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot inputSlot : this.slots) {
				matrices.push();
				matrices.translate((double)inputSlot.y, (double)inputSlot.x, 0.0);
				matrices.scale(0.375F, 0.375F, 1.0F);
				matrices.translate(-8.0, -8.0, 0.0);
				RecipeAlternativesWidget.this.client
					.getItemRenderer()
					.renderInGuiWithOverrides(matrices, inputSlot.stacks[MathHelper.floor(RecipeAlternativesWidget.this.time / 30.0F) % inputSlot.stacks.length], 0, 0);
				matrices.pop();
			}

			matrices.pop();
		}

		@Environment(EnvType.CLIENT)
		protected class InputSlot {
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
		public FurnaceAlternativeButtonWidget(int i, int j, Recipe<?> recipe, boolean bl) {
			super(i, j, recipe, bl);
		}

		@Override
		protected void alignRecipe(Recipe<?> recipe) {
			ItemStack[] itemStacks = recipe.getIngredients().get(0).getMatchingStacks();
			this.slots.add(new RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot(10, 10, itemStacks));
		}
	}
}
