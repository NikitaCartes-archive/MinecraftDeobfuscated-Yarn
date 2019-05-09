package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.RecipeBookGhostSlots;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.recipe.book.RecipeBookGroup;
import net.minecraft.client.recipe.book.RecipeDisplayListener;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.SearchManager;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RecipeBookGui extends DrawableHelper implements Drawable, Element, RecipeDisplayListener, RecipeGridAligner<Ingredient> {
	protected static final Identifier TEXTURE = new Identifier("textures/gui/recipe_book.png");
	private int leftOffset;
	private int parentWidth;
	private int parentHeight;
	protected final RecipeBookGhostSlots ghostSlots = new RecipeBookGhostSlots();
	private final List<GroupButtonWidget> tabButtons = Lists.<GroupButtonWidget>newArrayList();
	private GroupButtonWidget currentTab;
	protected ToggleButtonWidget toggleCraftableButton;
	protected CraftingContainer<?> craftingContainer;
	protected MinecraftClient client;
	private TextFieldWidget searchField;
	private String searchText = "";
	protected ClientRecipeBook recipeBook;
	protected final RecipeBookGuiResults recipesArea = new RecipeBookGuiResults();
	protected final RecipeFinder recipeFinder = new RecipeFinder();
	private int cachedInvChangeCount;
	private boolean field_3087;

	public void initialize(int i, int j, MinecraftClient minecraftClient, boolean bl, CraftingContainer<?> craftingContainer) {
		this.client = minecraftClient;
		this.parentWidth = i;
		this.parentHeight = j;
		this.craftingContainer = craftingContainer;
		minecraftClient.player.container = craftingContainer;
		this.recipeBook = minecraftClient.player.getRecipeBook();
		this.cachedInvChangeCount = minecraftClient.player.inventory.getChangeCount();
		if (this.isOpen()) {
			this.reset(bl);
		}

		minecraftClient.keyboard.enableRepeatEvents(true);
	}

	public void reset(boolean bl) {
		this.leftOffset = bl ? 0 : 86;
		int i = (this.parentWidth - 147) / 2 - this.leftOffset;
		int j = (this.parentHeight - 166) / 2;
		this.recipeFinder.clear();
		this.client.player.inventory.populateRecipeFinder(this.recipeFinder);
		this.craftingContainer.populateRecipeFinder(this.recipeFinder);
		String string = this.searchField != null ? this.searchField.getText() : "";
		this.searchField = new TextFieldWidget(this.client.textRenderer, i + 25, j + 14, 80, 9 + 5, I18n.translate("itemGroup.search"));
		this.searchField.setMaxLength(50);
		this.searchField.setHasBorder(false);
		this.searchField.setVisible(true);
		this.searchField.setEditableColor(16777215);
		this.searchField.setText(string);
		this.recipesArea.initialize(this.client, i, j);
		this.recipesArea.setGui(this);
		this.toggleCraftableButton = new ToggleButtonWidget(i + 110, j + 12, 26, 16, this.recipeBook.isFilteringCraftable(this.craftingContainer));
		this.setBookButtonTexture();
		this.tabButtons.clear();

		for (RecipeBookGroup recipeBookGroup : ClientRecipeBook.getGroupsForContainer(this.craftingContainer)) {
			this.tabButtons.add(new GroupButtonWidget(recipeBookGroup));
		}

		if (this.currentTab != null) {
			this.currentTab = (GroupButtonWidget)this.tabButtons
				.stream()
				.filter(groupButtonWidget -> groupButtonWidget.getCategory().equals(this.currentTab.getCategory()))
				.findFirst()
				.orElse(null);
		}

		if (this.currentTab == null) {
			this.currentTab = (GroupButtonWidget)this.tabButtons.get(0);
		}

		this.currentTab.setToggled(true);
		this.refreshResults(false);
		this.refreshTabButtons();
	}

	@Override
	public boolean changeFocus(boolean bl) {
		return false;
	}

	protected void setBookButtonTexture() {
		this.toggleCraftableButton.setTextureUV(152, 41, 28, 18, TEXTURE);
	}

	public void close() {
		this.searchField = null;
		this.currentTab = null;
		this.client.keyboard.enableRepeatEvents(false);
	}

	public int findLeftEdge(boolean bl, int i, int j) {
		int k;
		if (this.isOpen() && !bl) {
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
		return this.recipeBook.isGuiOpen();
	}

	protected void setOpen(boolean bl) {
		this.recipeBook.setGuiOpen(bl);
		if (!bl) {
			this.recipesArea.hideAlternates();
		}

		this.sendBookDataPacket();
	}

	public void slotClicked(@Nullable Slot slot) {
		if (slot != null && slot.id < this.craftingContainer.getCraftingSlotCount()) {
			this.ghostSlots.reset();
			if (this.isOpen()) {
				this.refreshInputs();
			}
		}
	}

	private void refreshResults(boolean bl) {
		List<RecipeResultCollection> list = this.recipeBook.getResultsForGroup(this.currentTab.getCategory());
		list.forEach(
			recipeResultCollection -> recipeResultCollection.computeCraftables(
					this.recipeFinder, this.craftingContainer.getCraftingWidth(), this.craftingContainer.getCraftingHeight(), this.recipeBook
				)
		);
		List<RecipeResultCollection> list2 = Lists.<RecipeResultCollection>newArrayList(list);
		list2.removeIf(recipeResultCollection -> !recipeResultCollection.isInitialized());
		list2.removeIf(recipeResultCollection -> !recipeResultCollection.hasFittableResults());
		String string = this.searchField.getText();
		if (!string.isEmpty()) {
			ObjectSet<RecipeResultCollection> objectSet = new ObjectLinkedOpenHashSet<>(
				this.client.getSearchableContainer(SearchManager.RECIPE_OUTPUT).findAll(string.toLowerCase(Locale.ROOT))
			);
			list2.removeIf(recipeResultCollection -> !objectSet.contains(recipeResultCollection));
		}

		if (this.recipeBook.isFilteringCraftable(this.craftingContainer)) {
			list2.removeIf(recipeResultCollection -> !recipeResultCollection.hasCraftableResults());
		}

		this.recipesArea.setResults(list2, bl);
	}

	private void refreshTabButtons() {
		int i = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
		int j = (this.parentHeight - 166) / 2 + 3;
		int k = 27;
		int l = 0;

		for (GroupButtonWidget groupButtonWidget : this.tabButtons) {
			RecipeBookGroup recipeBookGroup = groupButtonWidget.getCategory();
			if (recipeBookGroup == RecipeBookGroup.field_1809 || recipeBookGroup == RecipeBookGroup.field_1804) {
				groupButtonWidget.visible = true;
				groupButtonWidget.setPos(i, j + 27 * l++);
			} else if (groupButtonWidget.hasKnownRecipes(this.recipeBook)) {
				groupButtonWidget.setPos(i, j + 27 * l++);
				groupButtonWidget.checkForNewRecipes(this.client);
			}
		}
	}

	public void update() {
		if (this.isOpen()) {
			if (this.cachedInvChangeCount != this.client.player.inventory.getChangeCount()) {
				this.refreshInputs();
				this.cachedInvChangeCount = this.client.player.inventory.getChangeCount();
			}
		}
	}

	private void refreshInputs() {
		this.recipeFinder.clear();
		this.client.player.inventory.populateRecipeFinder(this.recipeFinder);
		this.craftingContainer.populateRecipeFinder(this.recipeFinder);
		this.refreshResults(false);
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.isOpen()) {
			GuiLighting.enableForItems();
			GlStateManager.disableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 100.0F);
			this.client.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int k = (this.parentWidth - 147) / 2 - this.leftOffset;
			int l = (this.parentHeight - 166) / 2;
			this.blit(k, l, 1, 1, 147, 166);
			this.searchField.render(i, j, f);
			GuiLighting.disable();

			for (GroupButtonWidget groupButtonWidget : this.tabButtons) {
				groupButtonWidget.render(i, j, f);
			}

			this.toggleCraftableButton.render(i, j, f);
			this.recipesArea.draw(k, l, i, j, f);
			GlStateManager.popMatrix();
		}
	}

	public void drawTooltip(int i, int j, int k, int l) {
		if (this.isOpen()) {
			this.recipesArea.drawTooltip(k, l);
			if (this.toggleCraftableButton.isHovered()) {
				String string = this.getCraftableButtonText();
				if (this.client.currentScreen != null) {
					this.client.currentScreen.renderTooltip(string, k, l);
				}
			}

			this.drawGhostSlotTooltip(i, j, k, l);
		}
	}

	protected String getCraftableButtonText() {
		return I18n.translate(this.toggleCraftableButton.isToggled() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
	}

	private void drawGhostSlotTooltip(int i, int j, int k, int l) {
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
			this.client.currentScreen.renderTooltip(this.client.currentScreen.getTooltipFromItem(itemStack), k, l);
		}
	}

	public void drawGhostSlots(int i, int j, boolean bl, float f) {
		this.ghostSlots.draw(this.client, i, j, bl, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.isOpen() && !this.client.player.isSpectator()) {
			if (this.recipesArea.mouseClicked(d, e, i, (this.parentWidth - 147) / 2 - this.leftOffset, (this.parentHeight - 166) / 2, 147, 166)) {
				Recipe<?> recipe = this.recipesArea.getLastClickedRecipe();
				RecipeResultCollection recipeResultCollection = this.recipesArea.getLastClickedResults();
				if (recipe != null && recipeResultCollection != null) {
					if (!recipeResultCollection.isCraftable(recipe) && this.ghostSlots.getRecipe() == recipe) {
						return false;
					}

					this.ghostSlots.reset();
					this.client.interactionManager.clickRecipe(this.client.player.container.syncId, recipe, Screen.hasShiftDown());
					if (!this.isWide()) {
						this.setOpen(false);
					}
				}

				return true;
			} else if (this.searchField.mouseClicked(d, e, i)) {
				return true;
			} else if (this.toggleCraftableButton.mouseClicked(d, e, i)) {
				boolean bl = this.toggleFilteringCraftable();
				this.toggleCraftableButton.setToggled(bl);
				this.sendBookDataPacket();
				this.refreshResults(false);
				return true;
			} else {
				for (GroupButtonWidget groupButtonWidget : this.tabButtons) {
					if (groupButtonWidget.mouseClicked(d, e, i)) {
						if (this.currentTab != groupButtonWidget) {
							this.currentTab.setToggled(false);
							this.currentTab = groupButtonWidget;
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

	protected boolean toggleFilteringCraftable() {
		boolean bl = !this.recipeBook.isFilteringCraftable();
		this.recipeBook.setFilteringCraftable(bl);
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
	public boolean keyPressed(int i, int j, int k) {
		this.field_3087 = false;
		if (!this.isOpen() || this.client.player.isSpectator()) {
			return false;
		} else if (i == 256 && !this.isWide()) {
			this.setOpen(false);
			return true;
		} else if (this.searchField.keyPressed(i, j, k)) {
			this.refreshSearchResults();
			return true;
		} else if (this.searchField.isFocused() && this.searchField.isVisible() && i != 256) {
			return true;
		} else if (this.client.options.keyChat.matchesKey(i, j) && !this.searchField.isFocused()) {
			this.field_3087 = true;
			this.searchField.method_1876(true);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyReleased(int i, int j, int k) {
		this.field_3087 = false;
		return Element.super.keyReleased(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.field_3087) {
			return false;
		} else if (!this.isOpen() || this.client.player.isSpectator()) {
			return false;
		} else if (this.searchField.charTyped(c, i)) {
			this.refreshSearchResults();
			return true;
		} else {
			return Element.super.charTyped(c, i);
		}
	}

	@Override
	public boolean isMouseOver(double d, double e) {
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

	private void triggerPirateSpeakEasterEgg(String string) {
		if ("excitedze".equals(string)) {
			LanguageManager languageManager = this.client.getLanguageManager();
			LanguageDefinition languageDefinition = languageManager.getLanguage("en_pt");
			if (languageManager.getLanguage().method_4673(languageDefinition) == 0) {
				return;
			}

			languageManager.setLanguage(languageDefinition);
			this.client.options.language = languageDefinition.getCode();
			this.client.reloadResources();
			this.client.textRenderer.setRightToLeft(languageManager.isRightToLeft());
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
	public void onRecipesDisplayed(List<Recipe<?>> list) {
		for (Recipe<?> recipe : list) {
			this.client.player.onRecipeDisplayed(recipe);
		}
	}

	public void showGhostRecipe(Recipe<?> recipe, List<Slot> list) {
		ItemStack itemStack = recipe.getOutput();
		this.ghostSlots.setRecipe(recipe);
		this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack), ((Slot)list.get(0)).xPosition, ((Slot)list.get(0)).yPosition);
		this.alignRecipeToGrid(
			this.craftingContainer.getCraftingWidth(),
			this.craftingContainer.getCraftingHeight(),
			this.craftingContainer.getCraftingResultSlotIndex(),
			recipe,
			recipe.getPreviewInputs().iterator(),
			0
		);
	}

	@Override
	public void acceptAlignedInput(Iterator<Ingredient> iterator, int i, int j, int k, int l) {
		Ingredient ingredient = (Ingredient)iterator.next();
		if (!ingredient.isEmpty()) {
			Slot slot = (Slot)this.craftingContainer.slotList.get(i);
			this.ghostSlots.addSlot(ingredient, slot.xPosition, slot.yPosition);
		}
	}

	protected void sendBookDataPacket() {
		if (this.client.getNetworkHandler() != null) {
			this.client
				.getNetworkHandler()
				.sendPacket(
					new RecipeBookDataC2SPacket(
						this.recipeBook.isGuiOpen(),
						this.recipeBook.isFilteringCraftable(),
						this.recipeBook.isFurnaceGuiOpen(),
						this.recipeBook.isFurnaceFilteringCraftable(),
						this.recipeBook.isBlastFurnaceGuiOpen(),
						this.recipeBook.isBlastFurnaceFilteringCraftable()
					)
				);
		}
	}
}
