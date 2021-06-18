/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

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
import net.minecraft.util.CsvWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Deviation;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.util.profiler.Sampler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecordDumper {
    public static final Path DEBUG_PROFILING_DIRECTORY = Paths.get("debug/profiling", new String[0]);
    public static final String METRICS_DIRECTORY = "metrics";
    public static final String DEVIATIONS_DIRECTORY = "deviations";
    public static final String FILE_NAME = "profiling.txt";
    private static final Logger LOGGER = LogManager.getLogger();
    private final String type;

    public RecordDumper(String type) {
        this.type = type;
    }

    public Path createDump(Set<Sampler> samplers, Map<Sampler, List<Deviation>> deviations, ProfileResult result) {
        try {
            Files.createDirectories(DEBUG_PROFILING_DIRECTORY, new FileAttribute[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
        try {
            Path path = Files.createTempDirectory("minecraft-profiling", new FileAttribute[0]);
            path.toFile().deleteOnExit();
            Files.createDirectories(DEBUG_PROFILING_DIRECTORY, new FileAttribute[0]);
            Path path2 = path.resolve(this.type);
            Path path3 = path2.resolve(METRICS_DIRECTORY);
            this.writeSamplers(samplers, path3);
            if (!deviations.isEmpty()) {
                this.writeDeviations(deviations, path2.resolve(DEVIATIONS_DIRECTORY));
            }
            this.save(result, path2);
            return path;
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }

    private void writeSamplers(Set<Sampler> samplers, Path directory) {
        if (samplers.isEmpty()) {
            throw new IllegalArgumentException("Expected at least one sampler to persist");
        }
        Map<SampleType, List<Sampler>> map = samplers.stream().collect(Collectors.groupingBy(Sampler::getType));
        map.forEach((type, sampler) -> this.writeSamplersInType((SampleType)((Object)type), (List<Sampler>)sampler, directory));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeSamplersInType(SampleType type, List<Sampler> samplers, Path directory) {
        Path path = directory.resolve(Util.replaceInvalidChars(type.getName(), Identifier::isPathCharacterValid) + ".csv");
        BufferedWriter writer = null;
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, new OpenOption[0]);
            CsvWriter.Header header = CsvWriter.makeHeader();
            header.addColumn("@tick");
            for (Sampler sampler : samplers) {
                header.addColumn(sampler.getName());
            }
            CsvWriter csvWriter = header.startBody(writer);
            List list = samplers.stream().map(Sampler::collectData).collect(Collectors.toList());
            int i = list.stream().mapToInt(Sampler.Data::getStartTick).summaryStatistics().getMin();
            int j = list.stream().mapToInt(Sampler.Data::getEndTick).summaryStatistics().getMax();
            for (int k = i; k <= j; ++k) {
                int l = k;
                Stream<String> stream = list.stream().map(data -> String.valueOf(data.getValue(l)));
                Object[] objects = Stream.concat(Stream.of(String.valueOf(k)), stream).toArray(String[]::new);
                csvWriter.printRow(objects);
            }
            LOGGER.info("Flushed metrics to {}", (Object)path);
            IOUtils.closeQuietly(writer);
        } catch (Exception exception) {
            LOGGER.error("Could not save profiler results to {}", (Object)path, (Object)exception);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private void writeDeviations(Map<Sampler, List<Deviation>> deviations, Path deviationsDirectory) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss.SSS", Locale.UK).withZone(ZoneId.systemDefault());
        deviations.forEach((sampler, sampleDeviations) -> sampleDeviations.forEach(deviation -> {
            String string = dateTimeFormatter.format(deviation.instant);
            Path path2 = deviationsDirectory.resolve(Util.replaceInvalidChars(sampler.getName(), Identifier::isPathCharacterValid)).resolve(String.format(Locale.ROOT, "%d@%s.txt", deviation.ticks, string));
            deviation.result.save(path2);
        }));
    }

    private void save(ProfileResult result, Path directory) {
        result.save(directory.resolve(FILE_NAME));
    }
}

