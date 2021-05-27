/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.profiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.util.profiler.Sample;
import net.minecraft.client.util.profiler.SamplingChannel;
import net.minecraft.client.util.profiler.SamplingRecorder;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.util.profiler.ProfileResult;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfilerDumper {
    public static final Path DEBUG_PROFILING_DIRECTORY = Paths.get("debug/profiling", new String[0]);
    public static final String METRICS_DIRECTORY = "metrics";
    public static final String DEVIATIONS_DIRECTORY = "deviations";
    public static final String FILE_NAME = "profiling.txt";
    private static final Logger LOGGER = LogManager.getLogger();
    private final String field_33903;

    public ProfilerDumper(String string) {
        this.field_33903 = string;
    }

    public Path createDump(Set<SamplingRecorder> set, Map<SamplingRecorder, List<Sample>> map, ProfileResult profileResult) {
        try {
            Files.createDirectories(DEBUG_PROFILING_DIRECTORY, new FileAttribute[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
        try {
            Path path = Files.createTempDirectory("minecraft-profiling", new FileAttribute[0]);
            path.toFile().deleteOnExit();
            Files.createDirectories(DEBUG_PROFILING_DIRECTORY, new FileAttribute[0]);
            Path path2 = path.resolve(this.field_33903);
            Path path3 = path2.resolve(METRICS_DIRECTORY);
            this.writeCategory(set, path3);
            if (!map.isEmpty()) {
                this.method_37212(map, path2.resolve(DEVIATIONS_DIRECTORY));
            }
            this.save(profileResult, path2);
            return path;
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    private void writeCategory(Set<SamplingRecorder> set, Path directory) {
        if (set.isEmpty()) {
            throw new IllegalArgumentException("Expected at least one sampler to persist");
        }
        Map<SamplingChannel, List<SamplingRecorder>> map = set.stream().collect(Collectors.groupingBy(SamplingRecorder::method_37172));
        map.forEach((samplingChannel, list) -> this.method_37208((SamplingChannel)((Object)samplingChannel), (List<SamplingRecorder>)list, directory));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void method_37208(SamplingChannel samplingChannel, List<SamplingRecorder> list, Path path) {
        Path path2 = path.resolve(Util.replaceInvalidChars(samplingChannel.getName(), Identifier::isPathCharacterValid) + ".csv");
        BufferedWriter writer = null;
        try {
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            writer = Files.newBufferedWriter(path2, StandardCharsets.UTF_8, new OpenOption[0]);
            CsvWriter.Header header = CsvWriter.makeHeader();
            header.addColumn("@tick");
            for (SamplingRecorder samplingRecorder : list) {
                header.addColumn(samplingRecorder.method_37171());
            }
            CsvWriter csvWriter = header.startBody(writer);
            List list2 = list.stream().map(SamplingRecorder::method_37173).collect(Collectors.toList());
            int i = list2.stream().mapToInt(SamplingRecorder.class_6398::method_37175).summaryStatistics().getMin();
            int j = list2.stream().mapToInt(SamplingRecorder.class_6398::method_37177).summaryStatistics().getMax();
            for (int k = i; k <= j; ++k) {
                int l = k;
                Stream<String> stream = list2.stream().map(arg -> String.valueOf(arg.method_37176(l)));
                Object[] objects = Stream.concat(Stream.of(String.valueOf(k)), stream).toArray(String[]::new);
                csvWriter.printRow(objects);
            }
            LOGGER.info("Flushed metrics to {}", (Object)path2);
            IOUtils.closeQuietly(writer);
        } catch (Exception exception) {
            LOGGER.error("Could not save profiler results to {}", (Object)path2, (Object)exception);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private void method_37212(Map<SamplingRecorder, List<Sample>> map, Path path) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss.SSS", Locale.UK).withZone(ZoneId.systemDefault());
        map.forEach((samplingRecorder, list) -> list.forEach(sample -> {
            String string = dateTimeFormatter.format(sample.samplingTimer);
            Path path2 = path.resolve(Util.replaceInvalidChars(samplingRecorder.method_37171(), Identifier::isPathCharacterValid)).resolve(String.format(Locale.ROOT, "%d@%s.txt", sample.ticks, string));
            sample.result.save(path2);
        }));
    }

    private void save(ProfileResult profileResult, Path directory) {
        profileResult.save(directory.resolve(FILE_NAME));
    }
}

