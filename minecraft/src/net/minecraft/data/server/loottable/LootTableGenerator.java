package net.minecraft.data.server.loottable;

import java.util.function.BiConsumer;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface LootTableGenerator {
	void accept(RegistryWrapper.WrapperLookup registryLookup, BiConsumer<Identifier, LootTable.Builder> consumer);
}
