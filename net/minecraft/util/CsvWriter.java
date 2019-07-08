/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

public class CsvWriter {
    private final Writer writer;
    private final int column;

    private CsvWriter(Writer writer, List<String> list) throws IOException {
        this.writer = writer;
        this.column = list.size();
        this.printRow(list.stream());
    }

    public static Header makeHeader() {
        return new Header();
    }

    public void printRow(Object ... objects) throws IOException {
        if (objects.length != this.column) {
            throw new IllegalArgumentException("Invalid number of columns, expected " + this.column + ", but got " + objects.length);
        }
        this.printRow(Stream.of(objects));
    }

    private void printRow(Stream<?> stream) throws IOException {
        this.writer.write(stream.map(CsvWriter::print).collect(Collectors.joining(",")) + "\r\n");
    }

    private static String print(@Nullable Object object) {
        return StringEscapeUtils.escapeCsv(object != null ? object.toString() : "[null]");
    }

    public static class Header {
        private final List<String> columns = Lists.newArrayList();

        public Header addColumn(String string) {
            this.columns.add(string);
            return this;
        }

        public CsvWriter startBody(Writer writer) throws IOException {
            return new CsvWriter(writer, this.columns);
        }
    }
}

