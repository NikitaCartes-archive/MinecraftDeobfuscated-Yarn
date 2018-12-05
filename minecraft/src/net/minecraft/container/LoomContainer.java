package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1662;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BannerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.recipe.Recipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LoomContainer extends CraftingContainer {
	private final World world;
	private final BlockPos pos;
	private int field_7847;
	public Inventory inv = new BasicInventory(new StringTextComponent("Loom"), 4) {
		@Override
		public void markDirty() {
			super.markDirty();
			LoomContainer.this.onContentChanged(this);
		}

		@Override
		public void setInvProperty(int i, int j) {
			LoomContainer.this.setProperty(i, j);
		}
	};

	public LoomContainer(PlayerInventory playerInventory, BlockPos blockPos) {
		this.world = playerInventory.player.world;
		this.pos = blockPos;
		this.addSlot(new Slot(this.inv, 0, 13, 26) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.getItem() instanceof BannerItem;
			}
		});
		this.addSlot(new Slot(this.inv, 1, 33, 26) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.getItem() instanceof DyeItem;
			}
		});
		this.addSlot(new Slot(this.inv, 2, 23, 45) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.getItem() instanceof BannerPatternItem;
			}
		});
		this.addSlot(new Slot(this.inv, 3, 143, 58) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return false;
			}

			@Override
			public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
				this.inventory.takeInvStack(0, 1);
				this.inventory.takeInvStack(1, 1);
				this.inventory.setInvProperty(1, 0);
				if (blockPos != null) {
					LoomContainer.this.world.playSound(null, blockPos, SoundEvents.field_15096, SoundCategory.field_15245, 1.0F, 1.0F);
				}

				return super.onTakeItem(playerEntity, itemStack);
			}
		});

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 3;
	}

	@Override
	public int getCraftingWidth() {
		return 1;
	}

	@Override
	public int getCrafitngHeight() {
		return 1;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_7658() {
		return 3;
	}

	@Override
	public void sendContentUpdates() {
		for (ContainerListener containerListener : this.listeners) {
			containerListener.onContainerPropertyUpdate(this, 1, this.field_7847);
		}

		super.sendContentUpdates();
	}

	@Override
	public void method_7654(class_1662 arg) {
	}

	@Override
	public void setProperty(int i, int j) {
		if (i == 1) {
			this.field_7847 = j;
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_7647() {
		return this.field_7847;
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.world.getBlockState(this.pos).getBlock() != Blocks.field_10083
			? false
			: playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
	}

	@Override
	public void clearCraftingSlots() {
		this.inv.clearInv();
	}

	@Override
	public boolean matches(Recipe recipe) {
		return false;
	}

	@Override
	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		this.setProperty(1, i);
		this.method_7648();
		return true;
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.inv.getInvStack(0);
		ItemStack itemStack2 = this.inv.getInvStack(1);
		ItemStack itemStack3 = this.inv.getInvStack(2);
		ItemStack itemStack4 = this.inv.getInvStack(3);
		if (itemStack4.isEmpty()
			|| !itemStack.isEmpty() && !itemStack2.isEmpty() && this.field_7847 > 0 && (this.field_7847 < BannerPattern.COUNT - 4 || !itemStack3.isEmpty())) {
			if (!itemStack3.isEmpty() && itemStack3.getItem() instanceof BannerPatternItem) {
				CompoundTag compoundTag = itemStack.getOrCreateSubCompoundTag("BlockEntityTag");
				boolean bl = compoundTag.containsKey("Patterns", 9) && !itemStack.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
				if (bl) {
					this.field_7847 = 0;
				} else {
					this.field_7847 = ((BannerPatternItem)itemStack3.getItem()).method_7704().ordinal();
					this.method_7648();
				}

				this.sendContentUpdates();
			}
		} else {
			this.inv.setInvStack(3, ItemStack.EMPTY);
			this.field_7847 = 0;
			this.sendContentUpdates();
		}
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i == 3) {
				if (!this.insertItem(itemStack2, 4, 40, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (i != 1 && i != 0 && i != 2) {
				if (itemStack2.getItem() instanceof BannerItem) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof DyeItem) {
					if (!this.insertItem(itemStack2, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof BannerPatternItem) {
					if (!this.insertItem(itemStack2, 2, 3, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 4 && i < 31) {
					if (!this.insertItem(itemStack2, 31, 40, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 31 && i < 40 && !this.insertItem(itemStack2, 4, 31, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 4, 40, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(playerEntity, itemStack2);
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		if (!this.world.isRemote) {
			this.inv.removeInvStack(3);
			this.method_7607(playerEntity, playerEntity.world, this.inv);
		}
	}

	private void method_7648() {
		if (!this.world.isRemote && this.field_7847 > 0) {
			ItemStack itemStack = this.inv.getInvStack(0);
			ItemStack itemStack2 = this.inv.getInvStack(1);
			ItemStack itemStack3 = ItemStack.EMPTY;
			if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
				itemStack3 = itemStack.copy();
				itemStack3.setAmount(1);
				BannerPattern bannerPattern = BannerPattern.values()[this.field_7847];
				DyeColor dyeColor = ((DyeItem)itemStack2.getItem()).getColor();
				CompoundTag compoundTag = itemStack3.getOrCreateSubCompoundTag("BlockEntityTag");
				ListTag listTag;
				if (compoundTag.containsKey("Patterns", 9)) {
					listTag = compoundTag.getList("Patterns", 10);
				} else {
					listTag = new ListTag();
					compoundTag.put("Patterns", listTag);
				}

				CompoundTag compoundTag2 = new CompoundTag();
				compoundTag2.putString("Pattern", bannerPattern.getId());
				compoundTag2.putInt("Color", dyeColor.getId());
				listTag.add((Tag)compoundTag2);
			}

			if (!ItemStack.areEqual(itemStack3, this.inv.getInvStack(3))) {
				this.inv.setInvStack(3, itemStack3);
			}
		}
	}
}
