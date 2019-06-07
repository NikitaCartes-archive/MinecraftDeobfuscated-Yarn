package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.SystemUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfileResultImpl implements ProfileResult {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, Long> timings;
	private final Map<String, Long> field_19382;
	private final long startTime;
	private final int startTick;
	private final long endTime;
	private final int endTick;
	private final int field_19383;

	public ProfileResultImpl(Map<String, Long> map, Map<String, Long> map2, long l, int i, long m, int j) {
		this.timings = map;
		this.field_19382 = map2;
		this.startTime = l;
		this.startTick = i;
		this.endTime = m;
		this.endTick = j;
		this.field_19383 = j - i;
	}

	@Override
	public List<ProfilerTiming> getTimings(String string) {
		String string2 = string;
		long l = this.timings.containsKey("root") ? (Long)this.timings.get("root") : 0L;
		long m = (Long)this.timings.getOrDefault(string, -1L);
		long n = (Long)this.field_19382.getOrDefault(string, 0L);
		List<ProfilerTiming> list = Lists.<ProfilerTiming>newArrayList();
		if (!string.isEmpty()) {
			string = string + ".";
		}

		long o = 0L;

		for (String string3 : this.timings.keySet()) {
			if (string3.length() > string.length() && string3.startsWith(string) && string3.indexOf(".", string.length() + 1) < 0) {
				o += this.timings.get(string3);
			}
		}

		float f = (float)o;
		if (o < m) {
			o = m;
		}

		if (l < o) {
			l = o;
		}

		Set<String> set = Sets.<String>newHashSet(this.timings.keySet());
		set.addAll(this.field_19382.keySet());

		for (String string4 : set) {
			if (string4.length() > string.length() && string4.startsWith(string) && string4.indexOf(".", string.length() + 1) < 0) {
				long p = (Long)this.timings.getOrDefault(string4, 0L);
				double d = (double)p * 100.0 / (double)o;
				double e = (double)p * 100.0 / (double)l;
				String string5 = string4.substring(string.length());
				long q = (Long)this.field_19382.getOrDefault(string4, 0L);
				list.add(new ProfilerTiming(string5, d, e, q));
			}
		}

		for (String string4x : this.timings.keySet()) {
			this.timings.put(string4x, (Long)this.timings.get(string4x) * 999L / 1000L);
		}

		if ((float)o > f) {
			list.add(new ProfilerTiming("unspecified", (double)((float)o - f) * 100.0 / (double)o, (double)((float)o - f) * 100.0 / (double)l, n));
		}

		Collections.sort(list);
		list.add(0, new ProfilerTiming(string2, 100.0, (double)o * 100.0 / (double)l, n));
		return list;
	}

	@Override
	public long getStartTime() {
		return this.startTime;
	}

	@Override
	public int getStartTick() {
		return this.startTick;
	}

	@Override
	public long getEndTime() {
		return this.endTime;
	}

	@Override
	public int getEndTick() {
		return this.endTick;
	}

	@Override
	public boolean saveToFile(File file) {
		file.getParentFile().mkdirs();
		Writer writer = null;

		boolean var4;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			writer.write(this.asString(this.getTimeSpan(), this.getTickSpan()));
			return true;
		} catch (Throwable var8) {
			LOGGER.error("Could not save profiler results to {}", file, var8);
			var4 = false;
		} finally {
			IOUtils.closeQuietly(writer);
		}

		return var4;
	}

	protected String asString(long l, int i) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("---- Minecraft Profiler Results ----\n");
		stringBuilder.append("// ");
		stringBuilder.append(generateWittyComment());
		stringBuilder.append("\n\n");
		stringBuilder.append("Time span: ").append(l / 1000000L).append(" ms\n");
		stringBuilder.append("Tick span: ").append(i).append(" ticks\n");
		stringBuilder.append("// This is approximately ")
			.append(String.format(Locale.ROOT, "%.2f", (float)i / ((float)l / 1.0E9F)))
			.append(" ticks per second. It should be ")
			.append(20)
			.append(" ticks per second\n\n");
		stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
		this.appendTiming(0, "root", stringBuilder);
		stringBuilder.append("--- END PROFILE DUMP ---\n\n");
		return stringBuilder.toString();
	}

	@Override
	public String getTimingTreeString() {
		StringBuilder stringBuilder = new StringBuilder();
		this.appendTiming(0, "root", stringBuilder);
		return stringBuilder.toString();
	}

	private void appendTiming(int i, String string, StringBuilder stringBuilder) {
		List<ProfilerTiming> list = this.getTimings(string);
		if (list.size() >= 3) {
			for (int j = 1; j < list.size(); j++) {
				ProfilerTiming profilerTiming = (ProfilerTiming)list.get(j);
				stringBuilder.append(String.format("[%02d] ", i));

				for (int k = 0; k < i; k++) {
					stringBuilder.append("|   ");
				}

				stringBuilder.append(profilerTiming.name)
					.append('(')
					.append(profilerTiming.field_19384)
					.append('/')
					.append(String.format(Locale.ROOT, "%.0f", (float)profilerTiming.field_19384 / (float)this.field_19383))
					.append(')')
					.append(" - ")
					.append(String.format(Locale.ROOT, "%.2f", profilerTiming.parentSectionUsagePercentage))
					.append("%/")
					.append(String.format(Locale.ROOT, "%.2f", profilerTiming.totalUsagePercentage))
					.append("%\n");
				if (!"unspecified".equals(profilerTiming.name)) {
					try {
						this.appendTiming(i + 1, string + "." + profilerTiming.name, stringBuilder);
					} catch (Exception var8) {
						stringBuilder.append("[[ EXCEPTION ").append(var8).append(" ]]");
					}
				}
			}
		}
	}

	private static String generateWittyComment() {
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
			return strings[(int)(SystemUtil.getMeasuringTimeNano() % (long)strings.length)];
		} catch (Throwable var2) {
			return "Witty comment unavailable :(";
		}
	}

	@Override
	public int getTickSpan() {
		return this.field_19383;
	}
}
