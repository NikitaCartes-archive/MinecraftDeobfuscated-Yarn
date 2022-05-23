package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class AdvancementProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataGenerator.PathResolver pathResolver;
	private final List<Consumer<Consumer<Advancement>>> tabGenerators = ImmutableList.of(
		new EndTabAdvancementGenerator(),
		new HusbandryTabAdvancementGenerator(),
		new AdventureTabAdvancementGenerator(),
		new NetherTabAdvancementGenerator(),
		new StoryTabAdvancementGenerator()
	);

	public AdvancementProvider(DataGenerator root) {
		this.pathResolver = root.createPathResolver(DataGenerator.OutputType.DATA_PACK, "advancements");
	}

	@Override
	public void run(DataWriter writer) {
		Set<Identifier> set = Sets.<Identifier>newHashSet();
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

		for (Consumer<Consumer<Advancement>> consumer2 : this.tabGenerators) {
			consumer2.accept(consumer);
		}
	}

	@Override
	public String getName() {
		return "Advancements";
	}
}
