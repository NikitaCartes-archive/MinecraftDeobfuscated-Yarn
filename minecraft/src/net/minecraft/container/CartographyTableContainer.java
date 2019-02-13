package net.minecraft.container;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class CartographyTableContainer extends Container {
	private final ContainerWorldContext context;
	private boolean field_17295;
	private ItemStack field_17296 = ItemStack.EMPTY;
	private ItemStack field_17297 = ItemStack.EMPTY;
	public final Inventory inventory = new BasicInventory(3) {
		@Override
		public void markDirty() {
			CartographyTableContainer.this.onContentChanged(this);
			super.markDirty();
		}
	};

	public CartographyTableContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, ContainerWorldContext.NO_OP_CONTEXT);
	}

	public CartographyTableContainer(int i, PlayerInventory playerInventory, ContainerWorldContext containerWorldContext) {
		super(ContainerType.CARTOGRAPHY, i);
		this.context = containerWorldContext;
		this.addSlot(new Slot(this.inventory, 0, 15, 15) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.getItem() == Items.field_8204;
			}
		});
		this.addSlot(new Slot(this.inventory, 1, 15, 52) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				Item item = itemStack.getItem();
				return item == Items.field_8407 || item == Items.field_8895 || item == Items.field_8141;
			}
		});
		this.addSlot(new Slot(this.inventory, 2, 145, 39) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return false;
			}

			@Override
			public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
				ItemStack itemStack2 = (ItemStack)containerWorldContext.apply((world, blockPos) -> {
					if (!CartographyTableContainer.this.field_17295 && CartographyTableContainer.this.inventory.getInvStack(1).getItem() == Items.field_8141) {
						ItemStack itemStack2x = FilledMapItem.method_17442(world, CartographyTableContainer.this.field_17296);
						if (itemStack2x != null) {
							itemStack2x.setAmount(1);
							return itemStack2x;
						}
					}

					return itemStack;
				}).orElse(itemStack);
				this.inventory.takeInvStack(0, 1);
				this.inventory.takeInvStack(1, 1);
				playerInventory.setCursorStack(itemStack2);
				itemStack2.getItem().onCrafted(itemStack2, playerEntity.world, playerEntity);
				containerWorldContext.run((world, blockPos) -> world.playSound(null, blockPos, SoundEvents.field_17484, SoundCategory.field_15245, 1.0F, 1.0F));
				return super.onTakeItem(playerEntity, itemStack2);
			}
		});

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return canUse(this.context, playerEntity, Blocks.field_16336);
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.inventory.getInvStack(0);
		ItemStack itemStack2 = this.inventory.getInvStack(1);
		boolean bl = !ItemStack.areEqual(itemStack, this.field_17296);
		boolean bl2 = !ItemStack.areEqual(itemStack2, this.field_17297);
		this.field_17296 = itemStack.copy();
		this.field_17297 = itemStack2.copy();
		if (bl || bl2) {
			ItemStack itemStack3 = this.inventory.getInvStack(2);
			if (itemStack3.isEmpty() || !itemStack.isEmpty() && !itemStack2.isEmpty()) {
				if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
					this.method_17381(itemStack, itemStack2, itemStack3);
				}
			} else {
				this.inventory.removeInvStack(2);
			}
		}
	}

	private void method_17381(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
		this.context.run((world, blockPos) -> {
			Item item = itemStack2.getItem();
			MapState mapState = FilledMapItem.method_7997(itemStack, world);
			if (mapState != null) {
				ItemStack itemStack4;
				if (item == Items.field_8407 && !mapState.field_17403 && mapState.scale < 4) {
					itemStack4 = itemStack.copy();
					itemStack4.setAmount(1);
					itemStack4.getOrCreateTag().putInt("map_scale_direction", 1);
				} else if (item == Items.field_8141 && !mapState.field_17403) {
					itemStack4 = itemStack.copy();
					itemStack4.setAmount(1);
				} else {
					if (item != Items.field_8895) {
						this.inventory.removeInvStack(2);
						this.sendContentUpdates();
						return;
					}

					itemStack4 = itemStack.copy();
					itemStack4.setAmount(2);
				}

				if (!ItemStack.areEqual(itemStack4, itemStack3)) {
					this.inventory.setInvStack(2, itemStack4);
					this.sendContentUpdates();
				}
			}
		});
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			ItemStack itemStack3 = itemStack2;
			Item item = itemStack2.getItem();
			itemStack = itemStack2.copy();
			if (i == 2) {
				if (this.inventory.getInvStack(1).getItem() == Items.field_8141) {
					itemStack3 = (ItemStack)this.context.apply((world, blockPos) -> {
						ItemStack itemStack2x = FilledMapItem.method_17442(world, this.field_17296);
						return itemStack2x != null ? itemStack2x : itemStack2;
					}).orElse(itemStack2);
				}

				item.onCrafted(itemStack3, playerEntity.world, playerEntity);
				if (!this.insertItem(itemStack3, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack3, itemStack);
			} else if (i != 1 && i != 0) {
				if (item == Items.field_8204) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (item != Items.field_8407 && item != Items.field_8895 && item != Items.field_8141) {
					if (i >= 3 && i < 30) {
						if (!this.insertItem(itemStack2, 30, 39, false)) {
							return ItemStack.EMPTY;
						}
					} else if (i >= 30 && i < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack3.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			}

			slot.markDirty();
			if (itemStack3.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			this.field_17295 = true;
			slot.onTakeItem(playerEntity, itemStack3);
			this.field_17295 = false;
			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.inventory.removeInvStack(2);
		this.context.run((world, blockPos) -> this.dropInventory(playerEntity, playerEntity.world, this.inventory));
	}
}
