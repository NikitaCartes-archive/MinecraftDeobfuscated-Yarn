package net.minecraft;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3692 implements class_3696 {
	private static final Logger field_16279 = LogManager.getLogger();
	private final Map<String, Long> field_16277;
	private final long field_16278;
	private final int field_16275;
	private final long field_16276;
	private final int field_16274;

	public class_3692(Map<String, Long> map, long l, int i, long m, int j) {
		this.field_16277 = map;
		this.field_16278 = l;
		this.field_16275 = i;
		this.field_16276 = m;
		this.field_16274 = j;
	}

	@Override
	public List<class_3534> method_16067(String string) {
		String string2 = string;
		long l = this.field_16277.containsKey("root") ? (Long)this.field_16277.get("root") : 0L;
		long m = this.field_16277.containsKey(string) ? (Long)this.field_16277.get(string) : -1L;
		List<class_3534> list = Lists.<class_3534>newArrayList();
		if (!string.isEmpty()) {
			string = string + ".";
		}

		long n = 0L;

		for (String string3 : this.field_16277.keySet()) {
			if (string3.length() > string.length() && string3.startsWith(string) && string3.indexOf(".", string.length() + 1) < 0) {
				n += this.field_16277.get(string3);
			}
		}

		float f = (float)n;
		if (n < m) {
			n = m;
		}

		if (l < n) {
			l = n;
		}

		for (String string4 : this.field_16277.keySet()) {
			if (string4.length() > string.length() && string4.startsWith(string) && string4.indexOf(".", string.length() + 1) < 0) {
				long o = (Long)this.field_16277.get(string4);
				double d = (double)o * 100.0 / (double)n;
				double e = (double)o * 100.0 / (double)l;
				String string5 = string4.substring(string.length());
				list.add(new class_3534(string5, d, e));
			}
		}

		for (String string4x : this.field_16277.keySet()) {
			this.field_16277.put(string4x, (Long)this.field_16277.get(string4x) * 999L / 1000L);
		}

		if ((float)n > f) {
			list.add(new class_3534("unspecified", (double)((float)n - f) * 100.0 / (double)n, (double)((float)n - f) * 100.0 / (double)l));
		}

		Collections.sort(list);
		list.add(0, new class_3534(string2, 100.0, (double)n * 100.0 / (double)l));
		return list;
	}

	@Override
	public long method_16068() {
		return this.field_16278;
	}

	@Override
	public int method_16072() {
		return this.field_16275;
	}

	@Override
	public long method_16073() {
		return this.field_16276;
	}

	@Override
	public int method_16070() {
		return this.field_16274;
	}

	@Override
	public boolean method_16069(File file) {
		file.getParentFile().mkdirs();
		Writer writer = null;

		boolean var4;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			writer.write(this.method_16063(this.method_16071(), this.method_16074()));
			return true;
		} catch (Throwable var8) {
			field_16279.error("Could not save profiler results to {}", file, var8);
			var4 = false;
		} finally {
			IOUtils.closeQuietly(writer);
		}

		return var4;
	}

	protected String method_16063(long l, int i) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("---- Minecraft Profiler Results ----\n");
		stringBuilder.append("// ");
		stringBuilder.append(method_16062());
		stringBuilder.append("\n\n");
		stringBuilder.append("Time span: ").append(l / 1000000L).append(" ms\n");
		stringBuilder.append("Tick span: ").append(i).append(" ticks\n");
		stringBuilder.append("// This is approximately ")
			.append(String.format(Locale.ROOT, "%.2f", (float)i / ((float)l / 1.0E9F)))
			.append(" ticks per second. It should be ")
			.append(20)
			.append(" ticks per second\n\n");
		stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
		this.method_16061(0, "root", stringBuilder);
		stringBuilder.append("--- END PROFILE DUMP ---\n\n");
		return stringBuilder.toString();
	}

	@Override
	public String method_18052() {
		StringBuilder stringBuilder = new StringBuilder();
		this.method_16061(0, "root", stringBuilder);
		return stringBuilder.toString();
	}

	private void method_16061(int i, String string, StringBuilder stringBuilder) {
		List<class_3534> list = this.method_16067(string);
		if (list.size() >= 3) {
			for (int j = 1; j < list.size(); j++) {
				class_3534 lv = (class_3534)list.get(j);
				stringBuilder.append(String.format("[%02d] ", i));

				for (int k = 0; k < i; k++) {
					stringBuilder.append("|   ");
				}

				stringBuilder.append(lv.field_15738)
					.append(" - ")
					.append(String.format(Locale.ROOT, "%.2f", lv.field_15739))
					.append("%/")
					.append(String.format(Locale.ROOT, "%.2f", lv.field_15737))
					.append("%\n");
				if (!"unspecified".equals(lv.field_15738)) {
					try {
						this.method_16061(i + 1, string + "." + lv.field_15738, stringBuilder);
					} catch (Exception var8) {
						stringBuilder.append("[[ EXCEPTION ").append(var8).append(" ]]");
					}
				}
			}
		}
	}

	private static String method_16062() {
		String[] strings = new String[]{
			"Shiny numbers!",
			"Am I not running fast enough? :(",
			"I'm working as hard as I can!",
			"Will I ever be good enough for you? :(",
			"Speedy. Zoooooom!",
			"Hello world",
			"40% better than a crash report.",
			"Now with extra numbers",
			"Now with less numbers",
			"Now with the same numbers",
			"You should add flames to things, it makes them go faster!",
			"Do you feel the need for... optimization?",
			"*cracks redstone whip*",
			"Maybe if you treated it better then it'll have more motivation to work faster! Poor server."
		};

		try {
			return strings[(int)(class_156.method_648() % (long)strings.length)];
		} catch (Throwable var2) {
			return "Witty comment unavailable :(";
		}
	}
}
