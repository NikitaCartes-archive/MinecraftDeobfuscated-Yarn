package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipe.book.RecipeDisplayListener;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;

@Environment(EnvType.CLIENT)
public class RecipeBookGuiResults {
	private final List<AnimatedResultButton> resultButtons = Lists.<AnimatedResultButton>newArrayListWithCapacity(20);
	private AnimatedResultButton hoveredResultButton;
	private final RecipeAlternatesWidget alternatesWidget = new RecipeAlternatesWidget();
	private MinecraftClient client;
	private final List<RecipeDisplayListener> recipeDisplayListeners = Lists.<RecipeDisplayListener>newArrayList();
	private List<RecipeResultCollection> resultCollections;
	private ToggleButtonWidget nextPageButton;
	private ToggleButtonWidget prevPageButton;
	private int pageCount;
	private int currentPage;
	private RecipeBook recipeBook;
	private Recipe<?> lastClickedRecipe;
	private RecipeResultCollection resultCollection;

	public RecipeBookGuiResults() {
		for (int i = 0; i < 20; i++) {
			this.resultButtons.add(new AnimatedResultButton());
		}
	}

	public void initialize(MinecraftClient minecraftClient, int i, int j) {
		this.client = minecraftClient;
		this.recipeBook = minecraftClient.player.getRecipeBook();

		for (int k = 0; k < this.resultButtons.size(); k++) {
			((AnimatedResultButton)this.resultButtons.get(k)).setPos(i + 11 + 25 * (k % 5), j + 31 + 25 * (k / 5));
		}

		this.nextPageButton = new ToggleButtonWidget(i + 93, j + 137, 12, 17, false);
		this.nextPageButton.setTextureUV(1, 208, 13, 18, RecipeBookGui.TEXTURE);
		this.prevPageButton = new ToggleButtonWidget(i + 38, j + 137, 12, 17, true);
		this.prevPageButton.setTextureUV(1, 208, 13, 18, RecipeBookGui.TEXTURE);
	}

	public void setGui(RecipeBookGui recipeBookGui) {
		this.recipeDisplayListeners.remove(recipeBookGui);
		this.recipeDisplayListeners.add(recipeBookGui);
	}

	public void setResults(List<RecipeResultCollection> list, boolean bl) {
		this.resultCollections = list;
		this.pageCount = (int)Math.ceil((double)list.size() / 20.0);
		if (this.pageCount <= this.currentPage || bl) {
			this.currentPage = 0;
		}

		this.refreshResultButtons();
	}

	private void refreshResultButtons() {
		int i = 20 * this.currentPage;

		for (int j = 0; j < this.resultButtons.size(); j++) {
			AnimatedResultButton animatedResultButton = (AnimatedResultButton)this.resultButtons.get(j);
			if (i + j < this.resultCollections.size()) {
				RecipeResultCollection recipeResultCollection = (RecipeResultCollection)this.resultCollections.get(i + j);
				animatedResultButton.showResultCollection(recipeResultCollection, this);
				animatedResultButton.visible = true;
			} else {
				animatedResultButton.visible = false;
			}
		}

		this.hideShowPageButtons();
	}

	private void hideShowPageButtons() {
		this.nextPageButton.visible = this.pageCount > 1 && this.currentPage < this.pageCount - 1;
		this.prevPageButton.visible = this.pageCount > 1 && this.currentPage > 0;
	}

	public void draw(int i, int j, int k, int l, float f) {
		if (this.pageCount > 1) {
			String string = this.currentPage + 1 + "/" + this.pageCount;
			int m = this.client.textRenderer.getStringWidth(string);
			this.client.textRenderer.draw(string, (float)(i - m / 2 + 73), (float)(j + 141), -1);
		}

		GuiLighting.disable();
		this.hoveredResultButton = null;

		for (AnimatedResultButton animatedResultButton : this.resultButtons) {
			animatedResultButton.render(k, l, f);
			if (animatedResultButton.visible && animatedResultButton.isHovered()) {
				this.hoveredResultButton = animatedResultButton;
			}
		}

		this.prevPageButton.render(k, l, f);
		this.nextPageButton.render(k, l, f);
		this.alternatesWidget.render(k, l, f);
	}

	public void drawTooltip(int i, int j) {
		if (this.client.currentScreen != null && this.hoveredResultButton != null && !this.alternatesWidget.isVisible()) {
			this.client.currentScreen.renderTooltip(this.hoveredResultButton.method_2644(this.client.currentScreen), i, j);
		}
	}

	@Nullable
	public Recipe<?> getLastClickedRecipe() {
		return this.lastClickedRecipe;
	}

	@Nullable
	public RecipeResultCollection getLastClickedResults() {
		return this.resultCollection;
	}

	public void hideAlternates() {
		this.alternatesWidget.setVisible(false);
	}

	public boolean mouseClicked(double d, double e, int i, int j, int k, int l, int m) {
		this.lastClickedRecipe = null;
		this.resultCollection = null;
		if (this.alternatesWidget.isVisible()) {
			if (this.alternatesWidget.mouseClicked(d, e, i)) {
				this.lastClickedRecipe = this.alternatesWidget.getLastClickedRecipe();
				this.resultCollection = this.alternatesWidget.getResults();
			} else {
				this.alternatesWidget.setVisible(false);
			}

			return true;
		} else if (this.nextPageButton.mouseClicked(d, e, i)) {
			this.currentPage++;
			this.refreshResultButtons();
			return true;
		} else if (this.prevPageButton.mouseClicked(d, e, i)) {
			this.currentPage--;
			this.refreshResultButtons();
			return true;
		} else {
			for (AnimatedResultButton animatedResultButton : this.resultButtons) {
				if (animatedResultButton.mouseClicked(d, e, i)) {
					if (i == 0) {
						this.lastClickedRecipe = animatedResultButton.currentRecipe();
						this.resultCollection = animatedResultButton.getResultCollection();
					} else if (i == 1 && !this.alternatesWidget.isVisible() && !animatedResultButton.hasResults()) {
						this.alternatesWidget
							.showAlternatesForResult(
								this.client,
								animatedResultButton.getResultCollection(),
								animatedResultButton.x,
								animatedResultButton.y,
								j + l / 2,
								k + 13 + m / 2,
								(float)animatedResultButton.getWidth()
							);
					}

					return true;
				}
			}

			return false;
		}
	}

	public void onRecipesDisplayed(List<Recipe<?>> list) {
		for (RecipeDisplayListener recipeDisplayListener : this.recipeDisplayListeners) {
			recipeDisplayListener.onRecipesDisplayed(list);
		}
	}

	public MinecraftClient getMinecraftClient() {
		return this.client;
	}

	public RecipeBook getRecipeBook() {
		return this.recipeBook;
	}
}
