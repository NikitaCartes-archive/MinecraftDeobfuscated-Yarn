/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.SimpleModelSupplier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModelProvider
implements DataProvider {
    private final DataOutput.PathResolver blockstatesPathResolver;
    private final DataOutput.PathResolver modelsPathResolver;

    public ModelProvider(DataOutput output) {
        this.blockstatesPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "blockstates");
        this.modelsPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        HashMap map = Maps.newHashMap();
        Consumer<BlockStateSupplier> consumer = blockStateSupplier -> {
            Block block = blockStateSupplier.getBlock();
            BlockStateSupplier blockStateSupplier2 = map.put(block, blockStateSupplier);
            if (blockStateSupplier2 != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        };
        HashMap map2 = Maps.newHashMap();
        HashSet set = Sets.newHashSet();
        BiConsumer<Identifier, Supplier<JsonElement>> biConsumer = (id, jsonSupplier) -> {
            Supplier supplier = map2.put(id, jsonSupplier);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + id);
            }
        };
        Consumer<Item> consumer2 = set::add;
        new BlockStateModelGenerator(consumer, biConsumer, consumer2).register();
        new ItemModelGenerator(biConsumer).register();
        List<Block> list = Registries.BLOCK.stream().filter(block -> !map.containsKey(block)).toList();
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + list);
        }
        Registries.BLOCK.forEach(block -> {
            Item item = Item.BLOCK_ITEMS.get(block);
            if (item != null) {
                if (set.contains(item)) {
                    return;
                }
                Identifier identifier = ModelIds.getItemModelId(item);
                if (!map2.containsKey(identifier)) {
                    map2.put(identifier, new SimpleModelSupplier(ModelIds.getBlockModelId(block)));
                }
            }
        });
        CompletableFuture[] completableFutureArray = new CompletableFuture[2];
        completableFutureArray[0] = this.writeJsons(writer, map, block -> this.blockstatesPathResolver.resolveJson(block.getRegistryEntry().registryKey().getValue()));
        completableFutureArray[1] = this.writeJsons(writer, map2, this.modelsPathResolver::resolveJson);
        return CompletableFuture.allOf(completableFutureArray);
    }

    private <T> CompletableFuture<?> writeJsons(DataWriter cache, Map<T, ? extends Supplier<JsonElement>> models, Function<T, Path> pathGetter) {
        return CompletableFuture.allOf((CompletableFuture[])models.entrySet().stream().map(entry -> {
            Path path = (Path)pathGetter.apply(entry.getKey());
            JsonElement jsonElement = (JsonElement)((Supplier)entry.getValue()).get();
            return DataProvider.writeToPath(cache, jsonElement, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public final String getName() {
        return "Model Definitions";
    }
}

