package net.minecraft.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BannerPatternTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;

public class LoomScreenHandler extends ScreenHandler {
	private static final int NO_PATTERN = -1;
	private static final int INVENTORY_START = 4;
	private static final int INVENTORY_END = 31;
	private static final int HOTBAR_START = 31;
	private static final int HOTBAR_END = 40;
	private final ScreenHandlerContext context;
	final Property selectedPattern = Property.create();
	private List<RegistryEntry<BannerPattern>> bannerPatterns = List.of();
	Runnable inventoryChangeListener = () -> {
	};
	final Slot bannerSlot;
	final Slot dyeSlot;
	private final Slot patternSlot;
	private final Slot outputSlot;
	long lastTakeResultTime;
	private final Inventory input = new SimpleInventory(3) {
		@Override
		public void markDirty() {
			super.markDirty();
			LoomScreenHandler.this.onContentChanged(this);
			LoomScreenHandler.this.inventoryChangeListener.run();
		}
	};
	private final Inventory output = new SimpleInventory(1) {
		@Override
		public void markDirty() {
			super.markDirty();
			LoomScreenHandler.this.inventoryChangeListener.run();
		}
	};

	public LoomScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
	}

	public LoomScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScreenHandlerType.LOOM, syncId);
		this.context = context;
		this.bannerSlot = this.addSlot(new Slot(this.input, 0, 13, 26) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() instanceof BannerItem;
			}
		});
		this.dyeSlot = this.addSlot(new Slot(this.input, 1, 33, 26) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() instanceof DyeItem;
			}
		});
		this.patternSlot = this.addSlot(new Slot(this.input, 2, 23, 45) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() instanceof BannerPatternItem;
			}
		});
		this.outputSlot = this.addSlot(new Slot(this.output, 0, 143, 58) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public void onTakeItem(PlayerEntity player, ItemStack stack) {
				LoomScreenHandler.this.bannerSlot.takeStack(1);
				LoomScreenHandler.this.dyeSlot.takeStack(1);
				if (!LoomScreenHandler.this.bannerSlot.hasStack() || !LoomScreenHandler.this.dyeSlot.hasStack()) {
					LoomScreenHandler.this.selectedPattern.set(-1);
				}

				context.run((world, pos) -> {
					long l = world.getTime();
					if (LoomScreenHandler.this.lastTakeResultTime != l) {
						world.playSound(null, pos, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
						LoomScreenHandler.this.lastTakeResultTime = l;
					}
				});
				super.onTakeItem(player, stack);
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

		this.addProperty(this.selectedPattern);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, Blocks.LOOM);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (id >= 0 && id < this.bannerPatterns.size()) {
			this.selectedPattern.set(id);
			this.updateOutputSlot((RegistryEntry<BannerPattern>)this.bannerPatterns.get(id));
			return true;
		} else {
			return false;
		}
	}

	private List<RegistryEntry<BannerPattern>> getPatternsFor(ItemStack stack) {
		if (stack.isEmpty()) {
			return (List<RegistryEntry<BannerPattern>>)Registries.BANNER_PATTERN
				.getEntryList(BannerPatternTags.NO_ITEM_REQUIRED)
				.map(ImmutableList::copyOf)
				.orElse(ImmutableList.of());
		} else {
			return stack.getItem() instanceof BannerPatternItem bannerPatternItem
				? (List)Registries.BANNER_PATTERN.getEntryList(bannerPatternItem.getPattern()).map(ImmutableList::copyOf).orElse(ImmutableList.of())
				: List.of();
		}
	}

	private boolean isPatternIndexValid(int index) {
		return index >= 0 && index < this.bannerPatterns.size();
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.bannerSlot.getStack();
		ItemStack itemStack2 = this.dyeSlot.getStack();
		ItemStack itemStack3 = this.patternSlot.getStack();
		if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
			int i = this.selectedPattern.get();
			boolean bl = this.isPatternIndexValid(i);
			List<RegistryEntry<BannerPattern>> list = this.bannerPatterns;
			this.bannerPatterns = this.getPatternsFor(itemStack3);
			RegistryEntry<BannerPattern> registryEntry;
			if (this.bannerPatterns.size() == 1) {
				this.selectedPattern.set(0);
				registryEntry = (RegistryEntry<BannerPattern>)this.bannerPatterns.get(0);
			} else if (!bl) {
				this.selectedPattern.set(-1);
				registryEntry = null;
			} else {
				RegistryEntry<BannerPattern> registryEntry2 = (RegistryEntry<BannerPattern>)list.get(i);
				int j = this.bannerPatterns.indexOf(registryEntry2);
				if (j != -1) {
					registryEntry = registryEntry2;
					this.selectedPattern.set(j);
				} else {
					registryEntry = null;
					this.selectedPattern.set(-1);
				}
			}

			if (registryEntry != null) {
				NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
				boolean bl2 = nbtCompound != null
					&& nbtCompound.contains("Patterns", NbtElement.LIST_TYPE)
					&& !itemStack.isEmpty()
					&& nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE).size() >= 6;
				if (bl2) {
					this.selectedPattern.set(-1);
					this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
				} else {
					this.updateOutputSlot(registryEntry);
				}
			} else {
				this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
			}

			this.sendContentUpdates();
		} else {
			this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
			this.bannerPatterns = List.of();
			this.selectedPattern.set(-1);
		}
	}

	public List<RegistryEntry<BannerPattern>> getBannerPatterns() {
		return this.bannerPatterns;
	}

	public int getSelectedPattern() {
		return this.selectedPattern.get();
	}

	public void setInventoryChangeListener(Runnable inventoryChangeListener) {
		this.inventoryChangeListener = inventoryChangeListener;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			if (slot == this.outputSlot.id) {
				if (!this.insertItem(itemStack2, 4, 40, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
			} else if (slot != this.dyeSlot.id && slot != this.bannerSlot.id && slot != this.patternSlot.id) {
				if (itemStack2.getItem() instanceof BannerItem) {
					if (!this.insertItem(itemStack2, this.bannerSlot.id, this.bannerSlot.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof DyeItem) {
					if (!this.insertItem(itemStack2, this.dyeSlot.id, this.dyeSlot.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.getItem() instanceof BannerPatternItem) {
					if (!this.insertItem(itemStack2, this.patternSlot.id, this.patternSlot.id + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slot >= 4 && slot < 31) {
					if (!this.insertItem(itemStack2, 31, 40, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slot >= 31 && slot < 40 && !this.insertItem(itemStack2, 4, 31, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 4, 40, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			} else {
				slot2.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.context.run((world, pos) -> this.dropInventory(player, this.input));
	}

	private void updateOutputSlot(RegistryEntry<BannerPattern> pattern) {
		ItemStack itemStack = this.bannerSlot.getStack();
		ItemStack itemStack2 = this.dyeSlot.getStack();
		ItemStack itemStack3 = ItemStack.EMPTY;
		if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
			itemStack3 = itemStack.copyWithCount(1);
			DyeColor dyeColor = ((DyeItem)itemStack2.getItem()).getColor();
			NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack3);
			NbtList nbtList;
			if (nbtCompound != null && nbtCompound.contains("Patterns", NbtElement.LIST_TYPE)) {
				nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE);
			} else {
				nbtList = new NbtList();
				if (nbtCompound == null) {
					nbtCompound = new NbtCompound();
				}

				nbtCompound.put("Patterns", nbtList);
			}

			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.putString("Pattern", pattern.value().getId());
			nbtCompound2.putInt("Color", dyeColor.getId());
			nbtList.add(nbtCompound2);
			BlockItem.setBlockEntityNbt(itemStack3, BlockEntityType.BANNER, nbtCompound);
		}

		if (!ItemStack.areEqual(itemStack3, this.outputSlot.getStack())) {
			this.outputSlot.setStackNoCallbacks(itemStack3);
		}
	}

	public Slot getBannerSlot() {
		return this.bannerSlot;
	}

	public Slot getDyeSlot() {
		return this.dyeSlot;
	}

	public Slot getPatternSlot() {
		return this.patternSlot;
	}

	public Slot getOutputSlot() {
		return this.outputSlot;
	}
}
