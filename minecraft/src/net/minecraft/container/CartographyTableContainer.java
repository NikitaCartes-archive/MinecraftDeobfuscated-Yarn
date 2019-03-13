package net.minecraft.container;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CartographyTableContainer extends Container {
	private final BlockContext field_17294;
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
		this(i, playerInventory, BlockContext.EMPTY);
	}

	public CartographyTableContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.CARTOGRAPHY, i);
		this.field_17294 = blockContext;
		this.method_7621(new Slot(this.inventory, 0, 15, 15) {
			@Override
			public boolean method_7680(ItemStack itemStack) {
				return itemStack.getItem() == Items.field_8204;
			}
		});
		this.method_7621(new Slot(this.inventory, 1, 15, 52) {
			@Override
			public boolean method_7680(ItemStack itemStack) {
				Item item = itemStack.getItem();
				return item == Items.field_8407 || item == Items.field_8895 || item == Items.GLASS_PANE;
			}
		});
		this.method_7621(
			new Slot(this.inventory, 2, 145, 39) {
				@Override
				public boolean method_7680(ItemStack itemStack) {
					return false;
				}

				@Override
				public ItemStack method_7667(PlayerEntity playerEntity, ItemStack itemStack) {
					ItemStack itemStack2 = (ItemStack)blockContext.run((BiFunction)((world, blockPos) -> {
						if (!CartographyTableContainer.this.field_17295 && CartographyTableContainer.this.inventory.method_5438(1).getItem() == Items.GLASS_PANE) {
							ItemStack itemStack2x = FilledMapItem.method_17442(world, CartographyTableContainer.this.field_17296);
							if (itemStack2x != null) {
								itemStack2x.setAmount(1);
								return itemStack2x;
							}
						}

						return itemStack;
					})).orElse(itemStack);
					this.inventory.method_5434(0, 1);
					this.inventory.method_5434(1, 1);
					playerInventory.method_7396(itemStack2);
					itemStack2.getItem().method_7843(itemStack2, playerEntity.field_6002, playerEntity);
					blockContext.run(
						(BiConsumer<World, BlockPos>)((world, blockPos) -> world.method_8396(null, blockPos, SoundEvents.field_17484, SoundCategory.field_15245, 1.0F, 1.0F))
					);
					return super.method_7667(playerEntity, itemStack2);
				}
			}
		);

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new Slot(playerInventory, j, 8 + j * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return method_17695(this.field_17294, playerEntity, Blocks.field_16336);
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.inventory.method_5438(0);
		ItemStack itemStack2 = this.inventory.method_5438(1);
		boolean bl = !ItemStack.areEqual(itemStack, this.field_17296);
		boolean bl2 = !ItemStack.areEqual(itemStack2, this.field_17297);
		this.field_17296 = itemStack.copy();
		this.field_17297 = itemStack2.copy();
		if (bl || bl2) {
			ItemStack itemStack3 = this.inventory.method_5438(2);
			if (itemStack3.isEmpty() || !itemStack.isEmpty() && !itemStack2.isEmpty()) {
				if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
					this.method_17381(itemStack, itemStack2, itemStack3);
				}
			} else {
				this.inventory.method_5441(2);
			}
		}
	}

	private void method_17381(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
		this.field_17294.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
			Item item = itemStack2.getItem();
			MapState mapState = FilledMapItem.method_7997(itemStack, world);
			if (mapState != null) {
				ItemStack itemStack4;
				if (item == Items.field_8407 && !mapState.field_17403 && mapState.scale < 4) {
					itemStack4 = itemStack.copy();
					itemStack4.setAmount(1);
					itemStack4.method_7948().putInt("map_scale_direction", 1);
				} else if (item == Items.GLASS_PANE && !mapState.field_17403) {
					itemStack4 = itemStack.copy();
					itemStack4.setAmount(1);
				} else {
					if (item != Items.field_8895) {
						this.inventory.method_5441(2);
						this.sendContentUpdates();
						return;
					}

					itemStack4 = itemStack.copy();
					itemStack4.setAmount(2);
				}

				if (!ItemStack.areEqual(itemStack4, itemStack3)) {
					this.inventory.method_5447(2, itemStack4);
					this.sendContentUpdates();
				}
			}
		}));
	}

	@Override
	public ItemStack method_7601(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.method_7677();
			ItemStack itemStack3 = itemStack2;
			Item item = itemStack2.getItem();
			itemStack = itemStack2.copy();
			if (i == 2) {
				if (this.inventory.method_5438(1).getItem() == Items.GLASS_PANE) {
					itemStack3 = (ItemStack)this.field_17294.run((BiFunction)((world, blockPos) -> {
						ItemStack itemStack2x = FilledMapItem.method_17442(world, this.field_17296);
						return itemStack2x != null ? itemStack2x : itemStack2;
					})).orElse(itemStack2);
				}

				item.method_7843(itemStack3, playerEntity.field_6002, playerEntity);
				if (!this.method_7616(itemStack3, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.method_7670(itemStack3, itemStack);
			} else if (i != 1 && i != 0) {
				if (item == Items.field_8204) {
					if (!this.method_7616(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (item != Items.field_8407 && item != Items.field_8895 && item != Items.GLASS_PANE) {
					if (i >= 3 && i < 30) {
						if (!this.method_7616(itemStack2, 30, 39, false)) {
							return ItemStack.EMPTY;
						}
					} else if (i >= 30 && i < 39 && !this.method_7616(itemStack2, 3, 30, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.method_7616(itemStack2, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.method_7616(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack3.isEmpty()) {
				slot.method_7673(ItemStack.EMPTY);
			}

			slot.markDirty();
			if (itemStack3.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			this.field_17295 = true;
			slot.method_7667(playerEntity, itemStack3);
			this.field_17295 = false;
			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.inventory.method_5441(2);
		this.field_17294.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.method_7607(playerEntity, playerEntity.field_6002, this.inventory)));
	}
}
