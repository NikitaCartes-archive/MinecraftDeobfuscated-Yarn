package net.minecraft.world.loot;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.context.LootContext;

public interface LootChoice {
	int getWeight(float f);

	void drop(Consumer<ItemStack> consumer, LootContext lootContext);
}
