/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.BarterLootTableGenerator;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.data.server.ChestLootTableGenerator;
import net.minecraft.data.server.EntityLootTableGenerator;
import net.minecraft.data.server.FishingLootTableGenerator;
import net.minecraft.data.server.GiftLootTableGenerator;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTablesProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator root;
    private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> lootTypeGenerators = ImmutableList.of(Pair.of(FishingLootTableGenerator::new, LootContextTypes.FISHING), Pair.of(ChestLootTableGenerator::new, LootContextTypes.CHEST), Pair.of(EntityLootTableGenerator::new, LootContextTypes.ENTITY), Pair.of(BlockLootTableGenerator::new, LootContextTypes.BLOCK), Pair.of(BarterLootTableGenerator::new, LootContextTypes.BARTER), Pair.of(GiftLootTableGenerator::new, LootContextTypes.GIFT));

    public LootTablesProvider(DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }

    @Override
    public void run(DataCache dataCache) {
        Path path = this.root.getOutput();
        HashMap<Identifier, LootTable> map = Maps.newHashMap();
        this.lootTypeGenerators.forEach(pair -> ((Consumer)((Supplier)pair.getFirst()).get()).accept((identifier, builder) -> {
            if (map.put((Identifier)identifier, builder.withType((LootContextType)pair.getSecond()).create()) != null) {
                throw new IllegalStateException("Duplicate loot table " + identifier);
            }
        }));
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, identifier -> null, map::get);
        Sets.SetView<Identifier> set = Sets.difference(LootTables.getAll(), map.keySet());
        for (Identifier identifier2 : set) {
            lootTableReporter.report("Missing built-in table: " + identifier2);
        }
        map.forEach((identifier, lootTable) -> LootManager.check(lootTableReporter, identifier, lootTable));
        Multimap<String, String> multimap = lootTableReporter.getMessages();
        if (!multimap.isEmpty()) {
            multimap.forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }
        map.forEach((identifier, lootTable) -> {
            Path path2 = LootTablesProvider.getOutput(path, identifier);
            try {
                DataProvider.writeToPath(GSON, dataCache, LootManager.toJson(lootTable), path2);
            } catch (IOException iOException) {
                LOGGER.error("Couldn't save loot table {}", (Object)path2, (Object)iOException);
            }
        });
    }

    private static Path getOutput(Path rootOutput, Identifier lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/loot_tables/" + lootTableId.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "LootTables";
    }
}

