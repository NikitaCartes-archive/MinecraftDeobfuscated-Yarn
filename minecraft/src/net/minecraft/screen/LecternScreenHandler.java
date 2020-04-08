package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class LecternScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;

	public LecternScreenHandler(int syncId) {
		this(syncId, new BasicInventory(1), new ArrayPropertyDelegate(1));
	}

	public LecternScreenHandler(int syncId, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.LECTERN, syncId);
		checkSize(inventory, 1);
		checkDataCount(propertyDelegate, 1);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		this.addSlot(new Slot(inventory, 0, 0, 0) {
			@Override
			public void markDirty() {
				super.markDirty();
				LecternScreenHandler.this.onContentChanged(this.inventory);
			}
		});
		this.addProperties(propertyDelegate);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (id >= 100) {
			int i = id - 100;
			this.setProperty(0, i);
			return true;
		} else {
			switch (id) {
				case 1: {
					int i = this.propertyDelegate.get(0);
					this.setProperty(0, i - 1);
					return true;
				}
				case 2: {
					int i = this.propertyDelegate.get(0);
					this.setProperty(0, i + 1);
					return true;
				}
				case 3:
					if (!player.canModifyBlocks()) {
						return false;
					}

					ItemStack itemStack = this.inventory.removeStack(0);
					this.inventory.markDirty();
					if (!player.inventory.insertStack(itemStack)) {
						player.dropItem(itemStack, false);
					}

					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void setProperty(int id, int value) {
		super.setProperty(id, value);
		this.sendContentUpdates();
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getBookItem() {
		return this.inventory.getStack(0);
	}

	@Environment(EnvType.CLIENT)
	public int getPage() {
		return this.propertyDelegate.get(0);
	}
}
