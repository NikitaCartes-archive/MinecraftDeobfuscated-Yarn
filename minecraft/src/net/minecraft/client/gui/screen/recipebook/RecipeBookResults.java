package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RecipeBookResults {
	public static final int field_32411 = 20;
	private static final ButtonTextures PAGE_FORWARD_TEXTURES = new ButtonTextures(
		new Identifier("recipe_book/page_forward"), new Identifier("recipe_book/page_forward_highlighted")
	);
	private static final ButtonTextures PAGE_BACKWARD_TEXTURES = new ButtonTextures(
		new Identifier("recipe_book/page_backward"), new Identifier("recipe_book/page_backward_highlighted")
	);
	private final List<AnimatedResultButton> resultButtons = Lists.<AnimatedResultButton>newArrayListWithCapacity(20);
	@Nullable
	private AnimatedResultButton hoveredResultButton;
	private final RecipeAlternativesWidget alternatesWidget = new RecipeAlternativesWidget();
	private MinecraftClient client;
	private final List<RecipeDisplayListener> recipeDisplayListeners = Lists.<RecipeDisplayListener>newArrayList();
	private List<RecipeResultCollection> resultCollections = ImmutableList.of();
	private ToggleButtonWidget nextPageButton;
	private ToggleButtonWidget prevPageButton;
	private int pageCount;
	private int currentPage;
	private RecipeBook recipeBook;
	@Nullable
	private RecipeEntry<?> lastClickedRecipe;
	@Nullable
	private RecipeResultCollection resultCollection;

	public RecipeBookResults() {
		for (int i = 0; i < 20; i++) {
			this.resultButtons.add(new AnimatedResultButton());
		}
	}

	public void initialize(MinecraftClient client, int parentLeft, int parentTop) {
		this.client = client;
		this.recipeBook = client.player.getRecipeBook();

		for (int i = 0; i < this.resultButtons.size(); i++) {
			((AnimatedResultButton)this.resultButtons.get(i)).setPosition(parentLeft + 11 + 25 * (i % 5), parentTop + 31 + 25 * (i / 5));
		}

		this.nextPageButton = new ToggleButtonWidget(parentLeft + 93, parentTop + 137, 12, 17, false);
		this.nextPageButton.setTextures(PAGE_FORWARD_TEXTURES);
		this.prevPageButton = new ToggleButtonWidget(parentLeft + 38, parentTop + 137, 12, 17, true);
		this.prevPageButton.setTextures(PAGE_BACKWARD_TEXTURES);
	}

	public void setGui(RecipeBookWidget widget) {
		this.recipeDisplayListeners.remove(widget);
		this.recipeDisplayListeners.add(widget);
	}

	public void setResults(List<RecipeResultCollection> resultCollections, boolean resetCurrentPage) {
		this.resultCollections = resultCollections;
		this.pageCount = (int)Math.ceil((double)resultCollections.size() / 20.0);
		if (this.pageCount <= this.currentPage || resetCurrentPage) {
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

	public void draw(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
		if (this.pageCount > 1) {
			Text text = Text.translatable("gui.recipebook.page", this.currentPage + 1, this.pageCount);
			int i = this.client.textRenderer.getWidth(text);
			context.drawText(this.client.textRenderer, text, x - i / 2 + 73, y + 141, Colors.WHITE, false);
		}

		this.hoveredResultButton = null;

		for (AnimatedResultButton animatedResultButton : this.resultButtons) {
			animatedResultButton.render(context, mouseX, mouseY, delta);
			if (animatedResultButton.visible && animatedResultButton.isSelected()) {
				this.hoveredResultButton = animatedResultButton;
			}
		}

		this.prevPageButton.render(context, mouseX, mouseY, delta);
		this.nextPageButton.render(context, mouseX, mouseY, delta);
		this.alternatesWidget.render(context, mouseX, mouseY, delta);
	}

	public void drawTooltip(DrawContext context, int x, int y) {
		if (this.client.currentScreen != null && this.hoveredResultButton != null && !this.alternatesWidget.isVisible()) {
			context.drawTooltip(this.client.textRenderer, this.hoveredResultButton.getTooltip(), x, y);
		}
	}

	@Nullable
	public RecipeEntry<?> getLastClickedRecipe() {
		return this.lastClickedRecipe;
	}

	@Nullable
	public RecipeResultCollection getLastClickedResults() {
		return this.resultCollection;
	}

	public void hideAlternates() {
		this.alternatesWidget.setVisible(false);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight) {
		this.lastClickedRecipe = null;
		this.resultCollection = null;
		if (this.alternatesWidget.isVisible()) {
			if (this.alternatesWidget.mouseClicked(mouseX, mouseY, button)) {
				this.lastClickedRecipe = this.alternatesWidget.getLastClickedRecipe();
				this.resultCollection = this.alternatesWidget.getResults();
			} else {
				this.alternatesWidget.setVisible(false);
			}

			return true;
		} else if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
			this.currentPage++;
			this.refreshResultButtons();
			return true;
		} else if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
			this.currentPage--;
			this.refreshResultButtons();
			return true;
		} else {
			for (AnimatedResultButton animatedResultButton : this.resultButtons) {
				if (animatedResultButton.mouseClicked(mouseX, mouseY, button)) {
					if (button == 0) {
						this.lastClickedRecipe = animatedResultButton.currentRecipe();
						this.resultCollection = animatedResultButton.getResultCollection();
					} else if (button == 1 && !this.alternatesWidget.isVisible() && !animatedResultButton.hasResults()) {
						this.alternatesWidget
							.showAlternativesForResult(
								this.client,
								animatedResultButton.getResultCollection(),
								animatedResultButton.getX(),
								animatedResultButton.getY(),
								areaLeft + areaWidth / 2,
								areaTop + 13 + areaHeight / 2,
								(float)animatedResultButton.getWidth()
							);
					}

					return true;
				}
			}

			return false;
		}
	}

	public void onRecipesDisplayed(List<RecipeEntry<?>> recipes) {
		for (RecipeDisplayListener recipeDisplayListener : this.recipeDisplayListeners) {
			recipeDisplayListener.onRecipesDisplayed(recipes);
		}
	}

	public MinecraftClient getClient() {
		return this.client;
	}

	public RecipeBook getRecipeBook() {
		return this.recipeBook;
	}

	protected void forEachButton(Consumer<ClickableWidget> consumer) {
		consumer.accept(this.nextPageButton);
		consumer.accept(this.prevPageButton);
		this.resultButtons.forEach(consumer);
	}
}
