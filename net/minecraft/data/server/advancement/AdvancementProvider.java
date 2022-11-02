/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.advancement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.util.registry.RegistryWrapper;

public class AdvancementProvider
implements DataProvider {
    private final DataOutput.PathResolver pathResolver;
    private final List<AdvancementTabGenerator> tabGenerators;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public AdvancementProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture, List<AdvancementTabGenerator> tabGenerators) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "advancements");
        this.tabGenerators = tabGenerators;
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.registryLookupFuture.thenCompose(lookup -> {
            HashSet set = new HashSet();
            ArrayList list = new ArrayList();
            Consumer<Advancement> consumer = advancement -> {
                if (!set.add(advancement.getId())) {
                    throw new IllegalStateException("Duplicate advancement " + advancement.getId());
                }
                Path path = this.pathResolver.resolveJson(advancement.getId());
                list.add(DataProvider.writeToPath(writer, advancement.createTask().toJson(), path));
            };
            for (AdvancementTabGenerator advancementTabGenerator : this.tabGenerators) {
                advancementTabGenerator.accept((RegistryWrapper.WrapperLookup)lookup, consumer);
            }
            return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public final String getName() {
        return "Advancements";
    }
}

