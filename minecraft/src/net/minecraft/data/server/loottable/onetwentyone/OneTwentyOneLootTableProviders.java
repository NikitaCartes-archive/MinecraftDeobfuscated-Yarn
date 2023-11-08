package net.minecraft.data.server.loottable.onetwentyone;

import java.util.List;
import java.util.Set;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.loottable.LootTableProvider;
import net.minecraft.loot.context.LootContextTypes;

public class OneTwentyOneLootTableProviders {
	public static LootTableProvider createOneTwentyOneProvider(DataOutput output) {
		return new LootTableProvider(
			output,
			Set.of(),
			List.of(
				new LootTableProvider.LootTypeGenerator(OneTwentyOneBlockLootTableGenerator::new, LootContextTypes.BLOCK),
				new LootTableProvider.LootTypeGenerator(OneTwentyOneChestLootTableGenerator::new, LootContextTypes.CHEST)
			)
		);
	}
}
