package net.minecraft;

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
public class class_5971 {
	public static final Path field_29616 = Paths.get("debug/profiling");
	private static final Logger field_29618 = LogManager.getLogger();
	public static final FileSystemProvider field_29617 = (FileSystemProvider)FileSystemProvider.installedProviders()
		.stream()
		.filter(fileSystemProvider -> fileSystemProvider.getScheme().equalsIgnoreCase("jar"))
		.findFirst()
		.orElseThrow(() -> new IllegalStateException("No jar file system provider found"));

	public Path method_34807(List<class_5969> list, List<class_5964> list2, TickTimeTracker tickTimeTracker) {
		try {
			Files.createDirectories(field_29616);
		} catch (IOException var20) {
			throw new UncheckedIOException(var20);
		}

		Path path = field_29616.resolve(new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".tmp");

		try {
			FileSystem fileSystem = field_29617.newFileSystem(path, ImmutableMap.of("create", "true"));
			Throwable var6 = null;

			try {
				Files.createDirectories(field_29616);
				Path path2 = fileSystem.getPath("/");
				Path path3 = path2.resolve("metrics");

				for (class_5969 lv : list) {
					this.method_34803(lv, path3);
				}

				if (!list2.isEmpty()) {
					this.method_34806(list2, path2.resolve("deviations"));
				}

				this.method_34802(tickTimeTracker, path2);
			} catch (Throwable var21) {
				var6 = var21;
				throw var21;
			} finally {
				if (fileSystem != null) {
					if (var6 != null) {
						try {
							fileSystem.close();
						} catch (Throwable var19) {
							var6.addSuppressed(var19);
						}
					} else {
						fileSystem.close();
					}
				}
			}
		} catch (IOException var23) {
			throw new UncheckedIOException(var23);
		}

		return this.method_34804(path);
	}

	private void method_34803(class_5969 arg, Path path) {
		String string = arg.method_34796();
		List<class_5965> list = arg.method_34797();
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Expected at least one sampler for category: " + string);
		} else {
			IntSummaryStatistics intSummaryStatistics = (IntSummaryStatistics)list.stream().collect(Collectors.summarizingInt(class_5965::method_34775));
			if (intSummaryStatistics.getMax() != intSummaryStatistics.getMin()) {
				throw new IllegalStateException(
					String.format("Expected all samples within category %s to contain same amount of samples, got %s", arg, intSummaryStatistics)
				);
			} else {
				Path path2 = path.resolve(Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + ".csv");
				Writer writer = null;

				try {
					Files.createDirectories(path2.getParent());
					writer = Files.newBufferedWriter(path2, StandardCharsets.UTF_8);
					CsvWriter.Header header = CsvWriter.makeHeader();

					for (class_5965 lv : list) {
						header.addColumn(lv.method_34783().method_34704());
					}

					CsvWriter csvWriter = header.startBody(writer);

					while (((class_5965)list.get(0)).method_34784()) {
						Double[] doubles = (Double[])list.stream().map(class_5965::method_34785).toArray(Double[]::new);
						csvWriter.printRow(doubles);
					}

					field_29618.info("Flushed metrics to {}", path2);
				} catch (Exception var14) {
					field_29618.error("Could not save profiler results to {}", path2, var14);
				} finally {
					IOUtils.closeQuietly(writer);
				}
			}
		}
	}

	private void method_34806(List<class_5964> list, Path path) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");

		for (class_5964 lv : list) {
			String string = simpleDateFormat.format(lv.field_29595);
			Path path2 = path.resolve(String.format("%d@%s.txt", lv.field_29596, string));
			lv.field_29597.save(path2);
		}
	}

	private void method_34802(TickTimeTracker tickTimeTracker, Path path) {
		tickTimeTracker.getResult().save(path.resolve("profiling.txt"));
	}

	private Path method_34804(Path path) {
		try {
			return Files.move(path, path.resolveSibling(StringUtils.substringBefore(path.getFileName().toString(), ".tmp") + ".zip"));
		} catch (IOException var3) {
			throw new UncheckedIOException(var3);
		}
	}
}
