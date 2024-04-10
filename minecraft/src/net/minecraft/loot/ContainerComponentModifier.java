package net.minecraft.loot;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.ItemStack;

public interface ContainerComponentModifier<T> {
	DataComponentType<T> getComponentType();

	T getDefault();

	T create(T component, Stream<ItemStack> contents);

	Stream<ItemStack> stream(T component);

	default void apply(ItemStack stack, T component, Stream<ItemStack> contents) {
		T object = stack.getOrDefault(this.getComponentType(), component);
		T object2 = this.create(object, contents);
		stack.set(this.getComponentType(), object2);
	}

	default void apply(ItemStack stack, Stream<ItemStack> contents) {
		this.apply(stack, this.getDefault(), contents);
	}

	default void apply(ItemStack stack, UnaryOperator<ItemStack> contentsOperator) {
		T object = stack.get(this.getComponentType());
		if (object != null) {
			UnaryOperator<ItemStack> unaryOperator = contentStack -> contentStack.isEmpty() ? contentStack : (ItemStack)contentsOperator.apply(contentStack);
			this.apply(stack, this.stream(object).map(unaryOperator));
		}
	}
}
