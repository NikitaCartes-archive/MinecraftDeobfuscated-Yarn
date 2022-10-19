package net.minecraft.data.server.loottable;

import java.util.List;
import java.util.Set;
import net.minecraft.data.DataOutput;
import net.minecraft.loot.context.LootContextTypes;

public class OneTwentyLootTableProviders {
	public static LootTableProvider createOneTwentyProvider(DataOutput output) {
		return new LootTableProvider(
			"1.20 Loot Tables", output, Set.of(), List.of(new LootTableProvider.LootTypeGenerator(OneTwentyBlockLootTableGenerator::new, LootContextTypes.BLOCK))
		);
	}
}
