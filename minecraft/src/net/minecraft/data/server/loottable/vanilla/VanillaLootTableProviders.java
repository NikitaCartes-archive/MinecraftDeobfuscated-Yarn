package net.minecraft.data.server.loottable.vanilla;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.loottable.LootTableProvider;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryWrapper;

public class VanillaLootTableProviders {
	public static LootTableProvider createVanillaProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		return new LootTableProvider(
			output,
			LootTables.getAll(),
			List.of(
				new LootTableProvider.LootTypeGenerator(VanillaFishingLootTableGenerator::new, LootContextTypes.FISHING),
				new LootTableProvider.LootTypeGenerator(VanillaChestLootTableGenerator::new, LootContextTypes.CHEST),
				new LootTableProvider.LootTypeGenerator(VanillaEntityLootTableGenerator::new, LootContextTypes.ENTITY),
				new LootTableProvider.LootTypeGenerator(VanillaEquipmentLootTableGenerator::new, LootContextTypes.EQUIPMENT),
				new LootTableProvider.LootTypeGenerator(VanillaBlockLootTableGenerator::new, LootContextTypes.BLOCK),
				new LootTableProvider.LootTypeGenerator(VanillaBarterLootTableGenerator::new, LootContextTypes.BARTER),
				new LootTableProvider.LootTypeGenerator(VanillaGiftLootTableGenerator::new, LootContextTypes.GIFT),
				new LootTableProvider.LootTypeGenerator(VanillaArchaeologyLootTableGenerator::new, LootContextTypes.ARCHAEOLOGY),
				new LootTableProvider.LootTypeGenerator(VanillaShearingLootTableGenerator::new, LootContextTypes.SHEARING)
			),
			registryLookupFuture
		);
	}
}
