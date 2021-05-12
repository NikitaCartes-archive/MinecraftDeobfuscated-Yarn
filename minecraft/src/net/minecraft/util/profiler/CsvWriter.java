package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class CsvWriter {
	private static final String CRLF = "\r\n";
	private static final String COMMA = ",";
	private final Writer writer;
	private final int column;

	CsvWriter(Writer writer, List<String> list) throws IOException {
		this.writer = writer;
		this.column = list.size();
		this.printRow(list.stream());
	}

	public static CsvWriter.Header makeHeader() {
		return new CsvWriter.Header();
	}

	public void printRow(Object... columns) throws IOException {
		if (columns.length != this.column) {
			throw new IllegalArgumentException("Invalid number of columns, expected " + this.column + ", but got " + columns.length);
		} else {
			this.printRow(Stream.of(columns));
		}
	}

	private void printRow(Stream<?> columns) throws IOException {
		this.writer.write((String)columns.map(CsvWriter::print).collect(Collectors.joining(",")) + "\r\n");
	}

	private static String print(@Nullable Object o) {
		return StringEscapeUtils.escapeCsv(o != null ? o.toString() : "[null]");
	}

	public static class Header {
		private final List<String> columns = Lists.<String>newArrayList();

		public CsvWriter.Header addColumn(String name) {
			this.columns.add(name);
			return this;
		}

		public CsvWriter startBody(Writer writer) throws IOException {
			return new CsvWriter(writer, this.columns);
		}
	}
}
