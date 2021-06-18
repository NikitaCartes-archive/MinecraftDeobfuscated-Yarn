package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RecipeBookWidget extends DrawableHelper implements Drawable, Element, Selectable, RecipeDisplayListener, RecipeGridAligner<Ingredient> {
	protected static final Identifier TEXTURE = new Identifier("textures/gui/recipe_book.png");
	private static final Text SEARCH_HINT_TEXT = new TranslatableText("gui.recipebook.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
	public static final int field_32408 = 147;
	public static final int field_32409 = 166;
	private static final int field_32410 = 86;
	private static final Text TOGGLE_CRAFTABLE_RECIPES_TEXT = new TranslatableText("gui.recipebook.toggleRecipes.craftable");
	private static final Text TOGGLE_ALL_RECIPES_TEXT = new TranslatableText("gui.recipebook.toggleRecipes.all");
	private int leftOffset;
	private int parentWidth;
	private int parentHeight;
	protected final RecipeBookGhostSlots ghostSlots = new RecipeBookGhostSlots();
	private final List<RecipeGroupButtonWidget> tabButtons = Lists.<RecipeGroupButtonWidget>newArrayList();
	@Nullable
	private RecipeGroupButtonWidget currentTab;
	protected ToggleButtonWidget toggleCraftableButton;
	protected AbstractRecipeScreenHandler<?> craftingScreenHandler;
	protected MinecraftClient client;
	@Nullable
	private TextFieldWidget searchField;
	private String searchText = "";
	private ClientRecipeBook recipeBook;
	private final RecipeBookResults recipesArea = new RecipeBookResults();
	private final RecipeMatcher recipeFinder = new RecipeMatcher();
	private int cachedInvChangeCount;
	private boolean searching;
	private boolean open;
	private boolean field_34001;

	public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow, AbstractRecipeScreenHandler<?> craftingScreenHandler) {
		this.client = client;
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		this.craftingScreenHandler = craftingScreenHandler;
		this.field_34001 = narrow;
		client.player.currentScreenHandler = craftingScreenHandler;
		this.recipeBook = client.player.getRecipeBook();
		this.cachedInvChangeCount = client.player.getInventory().getChangeCount();
		this.open = this.isGuiOpen();
		if (this.open) {
			this.reset();
		}

		client.keyboard.setRepeatEvents(true);
	}

	public void reset() {
		this.leftOffset = this.field_34001 ? 0 : 86;
		int i = (this.parentWidth - 147) / 2 - this.leftOffset;
		int j = (this.parentHeight - 166) / 2;
		this.recipeFinder.clear();
		this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
		this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
		String string = this.searchField != null ? this.searchField.getText() : "";
		this.searchField = new TextFieldWidget(this.client.textRenderer, i + 25, j + 14, 80, 9 + 5, new TranslatableText("itemGroup.search"));
		this.searchField.setMaxLength(50);
		this.searchField.setDrawsBackground(false);
		this.searchField.setVisible(true);
		this.searchField.setEditableColor(16777215);
		this.searchField.setText(string);
		this.recipesArea.initialize(this.client, i, j);
		this.recipesArea.setGui(this);
		this.toggleCraftableButton = new ToggleButtonWidget(i + 110, j + 12, 26, 16, this.recipeBook.isFilteringCraftable(this.craftingScreenHandler));
		this.setBookButtonTexture();
		this.tabButtons.clear();

		for (RecipeBookGroup recipeBookGroup : RecipeBookGroup.getGroups(this.craftingScreenHandler.getCategory())) {
			this.tabButtons.add(new RecipeGroupButtonWidget(recipeBookGroup));
		}

		if (this.currentTab != null) {
			this.currentTab = (RecipeGroupButtonWidget)this.tabButtons
				.stream()
				.filter(button -> button.getCategory().equals(this.currentTab.getCategory()))
				.findFirst()
				.orElse(null);
		}

		if (this.currentTab == null) {
			this.currentTab = (RecipeGroupButtonWidget)this.tabButtons.get(0);
		}

		this.currentTab.setToggled(true);
		this.refreshResults(false);
		this.refreshTabButtons();
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		return false;
	}

	protected void setBookButtonTexture() {
		this.toggleCraftableButton.setTextureUV(152, 41, 28, 18, TEXTURE);
	}

	public void close() {
		this.client.keyboard.setRepeatEvents(false);
	}

	public int findLeftEdge(int i, int j) {
		int k;
		if (this.isOpen() && !this.field_34001) {
			k = 177 + (i - j - 200) / 2;
		} else {
			k = (i - j) / 2;
		}

		return k;
	}

	public void toggleOpen() {
		this.setOpen(!this.isOpen());
	}

	public boolean isOpen() {
		return this.open;
	}

	private boolean isGuiOpen() {
		return this.recipeBook.isGuiOpen(this.craftingScreenHandler.getCategory());
	}

	protected void setOpen(boolean opened) {
		if (opened) {
			this.reset();
		}

		this.open = opened;
		this.recipeBook.setGuiOpen(this.craftingScreenHandler.getCategory(), opened);
		if (!opened) {
			this.recipesArea.hideAlternates();
		}

		this.sendBookDataPacket();
	}

	public void slotClicked(@Nullable Slot slot) {
		if (slot != null && slot.id < this.craftingScreenHandler.getCraftingSlotCount()) {
			this.ghostSlots.reset();
			if (this.isOpen()) {
				this.refreshInputs();
			}
		}
	}

	private void refreshResults(boolean resetCurrentPage) {
		List<RecipeResultCollection> list = this.recipeBook.getResultsForGroup(this.currentTab.getCategory());
		list.forEach(
			resultCollection -> resultCollection.computeCraftables(
					this.recipeFinder, this.craftingScreenHandler.getCraftingWidth(), this.craftingScreenHandler.getCraftingHeight(), this.recipeBook
				)
		);
		List<RecipeResultCollection> list2 = Lists.<RecipeResultCollection>newArrayList(list);
		list2.removeIf(resultCollection -> !resultCollection.isInitialized());
		list2.removeIf(resultCollection -> !resultCollection.hasFittingRecipes());
		String string = this.searchField.getText();
		if (!string.isEmpty()) {
			ObjectSet<RecipeResultCollection> objectSet = new ObjectLinkedOpenHashSet<>(
				this.client.getSearchableContainer(SearchManager.RECIPE_OUTPUT).findAll(string.toLowerCase(Locale.ROOT))
			);
			list2.removeIf(recipeResultCollection -> !objectSet.contains(recipeResultCollection));
		}

		if (this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)) {
			list2.removeIf(resultCollection -> !resultCollection.hasCraftableRecipes());
		}

		this.recipesArea.setResults(list2, resetCurrentPage);
	}

	private void refreshTabButtons() {
		int i = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
		int j = (this.parentHeight - 166) / 2 + 3;
		int k = 27;
		int l = 0;

		for (RecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
			RecipeBookGroup recipeBookGroup = recipeGroupButtonWidget.getCategory();
			if (recipeBookGroup == RecipeBookGroup.CRAFTING_SEARCH || recipeBookGroup == RecipeBookGroup.FURNACE_SEARCH) {
				recipeGroupButtonWidget.visible = true;
				recipeGroupButtonWidget.setPos(i, j + 27 * l++);
			} else if (recipeGroupButtonWidget.hasKnownRecipes(this.recipeBook)) {
				recipeGroupButtonWidget.setPos(i, j + 27 * l++);
				recipeGroupButtonWidget.checkForNewRecipes(this.client);
			}
		}
	}

	public void update() {
		boolean bl = this.isGuiOpen();
		if (this.isOpen() != bl) {
			this.setOpen(bl);
		}

		if (this.isOpen()) {
			if (this.cachedInvChangeCount != this.client.player.getInventory().getChangeCount()) {
				this.refreshInputs();
				this.cachedInvChangeCount = this.client.player.getInventory().getChangeCount();
			}

			this.searchField.tick();
		}
	}

	private void refreshInputs() {
		this.recipeFinder.clear();
		this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
		this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
		this.refreshResults(false);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.isOpen()) {
			matrices.push();
			matrices.translate(0.0, 0.0, 100.0);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			int i = (this.parentWidth - 147) / 2 - this.leftOffset;
			int j = (this.parentHeight - 166) / 2;
			this.drawTexture(matrices, i, j, 1, 1, 147, 166);
			if (!this.searchField.isFocused() && this.searchField.getText().isEmpty()) {
				drawTextWithShadow(matrices, this.client.textRenderer, SEARCH_HINT_TEXT, i + 25, j + 14, -1);
			} else {
				this.searchField.render(matrices, mouseX, mouseY, delta);
			}

			for (RecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
				recipeGroupButtonWidget.render(matrices, mouseX, mouseY, delta);
			}

			this.toggleCraftableButton.render(matrices, mouseX, mouseY, delta);
			this.recipesArea.draw(matrices, i, j, mouseX, mouseY, delta);
			matrices.pop();
		}
	}

	public void drawTooltip(MatrixStack matrices, int i, int j, int k, int l) {
		if (this.isOpen()) {
			this.recipesArea.drawTooltip(matrices, k, l);
			if (this.toggleCraftableButton.isHovered()) {
				Text text = this.getCraftableButtonText();
				if (this.client.currentScreen != null) {
					this.client.currentScreen.renderTooltip(matrices, text, k, l);
				}
			}

			this.drawGhostSlotTooltip(matrices, i, j, k, l);
		}
	}

	private Text getCraftableButtonText() {
		return this.toggleCraftableButton.isToggled() ? this.getToggleCraftableButtonText() : TOGGLE_ALL_RECIPES_TEXT;
	}

	protected Text getToggleCraftableButtonText() {
		return TOGGLE_CRAFTABLE_RECIPES_TEXT;
	}

	private void drawGhostSlotTooltip(MatrixStack matrices, int i, int j, int k, int l) {
		ItemStack itemStack = null;

		for (int m = 0; m < this.ghostSlots.getSlotCount(); m++) {
			RecipeBookGhostSlots.GhostInputSlot ghostInputSlot = this.ghostSlots.getSlot(m);
			int n = ghostInputSlot.getX() + i;
			int o = ghostInputSlot.getY() + j;
			if (k >= n && l >= o && k < n + 16 && l < o + 16) {
				itemStack = ghostInputSlot.getCurrentItemStack();
			}
		}

		if (itemStack != null && this.client.currentScreen != null) {
			this.client.currentScreen.renderTooltip(matrices, this.client.currentScreen.getTooltipFromItem(itemStack), k, l);
		}
	}

	public void drawGhostSlots(MatrixStack matrices, int i, int j, boolean bl, float f) {
		this.ghostSlots.draw(matrices, this.client, i, j, bl, f);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.isOpen() && !this.client.player.isSpectator()) {
			if (this.recipesArea.mouseClicked(mouseX, mouseY, button, (this.parentWidth - 147) / 2 - this.leftOffset, (this.parentHeight - 166) / 2, 147, 166)) {
				Recipe<?> recipe = this.recipesArea.getLastClickedRecipe();
				RecipeResultCollection recipeResultCollection = this.recipesArea.getLastClickedResults();
				if (recipe != null && recipeResultCollection != null) {
					if (!recipeResultCollection.isCraftable(recipe) && this.ghostSlots.getRecipe() == recipe) {
						return false;
					}

					this.ghostSlots.reset();
					this.client.interactionManager.clickRecipe(this.client.player.currentScreenHandler.syncId, recipe, Screen.hasShiftDown());
					if (!this.isWide()) {
						this.setOpen(false);
					}
				}

				return true;
			} else if (this.searchField.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else if (this.toggleCraftableButton.mouseClicked(mouseX, mouseY, button)) {
				boolean bl = this.toggleFilteringCraftable();
				this.toggleCraftableButton.setToggled(bl);
				this.sendBookDataPacket();
				this.refreshResults(false);
				return true;
			} else {
				for (RecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
					if (recipeGroupButtonWidget.mouseClicked(mouseX, mouseY, button)) {
						if (this.currentTab != recipeGroupButtonWidget) {
							if (this.currentTab != null) {
								this.currentTab.setToggled(false);
							}

							this.currentTab = recipeGroupButtonWidget;
							this.currentTab.setToggled(true);
							this.refreshResults(true);
						}

						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	private boolean toggleFilteringCraftable() {
		RecipeBookCategory recipeBookCategory = this.craftingScreenHandler.getCategory();
		boolean bl = !this.recipeBook.isFilteringCraftable(recipeBookCategory);
		this.recipeBook.setFilteringCraftable(recipeBookCategory, bl);
		return bl;
	}

	public boolean isClickOutsideBounds(double d, double e, int i, int j, int k, int l, int m) {
		if (!this.isOpen()) {
			return true;
		} else {
			boolean bl = d < (double)i || e < (double)j || d >= (double)(i + k) || e >= (double)(j + l);
			boolean bl2 = (double)(i - 147) < d && d < (double)i && (double)j < e && e < (double)(j + l);
			return bl && !bl2 && !this.currentTab.isHovered();
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		this.searching = false;
		if (!this.isOpen() || this.client.player.isSpectator()) {
			return false;
		} else if (keyCode == GLFW.GLFW_KEY_ESCAPE && !this.isWide()) {
			this.setOpen(false);
			return true;
		} else if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
			this.refreshSearchResults();
			return true;
		} else if (this.searchField.isFocused() && this.searchField.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
			return true;
		} else if (this.client.options.keyChat.matchesKey(keyCode, scanCode) && !this.searchField.isFocused()) {
			this.searching = true;
			this.searchField.setTextFieldFocused(true);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		this.searching = false;
		return Element.super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (this.searching) {
			return false;
		} else if (!this.isOpen() || this.client.player.isSpectator()) {
			return false;
		} else if (this.searchField.charTyped(chr, modifiers)) {
			this.refreshSearchResults();
			return true;
		} else {
			return Element.super.charTyped(chr, modifiers);
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}

	private void refreshSearchResults() {
		String string = this.searchField.getText().toLowerCase(Locale.ROOT);
		this.triggerPirateSpeakEasterEgg(string);
		if (!string.equals(this.searchText)) {
			this.refreshResults(false);
			this.searchText = string;
		}
	}

	private void triggerPirateSpeakEasterEgg(String search) {
		if ("excitedze".equals(search)) {
			LanguageManager languageManager = this.client.getLanguageManager();
			LanguageDefinition languageDefinition = languageManager.getLanguage("en_pt");
			if (languageManager.getLanguage().compareTo(languageDefinition) == 0) {
				return;
			}

			languageManager.setLanguage(languageDefinition);
			this.client.options.language = languageDefinition.getCode();
			this.client.reloadResources();
			this.client.options.write();
		}
	}

	private boolean isWide() {
		return this.leftOffset == 86;
	}

	public void refresh() {
		this.refreshTabButtons();
		if (this.isOpen()) {
			this.refreshResults(false);
		}
	}

	@Override
	public void onRecipesDisplayed(List<Recipe<?>> recipes) {
		for (Recipe<?> recipe : recipes) {
			this.client.player.onRecipeDisplayed(recipe);
		}
	}

	public void showGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
		ItemStack itemStack = recipe.getOutput();
		this.ghostSlots.setRecipe(recipe);
		this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), ((Slot)slots.get(0)).x, ((Slot)slots.get(0)).y);
		this.alignRecipeToGrid(
			this.craftingScreenHandler.getCraftingWidth(),
			this.craftingScreenHandler.getCraftingHeight(),
			this.craftingScreenHandler.getCraftingResultSlotIndex(),
			recipe,
			recipe.getIngredients().iterator(),
			0
		);
	}

	@Override
	public void acceptAlignedInput(Iterator<Ingredient> inputs, int slot, int amount, int gridX, int gridY) {
		Ingredient ingredient = (Ingredient)inputs.next();
		if (!ingredient.isEmpty()) {
			Slot slot2 = this.craftingScreenHandler.slots.get(slot);
			this.ghostSlots.addSlot(ingredient, slot2.x, slot2.y);
		}
	}

	protected void sendBookDataPacket() {
		if (this.client.getNetworkHandler() != null) {
			RecipeBookCategory recipeBookCategory = this.craftingScreenHandler.getCategory();
			boolean bl = this.recipeBook.getOptions().isGuiOpen(recipeBookCategory);
			boolean bl2 = this.recipeBook.getOptions().isFilteringCraftable(recipeBookCategory);
			this.client.getNetworkHandler().sendPacket(new RecipeCategoryOptionsC2SPacket(recipeBookCategory, bl, bl2));
		}
	}

	@Override
	public Selectable.SelectionType getType() {
		return this.open ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		List<Selectable> list = Lists.<Selectable>newArrayList();
		this.recipesArea.method_37083(clickableWidget -> {
			if (clickableWidget.method_37303()) {
				list.add(clickableWidget);
			}
		});
		list.add(this.searchField);
		list.add(this.toggleCraftableButton);
		list.addAll(this.tabButtons);
		Screen.SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, null);
		if (selectedElementNarrationData != null) {
			selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
		}
	}
}
