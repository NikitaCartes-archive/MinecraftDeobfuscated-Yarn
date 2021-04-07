package net.minecraft.screen.slot;

public enum SlotActionType {
	/**
	 * Performs a normal slot click. This can pickup or place items in the slot, possibly merging the cursor stack into the slot, or swapping the slot stack with the cursor stack if they can't be merged.
	 */
	PICKUP,
	/**
	 * Performs a shift-click. This usually quickly moves items between the player's inventory and the open screen handler.
	 */
	QUICK_MOVE,
	/**
	 * Exchanges items between a slot and a hotbar slot. This is usually triggered by the player pressing a 1-9 number key while hovering over a slot.
	 * 
	 * <p>When the action type is swap, the click data is the hotbar slot to swap with (0-8).
	 */
	SWAP,
	/**
	 * Clones the item in the slot. Usually triggered by middle clicking an item in creative mode.
	 */
	CLONE,
	/**
	 * Throws the item out of the inventory. This is usually triggered by the player pressing Q while hovering over a slot, or clicking outside the window.
	 * 
	 * <p>When the action type is throw, the click data determines whether to throw a whole stack (1) or a single item from that stack (0).
	 */
	THROW,
	/**
	 * Drags items between multiple slots. This is usually triggered by the player clicking and dragging between slots.
	 * 
	 * <p>This action happens in 3 stages. Stage 0 signals that the drag has begun, and stage 2 signals that the drag has ended. In between multiple stage 1s signal which slots were dragged on.
	 * 
	 * <p>The stage is packed into the click data along with the mouse button that was clicked. See {@link net.minecraft.screen.ScreenHandler#packQuickCraftData(int, int) ScreenHandler.packQuickCraftData(int, int)} for details.
	 */
	QUICK_CRAFT,
	/**
	 * Replenishes the cursor stack with items from the screen handler. This is usually triggered by the player double clicking.
	 */
	PICKUP_ALL;
}
