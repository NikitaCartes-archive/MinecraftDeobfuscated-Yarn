/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.loottable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class LootTableProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;
    private final DataOutput.PathResolver pathResolver;
    private final Set<Identifier> lootTableIds;
    private final List<LootTypeGenerator> lootTypeGenerators;

    public LootTableProvider(String name, DataOutput output, Set<Identifier> lootTableIds, List<LootTypeGenerator> lootTableGenerators) {
        this.name = name;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "loot_tables");
        this.lootTypeGenerators = lootTableGenerators;
        this.lootTableIds = lootTableIds;
    }

    @Override
    public void run(DataWriter writer) {
        HashMap<Identifier, LootTable> map = Maps.newHashMap();
        this.lootTypeGenerators.forEach(lootTypeGenerator -> lootTypeGenerator.provider().get().accept((id, builder) -> {
            if (map.put((Identifier)id, builder.type(lootTypeGenerator.paramSet).build()) != null) {
                throw new IllegalStateException("Duplicate loot table " + id);
            }
        }));
        LootTableReporter lootTableReporter = new LootTableReporter(LootContextTypes.GENERIC, id -> null, map::get);
        Sets.SetView<Identifier> set = Sets.difference(this.lootTableIds, map.keySet());
        for (Identifier identifier : set) {
            lootTableReporter.report("Missing built-in table: " + identifier);
        }
        map.forEach((id, table) -> LootManager.validate(lootTableReporter, id, table));
        Multimap<String, String> multimap = lootTableReporter.getMessages();
        if (!multimap.isEmpty()) {
            multimap.forEach((name, message) -> LOGGER.warn("Found validation problem in {}: {}", name, message));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }
        map.forEach((id, table) -> {
            Path path = this.pathResolver.resolveJson((Identifier)id);
            try {
                DataProvider.writeToPath(writer, LootManager.toJson(table), path);
            } catch (IOException iOException) {
                LOGGER.error("Couldn't save loot table {}", (Object)path, (Object)iOException);
            }
        });
    }

    @Override
    public String getName() {
        return this.name;
    }

    public record LootTypeGenerator(Supplier<LootTableGenerator> provider, LootContextType paramSet) {
    }
}

