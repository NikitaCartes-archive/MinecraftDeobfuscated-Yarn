package net.minecraft.inventory;

/**
 * A functional interface used in {@link SimpleInventory#addListener}.
 * 
 * <p>Other inventories can listen for inventory changes by overriding
 * {@link Inventory#markDirty}.
 */
public interface InventoryChangedListener {
	void onInventoryChanged(Inventory sender);
}
