package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

/**
 * A generic implementation of {@link Inventory}. This is used in a number of
 * places, mostly:
 * 
 * <ul>
 * <li>To store the input of a {@link net.minecraft.screen.ScreenHandler} while
 * it is open. The inventory is stored as a field, and the screen handler will have
 * a slot backed by that inventory.</li>
 * <li>When defining the clientside constructor for a {@link
 * net.minecraft.screen.ScreenHandler} subclass. The contents of the inventory will
 * then be automatically synced from the serverside screen handler, which queries
 * the original inventory.</li>
 * <li>For entities and block entities which do not interact with hoppers and therefore
 * do not need to implement {@link Inventory} themselves.
 * </ul>
 * 
 * <p>Changes to the inventory can be listened to either by subclassing this and
 * overriding {@link #markDirty}, or by using {@link #addListener}.
 */
public class SimpleInventory implements Inventory, RecipeInputProvider {
	private final int size;
	private final DefaultedList<ItemStack> heldStacks;
	@Nullable
	private List<InventoryChangedListener> listeners;

	public SimpleInventory(int size) {
		this.size = size;
		this.heldStacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	public SimpleInventory(ItemStack... items) {
		this.size = items.length;
		this.heldStacks = DefaultedList.copyOf(ItemStack.EMPTY, items);
	}

	/**
	 * Adds a {@code listener} for inventory modifications. If a listener is
	 * added multiple times, it will also be triggered multiple times.
	 */
	public void addListener(InventoryChangedListener listener) {
		if (this.listeners == null) {
			this.listeners = Lists.<InventoryChangedListener>newArrayList();
		}

		this.listeners.add(listener);
	}

	/**
	 * Removes a {@code listener} previously added by {@code #addListener}.
	 * Does nothing when the listener was not found.
	 */
	public void removeListener(InventoryChangedListener listener) {
		if (this.listeners != null) {
			this.listeners.remove(listener);
		}
	}

	@Override
	public ItemStack getStack(int slot) {
		return slot >= 0 && slot < this.heldStacks.size() ? this.heldStacks.get(slot) : ItemStack.EMPTY;
	}

	/**
	 * Clears this inventory and returns all the non-empty stacks in a list.
	 * 
	 * @return the non-empty stacks previously in the inventory
	 */
	public List<ItemStack> clearToList() {
		List<ItemStack> list = (List<ItemStack>)this.heldStacks.stream().filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
		this.clear();
		return list;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack itemStack = Inventories.splitStack(this.heldStacks, slot, amount);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	/**
	 * Searches this inventory for the specified item and removes the given amount from this inventory.
	 * 
	 * @return the stack of removed items
	 */
	public ItemStack removeItem(Item item, int count) {
		ItemStack itemStack = new ItemStack(item, 0);

		for (int i = this.size - 1; i >= 0; i--) {
			ItemStack itemStack2 = this.getStack(i);
			if (itemStack2.getItem().equals(item)) {
				int j = count - itemStack.getCount();
				ItemStack itemStack3 = itemStack2.split(j);
				itemStack.increment(itemStack3.getCount());
				if (itemStack.getCount() == count) {
					break;
				}
			}
		}

		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	/**
	 * Adds {@code stack} to this inventory as much as possible. It is possible
	 * that the item stack gets inserted into a non-empty slot or spread across
	 * several slots, if it can combine with other stack(s) in this inventory.
	 * 
	 * @return the leftover part of the stack, or {@code ItemStack#EMPTY} if the entire
	 * stack fit inside the inventory
	 */
	public ItemStack addStack(ItemStack stack) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			ItemStack itemStack = stack.copy();
			this.addToExistingSlot(itemStack);
			if (itemStack.isEmpty()) {
				return ItemStack.EMPTY;
			} else {
				this.addToNewSlot(itemStack);
				return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
			}
		}
	}

	/**
	 * {@return whether {@code stack} can be inserted into this inventory}
	 */
	public boolean canInsert(ItemStack stack) {
		boolean bl = false;

		for (ItemStack itemStack : this.heldStacks) {
			if (itemStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(itemStack, stack) && itemStack.getCount() < itemStack.getMaxCount()) {
				bl = true;
				break;
			}
		}

		return bl;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack itemStack = this.heldStacks.get(slot);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.heldStacks.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.heldStacks.set(slot, stack);
		stack.capCount(this.getMaxCount(stack));
		this.markDirty();
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.heldStacks) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void markDirty() {
		if (this.listeners != null) {
			for (InventoryChangedListener inventoryChangedListener : this.listeners) {
				inventoryChangedListener.onInventoryChanged(this);
			}
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		this.heldStacks.clear();
		this.markDirty();
	}

	@Override
	public void provideRecipeInputs(RecipeFinder finder) {
		for (ItemStack itemStack : this.heldStacks) {
			finder.addInput(itemStack);
		}
	}

	public String toString() {
		return ((List)this.heldStacks.stream().filter(stack -> !stack.isEmpty()).collect(Collectors.toList())).toString();
	}

	private void addToNewSlot(ItemStack stack) {
		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack = this.getStack(i);
			if (itemStack.isEmpty()) {
				this.setStack(i, stack.copyAndEmpty());
				return;
			}
		}
	}

	private void addToExistingSlot(ItemStack stack) {
		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack = this.getStack(i);
			if (ItemStack.areItemsAndComponentsEqual(itemStack, stack)) {
				this.transfer(stack, itemStack);
				if (stack.isEmpty()) {
					return;
				}
			}
		}
	}

	private void transfer(ItemStack source, ItemStack target) {
		int i = this.getMaxCount(target);
		int j = Math.min(source.getCount(), i - target.getCount());
		if (j > 0) {
			target.increment(j);
			source.decrement(j);
			this.markDirty();
		}
	}

	/**
	 * Reads the item stacks from {@code nbtList}.
	 * 
	 * @see #toNbtList
	 */
	public void readNbtList(NbtList list, RegistryWrapper.WrapperLookup registries) {
		this.clear();

		for (int i = 0; i < list.size(); i++) {
			ItemStack.fromNbt(registries, list.getCompound(i)).ifPresent(this::addStack);
		}
	}

	/**
	 * {@return an NBT list of non-empty {@linkplain ItemStack#encode(RegistryWrapper.WrapperLookup) item stacks}}
	 * 
	 * <p>Unlike {@link Inventories#writeNbt(NbtCompound, DefaultedList, boolean, RegistryWrapper.WrapperLookup)},
	 * this does not serialize the slots.
	 * 
	 * @see #readNbtList
	 */
	public NbtList toNbtList(RegistryWrapper.WrapperLookup registries) {
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.size(); i++) {
			ItemStack itemStack = this.getStack(i);
			if (!itemStack.isEmpty()) {
				nbtList.add(itemStack.toNbt(registries));
			}
		}

		return nbtList;
	}

	public DefaultedList<ItemStack> getHeldStacks() {
		return this.heldStacks;
	}
}
