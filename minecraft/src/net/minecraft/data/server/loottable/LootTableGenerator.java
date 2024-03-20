package net.minecraft.data.server.loottable;

import java.util.function.BiConsumer;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

@FunctionalInterface
public interface LootTableGenerator {
	void accept(RegistryWrapper.WrapperLookup registryLookup, BiConsumer<RegistryKey<LootTable>, LootTable.Builder> consumer);
}
