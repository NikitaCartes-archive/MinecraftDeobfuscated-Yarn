package net.minecraft.container;

import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LoomContainer extends Container {
	private final BlockContext context;
	private final Property selectedPattern = Property.create();
	private Runnable inventoryChangeListener = () -> {
	};
	private final Slot field_17319;
	private final Slot field_17320;
	private final Slot field_17321;
	private final Slot field_17322;
	private final Inventory inputInventory = new BasicInventory(3) {
		@Override
		public void markDirty() {
			super.markDirty();
			LoomContainer.this.onContentChanged(this);
			LoomContainer.this.inventoryChangeListener.run();
		}
	};
	private final Inventory outputInventory = new BasicInventory(1) {
		@Override
		public void markDirty() {
			super.markDirty();
			LoomContainer.this.inventoryChangeListener.run();
		}
	};

	public LoomContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, BlockContext.EMPTY);
	}

	public LoomContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.LOOM, i);
		this.context = blockContext;
		this.field_17319 = this.method_7621(new Slot(this.inputInventory, 0, 13, 26) {
			@Override
			public boolean method_7680(ItemStack itemStack) {
				return itemStack.getItem() instanceof BannerItem;
			}
		});
		this.field_17320 = this.method_7621(new Slot(this.inputInventory, 1, 33, 26) {
			@Override
			public boolean method_7680(ItemStack itemStack) {
				return itemStack.getItem() instanceof DyeItem;
			}
		});
		this.field_17321 = this.method_7621(new Slot(this.inputInventory, 2, 23, 45) {
			@Override
			public boolean method_7680(ItemStack itemStack) {
				return itemStack.getItem() instanceof BannerPatternItem;
			}
		});
		this.field_17322 = this.method_7621(
			new Slot(this.outputInventory, 0, 143, 58) {
				@Override
				public boolean method_7680(ItemStack itemStack) {
					return false;
				}

				@Override
				public ItemStack method_7667(PlayerEntity playerEntity, ItemStack itemStack) {
					LoomContainer.this.field_17319.method_7671(1);
					LoomContainer.this.field_17320.method_7671(1);
					if (!LoomContainer.this.field_17319.hasStack() || !LoomContainer.this.field_17320.hasStack()) {
						LoomContainer.this.selectedPattern.set(0);
					}

					blockContext.run(
						(BiConsumer<World, BlockPos>)((world, blockPos) -> world.method_8396(null, blockPos, SoundEvents.field_15096, SoundCategory.field_15245, 1.0F, 1.0F))
					);
					return super.method_7667(playerEntity, itemStack);
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

		this.method_17362(this.selectedPattern);
	}

	@Environment(EnvType.CLIENT)
	public int getSelectedPattern() {
		return this.selectedPattern.get();
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return method_17695(this.context, playerEntity, Blocks.field_10083);
	}

	@Override
	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		if (i > 0 && i <= BannerPattern.field_18283) {
			this.selectedPattern.set(i);
			this.updateOutputSlot();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.field_17319.method_7677();
		ItemStack itemStack2 = this.field_17320.method_7677();
		ItemStack itemStack3 = this.field_17321.method_7677();
		ItemStack itemStack4 = this.field_17322.method_7677();
		if (itemStack4.isEmpty()
			|| !itemStack.isEmpty()
				&& !itemStack2.isEmpty()
				&& this.selectedPattern.get() > 0
				&& (this.selectedPattern.get() < BannerPattern.COUNT - 5 || !itemStack3.isEmpty())) {
			if (!itemStack3.isEmpty() && itemStack3.getItem() instanceof BannerPatternItem) {
				CompoundTag compoundTag = itemStack.method_7911("BlockEntityTag");
				boolean bl = compoundTag.containsKey("Patterns", 9) && !itemStack.isEmpty() && compoundTag.method_10554("Patterns", 10).size() >= 6;
				if (bl) {
					this.selectedPattern.set(0);
				} else {
					this.selectedPattern.set(((BannerPatternItem)itemStack3.getItem()).method_7704().ordinal());
				}
			}
		} else {
			this.field_17322.method_7673(ItemStack.EMPTY);
			this.selectedPattern.set(0);
		}

		this.updateOutputSlot();
		this.sendContentUpdates();
	}

	@Environment(EnvType.CLIENT)
	public void setInventoryChangeListener(Runnable runnable) {
		this.inventoryChangeListener = runnable;
	}

	@Override
	public ItemStack method_7601(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.method_7677();
			itemStack = itemStack2.copy();
			if (i == this.field_17322.id) {
				if (!this.method_7616(itemStack2, 4, 40, true)) {
					return ItemStack.EMPTY;
				}

				slot.method_7670(itemStack2, itemStack);
			} else if (i != this.field_17320.id && i != this.field_17319.id && i != this.field_17321.id) {
				if (itemStack2.getItem() instanceof BannerItem) {
					if (!this.method_7616(itemStack2, this.field_17319.id, this.field_17319.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof DyeItem) {
					if (!this.method_7616(itemStack2, this.field_17320.id, this.field_17320.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof BannerPatternItem) {
					if (!this.method_7616(itemStack2, this.field_17321.id, this.field_17321.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 4 && i < 31) {
					if (!this.method_7616(itemStack2, 31, 40, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 31 && i < 40 && !this.method_7616(itemStack2, 4, 31, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.method_7616(itemStack2, 4, 40, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.method_7673(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.method_7667(playerEntity, itemStack2);
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.method_7607(playerEntity, playerEntity.field_6002, this.inputInventory)));
	}

	private void updateOutputSlot() {
		if (this.selectedPattern.get() > 0) {
			ItemStack itemStack = this.field_17319.method_7677();
			ItemStack itemStack2 = this.field_17320.method_7677();
			ItemStack itemStack3 = ItemStack.EMPTY;
			if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
				itemStack3 = itemStack.copy();
				itemStack3.setAmount(1);
				BannerPattern bannerPattern = BannerPattern.values()[this.selectedPattern.get()];
				DyeColor dyeColor = ((DyeItem)itemStack2.getItem()).getColor();
				CompoundTag compoundTag = itemStack3.method_7911("BlockEntityTag");
				ListTag listTag;
				if (compoundTag.containsKey("Patterns", 9)) {
					listTag = compoundTag.method_10554("Patterns", 10);
				} else {
					listTag = new ListTag();
					compoundTag.method_10566("Patterns", listTag);
				}

				CompoundTag compoundTag2 = new CompoundTag();
				compoundTag2.putString("Pattern", bannerPattern.getId());
				compoundTag2.putInt("Color", dyeColor.getId());
				listTag.add(compoundTag2);
			}

			if (!ItemStack.areEqual(itemStack3, this.field_17322.method_7677())) {
				this.field_17322.method_7673(itemStack3);
			}
		}
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
