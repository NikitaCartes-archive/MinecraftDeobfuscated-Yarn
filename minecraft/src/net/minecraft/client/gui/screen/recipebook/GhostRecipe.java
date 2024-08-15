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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

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

	public void put(ItemStack item, Slot slot) {
		this.items.put(slot, new GhostRecipe.CyclingItem(List.of(item), true));
	}

	public void put(List<ItemStack> items, Slot slot) {
		this.items.put(slot, new GhostRecipe.CyclingItem(items, false));
	}

	public void draw(DrawContext drawContext, MinecraftClient client, int x, int y, boolean notInventory) {
		this.items.forEach((slot, item) -> {
			int k = slot.x + x;
			int l = slot.y + y;
			if (item.isResultSlot && notInventory) {
				drawContext.fill(k - 4, l - 4, k + 20, l + 20, 822018048);
			} else {
				drawContext.fill(k, l, k + 16, l + 16, 822018048);
			}

			ItemStack itemStack = item.get(this.currentIndexProvider.currentIndex());
			drawContext.drawItemWithoutEntity(itemStack, k, l);
			drawContext.fill(RenderLayer.getGuiGhostRecipeOverlay(), k, l, k + 16, l + 16, 822083583);
			if (item.isResultSlot) {
				drawContext.drawItemInSlot(client.textRenderer, itemStack, k, l);
			}
		});
	}

	public void drawTooltip(DrawContext drawContext, MinecraftClient client, int i, int j, @Nullable Slot slot) {
		if (slot != null) {
			GhostRecipe.CyclingItem cyclingItem = this.items.get(slot);
			if (cyclingItem != null) {
				ItemStack itemStack = cyclingItem.get(this.currentIndexProvider.currentIndex());
				drawContext.drawTooltip(client.textRenderer, Screen.getTooltipFromItem(client, itemStack), i, j);
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
