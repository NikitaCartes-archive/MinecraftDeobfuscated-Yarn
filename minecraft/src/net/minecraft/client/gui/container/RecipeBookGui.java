package net.minecraft.client.gui.container;

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
import net.minecraft.class_1662;
import net.minecraft.class_2853;
import net.minecraft.class_2952;
import net.minecraft.class_308;
import net.minecraft.class_361;
import net.minecraft.class_505;
import net.minecraft.class_512;
import net.minecraft.class_513;
import net.minecraft.class_515;
import net.minecraft.class_516;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.recipe.book.RecipeBookGroup;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.SearchManager;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RecipeBookGui extends Drawable implements GuiEventListener, class_515, class_2952<Ingredient> {
	public static final Identifier TEXTURE = new Identifier("textures/gui/recipe_book.png");
	private int field_3102;
	private int field_3101;
	private int field_3100;
	protected final class_505 field_3092 = new class_505();
	private final List<class_512> field_3094 = Lists.<class_512>newArrayList();
	private class_512 field_3098;
	protected class_361 field_3088;
	protected CraftingContainer field_3095;
	protected MinecraftClient client;
	private TextFieldWidget searchField;
	private String field_3099 = "";
	protected ClientRecipeBook recipeBook;
	protected final class_513 field_3086 = new class_513();
	protected final class_1662 field_3090 = new class_1662();
	private int cachedInvChangeCount;
	private boolean field_3087;

	public void method_2597(int i, int j, MinecraftClient minecraftClient, boolean bl, CraftingContainer craftingContainer) {
		this.client = minecraftClient;
		this.field_3101 = i;
		this.field_3100 = j;
		this.field_3095 = craftingContainer;
		minecraftClient.player.container = craftingContainer;
		this.recipeBook = minecraftClient.player.getRecipeBook();
		this.cachedInvChangeCount = minecraftClient.player.inventory.getChangeCount();
		if (this.isOpen()) {
			this.method_2579(bl);
		}

		minecraftClient.keyboard.enableRepeatEvents(true);
	}

	public void method_2579(boolean bl) {
		this.field_3102 = bl ? 0 : 86;
		int i = (this.field_3101 - 147) / 2 - this.field_3102;
		int j = (this.field_3100 - 166) / 2;
		this.field_3090.method_7409();
		this.client.player.inventory.method_7387(this.field_3090);
		this.field_3095.method_7654(this.field_3090);
		String string = this.searchField != null ? this.searchField.getText() : "";
		this.searchField = new TextFieldWidget(0, this.client.fontRenderer, i + 25, j + 14, 80, this.client.fontRenderer.FONT_HEIGHT + 5);
		this.searchField.setMaxLength(50);
		this.searchField.method_1858(false);
		this.searchField.setVisible(true);
		this.searchField.method_1868(16777215);
		this.searchField.setText(string);
		this.field_3086.method_2636(this.client, i, j);
		this.field_3086.method_2630(this);
		this.field_3088 = new class_361(0, i + 110, j + 12, 26, 16, this.recipeBook.isFilteringCraftable(this.field_3095));
		this.method_2585();
		this.field_3094.clear();

		for (RecipeBookGroup recipeBookGroup : ClientRecipeBook.method_1395(this.field_3095)) {
			this.field_3094.add(new class_512(0, recipeBookGroup));
		}

		if (this.field_3098 != null) {
			this.field_3098 = (class_512)this.field_3094.stream().filter(arg -> arg.getCategory().equals(this.field_3098.getCategory())).findFirst().orElse(null);
		}

		if (this.field_3098 == null) {
			this.field_3098 = (class_512)this.field_3094.get(0);
		}

		this.field_3098.method_1964(true);
		this.method_2603(false);
		this.method_2606();
	}

	protected void method_2585() {
		this.field_3088.method_1962(152, 41, 28, 18, TEXTURE);
	}

	public void close() {
		this.searchField = null;
		this.field_3098 = null;
		this.client.keyboard.enableRepeatEvents(false);
	}

	public int method_2595(boolean bl, int i, int j) {
		int k;
		if (this.isOpen() && !bl) {
			k = 177 + (i - j - 200) / 2;
		} else {
			k = (i - j) / 2;
		}

		return k;
	}

	public void method_2591() {
		this.method_2593(!this.isOpen());
	}

	public boolean isOpen() {
		return this.recipeBook.isGuiOpen();
	}

	protected void method_2593(boolean bl) {
		this.recipeBook.setGuiOpen(bl);
		if (!bl) {
			this.field_3086.method_2638();
		}

		this.method_2588();
	}

	public void method_2600(@Nullable Slot slot) {
		if (slot != null && slot.id < this.field_3095.method_7658()) {
			this.field_3092.method_2571();
			if (this.isOpen()) {
				this.method_2587();
			}
		}
	}

	private void method_2603(boolean bl) {
		List<class_516> list = this.recipeBook.method_1396(this.field_3098.getCategory());
		list.forEach(arg -> arg.method_2649(this.field_3090, this.field_3095.getCraftingWidth(), this.field_3095.getCrafitngHeight(), this.recipeBook));
		List<class_516> list2 = Lists.<class_516>newArrayList(list);
		list2.removeIf(arg -> !arg.method_2652());
		list2.removeIf(arg -> !arg.method_2657());
		String string = this.searchField.getText();
		if (!string.isEmpty()) {
			ObjectSet<class_516> objectSet = new ObjectLinkedOpenHashSet<>(
				this.client.getSearchableContainer(SearchManager.field_5496).findAll(string.toLowerCase(Locale.ROOT))
			);
			list2.removeIf(arg -> !objectSet.contains(arg));
		}

		if (this.recipeBook.isFilteringCraftable(this.field_3095)) {
			list2.removeIf(arg -> !arg.method_2655());
		}

		this.field_3086.method_2627(list2, bl);
	}

	private void method_2606() {
		int i = (this.field_3101 - 147) / 2 - this.field_3102 - 30;
		int j = (this.field_3100 - 166) / 2 + 3;
		int k = 27;
		int l = 0;

		for (class_512 lv : this.field_3094) {
			RecipeBookGroup recipeBookGroup = lv.getCategory();
			if (recipeBookGroup == RecipeBookGroup.field_1809 || recipeBookGroup == RecipeBookGroup.field_1804) {
				lv.visible = true;
				lv.setPos(i, j + 27 * l++);
			} else if (lv.method_2624(this.recipeBook)) {
				lv.setPos(i, j + 27 * l++);
				lv.method_2622(this.client);
			}
		}
	}

	public void update() {
		if (this.isOpen()) {
			if (this.cachedInvChangeCount != this.client.player.inventory.getChangeCount()) {
				this.method_2587();
				this.cachedInvChangeCount = this.client.player.inventory.getChangeCount();
			}
		}
	}

	private void method_2587() {
		this.field_3090.method_7409();
		this.client.player.inventory.method_7387(this.field_3090);
		this.field_3095.method_7654(this.field_3090);
		this.method_2603(false);
	}

	public void method_2578(int i, int j, float f) {
		if (this.isOpen()) {
			class_308.method_1453();
			GlStateManager.disableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 100.0F);
			this.client.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			int k = (this.field_3101 - 147) / 2 - this.field_3102;
			int l = (this.field_3100 - 166) / 2;
			this.drawTexturedRect(k, l, 1, 1, 147, 166);
			this.searchField.render(i, j, f);
			class_308.method_1450();

			for (class_512 lv : this.field_3094) {
				lv.draw(i, j, f);
			}

			this.field_3088.draw(i, j, f);
			this.field_3086.method_2634(k, l, i, j, f);
			GlStateManager.popMatrix();
		}
	}

	public void method_2601(int i, int j, int k, int l) {
		if (this.isOpen()) {
			this.field_3086.method_2628(k, l);
			if (this.field_3088.isHovered()) {
				String string = this.method_2599();
				if (this.client.currentGui != null) {
					this.client.currentGui.drawTooltip(string, k, l);
				}
			}

			this.method_2602(i, j, k, l);
		}
	}

	protected String method_2599() {
		return I18n.translate(this.field_3088.method_1965() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
	}

	private void method_2602(int i, int j, int k, int l) {
		ItemStack itemStack = null;

		for (int m = 0; m < this.field_3092.method_2572(); m++) {
			class_505.class_506 lv = this.field_3092.method_2570(m);
			int n = lv.method_2574() + i;
			int o = lv.method_2575() + j;
			if (k >= n && l >= o && k < n + 16 && l < o + 16) {
				itemStack = lv.method_2573();
			}
		}

		if (itemStack != null && this.client.currentGui != null) {
			this.client.currentGui.drawTooltip(this.client.currentGui.getStackTooltip(itemStack), k, l);
		}
	}

	public void method_2581(int i, int j, boolean bl, float f) {
		this.field_3092.method_2567(this.client, i, j, bl, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.isOpen() && !this.client.player.isSpectator()) {
			if (this.field_3086.method_2632(d, e, i, (this.field_3101 - 147) / 2 - this.field_3102, (this.field_3100 - 166) / 2, 147, 166)) {
				Recipe recipe = this.field_3086.method_2631();
				class_516 lv = this.field_3086.method_2635();
				if (recipe != null && lv != null) {
					if (!lv.method_2653(recipe) && this.field_3092.method_2566() == recipe) {
						return false;
					}

					this.field_3092.method_2571();
					this.client.interactionManager.clickRecipe(this.client.player.container.syncId, recipe, Gui.isShiftPressed());
					if (!this.method_2604()) {
						this.method_2593(false);
					}
				}

				return true;
			} else if (this.searchField.mouseClicked(d, e, i)) {
				return true;
			} else if (this.field_3088.mouseClicked(d, e, i)) {
				boolean bl = this.method_2589();
				this.field_3088.method_1964(bl);
				this.method_2588();
				this.method_2603(false);
				return true;
			} else {
				for (class_512 lv2 : this.field_3094) {
					if (lv2.mouseClicked(d, e, i)) {
						if (this.field_3098 != lv2) {
							this.field_3098.method_1964(false);
							this.field_3098 = lv2;
							this.field_3098.method_1964(true);
							this.method_2603(true);
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

	protected boolean method_2589() {
		boolean bl = !this.recipeBook.isFilteringCraftable();
		this.recipeBook.setFilteringCraftable(bl);
		return bl;
	}

	public boolean method_2598(double d, double e, int i, int j, int k, int l, int m) {
		if (!this.isOpen()) {
			return true;
		} else {
			boolean bl = d < (double)i || e < (double)j || d >= (double)(i + k) || e >= (double)(j + l);
			boolean bl2 = (double)(i - 147) < d && d < (double)i && (double)j < e && e < (double)(j + l);
			return bl && !bl2 && !this.field_3098.isHovered();
		}
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		this.field_3087 = false;
		if (!this.isOpen() || this.client.player.isSpectator()) {
			return false;
		} else if (i == 256 && !this.method_2604()) {
			this.method_2593(false);
			return true;
		} else if (this.searchField.keyPressed(i, j, k)) {
			this.method_2586();
			return true;
		} else if (this.client.options.keyChat.matches(i, j) && !this.searchField.isFocused()) {
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
		return GuiEventListener.super.keyReleased(i, j, k);
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (this.field_3087) {
			return false;
		} else if (!this.isOpen() || this.client.player.isSpectator()) {
			return false;
		} else if (this.searchField.charTyped(c, i)) {
			this.method_2586();
			return true;
		} else {
			return GuiEventListener.super.charTyped(c, i);
		}
	}

	private void method_2586() {
		String string = this.searchField.getText().toLowerCase(Locale.ROOT);
		this.triggerPirateSpeakEasterEgg(string);
		if (!string.equals(this.field_3099)) {
			this.method_2603(false);
			this.field_3099 = string;
		}
	}

	private void triggerPirateSpeakEasterEgg(String string) {
		if ("excitedze".equals(string)) {
			LanguageManager languageManager = this.client.getLanguageManager();
			LanguageDefinition languageDefinition = languageManager.method_4668("en_pt");
			if (languageManager.getLanguage().compareTo(languageDefinition) == 0) {
				return;
			}

			languageManager.setLanguage(languageDefinition);
			this.client.options.language = languageDefinition.getCode();
			this.client.reloadResources();
			this.client.fontRenderer.setRightToLeft(languageManager.isRightToLeft());
			this.client.options.write();
		}
	}

	private boolean method_2604() {
		return this.field_3102 == 86;
	}

	public void method_2592() {
		this.method_2606();
		if (this.isOpen()) {
			this.method_2603(false);
		}
	}

	@Override
	public void method_2646(List<Recipe> list) {
		for (Recipe recipe : list) {
			this.client.player.method_3141(recipe);
		}
	}

	public void method_2596(Recipe recipe, List<Slot> list) {
		ItemStack itemStack = recipe.getOutput();
		this.field_3092.method_2565(recipe);
		this.field_3092.method_2569(Ingredient.ofStacks(itemStack), ((Slot)list.get(0)).xPosition, ((Slot)list.get(0)).yPosition);
		this.method_12816(
			this.field_3095.getCraftingWidth(),
			this.field_3095.getCrafitngHeight(),
			this.field_3095.getCraftingResultSlotIndex(),
			recipe,
			recipe.getPreviewInputs().iterator(),
			0
		);
	}

	@Override
	public void method_12815(Iterator<Ingredient> iterator, int i, int j, int k, int l) {
		Ingredient ingredient = (Ingredient)iterator.next();
		if (!ingredient.method_8103()) {
			Slot slot = (Slot)this.field_3095.slotList.get(i);
			this.field_3092.method_2569(ingredient, slot.xPosition, slot.yPosition);
		}
	}

	protected void method_2588() {
		if (this.client.getNetworkHandler() != null) {
			this.client
				.getNetworkHandler()
				.sendPacket(
					new class_2853(
						this.recipeBook.isGuiOpen(), this.recipeBook.isFilteringCraftable(), this.recipeBook.isFurnaceGuiOpen(), this.recipeBook.isFurnaceFilteringCraftable()
					)
				);
		}
	}
}
