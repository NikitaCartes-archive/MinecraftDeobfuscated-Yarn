/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.loottable;

import java.util.List;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.loottable.BarterLootTableGenerator;
import net.minecraft.data.server.loottable.ChestLootTableGenerator;
import net.minecraft.data.server.loottable.FishingLootTableGenerator;
import net.minecraft.data.server.loottable.GiftLootTableGenerator;
import net.minecraft.data.server.loottable.LootTableProvider;
import net.minecraft.data.server.loottable.VanillaBlockLootTableGenerator;
import net.minecraft.data.server.loottable.VanillaEntityLootTableGenerator;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextTypes;

public class VanillaLootTableProviders {
    public static LootTableProvider createVanillaProvider(DataOutput output) {
        return new LootTableProvider(output, LootTables.getAll(), List.of(new LootTableProvider.LootTypeGenerator(FishingLootTableGenerator::new, LootContextTypes.FISHING), new LootTableProvider.LootTypeGenerator(ChestLootTableGenerator::new, LootContextTypes.CHEST), new LootTableProvider.LootTypeGenerator(VanillaEntityLootTableGenerator::new, LootContextTypes.ENTITY), new LootTableProvider.LootTypeGenerator(VanillaBlockLootTableGenerator::new, LootContextTypes.BLOCK), new LootTableProvider.LootTypeGenerator(BarterLootTableGenerator::new, LootContextTypes.BARTER), new LootTableProvider.LootTypeGenerator(GiftLootTableGenerator::new, LootContextTypes.GIFT)));
    }
}

