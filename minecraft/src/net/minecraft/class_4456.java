package net.minecraft;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class class_4456 {
	private final Writer field_20284;
	private final int field_20285;

	private class_4456(Writer writer, List<String> list) throws IOException {
		this.field_20284 = writer;
		this.field_20285 = list.size();
		this.method_21629(list.stream());
	}

	public static class_4456.class_4457 method_21627() {
		return new class_4456.class_4457();
	}

	public void method_21630(Object... objects) throws IOException {
		if (objects.length != this.field_20285) {
			throw new IllegalArgumentException("Invalid number of columns, expected " + this.field_20285 + ", but got " + objects.length);
		} else {
			this.method_21629(Stream.of(objects));
		}
	}

	private void method_21629(Stream<?> stream) throws IOException {
		this.field_20284.write((String)stream.map(class_4456::method_21628).collect(Collectors.joining(",")) + "\r\n");
	}

	private static String method_21628(@Nullable Object object) {
		return StringEscapeUtils.escapeCsv(object != null ? object.toString() : "[null]");
	}

	public static class class_4457 {
		private final List<String> field_20286 = Lists.<String>newArrayList();

		public class_4456.class_4457 method_21632(String string) {
			this.field_20286.add(string);
			return this;
		}

		public class_4456 method_21631(Writer writer) throws IOException {
			return new class_4456(writer, this.field_20286);
		}
	}
}
