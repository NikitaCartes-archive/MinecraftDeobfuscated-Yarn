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

	public DataGenerator(Path output, Collection<Path> inputs) {
		this.output = output;
		this.inputs = inputs;
	}

	public Collection<Path> getInputs() {
		return this.inputs;
	}

	public Path getOutput() {
		return this.output;
	}

	public void run() throws IOException {
		DataCache dataCache = new DataCache(this.output, "cache");
		dataCache.ignore(this.getOutput().resolve("version.json"));
		Stopwatch stopwatch = Stopwatch.createStarted();
		Stopwatch stopwatch2 = Stopwatch.createUnstarted();

		for (DataProvider dataProvider : this.providers) {
			LOGGER.info("Starting provider: {}", dataProvider.getName());
			stopwatch2.start();
			dataProvider.run(dataCache);
			stopwatch2.stop();
			LOGGER.info("{} finished after {} ms", dataProvider.getName(), stopwatch2.elapsed(TimeUnit.MILLISECONDS));
			stopwatch2.reset();
		}

		LOGGER.info("All providers took: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
		dataCache.write();
	}

	public void addProvider(DataProvider provider) {
		this.providers.add(provider);
	}

	static {
		Bootstrap.initialize();
	}
}
