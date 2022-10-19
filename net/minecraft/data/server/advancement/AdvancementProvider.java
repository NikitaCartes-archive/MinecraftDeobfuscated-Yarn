/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.advancement;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import org.slf4j.Logger;

public class AdvancementProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;
    private final DataOutput.PathResolver pathResolver;
    private final List<AdvancementTabGenerator> tabGenerators;

    public AdvancementProvider(String name, DataOutput output, List<AdvancementTabGenerator> tabGenerators) {
        this.name = name;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "advancements");
        this.tabGenerators = tabGenerators;
    }

    @Override
    public void run(DataWriter writer) {
        HashSet set = new HashSet();
        Consumer<Advancement> consumer = advancement -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }
            Path path = this.pathResolver.resolveJson(advancement.getId());
            try {
                DataProvider.writeToPath(writer, advancement.createTask().toJson(), path);
            } catch (IOException iOException) {
                LOGGER.error("Couldn't save advancement {}", (Object)path, (Object)iOException);
            }
        };
        for (AdvancementTabGenerator advancementTabGenerator : this.tabGenerators) {
            advancementTabGenerator.accept(consumer);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
}

