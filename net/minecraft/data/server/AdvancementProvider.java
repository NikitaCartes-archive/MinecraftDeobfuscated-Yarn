/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.AdventureTabAdvancementGenerator;
import net.minecraft.data.server.EndTabAdvancementGenerator;
import net.minecraft.data.server.HusbandryTabAdvancementGenerator;
import net.minecraft.data.server.NetherTabAdvancementGenerator;
import net.minecraft.data.server.StoryTabAdvancementGenerator;
import org.slf4j.Logger;

public class AdvancementProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator root;
    private final List<Consumer<Consumer<Advancement>>> tabGenerators = ImmutableList.of(new EndTabAdvancementGenerator(), new HusbandryTabAdvancementGenerator(), new AdventureTabAdvancementGenerator(), new NetherTabAdvancementGenerator(), new StoryTabAdvancementGenerator());

    public AdvancementProvider(DataGenerator root) {
        this.root = root;
    }

    @Override
    public void run(DataCache cache) {
        Path path = this.root.getOutput();
        HashSet set = Sets.newHashSet();
        Consumer<Advancement> consumer = advancement -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }
            Path path2 = AdvancementProvider.getOutput(path, advancement);
            try {
                DataProvider.writeToPath(GSON, cache, advancement.createTask().toJson(), path2);
            } catch (IOException iOException) {
                LOGGER.error("Couldn't save advancement {}", (Object)path2, (Object)iOException);
            }
        };
        for (Consumer<Consumer<Advancement>> consumer2 : this.tabGenerators) {
            consumer2.accept(consumer);
        }
    }

    private static Path getOutput(Path rootOutput, Advancement advancement) {
        return rootOutput.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Advancements";
    }
}

