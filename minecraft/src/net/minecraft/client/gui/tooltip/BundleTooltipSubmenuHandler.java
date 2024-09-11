package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BundleItemSelectedC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.joml.Vector2i;

@Environment(EnvType.CLIENT)
public class BundleTooltipSubmenuHandler implements TooltipSubmenuHandler {
	private final MinecraftClient client;
	private final Scroller scroller;

	public BundleTooltipSubmenuHandler(MinecraftClient client) {
		this.client = client;
		this.scroller = new Scroller();
	}

	@Override
	public boolean isApplicableTo(Slot slot) {
		return slot.getStack().isOf(Items.BUNDLE);
	}

	@Override
	public boolean onScroll(double horizontal, double vertical, int slotId, ItemStack item) {
		int i = BundleItem.getNumberOfStacksShown(item);
		if (i == 0) {
			return false;
		} else {
			Vector2i vector2i = this.scroller.update(horizontal, vertical);
			int j = vector2i.y == 0 ? -vector2i.x : vector2i.y;
			if (j != 0) {
				int k = BundleItem.getSelectedStackIndex(item);
				k = Scroller.scrollCycling((double)j, k, i);
				this.sendPacket(item, slotId, k);
			}

			return true;
		}
	}

	@Override
	public void reset(Slot slot) {
		this.reset(slot.getStack(), slot.id);
	}

	@Override
	public void onMouseClick(Slot slot, SlotActionType actionType) {
		if (actionType == SlotActionType.QUICK_MOVE) {
			this.reset(slot.getStack(), slot.id);
		}
	}

	private void sendPacket(ItemStack item, int slotId, int selectedItemIndex) {
		if (this.client.getNetworkHandler() != null && selectedItemIndex < BundleItem.getNumberOfStacksShown(item)) {
			ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
			BundleItem.setSelectedStackIndex(item, selectedItemIndex);
			clientPlayNetworkHandler.sendPacket(new BundleItemSelectedC2SPacket(slotId, selectedItemIndex));
		}
	}

	public void reset(ItemStack item, int slotId) {
		this.sendPacket(item, slotId, -1);
	}
}
