package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class CsvWriter {
	private final Writer writer;
	private final int column;

	private CsvWriter(Writer writer, List<String> list) throws IOException {
		this.writer = writer;
		this.column = list.size();
		this.printRow(list.stream());
	}

	public static CsvWriter.Header makeHeader() {
		return new CsvWriter.Header();
	}

	public void printRow(Object... objects) throws IOException {
		if (objects.length != this.column) {
			throw new IllegalArgumentException("Invalid number of columns, expected " + this.column + ", but got " + objects.length);
		} else {
			this.printRow(Stream.of(objects));
		}
	}

	private void printRow(Stream<?> stream) throws IOException {
		this.writer.write((String)stream.map(CsvWriter::print).collect(Collectors.joining(",")) + "\r\n");
	}

	private static String print(@Nullable Object object) {
		return StringEscapeUtils.escapeCsv(object != null ? object.toString() : "[null]");
	}

	public static class Header {
		private final List<String> columns = Lists.newArrayList();

		public CsvWriter.Header addColumn(String string) {
			this.columns.add(string);
			return this;
		}

		public CsvWriter startBody(Writer writer) throws IOException {
			return new CsvWriter(writer, this.columns);
		}
	}
}
