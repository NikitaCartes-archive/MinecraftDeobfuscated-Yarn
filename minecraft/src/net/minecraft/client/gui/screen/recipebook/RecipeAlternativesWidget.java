package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RecipeAlternativesWidget implements Drawable, Element {
	private static final Identifier OVERLAY_RECIPE_TEXTURE = Identifier.ofVanilla("recipe_book/overlay_recipe");
	private static final int field_32406 = 4;
	private static final int field_32407 = 5;
	private static final float field_33739 = 0.375F;
	public static final int field_42162 = 25;
	private final List<RecipeAlternativesWidget.AlternativeButtonWidget> alternativeButtons = Lists.<RecipeAlternativesWidget.AlternativeButtonWidget>newArrayList();
	private boolean visible;
	private int buttonX;
	private int buttonY;
	private RecipeResultCollection resultCollection;
	@Nullable
	private RecipeEntry<?> lastClickedRecipe;
	final CurrentIndexProvider currentIndexProvider;
	private final boolean furnace;

	public RecipeAlternativesWidget(CurrentIndexProvider currentIndexProvider, boolean furnace) {
		this.currentIndexProvider = currentIndexProvider;
		this.furnace = furnace;
	}

	public void showAlternativesForResult(
		RecipeResultCollection resultCollection, boolean filteringCraftable, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta
	) {
		this.resultCollection = resultCollection;
		List<RecipeEntry<?>> list = resultCollection.filter(RecipeResultCollection.RecipeFilterMode.CRAFTABLE);
		List<RecipeEntry<?>> list2 = filteringCraftable ? Collections.emptyList() : resultCollection.filter(RecipeResultCollection.RecipeFilterMode.NOT_CRAFTABLE);
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
			boolean bl = p < i;
			RecipeEntry<?> recipeEntry = bl ? (RecipeEntry)list.get(p) : (RecipeEntry)list2.get(p - i);
			int q = this.buttonX + 4 + 25 * (p % k);
			int r = this.buttonY + 5 + 25 * (p / k);
			if (this.furnace) {
				this.alternativeButtons.add(new RecipeAlternativesWidget.FurnaceAlternativeButtonWidget(q, r, recipeEntry, bl));
			} else {
				this.alternativeButtons.add(new RecipeAlternativesWidget.CraftingAlternativeButtonWidget(q, r, recipeEntry, bl));
			}
		}

		this.lastClickedRecipe = null;
	}

	public RecipeResultCollection getResults() {
		return this.resultCollection;
	}

	@Nullable
	public RecipeEntry<?> getLastClickedRecipe() {
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
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			context.getMatrices().push();
			context.getMatrices().translate(0.0F, 0.0F, 1000.0F);
			int i = this.alternativeButtons.size() <= 16 ? 4 : 5;
			int j = Math.min(this.alternativeButtons.size(), i);
			int k = MathHelper.ceil((float)this.alternativeButtons.size() / (float)i);
			int l = 4;
			context.drawGuiTexture(RenderLayer::getGuiTextured, OVERLAY_RECIPE_TEXTURE, this.buttonX, this.buttonY, j * 25 + 8, k * 25 + 8);

			for (RecipeAlternativesWidget.AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
				alternativeButtonWidget.render(context, mouseX, mouseY, delta);
			}

			context.getMatrices().pop();
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
	abstract class AlternativeButtonWidget extends ClickableWidget {
		final RecipeEntry<?> recipe;
		private final boolean craftable;
		private final List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> inputSlots;

		public AlternativeButtonWidget(
			final int x,
			final int y,
			final RecipeEntry<?> recipe,
			final boolean craftable,
			final List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> inputSlots
		) {
			super(x, y, 24, 24, ScreenTexts.EMPTY);
			this.inputSlots = inputSlots;
			this.recipe = recipe;
			this.craftable = craftable;
		}

		protected static RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot slot(int x, int y, List<ItemStack> stacks) {
			return new RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot(3 + x * 7, 3 + y * 7, stacks);
		}

		protected abstract Identifier getOverlayTexture(boolean enabled);

		@Override
		public void appendClickableNarrations(NarrationMessageBuilder builder) {
			this.appendDefaultNarrations(builder);
		}

		@Override
		public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
			context.drawGuiTexture(RenderLayer::getGuiTextured, this.getOverlayTexture(this.craftable), this.getX(), this.getY(), this.width, this.height);
			float f = (float)(this.getX() + 2);
			float g = (float)(this.getY() + 2);
			float h = 150.0F;

			for (RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot inputSlot : this.inputSlots) {
				context.getMatrices().push();
				context.getMatrices().translate(f + (float)inputSlot.y, g + (float)inputSlot.x, 150.0F);
				context.getMatrices().scale(0.375F, 0.375F, 1.0F);
				context.getMatrices().translate(-8.0F, -8.0F, 0.0F);
				context.drawItem(inputSlot.get(RecipeAlternativesWidget.this.currentIndexProvider.currentIndex()), 0, 0);
				context.getMatrices().pop();
			}
		}

		@Environment(EnvType.CLIENT)
		protected static record InputSlot(int y, int x, List<ItemStack> stacks) {

			public InputSlot(int y, int x, List<ItemStack> stacks) {
				if (stacks.isEmpty()) {
					throw new IllegalArgumentException("Ingredient list must be non-empty");
				} else {
					this.y = y;
					this.x = x;
					this.stacks = stacks;
				}
			}

			public ItemStack get(int index) {
				return (ItemStack)this.stacks.get(index % this.stacks.size());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class CraftingAlternativeButtonWidget extends RecipeAlternativesWidget.AlternativeButtonWidget {
		private static final Identifier CRAFTING_OVERLAY = Identifier.ofVanilla("recipe_book/crafting_overlay");
		private static final Identifier CRAFTING_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_highlighted");
		private static final Identifier CRAFTING_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled");
		private static final Identifier CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled_highlighted");

		public CraftingAlternativeButtonWidget(final int x, final int y, final RecipeEntry<?> entry, final boolean craftable) {
			super(x, y, entry, craftable, collectInputSlots(entry));
		}

		private static List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> collectInputSlots(RecipeEntry<?> recipe) {
			List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> list = new ArrayList();
			RecipeGridAligner.alignRecipeToGrid(
				3,
				3,
				recipe,
				recipe.value().getIngredientPlacement().getPlacementSlots(),
				(slot, index, x, y) -> slot.ifPresent(slot2 -> list.add(slot(x, y, slot2.possibleItems())))
			);
			return list;
		}

		@Override
		protected Identifier getOverlayTexture(boolean enabled) {
			if (enabled) {
				return this.isSelected() ? CRAFTING_OVERLAY_HIGHLIGHTED : CRAFTING_OVERLAY;
			} else {
				return this.isSelected() ? CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED : CRAFTING_OVERLAY_DISABLED;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class FurnaceAlternativeButtonWidget extends RecipeAlternativesWidget.AlternativeButtonWidget {
		private static final Identifier FURNACE_OVERLAY = Identifier.ofVanilla("recipe_book/furnace_overlay");
		private static final Identifier FURNACE_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_highlighted");
		private static final Identifier FURNACE_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled");
		private static final Identifier FURNACE_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled_highlighted");

		public FurnaceAlternativeButtonWidget(final int x, final int y, final RecipeEntry<?> entry, final boolean craftable) {
			super(x, y, entry, craftable, alignRecipe(entry));
		}

		private static List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> alignRecipe(RecipeEntry<?> entry) {
			return (List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot>)((Optional)entry.value().getIngredientPlacement().getPlacementSlots().getFirst())
				.map(placementSlot -> List.of(slot(1, 1, placementSlot.possibleItems())))
				.orElse(List.of());
		}

		@Override
		protected Identifier getOverlayTexture(boolean enabled) {
			if (enabled) {
				return this.isSelected() ? FURNACE_OVERLAY_HIGHLIGHTED : FURNACE_OVERLAY;
			} else {
				return this.isSelected() ? FURNACE_OVERLAY_DISABLED_HIGHLIGHTED : FURNACE_OVERLAY_DISABLED;
			}
		}
	}
}
