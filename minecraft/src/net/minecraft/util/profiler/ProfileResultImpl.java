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
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
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

	public ProfileResultImpl(Map<String, Long> timings, Map<String, Long> map, long l, int i, long m, int j) {
		this.timings = timings;
		this.field_19382 = map;
		this.startTime = l;
		this.startTick = i;
		this.endTime = m;
		this.endTick = j;
		this.field_19383 = j - i;
	}

	@Override
	public List<ProfilerTiming> getTimings(String parentPath) {
		String string = parentPath;
		long l = this.timings.containsKey("root") ? (Long)this.timings.get("root") : 0L;
		long m = (Long)this.timings.getOrDefault(parentPath, -1L);
		long n = (Long)this.field_19382.getOrDefault(parentPath, 0L);
		List<ProfilerTiming> list = Lists.<ProfilerTiming>newArrayList();
		if (!parentPath.isEmpty()) {
			parentPath = parentPath + '\u001e';
		}

		long o = 0L;

		for (String string2 : this.timings.keySet()) {
			if (string2.length() > parentPath.length() && string2.startsWith(parentPath) && string2.indexOf(30, parentPath.length() + 1) < 0) {
				o += this.timings.get(string2);
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

		for (String string3 : set) {
			if (string3.length() > parentPath.length() && string3.startsWith(parentPath) && string3.indexOf(30, parentPath.length() + 1) < 0) {
				long p = (Long)this.timings.getOrDefault(string3, 0L);
				double d = (double)p * 100.0 / (double)o;
				double e = (double)p * 100.0 / (double)l;
				String string4 = string3.substring(parentPath.length());
				long q = (Long)this.field_19382.getOrDefault(string3, 0L);
				list.add(new ProfilerTiming(string4, d, e, q));
			}
		}

		for (String string3x : this.timings.keySet()) {
			this.timings.put(string3x, (Long)this.timings.get(string3x) * 999L / 1000L);
		}

		if ((float)o > f) {
			list.add(new ProfilerTiming("unspecified", (double)((float)o - f) * 100.0 / (double)o, (double)((float)o - f) * 100.0 / (double)l, n));
		}

		Collections.sort(list);
		list.add(0, new ProfilerTiming(string, 100.0, (double)o * 100.0 / (double)l, n));
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
	public boolean save(File file) {
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

	protected String asString(long timeSpan, int tickSpan) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("---- Minecraft Profiler Results ----\n");
		stringBuilder.append("// ");
		stringBuilder.append(generateWittyComment());
		stringBuilder.append("\n\n");
		stringBuilder.append("Version: ").append(SharedConstants.getGameVersion().getId()).append('\n');
		stringBuilder.append("Time span: ").append(timeSpan / 1000000L).append(" ms\n");
		stringBuilder.append("Tick span: ").append(tickSpan).append(" ticks\n");
		stringBuilder.append("// This is approximately ")
			.append(String.format(Locale.ROOT, "%.2f", (float)tickSpan / ((float)timeSpan / 1.0E9F)))
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

	private void appendTiming(int level, String name, StringBuilder sb) {
		List<ProfilerTiming> list = this.getTimings(name);
		if (list.size() >= 3) {
			for (int i = 1; i < list.size(); i++) {
				ProfilerTiming profilerTiming = (ProfilerTiming)list.get(i);
				sb.append(String.format("[%02d] ", level));

				for (int j = 0; j < level; j++) {
					sb.append("|   ");
				}

				sb.append(profilerTiming.name)
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
						this.appendTiming(level + 1, name + '\u001e' + profilerTiming.name, sb);
					} catch (Exception var8) {
						sb.append("[[ EXCEPTION ").append(var8).append(" ]]");
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
			return strings[(int)(Util.getMeasuringTimeNano() % (long)strings.length)];
		} catch (Throwable var2) {
			return "Witty comment unavailable :(";
		}
	}

	@Override
	public int getTickSpan() {
		return this.field_19383;
	}
}
