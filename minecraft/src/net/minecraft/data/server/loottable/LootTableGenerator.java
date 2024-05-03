package net.minecraft.data.server.loottable;

import java.util.function.BiConsumer;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;

@FunctionalInterface
public interface LootTableGenerator {
	void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer);
}
