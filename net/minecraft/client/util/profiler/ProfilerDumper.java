/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.profiler;

import com.google.common.collect.ImmutableMap;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.profiler.Category;
import net.minecraft.client.util.profiler.Sample;
import net.minecraft.client.util.profiler.SamplingRecorder;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.util.profiler.TickTimeTracker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ProfilerDumper {
    public static final Path DEBUG_PROFILING_DIRECTORY = Paths.get("debug/profiling", new String[0]);
    public static final String field_32677 = "metrics";
    public static final String field_32678 = "deviations";
    public static final String field_32679 = "profiling.txt";
    private static final Logger LOGGER = LogManager.getLogger();
    public static final FileSystemProvider FILE_SYSTEM_PROVIDER = FileSystemProvider.installedProviders().stream().filter(fileSystemProvider -> fileSystemProvider.getScheme().equalsIgnoreCase("jar")).findFirst().orElseThrow(() -> new IllegalStateException("No jar file system provider found"));

    public Path createDump(List<Category> categories, List<Sample> deviations, TickTimeTracker timeTracker) {
        try {
            Files.createDirectories(DEBUG_PROFILING_DIRECTORY, new FileAttribute[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
        Path path = DEBUG_PROFILING_DIRECTORY.resolve(new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".tmp");
        try (FileSystem fileSystem = FILE_SYSTEM_PROVIDER.newFileSystem(path, ImmutableMap.of("create", "true"));){
            Files.createDirectories(DEBUG_PROFILING_DIRECTORY, new FileAttribute[0]);
            Path path2 = fileSystem.getPath("/", new String[0]);
            Path path3 = path2.resolve(field_32677);
            for (Category category : categories) {
                this.writeCategory(category, path3);
            }
            if (!deviations.isEmpty()) {
                this.writeSamples(deviations, path2.resolve(field_32678));
            }
            this.save(timeTracker, path2);
        } catch (IOException iOException2) {
            throw new UncheckedIOException(iOException2);
        }
        return this.compressAndSave(path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeCategory(Category category, Path directory) {
        String string = category.getName();
        List<SamplingRecorder> list = category.getSamplers();
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Expected at least one sampler for category: " + string);
        }
        IntSummaryStatistics intSummaryStatistics = list.stream().collect(Collectors.summarizingInt(SamplingRecorder::length));
        if (intSummaryStatistics.getMax() != intSummaryStatistics.getMin()) {
            throw new IllegalStateException(String.format("Expected all samples within category %s to contain same amount of samples, got %s", category, intSummaryStatistics));
        }
        Path path = directory.resolve(Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + ".csv");
        BufferedWriter writer = null;
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, new OpenOption[0]);
            CsvWriter.Header header = CsvWriter.makeHeader();
            for (SamplingRecorder samplingRecorder : list) {
                header.addColumn(samplingRecorder.getMetric().getName());
            }
            CsvWriter csvWriter = header.startBody(writer);
            while (list.get(0).canRead()) {
                Double[] doubles = (Double[])list.stream().map(SamplingRecorder::read).toArray(Double[]::new);
                csvWriter.printRow(doubles);
            }
            LOGGER.info("Flushed metrics to {}", (Object)path);
            IOUtils.closeQuietly(writer);
        } catch (Exception exception) {
            LOGGER.error("Could not save profiler results to {}", (Object)path, (Object)exception);
        } finally {
            IOUtils.closeQuietly(writer);
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
        timeTracker.getResult().save(directory.resolve(field_32679));
    }

    private Path compressAndSave(Path filePath) {
        try {
            return Files.move(filePath, filePath.resolveSibling(StringUtils.substringBefore(filePath.getFileName().toString(), ".tmp") + ".zip"), new CopyOption[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }
}

