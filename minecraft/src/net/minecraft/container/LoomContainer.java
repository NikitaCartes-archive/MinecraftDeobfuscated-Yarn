package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3914;
import net.minecraft.class_3915;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;

public class LoomContainer extends Container {
	private final class_3914 field_17316;
	private final class_3915 field_17317 = class_3915.method_17403();
	private Runnable field_17318 = () -> {
	};
	private final Slot field_17319;
	private final Slot field_17320;
	private final Slot field_17321;
	private final Slot field_17322;
	private final Inventory inv = new BasicInventory(3) {
		@Override
		public void markDirty() {
			super.markDirty();
			LoomContainer.this.onContentChanged(this);
			LoomContainer.this.field_17318.run();
		}
	};
	private final Inventory field_17323 = new BasicInventory(1) {
		@Override
		public void markDirty() {
			super.markDirty();
			LoomContainer.this.field_17318.run();
		}
	};

	public LoomContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, class_3914.field_17304);
	}

	public LoomContainer(int i, PlayerInventory playerInventory, class_3914 arg) {
		super(ContainerType.LOOM, i);
		this.field_17316 = arg;
		this.field_17319 = this.addSlot(new Slot(this.inv, 0, 13, 26) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.getItem() instanceof BannerItem;
			}
		});
		this.field_17320 = this.addSlot(new Slot(this.inv, 1, 33, 26) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.getItem() instanceof DyeItem;
			}
		});
		this.field_17321 = this.addSlot(new Slot(this.inv, 2, 23, 45) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return itemStack.getItem() instanceof BannerPatternItem;
			}
		});
		this.field_17322 = this.addSlot(new Slot(this.field_17323, 0, 143, 58) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return false;
			}

			@Override
			public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
				LoomContainer.this.field_17319.takeStack(1);
				LoomContainer.this.field_17320.takeStack(1);
				LoomContainer.this.field_17317.method_17404(0);
				arg.method_17393((world, blockPos) -> world.playSound(null, blockPos, SoundEvents.field_15096, SoundCategory.field_15245, 1.0F, 1.0F));
				return super.onTakeItem(playerEntity, itemStack);
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

		this.method_17362(this.field_17317);
	}

	@Environment(EnvType.CLIENT)
	public int method_7647() {
		return this.field_17317.method_17407();
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return method_17695(this.field_17316, playerEntity, Blocks.field_10083);
	}

	@Override
	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		this.field_17317.method_17404(i);
		this.method_7648();
		return true;
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.field_17319.getStack();
		ItemStack itemStack2 = this.field_17320.getStack();
		ItemStack itemStack3 = this.field_17321.getStack();
		ItemStack itemStack4 = this.field_17322.getStack();
		if (itemStack4.isEmpty()
			|| !itemStack.isEmpty()
				&& !itemStack2.isEmpty()
				&& this.field_17317.method_17407() > 0
				&& (this.field_17317.method_17407() < BannerPattern.COUNT - 4 || !itemStack3.isEmpty())) {
			if (!itemStack3.isEmpty() && itemStack3.getItem() instanceof BannerPatternItem) {
				CompoundTag compoundTag = itemStack.getOrCreateSubCompoundTag("BlockEntityTag");
				boolean bl = compoundTag.containsKey("Patterns", 9) && !itemStack.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
				if (bl) {
					this.field_17317.method_17404(0);
				} else {
					this.field_17317.method_17404(((BannerPatternItem)itemStack3.getItem()).getPattern().ordinal());
				}
			}
		} else {
			this.field_17322.setStack(ItemStack.EMPTY);
			this.field_17317.method_17404(0);
		}

		this.method_7648();
		this.sendContentUpdates();
	}

	@Environment(EnvType.CLIENT)
	public void method_17423(Runnable runnable) {
		this.field_17318 = runnable;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i == this.field_17322.id) {
				if (!this.insertItem(itemStack2, 4, 40, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (i != this.field_17320.id && i != this.field_17319.id && i != this.field_17321.id) {
				if (itemStack2.getItem() instanceof BannerItem) {
					if (!this.insertItem(itemStack2, this.field_17319.id, this.field_17319.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof DyeItem) {
					if (!this.insertItem(itemStack2, this.field_17320.id, this.field_17320.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof BannerPatternItem) {
					if (!this.insertItem(itemStack2, this.field_17321.id, this.field_17321.id + 1, false)) {
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
		this.field_17316.method_17393((world, blockPos) -> this.method_7607(playerEntity, playerEntity.world, this.inv));
	}

	private void method_7648() {
		this.field_17316.method_17393((world, blockPos) -> {
			if (this.field_17317.method_17407() > 0) {
				ItemStack itemStack = this.field_17319.getStack();
				ItemStack itemStack2 = this.field_17320.getStack();
				ItemStack itemStack3 = ItemStack.EMPTY;
				if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
					itemStack3 = itemStack.copy();
					itemStack3.setAmount(1);
					BannerPattern bannerPattern = BannerPattern.values()[this.field_17317.method_17407()];
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
					listTag.add(compoundTag2);
				}

				if (!ItemStack.areEqual(itemStack3, this.field_17322.getStack())) {
					this.field_17322.setStack(itemStack3);
				}
			}
		});
	}

	@Environment(EnvType.CLIENT)
	public Slot method_17428() {
		return this.field_17319;
	}

	@Environment(EnvType.CLIENT)
	public Slot method_17429() {
		return this.field_17320;
	}

	@Environment(EnvType.CLIENT)
	public Slot method_17430() {
		return this.field_17321;
	}

	@Environment(EnvType.CLIENT)
	public Slot method_17431() {
		return this.field_17322;
	}
}
