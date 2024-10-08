package net.minecraft.client.gui.screen.recipebook;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.context.ContextParameterMap;

@Environment(EnvType.CLIENT)
public class GhostRecipe {
	private final Reference2ObjectMap<Slot, GhostRecipe.CyclingItem> items = new Reference2ObjectArrayMap<>();
	private final CurrentIndexProvider currentIndexProvider;

	public GhostRecipe(CurrentIndexProvider currentIndexProvider) {
		this.currentIndexProvider = currentIndexProvider;
	}

	public void clear() {
		this.items.clear();
	}

	private void addItems(Slot slot, ContextParameterMap context, SlotDisplay display, boolean resultSlot) {
		List<ItemStack> list = display.getStacks(context);
		if (!list.isEmpty()) {
			this.items.put(slot, new GhostRecipe.CyclingItem(list, resultSlot));
		}
	}

	protected void addInputs(Slot slot, ContextParameterMap context, SlotDisplay display) {
		this.addItems(slot, context, display, false);
	}

	protected void addResults(Slot slot, ContextParameterMap context, SlotDisplay display) {
		this.addItems(slot, context, display, true);
	}

	public void draw(DrawContext context, MinecraftClient client, boolean resultHasPadding) {
		this.items.forEach((slot, item) -> {
			int i = slot.x;
			int j = slot.y;
			if (item.isResultSlot && resultHasPadding) {
				context.fill(i - 4, j - 4, i + 20, j + 20, 822018048);
			} else {
				context.fill(i, j, i + 16, j + 16, 822018048);
			}

			ItemStack itemStack = item.get(this.currentIndexProvider.currentIndex());
			context.drawItemWithoutEntity(itemStack, i, j);
			context.fill(RenderLayer.getGuiGhostRecipeOverlay(), i, j, i + 16, j + 16, 822083583);
			if (item.isResultSlot) {
				context.drawStackOverlay(client.textRenderer, itemStack, i, j);
			}
		});
	}

	public void drawTooltip(DrawContext context, MinecraftClient client, int x, int y, @Nullable Slot slot) {
		if (slot != null) {
			GhostRecipe.CyclingItem cyclingItem = this.items.get(slot);
			if (cyclingItem != null) {
				ItemStack itemStack = cyclingItem.get(this.currentIndexProvider.currentIndex());
				context.drawTooltip(client.textRenderer, Screen.getTooltipFromItem(client, itemStack), x, y, itemStack.get(DataComponentTypes.TOOLTIP_STYLE));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static record CyclingItem(List<ItemStack> items, boolean isResultSlot) {

		public ItemStack get(int index) {
			int i = this.items.size();
			return i == 0 ? ItemStack.EMPTY : (ItemStack)this.items.get(index % i);
		}
	}
}
