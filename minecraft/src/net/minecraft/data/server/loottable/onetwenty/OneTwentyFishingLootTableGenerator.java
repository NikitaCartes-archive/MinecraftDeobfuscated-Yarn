package net.minecraft.data.server.loottable.onetwenty;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.data.server.loottable.vanilla.VanillaFishingLootTableGenerator;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;

public class OneTwentyFishingLootTableGenerator implements LootTableGenerator {
	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
		exporter.accept(LootTables.FISHING_FISH_GAMEPLAY, VanillaFishingLootTableGenerator.createFishTableBuilder());
	}
}
