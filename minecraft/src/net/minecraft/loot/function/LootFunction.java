package net.minecraft.loot.function;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;

public interface LootFunction extends LootContextAware, BiFunction<ItemStack, LootContext, ItemStack> {
	LootFunctionType getType();

	static Consumer<ItemStack> apply(BiFunction<ItemStack, LootContext, ItemStack> itemApplier, Consumer<ItemStack> lootConsumer, LootContext context) {
		return stack -> lootConsumer.accept((ItemStack)itemApplier.apply(stack, context));
	}

	public interface Builder {
		LootFunction build();
	}
}
