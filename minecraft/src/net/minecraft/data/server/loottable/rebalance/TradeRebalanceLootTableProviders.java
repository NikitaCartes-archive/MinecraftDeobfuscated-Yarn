package net.minecraft.data.server.loottable.rebalance;

import java.util.List;
import java.util.Set;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.loottable.LootTableProvider;
import net.minecraft.loot.context.LootContextTypes;

public class TradeRebalanceLootTableProviders {
	public static LootTableProvider createTradeRebalanceProvider(DataOutput output) {
		return new LootTableProvider(
			output, Set.of(), List.of(new LootTableProvider.LootTypeGenerator(TradeRebalanceChestLootTableGenerator::new, LootContextTypes.ENTITY))
		);
	}
}
