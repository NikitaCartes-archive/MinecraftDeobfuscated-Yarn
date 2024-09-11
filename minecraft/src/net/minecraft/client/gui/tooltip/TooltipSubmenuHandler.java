package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

@Environment(EnvType.CLIENT)
public interface TooltipSubmenuHandler {
	boolean isApplicableTo(Slot slot);

	boolean onScroll(double horizontal, double vertical, int slotId, ItemStack item);

	void reset(Slot slot);

	void onMouseClick(Slot slot, SlotActionType actionType);
}
