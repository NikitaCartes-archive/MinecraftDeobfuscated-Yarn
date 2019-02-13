package net.minecraft.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class Container {
	private final DefaultedList<ItemStack> stackList = DefaultedList.create();
	public final List<Slot> slotList = Lists.<Slot>newArrayList();
	private final List<Property> properties = Lists.<Property>newArrayList();
	@Nullable
	private final ContainerType<?> type;
	public final int syncId;
	@Environment(EnvType.CLIENT)
	private short actionId;
	private int quickCraftStage = -1;
	private int quickCraftButton;
	private final Set<Slot> quickCraftSlots = Sets.<Slot>newHashSet();
	private final List<ContainerListener> listeners = Lists.<ContainerListener>newArrayList();
	private final Set<PlayerEntity> restrictedPlayers = Sets.<PlayerEntity>newHashSet();

	protected Container(@Nullable ContainerType<?> containerType, int i) {
		this.type = containerType;
		this.syncId = i;
	}

	protected static boolean canUse(ContainerWorldContext containerWorldContext, PlayerEntity playerEntity, Block block) {
		return containerWorldContext.apply(
			(world, blockPos) -> world.getBlockState(blockPos).getBlock() != block
					? false
					: playerEntity.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0,
			true
		);
	}

	public ContainerType<?> getType() {
		if (this.type == null) {
			throw new UnsupportedOperationException("Unable to construct this menu by type");
		} else {
			return this.type;
		}
	}

	protected static void checkContainerSize(Inventory inventory, int i) {
		int j = inventory.getInvSize();
		if (j < i) {
			throw new IllegalArgumentException("Container size " + j + " is smaller than expected " + i);
		}
	}

	protected static void checkContainerDataCount(PropertyDelegate propertyDelegate, int i) {
		int j = propertyDelegate.size();
		if (j < i) {
			throw new IllegalArgumentException("Container data count " + j + " is smaller than expected " + i);
		}
	}

	protected Slot addSlot(Slot slot) {
		slot.id = this.slotList.size();
		this.slotList.add(slot);
		this.stackList.add(ItemStack.EMPTY);
		return slot;
	}

	protected Property addProperty(Property property) {
		this.properties.add(property);
		return property;
	}

	protected void addProperties(PropertyDelegate propertyDelegate) {
		for (int i = 0; i < propertyDelegate.size(); i++) {
			this.addProperty(Property.create(propertyDelegate, i));
		}
	}

	public void addListener(ContainerListener containerListener) {
		if (!this.listeners.contains(containerListener)) {
			this.listeners.add(containerListener);
			containerListener.onContainerRegistered(this, this.getStacks());
			this.sendContentUpdates();
		}
	}

	@Environment(EnvType.CLIENT)
	public void removeListener(ContainerListener containerListener) {
		this.listeners.remove(containerListener);
	}

	public DefaultedList<ItemStack> getStacks() {
		DefaultedList<ItemStack> defaultedList = DefaultedList.create();

		for (int i = 0; i < this.slotList.size(); i++) {
			defaultedList.add(((Slot)this.slotList.get(i)).getStack());
		}

		return defaultedList;
	}

	public void sendContentUpdates() {
		for (int i = 0; i < this.slotList.size(); i++) {
			ItemStack itemStack = ((Slot)this.slotList.get(i)).getStack();
			ItemStack itemStack2 = this.stackList.get(i);
			if (!ItemStack.areEqual(itemStack2, itemStack)) {
				itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
				this.stackList.set(i, itemStack2);

				for (ContainerListener containerListener : this.listeners) {
					containerListener.onContainerSlotUpdate(this, i, itemStack2);
				}
			}
		}

		for (int ix = 0; ix < this.properties.size(); ix++) {
			Property property = (Property)this.properties.get(ix);
			if (property.detectChanges()) {
				for (ContainerListener containerListener2 : this.listeners) {
					containerListener2.onContainerPropertyUpdate(this, ix, property.get());
				}
			}
		}
	}

	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		return false;
	}

	public Slot getSlot(int i) {
		return (Slot)this.slotList.get(i);
	}

	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		Slot slot = (Slot)this.slotList.get(i);
		return slot != null ? slot.getStack() : ItemStack.EMPTY;
	}

	public ItemStack onSlotClick(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity) {
		ItemStack itemStack = ItemStack.EMPTY;
		PlayerInventory playerInventory = playerEntity.inventory;
		if (slotActionType == SlotActionType.field_7789) {
			int k = this.quickCraftButton;
			this.quickCraftButton = unpackButtonId(j);
			if ((k != 1 || this.quickCraftButton != 2) && k != this.quickCraftButton) {
				this.endQuickCraft();
			} else if (playerInventory.getCursorStack().isEmpty()) {
				this.endQuickCraft();
			} else if (this.quickCraftButton == 0) {
				this.quickCraftStage = unpackQuickCraftStage(j);
				if (shouldQuickCraftContinue(this.quickCraftStage, playerEntity)) {
					this.quickCraftButton = 1;
					this.quickCraftSlots.clear();
				} else {
					this.endQuickCraft();
				}
			} else if (this.quickCraftButton == 1) {
				Slot slot = (Slot)this.slotList.get(i);
				ItemStack itemStack2 = playerInventory.getCursorStack();
				if (slot != null
					&& canInsertItemIntoSlot(slot, itemStack2, true)
					&& slot.canInsert(itemStack2)
					&& (this.quickCraftStage == 2 || itemStack2.getAmount() > this.quickCraftSlots.size())
					&& this.canInsertIntoSlot(slot)) {
					this.quickCraftSlots.add(slot);
				}
			} else if (this.quickCraftButton == 2) {
				if (!this.quickCraftSlots.isEmpty()) {
					ItemStack itemStack3 = playerInventory.getCursorStack().copy();
					int l = playerInventory.getCursorStack().getAmount();

					for (Slot slot2 : this.quickCraftSlots) {
						ItemStack itemStack4 = playerInventory.getCursorStack();
						if (slot2 != null
							&& canInsertItemIntoSlot(slot2, itemStack4, true)
							&& slot2.canInsert(itemStack4)
							&& (this.quickCraftStage == 2 || itemStack4.getAmount() >= this.quickCraftSlots.size())
							&& this.canInsertIntoSlot(slot2)) {
							ItemStack itemStack5 = itemStack3.copy();
							int m = slot2.hasStack() ? slot2.getStack().getAmount() : 0;
							calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack5, m);
							int n = Math.min(itemStack5.getMaxAmount(), slot2.getMaxStackAmount(itemStack5));
							if (itemStack5.getAmount() > n) {
								itemStack5.setAmount(n);
							}

							l -= itemStack5.getAmount() - m;
							slot2.setStack(itemStack5);
						}
					}

					itemStack3.setAmount(l);
					playerInventory.setCursorStack(itemStack3);
				}

				this.endQuickCraft();
			} else {
				this.endQuickCraft();
			}
		} else if (this.quickCraftButton != 0) {
			this.endQuickCraft();
		} else if ((slotActionType == SlotActionType.field_7790 || slotActionType == SlotActionType.field_7794) && (j == 0 || j == 1)) {
			if (i == -999) {
				if (!playerInventory.getCursorStack().isEmpty()) {
					if (j == 0) {
						playerEntity.dropItem(playerInventory.getCursorStack(), true);
						playerInventory.setCursorStack(ItemStack.EMPTY);
					}

					if (j == 1) {
						playerEntity.dropItem(playerInventory.getCursorStack().split(1), true);
					}
				}
			} else if (slotActionType == SlotActionType.field_7794) {
				if (i < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot3 = (Slot)this.slotList.get(i);
				if (slot3 == null || !slot3.canTakeItems(playerEntity)) {
					return ItemStack.EMPTY;
				}

				for (ItemStack itemStack3 = this.transferSlot(playerEntity, i);
					!itemStack3.isEmpty() && ItemStack.areEqualIgnoreTags(slot3.getStack(), itemStack3);
					itemStack3 = this.transferSlot(playerEntity, i)
				) {
					itemStack = itemStack3.copy();
				}
			} else {
				if (i < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot3 = (Slot)this.slotList.get(i);
				if (slot3 != null) {
					ItemStack itemStack3 = slot3.getStack();
					ItemStack itemStack2 = playerInventory.getCursorStack();
					if (!itemStack3.isEmpty()) {
						itemStack = itemStack3.copy();
					}

					if (itemStack3.isEmpty()) {
						if (!itemStack2.isEmpty() && slot3.canInsert(itemStack2)) {
							int o = j == 0 ? itemStack2.getAmount() : 1;
							if (o > slot3.getMaxStackAmount(itemStack2)) {
								o = slot3.getMaxStackAmount(itemStack2);
							}

							slot3.setStack(itemStack2.split(o));
						}
					} else if (slot3.canTakeItems(playerEntity)) {
						if (itemStack2.isEmpty()) {
							if (itemStack3.isEmpty()) {
								slot3.setStack(ItemStack.EMPTY);
								playerInventory.setCursorStack(ItemStack.EMPTY);
							} else {
								int o = j == 0 ? itemStack3.getAmount() : (itemStack3.getAmount() + 1) / 2;
								playerInventory.setCursorStack(slot3.takeStack(o));
								if (itemStack3.isEmpty()) {
									slot3.setStack(ItemStack.EMPTY);
								}

								slot3.onTakeItem(playerEntity, playerInventory.getCursorStack());
							}
						} else if (slot3.canInsert(itemStack2)) {
							if (canStacksCombine(itemStack3, itemStack2)) {
								int o = j == 0 ? itemStack2.getAmount() : 1;
								if (o > slot3.getMaxStackAmount(itemStack2) - itemStack3.getAmount()) {
									o = slot3.getMaxStackAmount(itemStack2) - itemStack3.getAmount();
								}

								if (o > itemStack2.getMaxAmount() - itemStack3.getAmount()) {
									o = itemStack2.getMaxAmount() - itemStack3.getAmount();
								}

								itemStack2.subtractAmount(o);
								itemStack3.addAmount(o);
							} else if (itemStack2.getAmount() <= slot3.getMaxStackAmount(itemStack2)) {
								slot3.setStack(itemStack2);
								playerInventory.setCursorStack(itemStack3);
							}
						} else if (itemStack2.getMaxAmount() > 1 && canStacksCombine(itemStack3, itemStack2) && !itemStack3.isEmpty()) {
							int ox = itemStack3.getAmount();
							if (ox + itemStack2.getAmount() <= itemStack2.getMaxAmount()) {
								itemStack2.addAmount(ox);
								itemStack3 = slot3.takeStack(ox);
								if (itemStack3.isEmpty()) {
									slot3.setStack(ItemStack.EMPTY);
								}

								slot3.onTakeItem(playerEntity, playerInventory.getCursorStack());
							}
						}
					}

					slot3.markDirty();
				}
			}
		} else if (slotActionType == SlotActionType.field_7791 && j >= 0 && j < 9) {
			Slot slot3 = (Slot)this.slotList.get(i);
			ItemStack itemStack3x = playerInventory.getInvStack(j);
			ItemStack itemStack2x = slot3.getStack();
			if (!itemStack3x.isEmpty() || !itemStack2x.isEmpty()) {
				if (itemStack3x.isEmpty()) {
					if (slot3.canTakeItems(playerEntity)) {
						playerInventory.setInvStack(j, itemStack2x);
						slot3.onTake(itemStack2x.getAmount());
						slot3.setStack(ItemStack.EMPTY);
						slot3.onTakeItem(playerEntity, itemStack2x);
					}
				} else if (itemStack2x.isEmpty()) {
					if (slot3.canInsert(itemStack3x)) {
						int ox = slot3.getMaxStackAmount(itemStack3x);
						if (itemStack3x.getAmount() > ox) {
							slot3.setStack(itemStack3x.split(ox));
						} else {
							slot3.setStack(itemStack3x);
							playerInventory.setInvStack(j, ItemStack.EMPTY);
						}
					}
				} else if (slot3.canTakeItems(playerEntity) && slot3.canInsert(itemStack3x)) {
					int ox = slot3.getMaxStackAmount(itemStack3x);
					if (itemStack3x.getAmount() > ox) {
						slot3.setStack(itemStack3x.split(ox));
						slot3.onTakeItem(playerEntity, itemStack2x);
						if (!playerInventory.insertStack(itemStack2x)) {
							playerEntity.dropItem(itemStack2x, true);
						}
					} else {
						slot3.setStack(itemStack3x);
						playerInventory.setInvStack(j, itemStack2x);
						slot3.onTakeItem(playerEntity, itemStack2x);
					}
				}
			}
		} else if (slotActionType == SlotActionType.field_7796 && playerEntity.abilities.creativeMode && playerInventory.getCursorStack().isEmpty() && i >= 0) {
			Slot slot3 = (Slot)this.slotList.get(i);
			if (slot3 != null && slot3.hasStack()) {
				ItemStack itemStack3x = slot3.getStack().copy();
				itemStack3x.setAmount(itemStack3x.getMaxAmount());
				playerInventory.setCursorStack(itemStack3x);
			}
		} else if (slotActionType == SlotActionType.field_7795 && playerInventory.getCursorStack().isEmpty() && i >= 0) {
			Slot slot3 = (Slot)this.slotList.get(i);
			if (slot3 != null && slot3.hasStack() && slot3.canTakeItems(playerEntity)) {
				ItemStack itemStack3x = slot3.takeStack(j == 0 ? 1 : slot3.getStack().getAmount());
				slot3.onTakeItem(playerEntity, itemStack3x);
				playerEntity.dropItem(itemStack3x, true);
			}
		} else if (slotActionType == SlotActionType.field_7793 && i >= 0) {
			Slot slot3 = (Slot)this.slotList.get(i);
			ItemStack itemStack3x = playerInventory.getCursorStack();
			if (!itemStack3x.isEmpty() && (slot3 == null || !slot3.hasStack() || !slot3.canTakeItems(playerEntity))) {
				int l = j == 0 ? 0 : this.slotList.size() - 1;
				int ox = j == 0 ? 1 : -1;

				for (int p = 0; p < 2; p++) {
					for (int q = l; q >= 0 && q < this.slotList.size() && itemStack3x.getAmount() < itemStack3x.getMaxAmount(); q += ox) {
						Slot slot4 = (Slot)this.slotList.get(q);
						if (slot4.hasStack() && canInsertItemIntoSlot(slot4, itemStack3x, true) && slot4.canTakeItems(playerEntity) && this.canInsertIntoSlot(itemStack3x, slot4)
							)
						 {
							ItemStack itemStack6 = slot4.getStack();
							if (p != 0 || itemStack6.getAmount() != itemStack6.getMaxAmount()) {
								int n = Math.min(itemStack3x.getMaxAmount() - itemStack3x.getAmount(), itemStack6.getAmount());
								ItemStack itemStack7 = slot4.takeStack(n);
								itemStack3x.addAmount(n);
								if (itemStack7.isEmpty()) {
									slot4.setStack(ItemStack.EMPTY);
								}

								slot4.onTakeItem(playerEntity, itemStack7);
							}
						}
					}
				}
			}

			this.sendContentUpdates();
		}

		return itemStack;
	}

	public static boolean canStacksCombine(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(itemStack, itemStack2);
	}

	public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
		return true;
	}

	public void close(PlayerEntity playerEntity) {
		PlayerInventory playerInventory = playerEntity.inventory;
		if (!playerInventory.getCursorStack().isEmpty()) {
			playerEntity.dropItem(playerInventory.getCursorStack(), false);
			playerInventory.setCursorStack(ItemStack.EMPTY);
		}
	}

	protected void dropInventory(PlayerEntity playerEntity, World world, Inventory inventory) {
		if (!playerEntity.isValid() || playerEntity instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerEntity).method_14239()) {
			for (int i = 0; i < inventory.getInvSize(); i++) {
				playerEntity.dropItem(inventory.removeInvStack(i), false);
			}
		} else {
			for (int i = 0; i < inventory.getInvSize(); i++) {
				playerEntity.inventory.method_7398(world, inventory.removeInvStack(i));
			}
		}
	}

	public void onContentChanged(Inventory inventory) {
		this.sendContentUpdates();
	}

	public void setStackInSlot(int i, ItemStack itemStack) {
		this.getSlot(i).setStack(itemStack);
	}

	@Environment(EnvType.CLIENT)
	public void updateSlotStacks(List<ItemStack> list) {
		for (int i = 0; i < list.size(); i++) {
			this.getSlot(i).setStack((ItemStack)list.get(i));
		}
	}

	public void setProperty(int i, int j) {
		((Property)this.properties.get(i)).set(j);
	}

	@Environment(EnvType.CLIENT)
	public short getNextActionId(PlayerInventory playerInventory) {
		this.actionId++;
		return this.actionId;
	}

	public boolean isRestricted(PlayerEntity playerEntity) {
		return !this.restrictedPlayers.contains(playerEntity);
	}

	public void setPlayerRestriction(PlayerEntity playerEntity, boolean bl) {
		if (bl) {
			this.restrictedPlayers.remove(playerEntity);
		} else {
			this.restrictedPlayers.add(playerEntity);
		}
	}

	public abstract boolean canUse(PlayerEntity playerEntity);

	protected boolean insertItem(ItemStack itemStack, int i, int j, boolean bl) {
		boolean bl2 = false;
		int k = i;
		if (bl) {
			k = j - 1;
		}

		if (itemStack.canStack()) {
			while (!itemStack.isEmpty() && (bl ? k >= i : k < j)) {
				Slot slot = (Slot)this.slotList.get(k);
				ItemStack itemStack2 = slot.getStack();
				if (!itemStack2.isEmpty() && canStacksCombine(itemStack, itemStack2)) {
					int l = itemStack2.getAmount() + itemStack.getAmount();
					if (l <= itemStack.getMaxAmount()) {
						itemStack.setAmount(0);
						itemStack2.setAmount(l);
						slot.markDirty();
						bl2 = true;
					} else if (itemStack2.getAmount() < itemStack.getMaxAmount()) {
						itemStack.subtractAmount(itemStack.getMaxAmount() - itemStack2.getAmount());
						itemStack2.setAmount(itemStack.getMaxAmount());
						slot.markDirty();
						bl2 = true;
					}
				}

				if (bl) {
					k--;
				} else {
					k++;
				}
			}
		}

		if (!itemStack.isEmpty()) {
			if (bl) {
				k = j - 1;
			} else {
				k = i;
			}

			while (bl ? k >= i : k < j) {
				Slot slotx = (Slot)this.slotList.get(k);
				ItemStack itemStack2x = slotx.getStack();
				if (itemStack2x.isEmpty() && slotx.canInsert(itemStack)) {
					if (itemStack.getAmount() > slotx.getMaxStackAmount()) {
						slotx.setStack(itemStack.split(slotx.getMaxStackAmount()));
					} else {
						slotx.setStack(itemStack.split(itemStack.getAmount()));
					}

					slotx.markDirty();
					bl2 = true;
					break;
				}

				if (bl) {
					k--;
				} else {
					k++;
				}
			}
		}

		return bl2;
	}

	public static int unpackQuickCraftStage(int i) {
		return i >> 2 & 3;
	}

	public static int unpackButtonId(int i) {
		return i & 3;
	}

	@Environment(EnvType.CLIENT)
	public static int packClickData(int i, int j) {
		return i & 3 | (j & 3) << 2;
	}

	public static boolean shouldQuickCraftContinue(int i, PlayerEntity playerEntity) {
		if (i == 0) {
			return true;
		} else {
			return i == 1 ? true : i == 2 && playerEntity.abilities.creativeMode;
		}
	}

	protected void endQuickCraft() {
		this.quickCraftButton = 0;
		this.quickCraftSlots.clear();
	}

	public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack itemStack, boolean bl) {
		boolean bl2 = slot == null || !slot.hasStack();
		return !bl2 && itemStack.isEqualIgnoreTags(slot.getStack()) && ItemStack.areTagsEqual(slot.getStack(), itemStack)
			? slot.getStack().getAmount() + (bl ? 0 : itemStack.getAmount()) <= itemStack.getMaxAmount()
			: bl2;
	}

	public static void calculateStackSize(Set<Slot> set, int i, ItemStack itemStack, int j) {
		switch (i) {
			case 0:
				itemStack.setAmount(MathHelper.floor((float)itemStack.getAmount() / (float)set.size()));
				break;
			case 1:
				itemStack.setAmount(1);
				break;
			case 2:
				itemStack.setAmount(itemStack.getItem().getMaxAmount());
		}

		itemStack.addAmount(j);
	}

	public boolean canInsertIntoSlot(Slot slot) {
		return true;
	}

	public static int calculateComparatorOutput(@Nullable BlockEntity blockEntity) {
		return blockEntity instanceof Inventory ? calculateComparatorOutput((Inventory)blockEntity) : 0;
	}

	public static int calculateComparatorOutput(@Nullable Inventory inventory) {
		if (inventory == null) {
			return 0;
		} else {
			int i = 0;
			float f = 0.0F;

			for (int j = 0; j < inventory.getInvSize(); j++) {
				ItemStack itemStack = inventory.getInvStack(j);
				if (!itemStack.isEmpty()) {
					f += (float)itemStack.getAmount() / (float)Math.min(inventory.getInvMaxStackAmount(), itemStack.getMaxAmount());
					i++;
				}
			}

			f /= (float)inventory.getInvSize();
			return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
		}
	}
}
