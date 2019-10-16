package net.minecraft.loot;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

public interface LootChoice {
	int getWeight(float f);

	void drop(Consumer<ItemStack> consumer, LootContext lootContext);
}
