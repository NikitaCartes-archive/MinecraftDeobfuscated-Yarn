package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.minecraft.Bootstrap;
import net.minecraft.GameVersion;
import org.slf4j.Logger;

public class DataGenerator {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Path outputPath;
	private final DataOutput output;
	final Set<String> providerNames = new HashSet();
	final Map<String, DataProvider> runningProviders = new LinkedHashMap();
	private final GameVersion gameVersion;
	private final boolean ignoreCache;

	public DataGenerator(Path outputPath, GameVersion gameVersion, boolean ignoreCache) {
		this.outputPath = outputPath;
		this.output = new DataOutput(this.outputPath);
		this.gameVersion = gameVersion;
		this.ignoreCache = ignoreCache;
	}

	public void run() throws IOException {
		DataCache dataCache = new DataCache(this.outputPath, this.providerNames, this.gameVersion);
		Stopwatch stopwatch = Stopwatch.createStarted();
		Stopwatch stopwatch2 = Stopwatch.createUnstarted();
		this.runningProviders.forEach((name, provider) -> {
			if (!this.ignoreCache && !dataCache.isVersionDifferent(name)) {
				LOGGER.debug("Generator {} already run for version {}", name, this.gameVersion.getName());
			} else {
				LOGGER.info("Starting provider: {}", name);
				stopwatch2.start();
				dataCache.store((DataCache.RunResult)dataCache.run(name, provider::run).join());
				stopwatch2.stop();
				LOGGER.info("{} finished after {} ms", name, stopwatch2.elapsed(TimeUnit.MILLISECONDS));
				stopwatch2.reset();
			}
		});
		LOGGER.info("All providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
		dataCache.write();
	}

	public DataGenerator.Pack createVanillaPack(boolean shouldRun) {
		return new DataGenerator.Pack(shouldRun, "vanilla", this.output);
	}

	public DataGenerator.Pack createVanillaSubPack(boolean shouldRun, String packName) {
		Path path = this.output.resolvePath(DataOutput.OutputType.DATA_PACK).resolve("minecraft").resolve("datapacks").resolve(packName);
		return new DataGenerator.Pack(shouldRun, packName, new DataOutput(path));
	}

	static {
		Bootstrap.initialize();
	}

	public class Pack {
		private final boolean shouldRun;
		private final String packName;
		private final DataOutput output;

		Pack(final boolean shouldRun, final String name, final DataOutput output) {
			this.shouldRun = shouldRun;
			this.packName = name;
			this.output = output;
		}

		public <T extends DataProvider> T addProvider(DataProvider.Factory<T> factory) {
			T dataProvider = factory.create(this.output);
			String string = this.packName + "/" + dataProvider.getName();
			if (!DataGenerator.this.providerNames.add(string)) {
				throw new IllegalStateException("Duplicate provider: " + string);
			} else {
				if (this.shouldRun) {
					DataGenerator.this.runningProviders.put(string, dataProvider);
				}

				return dataProvider;
			}
		}
	}
}
