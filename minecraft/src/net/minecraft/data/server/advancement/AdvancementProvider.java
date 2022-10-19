package net.minecraft.data.server.advancement;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class AdvancementProvider implements DataProvider {
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
		Set<Identifier> set = new HashSet();
		Consumer<Advancement> consumer = advancement -> {
			if (!set.add(advancement.getId())) {
				throw new IllegalStateException("Duplicate advancement " + advancement.getId());
			} else {
				Path path = this.pathResolver.resolveJson(advancement.getId());

				try {
					DataProvider.writeToPath(writer, advancement.createTask().toJson(), path);
				} catch (IOException var6) {
					LOGGER.error("Couldn't save advancement {}", path, var6);
				}
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
