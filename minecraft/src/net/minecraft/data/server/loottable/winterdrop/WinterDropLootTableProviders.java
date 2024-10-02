package net.minecraft.data.server.loottable.winterdrop;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.loottable.LootTableProvider;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryWrapper;

public class WinterDropLootTableProviders {
	public static LootTableProvider createWinterDropProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		return new LootTableProvider(
			output, Set.of(), List.of(new LootTableProvider.LootTypeGenerator(WinterDropBlockLootTableGenerator::new, LootContextTypes.BLOCK)), registriesFuture
		);
	}
}
