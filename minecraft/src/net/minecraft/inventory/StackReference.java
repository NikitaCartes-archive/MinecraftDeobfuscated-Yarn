package net.minecraft.inventory;

import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * Represents a reference to a stack that supports getting and setting.
 * Often for command access. Usually obtained from entities.
 * 
 * <p>Screen handlers also use stack references to pass a mutable cursor
 * stack to some methods.
 * 
 * @see net.minecraft.entity.Entity#getStackReference(int)
 */
public interface StackReference {
	/**
	 * An immutable empty stack reference.
	 */
	StackReference EMPTY = new StackReference() {
		@Override
		public ItemStack get() {
			return ItemStack.EMPTY;
		}

		@Override
		public boolean set(ItemStack stack) {
			return false;
		}
	};

	/**
	 * Creates a stack reference backed by an index within an inventory and guarded
	 * by a condition for setting stacks into the inventory.
	 * 
	 * @param stackFilter the condition to guard stack setting
	 */
	static StackReference of(Inventory inventory, int index, Predicate<ItemStack> stackFilter) {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return inventory.getStack(index);
			}

			@Override
			public boolean set(ItemStack stack) {
				if (!stackFilter.test(stack)) {
					return false;
				} else {
					inventory.setStack(index, stack);
					return true;
				}
			}
		};
	}

	/**
	 * Creates a stack reference backed by an index within an inventory.
	 */
	static StackReference of(Inventory inventory, int index) {
		return of(inventory, index, stack -> true);
	}

	/**
	 * Creates a stack reference backed by an equipment slot of a living entity and
	 * guarded by a condition for setting stacks into the inventory.
	 * 
	 * @param filter the condition to guard stack setting
	 */
	static StackReference of(LivingEntity entity, EquipmentSlot slot, Predicate<ItemStack> filter) {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return entity.getEquippedStack(slot);
			}

			@Override
			public boolean set(ItemStack stack) {
				if (!filter.test(stack)) {
					return false;
				} else {
					entity.equipStack(slot, stack);
					return true;
				}
			}
		};
	}

	/**
	 * Creates a stack reference backed by an equipment slot of a living entity with
	 * no filter, allowing direct manipulation of the equipment slot.
	 */
	static StackReference of(LivingEntity entity, EquipmentSlot slot) {
		return of(entity, slot, stack -> true);
	}

	/**
	 * Gets the current item stack.
	 */
	ItemStack get();

	/**
	 * Sets the {@code stack}.
	 * 
	 * @return {@code true} if the setting is successful, {@code false} if rejected
	 * 
	 * @param stack the item stack to set
	 */
	boolean set(ItemStack stack);
}
