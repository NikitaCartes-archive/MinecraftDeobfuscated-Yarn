package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public abstract class RecipeBookWidget<T extends AbstractRecipeScreenHandler> implements Drawable, Element, Selectable, RecipeDisplayListener {
	public static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(
		Identifier.ofVanilla("recipe_book/button"), Identifier.ofVanilla("recipe_book/button_highlighted")
	);
	protected static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/recipe_book.png");
	private static final int field_52839 = 256;
	private static final int field_52840 = 256;
	private static final Text SEARCH_HINT_TEXT = Text.translatable("gui.recipebook.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
	public static final int field_32408 = 147;
	public static final int field_32409 = 166;
	private static final int field_32410 = 86;
	private static final Text TOGGLE_ALL_RECIPES_TEXT = Text.translatable("gui.recipebook.toggleRecipes.all");
	private static final int field_52841 = 30;
	private int leftOffset;
	private int parentWidth;
	private int parentHeight;
	private float field_52842;
	@Nullable
	private RecipeEntry<?> ghostSlots;
	private final GhostRecipe ghostRecipe;
	private final List<RecipeGroupButtonWidget> tabButtons = Lists.<RecipeGroupButtonWidget>newArrayList();
	@Nullable
	private RecipeGroupButtonWidget currentTab;
	protected ToggleButtonWidget toggleCraftableButton;
	protected final T craftingScreenHandler;
	protected MinecraftClient client;
	@Nullable
	private TextFieldWidget searchField;
	private String searchText = "";
	private ClientRecipeBook recipeBook;
	private final RecipeBookResults recipesArea;
	private final RecipeFinder recipeFinder = new RecipeFinder();
	private int cachedInvChangeCount;
	private boolean searching;
	private boolean open;
	private boolean narrow;

	public RecipeBookWidget(T craftingScreenHandler) {
		this.craftingScreenHandler = craftingScreenHandler;
		CurrentIndexProvider currentIndexProvider = () -> MathHelper.floor(this.field_52842 / 30.0F);
		this.ghostRecipe = new GhostRecipe(currentIndexProvider);
		this.recipesArea = new RecipeBookResults(currentIndexProvider, craftingScreenHandler instanceof AbstractFurnaceScreenHandler);
	}

	public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow) {
		this.client = client;
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		this.narrow = narrow;
		this.recipeBook = client.player.getRecipeBook();
		this.cachedInvChangeCount = client.player.getInventory().getChangeCount();
		this.open = this.isGuiOpen();
		if (this.open) {
			this.reset();
		}
	}

	private void reset() {
		boolean bl = this.isFilteringCraftable();
		this.leftOffset = this.narrow ? 0 : 86;
		int i = (this.parentWidth - 147) / 2 - this.leftOffset;
		int j = (this.parentHeight - 166) / 2;
		this.recipeFinder.clear();
		this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
		this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
		String string = this.searchField != null ? this.searchField.getText() : "";
		this.searchField = new TextFieldWidget(this.client.textRenderer, i + 25, j + 13, 81, 9 + 5, Text.translatable("itemGroup.search"));
		this.searchField.setMaxLength(50);
		this.searchField.setVisible(true);
		this.searchField.setEditableColor(16777215);
		this.searchField.setText(string);
		this.searchField.setPlaceholder(SEARCH_HINT_TEXT);
		this.recipesArea.initialize(this.client, i, j);
		this.recipesArea.setGui(this);
		this.toggleCraftableButton = new ToggleButtonWidget(i + 110, j + 12, 26, 16, bl);
		this.updateTooltip();
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
		this.refreshResults(false, bl);
		this.refreshTabButtons(bl);
	}

	private void updateTooltip() {
		this.toggleCraftableButton
			.setTooltip(this.toggleCraftableButton.isToggled() ? Tooltip.of(this.getToggleCraftableButtonText()) : Tooltip.of(TOGGLE_ALL_RECIPES_TEXT));
	}

	protected abstract void setBookButtonTexture();

	public int findLeftEdge(int width, int backgroundWidth) {
		int i;
		if (this.isOpen() && !this.narrow) {
			i = 177 + (width - backgroundWidth - 200) / 2;
		} else {
			i = (width - backgroundWidth) / 2;
		}

		return i;
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

	protected abstract boolean isValid(Slot slot);

	public void onMouseClick(@Nullable Slot slot) {
		if (slot != null && this.isValid(slot)) {
			this.clearGhostRecipe();
			if (this.isOpen()) {
				this.refreshInputs();
			}
		}
	}

	protected abstract void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder, RecipeBook recipeBook);

	private void refreshResults(boolean resetCurrentPage, boolean filteringCraftable) {
		List<RecipeResultCollection> list = this.recipeBook.getResultsForGroup(this.currentTab.getCategory());
		list.forEach(recipeResultCollection -> this.populateRecipes(recipeResultCollection, this.recipeFinder, this.recipeBook));
		List<RecipeResultCollection> list2 = Lists.<RecipeResultCollection>newArrayList(list);
		list2.removeIf(resultCollection -> !resultCollection.isInitialized());
		list2.removeIf(resultCollection -> !resultCollection.hasFittingRecipes());
		String string = this.searchField.getText();
		if (!string.isEmpty()) {
			ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
			if (clientPlayNetworkHandler != null) {
				ObjectSet<RecipeResultCollection> objectSet = new ObjectLinkedOpenHashSet<>(
					clientPlayNetworkHandler.getSearchManager().getRecipeOutputReloadFuture().findAll(string.toLowerCase(Locale.ROOT))
				);
				list2.removeIf(resultCollection -> !objectSet.contains(resultCollection));
			}
		}

		if (filteringCraftable) {
			list2.removeIf(resultCollection -> !resultCollection.hasCraftableRecipes());
		}

		this.recipesArea.setResults(list2, resetCurrentPage, filteringCraftable);
	}

	private void refreshTabButtons(boolean filteringCraftable) {
		int i = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
		int j = (this.parentHeight - 166) / 2 + 3;
		int k = 27;
		int l = 0;

		for (RecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
			RecipeBookGroup recipeBookGroup = recipeGroupButtonWidget.getCategory();
			if (recipeBookGroup == RecipeBookGroup.CRAFTING_SEARCH || recipeBookGroup == RecipeBookGroup.FURNACE_SEARCH) {
				recipeGroupButtonWidget.visible = true;
				recipeGroupButtonWidget.setPosition(i, j + 27 * l++);
			} else if (recipeGroupButtonWidget.hasKnownRecipes(this.recipeBook)) {
				recipeGroupButtonWidget.setPosition(i, j + 27 * l++);
				recipeGroupButtonWidget.checkForNewRecipes(this.recipeBook, filteringCraftable);
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
		}
	}

	private void refreshInputs() {
		this.recipeFinder.clear();
		this.client.player.getInventory().populateRecipeFinder(this.recipeFinder);
		this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
		this.refreshResults(false, this.isFilteringCraftable());
	}

	private boolean isFilteringCraftable() {
		return this.recipeBook.isFilteringCraftable(this.craftingScreenHandler.getCategory());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.isOpen()) {
			if (!Screen.hasControlDown()) {
				this.field_52842 += delta;
			}

			context.getMatrices().push();
			context.getMatrices().translate(0.0F, 0.0F, 100.0F);
			int i = (this.parentWidth - 147) / 2 - this.leftOffset;
			int j = (this.parentHeight - 166) / 2;
			context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 1.0F, 1.0F, 147, 166, 256, 256);
			this.searchField.render(context, mouseX, mouseY, delta);

			for (RecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
				recipeGroupButtonWidget.render(context, mouseX, mouseY, delta);
			}

			this.toggleCraftableButton.render(context, mouseX, mouseY, delta);
			this.recipesArea.draw(context, i, j, mouseX, mouseY, delta);
			context.getMatrices().pop();
		}
	}

	public void drawTooltip(DrawContext context, int x, int y, @Nullable Slot slot) {
		if (this.isOpen()) {
			this.recipesArea.drawTooltip(context, x, y);
			this.ghostRecipe.drawTooltip(context, this.client, x, y, slot);
		}
	}

	protected abstract Text getToggleCraftableButtonText();

	public void drawGhostSlots(DrawContext context, int x, int y, boolean notInventory) {
		this.ghostRecipe.draw(context, this.client, x, y, notInventory);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.isOpen() && !this.client.player.isSpectator()) {
			if (this.recipesArea.mouseClicked(mouseX, mouseY, button, (this.parentWidth - 147) / 2 - this.leftOffset, (this.parentHeight - 166) / 2, 147, 166)) {
				RecipeEntry<?> recipeEntry = this.recipesArea.getLastClickedRecipe();
				RecipeResultCollection recipeResultCollection = this.recipesArea.getLastClickedResults();
				if (recipeEntry != null && recipeResultCollection != null) {
					if (!recipeResultCollection.isCraftable(recipeEntry) && this.ghostSlots == recipeEntry) {
						return false;
					}

					this.clearGhostRecipe();
					this.client.interactionManager.clickRecipe(this.client.player.currentScreenHandler.syncId, recipeEntry, Screen.hasShiftDown());
					if (!this.isWide()) {
						this.setOpen(false);
					}
				}

				return true;
			} else if (this.searchField.mouseClicked(mouseX, mouseY, button)) {
				this.searchField.setFocused(true);
				return true;
			} else {
				this.searchField.setFocused(false);
				if (this.toggleCraftableButton.mouseClicked(mouseX, mouseY, button)) {
					boolean bl = this.toggleFilteringCraftable();
					this.toggleCraftableButton.setToggled(bl);
					this.updateTooltip();
					this.sendBookDataPacket();
					this.refreshResults(false, bl);
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
								this.refreshResults(true, this.isFilteringCraftable());
							}

							return true;
						}
					}

					return false;
				}
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

	public boolean isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int backgroundWidth, int backgroundHeight, int button) {
		if (!this.isOpen()) {
			return true;
		} else {
			boolean bl = mouseX < (double)x || mouseY < (double)y || mouseX >= (double)(x + backgroundWidth) || mouseY >= (double)(y + backgroundHeight);
			boolean bl2 = (double)(x - 147) < mouseX && mouseX < (double)x && (double)y < mouseY && mouseY < (double)(y + backgroundHeight);
			return bl && !bl2 && !this.currentTab.isSelected();
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
		} else if (this.client.options.chatKey.matchesKey(keyCode, scanCode) && !this.searchField.isFocused()) {
			this.searching = true;
			this.searchField.setFocused(true);
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

	@Override
	public void setFocused(boolean focused) {
	}

	@Override
	public boolean isFocused() {
		return false;
	}

	private void refreshSearchResults() {
		String string = this.searchField.getText().toLowerCase(Locale.ROOT);
		this.triggerPirateSpeakEasterEgg(string);
		if (!string.equals(this.searchText)) {
			this.refreshResults(false, this.isFilteringCraftable());
			this.searchText = string;
		}
	}

	private void triggerPirateSpeakEasterEgg(String search) {
		if ("excitedze".equals(search)) {
			LanguageManager languageManager = this.client.getLanguageManager();
			String string = "en_pt";
			LanguageDefinition languageDefinition = languageManager.getLanguage("en_pt");
			if (languageDefinition == null || languageManager.getLanguage().equals("en_pt")) {
				return;
			}

			languageManager.setLanguage("en_pt");
			this.client.options.language = "en_pt";
			this.client.reloadResources();
			this.client.options.write();
		}
	}

	private boolean isWide() {
		return this.leftOffset == 86;
	}

	public void refresh() {
		this.refreshTabButtons(this.isFilteringCraftable());
		if (this.isOpen()) {
			this.refreshResults(false, this.isFilteringCraftable());
		}
	}

	@Override
	public void onRecipesDisplayed(List<RecipeEntry<?>> recipes) {
		for (RecipeEntry<?> recipeEntry : recipes) {
			this.client.player.onRecipeDisplayed(recipeEntry);
		}
	}

	private void clearGhostRecipe() {
		this.ghostSlots = null;
		this.ghostRecipe.clear();
	}

	public void setGhostRecipe(RecipeEntry<?> ghostSlots) {
		this.ghostSlots = ghostSlots;
		this.ghostRecipe.clear();
		this.showGhostRecipe(this.ghostRecipe, ghostSlots);
	}

	protected abstract void showGhostRecipe(GhostRecipe ghostRecipe, RecipeEntry<?> entry);

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
		this.recipesArea.forEachButton(button -> {
			if (button.isNarratable()) {
				list.add(button);
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
