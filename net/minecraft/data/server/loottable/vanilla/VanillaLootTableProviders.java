/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.loottable.vanilla;

import java.util.List;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.loottable.LootTableProvider;
import net.minecraft.data.server.loottable.vanilla.VanillaBarterLootTableGenerator;
import net.minecraft.data.server.loottable.vanilla.VanillaBlockLootTableGenerator;
import net.minecraft.data.server.loottable.vanilla.VanillaChestLootTableGenerator;
import net.minecraft.data.server.loottable.vanilla.VanillaEntityLootTableGenerator;
import net.minecraft.data.server.loottable.vanilla.VanillaFishingLootTableGenerator;
import net.minecraft.data.server.loottable.vanilla.VanillaGiftLootTableGenerator;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextTypes;

public class VanillaLootTableProviders {
    public static LootTableProvider createVanillaProvider(DataOutput output) {
        return new LootTableProvider(output, LootTables.getAll(), List.of(new LootTableProvider.LootTypeGenerator(VanillaFishingLootTableGenerator::new, LootContextTypes.FISHING), new LootTableProvider.LootTypeGenerator(VanillaChestLootTableGenerator::new, LootContextTypes.CHEST), new LootTableProvider.LootTypeGenerator(VanillaEntityLootTableGenerator::new, LootContextTypes.ENTITY), new LootTableProvider.LootTypeGenerator(VanillaBlockLootTableGenerator::new, LootContextTypes.BLOCK), new LootTableProvider.LootTypeGenerator(VanillaBarterLootTableGenerator::new, LootContextTypes.BARTER), new LootTableProvider.LootTypeGenerator(VanillaGiftLootTableGenerator::new, LootContextTypes.GIFT)));
    }
}

