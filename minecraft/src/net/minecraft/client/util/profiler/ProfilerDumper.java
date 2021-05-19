package net.minecraft.client.util.profiler;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.util.profiler.TickTimeTracker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ProfilerDumper {
	public static final Path DEBUG_PROFILING_DIRECTORY = Paths.get("debug/profiling");
	public static final String METRICS_DIRECTORY = "metrics";
	public static final String DEVIATIONS_DIRECTORY = "deviations";
	public static final String FILE_NAME = "profiling.txt";
	private static final Logger LOGGER = LogManager.getLogger();
	public static final FileSystemProvider FILE_SYSTEM_PROVIDER = (FileSystemProvider)FileSystemProvider.installedProviders()
		.stream()
		.filter(fileSystemProvider -> fileSystemProvider.getScheme().equalsIgnoreCase("jar"))
		.findFirst()
		.orElseThrow(() -> new IllegalStateException("No jar file system provider found"));

	public Path createDump(List<Category> categories, List<Sample> deviations, TickTimeTracker timeTracker) {
		try {
			Files.createDirectories(DEBUG_PROFILING_DIRECTORY);
		} catch (IOException var11) {
			throw new UncheckedIOException(var11);
		}

		Path path = DEBUG_PROFILING_DIRECTORY.resolve(new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".tmp");

		try {
			FileSystem fileSystem = FILE_SYSTEM_PROVIDER.newFileSystem(path, ImmutableMap.of("create", "true"));

			try {
				Files.createDirectories(DEBUG_PROFILING_DIRECTORY);
				Path path2 = fileSystem.getPath("/");
				Path path3 = path2.resolve("metrics");

				for (Category category : categories) {
					this.writeCategory(category, path3);
				}

				if (!deviations.isEmpty()) {
					this.writeSamples(deviations, path2.resolve("deviations"));
				}

				this.save(timeTracker, path2);
			} catch (Throwable var12) {
				if (fileSystem != null) {
					try {
						fileSystem.close();
					} catch (Throwable var10) {
						var12.addSuppressed(var10);
					}
				}

				throw var12;
			}

			if (fileSystem != null) {
				fileSystem.close();
			}
		} catch (IOException var13) {
			throw new UncheckedIOException(var13);
		}

		return this.compressAndSave(path);
	}

	private void writeCategory(Category category, Path directory) {
		String string = category.getName();
		List<SamplingRecorder> list = category.getSamplers();
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Expected at least one sampler for category: " + string);
		} else {
			IntSummaryStatistics intSummaryStatistics = (IntSummaryStatistics)list.stream().collect(Collectors.summarizingInt(SamplingRecorder::length));
			if (intSummaryStatistics.getMax() != intSummaryStatistics.getMin()) {
				throw new IllegalStateException(
					String.format("Expected all samples within category %s to contain same amount of samples, got %s", category, intSummaryStatistics)
				);
			} else {
				Path path = directory.resolve(Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + ".csv");
				Writer writer = null;

				try {
					Files.createDirectories(path.getParent());
					writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
					CsvWriter.Header header = CsvWriter.makeHeader();

					for (SamplingRecorder samplingRecorder : list) {
						header.addColumn(samplingRecorder.getMetric().getName());
					}

					CsvWriter csvWriter = header.startBody(writer);

					while (((SamplingRecorder)list.get(0)).canRead()) {
						Double[] doubles = (Double[])list.stream().map(SamplingRecorder::read).toArray(Double[]::new);
						csvWriter.printRow(doubles);
					}

					LOGGER.info("Flushed metrics to {}", path);
				} catch (Exception var14) {
					LOGGER.error("Could not save profiler results to {}", path, var14);
				} finally {
					IOUtils.closeQuietly(writer);
				}
			}
		}
	}

	private void writeSamples(List<Sample> samples, Path directory) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");

		for (Sample sample : samples) {
			String string = simpleDateFormat.format(sample.samplingTimer);
			Path path = directory.resolve(String.format("%d@%s.txt", sample.ticks, string));
			sample.result.save(path);
		}
	}

	private void save(TickTimeTracker timeTracker, Path directory) {
		timeTracker.getResult().save(directory.resolve("profiling.txt"));
	}

	private Path compressAndSave(Path filePath) {
		try {
			return Files.move(filePath, filePath.resolveSibling(StringUtils.substringBefore(filePath.getFileName().toString(), ".tmp") + ".zip"));
		} catch (IOException var3) {
			throw new UncheckedIOException(var3);
		}
	}
}
