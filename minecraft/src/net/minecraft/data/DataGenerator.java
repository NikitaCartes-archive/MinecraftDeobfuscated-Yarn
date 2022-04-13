package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.Bootstrap;
import net.minecraft.GameVersion;
import org.slf4j.Logger;

public class DataGenerator {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Collection<Path> inputs;
	private final Path output;
	private final List<DataProvider> providers = Lists.<DataProvider>newArrayList();
	private final List<DataProvider> runningProviders = Lists.<DataProvider>newArrayList();
	private final GameVersion gameVersion;
	private final boolean ignoreCache;

	public DataGenerator(Path output, Collection<Path> inputs, GameVersion gameVersion, boolean ignoreCache) {
		this.output = output;
		this.inputs = inputs;
		this.gameVersion = gameVersion;
		this.ignoreCache = ignoreCache;
	}

	public Collection<Path> getInputs() {
		return this.inputs;
	}

	public Path getOutput() {
		return this.output;
	}

	public void run() throws IOException {
		DataCache dataCache = new DataCache(this.output, this.providers, this.gameVersion);
		Stopwatch stopwatch = Stopwatch.createStarted();
		Stopwatch stopwatch2 = Stopwatch.createUnstarted();

		for (DataProvider dataProvider : this.runningProviders) {
			if (!this.ignoreCache && !dataCache.isVersionDifferent(dataProvider)) {
				LOGGER.debug("Generator {} already run for version {}", dataProvider.getName(), this.gameVersion.getName());
			} else {
				LOGGER.info("Starting provider: {}", dataProvider.getName());
				stopwatch2.start();
				dataProvider.run(dataCache.getOrCreateWriter(dataProvider));
				stopwatch2.stop();
				LOGGER.info("{} finished after {} ms", dataProvider.getName(), stopwatch2.elapsed(TimeUnit.MILLISECONDS));
				stopwatch2.reset();
			}
		}

		LOGGER.info("All providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
		dataCache.write();
	}

	public void addProvider(boolean shouldRun, DataProvider provider) {
		if (shouldRun) {
			this.runningProviders.add(provider);
		}

		this.providers.add(provider);
	}

	static {
		Bootstrap.initialize();
	}
}
