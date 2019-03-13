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
		method_17361(propertyDelegate, 1);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		this.method_7621(new Slot(inventory, 0, 0, 0) {
			@Override
			public void markDirty() {
				super.markDirty();
				LecternContainer.this.onContentChanged(this.inventory);
			}
		});
		this.method_17360(propertyDelegate);
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

					ItemStack itemStack = this.inventory.method_5441(0);
					this.inventory.markDirty();
					if (!playerEntity.inventory.method_7394(itemStack)) {
						playerEntity.method_7328(itemStack, false);
					}

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
		return this.inventory.method_5443(playerEntity);
	}

	@Environment(EnvType.CLIENT)
	public ItemStack method_17418() {
		return this.inventory.method_5438(0);
	}

	@Environment(EnvType.CLIENT)
	public int getPage() {
		return this.propertyDelegate.get(0);
	}
}
