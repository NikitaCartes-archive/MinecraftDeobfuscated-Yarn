package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Collection<Path> inputs;
	private final Path output;
	private final List<DataProvider> providers = Lists.<DataProvider>newArrayList();

	public DataGenerator(Path path, Collection<Path> collection) {
		this.output = path;
		this.inputs = collection;
	}

	public Collection<Path> getInputs() {
		return this.inputs;
	}

	public Path getOutput() {
		return this.output;
	}

	public void run() throws IOException {
		DataCache dataCache = new DataCache(this.output, "cache");
		dataCache.method_16674(this.getOutput().resolve("version.json"));
		Stopwatch stopwatch = Stopwatch.createUnstarted();

		for (DataProvider dataProvider : this.providers) {
			LOGGER.info("Starting provider: {}", dataProvider.getName());
			stopwatch.start();
			dataProvider.run(dataCache);
			stopwatch.stop();
			LOGGER.info("{} finished after {} ms", dataProvider.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
			stopwatch.reset();
		}

		dataCache.write();
	}

	public void install(DataProvider dataProvider) {
		this.providers.add(dataProvider);
	}

	static {
		Bootstrap.initialize();
	}
}
