package net.minecraft.predicate.item;

import net.minecraft.component.DataComponentType;
import net.minecraft.item.ItemStack;

public interface ComponentSubPredicate<T> extends ItemSubPredicate {
	@Override
	default boolean test(ItemStack stack) {
		T object = stack.get(this.getComponentType());
		return object != null && this.test(stack, object);
	}

	DataComponentType<T> getComponentType();

	boolean test(ItemStack stack, T component);
}
