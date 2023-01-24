package net.minecraft.data.server.loottable.onetwenty;

import java.util.List;
import java.util.Set;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.loottable.LootTableProvider;
import net.minecraft.loot.context.LootContextTypes;

public class OneTwentyLootTableProviders {
	public static LootTableProvider createOneTwentyProvider(DataOutput output) {
		return new LootTableProvider(
			output,
			Set.of(),
			List.of(
				new LootTableProvider.LootTypeGenerator(OneTwentyFishingLootTableGenerator::new, LootContextTypes.FISHING),
				new LootTableProvider.LootTypeGenerator(OneTwentyBlockLootTableGenerator::new, LootContextTypes.BLOCK),
				new LootTableProvider.LootTypeGenerator(OneTwentyChestLootTableGenerator::new, LootContextTypes.CHEST),
				new LootTableProvider.LootTypeGenerator(OneTwentyEntityLootTableGenerator::new, LootContextTypes.ENTITY)
			)
		);
	}
}
