package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class LecternContainer extends Container {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;

	public LecternContainer(int i) {
		this(i, new BasicInventory(1), new ArrayPropertyDelegate(1));
	}

	public LecternContainer(int i, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ContainerType.LECTERN, i);
		checkContainerSize(inventory, 1);
		checkContainerDataCount(propertyDelegate, 1);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		this.addSlot(new Slot(inventory, 0, 0, 0) {
			@Override
			public void markDirty() {
				super.markDirty();
				LecternContainer.this.onContentChanged(this.inventory);
			}
		});
		this.readData(propertyDelegate);
	}

	@Override
	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		if (i >= 100) {
			int j = i - 100;
			this.setProperty(0, j);
			return true;
		} else {
			switch (i) {
				case 1: {
					int j = this.propertyDelegate.get(0);
					this.setProperty(0, j - 1);
					return true;
				}
				case 2: {
					int j = this.propertyDelegate.get(0);
					this.setProperty(0, j + 1);
					return true;
				}
				case 3:
					if (!playerEntity.canModifyWorld()) {
						return false;
					}

					ItemStack itemStack = this.inventory.removeInvStack(0);
					this.inventory.markDirty();
					playerEntity.inventory.insertStack(itemStack);
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void setProperty(int i, int j) {
		super.setProperty(i, j);
		this.sendContentUpdates();
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.inventory.canPlayerUseInv(playerEntity);
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getBookItem() {
		return this.inventory.getInvStack(0);
	}

	@Environment(EnvType.CLIENT)
	public int method_17419() {
		return this.propertyDelegate.get(0);
	}
}
