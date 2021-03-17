/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
import net.minecraft.class_5964;
import net.minecraft.class_5965;
import net.minecraft.class_5969;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.CsvWriter;
import net.minecraft.util.profiler.TickTimeTracker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class class_5971 {
    public static final Path field_29616 = Paths.get("debug/profiling", new String[0]);
    private static final Logger field_29618 = LogManager.getLogger();
    public static final FileSystemProvider field_29617 = FileSystemProvider.installedProviders().stream().filter(fileSystemProvider -> fileSystemProvider.getScheme().equalsIgnoreCase("jar")).findFirst().orElseThrow(() -> new IllegalStateException("No jar file system provider found"));

    public Path method_34807(List<class_5969> list, List<class_5964> list2, TickTimeTracker tickTimeTracker) {
        try {
            Files.createDirectories(field_29616, new FileAttribute[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
        Path path = field_29616.resolve(new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".tmp");
        try (FileSystem fileSystem = field_29617.newFileSystem(path, ImmutableMap.of("create", "true"));){
            Files.createDirectories(field_29616, new FileAttribute[0]);
            Path path2 = fileSystem.getPath("/", new String[0]);
            Path path3 = path2.resolve("metrics");
            for (class_5969 lv : list) {
                this.method_34803(lv, path3);
            }
            if (!list2.isEmpty()) {
                this.method_34806(list2, path2.resolve("deviations"));
            }
            this.method_34802(tickTimeTracker, path2);
        } catch (IOException iOException2) {
            throw new UncheckedIOException(iOException2);
        }
        return this.method_34804(path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void method_34803(class_5969 arg, Path path) {
        String string = arg.method_34796();
        List<class_5965> list = arg.method_34797();
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Expected at least one sampler for category: " + string);
        }
        IntSummaryStatistics intSummaryStatistics = list.stream().collect(Collectors.summarizingInt(class_5965::method_34775));
        if (intSummaryStatistics.getMax() != intSummaryStatistics.getMin()) {
            throw new IllegalStateException(String.format("Expected all samples within category %s to contain same amount of samples, got %s", arg, intSummaryStatistics));
        }
        Path path2 = path.resolve(Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + ".csv");
        BufferedWriter writer = null;
        try {
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            writer = Files.newBufferedWriter(path2, StandardCharsets.UTF_8, new OpenOption[0]);
            CsvWriter.Header header = CsvWriter.makeHeader();
            for (class_5965 lv : list) {
                header.addColumn(lv.method_34783().method_34704());
            }
            CsvWriter csvWriter = header.startBody(writer);
            while (list.get(0).method_34784()) {
                Double[] doubles = (Double[])list.stream().map(class_5965::method_34785).toArray(Double[]::new);
                csvWriter.printRow(doubles);
            }
            field_29618.info("Flushed metrics to {}", (Object)path2);
            IOUtils.closeQuietly(writer);
        } catch (Exception exception) {
            field_29618.error("Could not save profiler results to {}", (Object)path2, (Object)exception);
        } finally {
            IOUtils.closeQuietly(writer);
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
            return Files.move(path, path.resolveSibling(StringUtils.substringBefore(path.getFileName().toString(), ".tmp") + ".zip"), new CopyOption[0]);
        } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
    }
}

