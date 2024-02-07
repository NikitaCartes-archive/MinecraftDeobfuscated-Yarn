package net.minecraft.data.server.loottable.onetwentyone;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.loottable.LootTableProvider;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryWrapper;

public class OneTwentyOneLootTableProviders {
	public static LootTableProvider createOneTwentyOneProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		return new LootTableProvider(
			output,
			Set.of(),
			List.of(
				new LootTableProvider.LootTypeGenerator(OneTwentyOneBlockLootTableGenerator::new, LootContextTypes.BLOCK),
				new LootTableProvider.LootTypeGenerator(OneTwentyOneChestLootTableGenerator::new, LootContextTypes.CHEST),
				new LootTableProvider.LootTypeGenerator(OneTwentyOneEntityLootTableGenerator::new, LootContextTypes.ENTITY)
			),
			registryLookupFuture
		);
	}
}
