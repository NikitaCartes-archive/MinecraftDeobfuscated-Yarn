package net.minecraft.util.profiler;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.class_4748;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfileResultImpl implements ProfileResult {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final class_4748 field_21823 = new class_4748() {
		@Override
		public long method_24272() {
			return 0L;
		}

		@Override
		public long method_24273() {
			return 0L;
		}

		@Override
		public Object2LongMap<String> method_24274() {
			return Object2LongMaps.emptyMap();
		}
	};
	private static final Splitter field_21824 = Splitter.on('\u001e');
	private static final Comparator<Entry<String, ProfileResultImpl.class_4747>> field_21825 = Entry.comparingByValue(
			Comparator.comparingLong(arg -> arg.field_21828)
		)
		.reversed();
	private final Map<String, ? extends class_4748> field_21826;
	private final long startTime;
	private final int startTick;
	private final long endTime;
	private final int endTick;
	private final int field_19383;

	public ProfileResultImpl(Map<String, ? extends class_4748> timings, long l, int i, long m, int j) {
		this.field_21826 = timings;
		this.startTime = l;
		this.startTick = i;
		this.endTime = m;
		this.endTick = j;
		this.field_19383 = j - i;
	}

	private class_4748 method_24262(String string) {
		class_4748 lv = (class_4748)this.field_21826.get(string);
		return lv != null ? lv : field_21823;
	}

	@Override
	public List<ProfilerTiming> getTimings(String parentTiming) {
		String string = parentTiming;
		class_4748 lv = this.method_24262("root");
		long l = lv.method_24272();
		class_4748 lv2 = this.method_24262(parentTiming);
		long m = lv2.method_24272();
		long n = lv2.method_24273();
		List<ProfilerTiming> list = Lists.<ProfilerTiming>newArrayList();
		if (!parentTiming.isEmpty()) {
			parentTiming = parentTiming + '\u001e';
		}

		long o = 0L;

		for (String string2 : this.field_21826.keySet()) {
			if (method_24255(parentTiming, string2)) {
				o += this.method_24262(string2).method_24272();
			}
		}

		float f = (float)o;
		if (o < m) {
			o = m;
		}

		if (l < o) {
			l = o;
		}

		for (String string3 : this.field_21826.keySet()) {
			if (method_24255(parentTiming, string3)) {
				class_4748 lv3 = this.method_24262(string3);
				long p = lv3.method_24272();
				double d = (double)p * 100.0 / (double)o;
				double e = (double)p * 100.0 / (double)l;
				String string4 = string3.substring(parentTiming.length());
				list.add(new ProfilerTiming(string4, d, e, lv3.method_24273()));
			}
		}

		if ((float)o > f) {
			list.add(new ProfilerTiming("unspecified", (double)((float)o - f) * 100.0 / (double)o, (double)((float)o - f) * 100.0 / (double)l, n));
		}

		Collections.sort(list);
		list.add(0, new ProfilerTiming(string, 100.0, (double)o * 100.0 / (double)l, n));
		return list;
	}

	private static boolean method_24255(String string, String string2) {
		return string2.length() > string.length() && string2.startsWith(string) && string2.indexOf(30, string.length() + 1) < 0;
	}

	private Map<String, ProfileResultImpl.class_4747> method_24264() {
		Map<String, ProfileResultImpl.class_4747> map = Maps.newTreeMap();
		this.field_21826
			.forEach(
				(string, arg) -> {
					Object2LongMap<String> object2LongMap = arg.method_24274();
					if (!object2LongMap.isEmpty()) {
						List<String> list = field_21824.splitToList(string);
						object2LongMap.forEach(
							(stringx, long_) -> ((ProfileResultImpl.class_4747)map.computeIfAbsent(stringx, stringxx -> new ProfileResultImpl.class_4747()))
									.method_24267(list.iterator(), long_)
						);
					}
				}
			);
		return map;
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
		Map<String, ProfileResultImpl.class_4747> map = this.method_24264();
		if (!map.isEmpty()) {
			stringBuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
			this.method_24260(map, stringBuilder, tickSpan);
			stringBuilder.append("--- END COUNTER DUMP ---\n\n");
		}

		return stringBuilder.toString();
	}

	private static StringBuilder method_24256(StringBuilder stringBuilder, int i) {
		stringBuilder.append(String.format("[%02d] ", i));

		for (int j = 0; j < i; j++) {
			stringBuilder.append("|   ");
		}

		return stringBuilder;
	}

	private void appendTiming(int level, String name, StringBuilder sb) {
		List<ProfilerTiming> list = this.getTimings(name);
		Object2LongMap<String> object2LongMap = ((class_4748)this.field_21826.get(name)).method_24274();
		object2LongMap.forEach(
			(string, long_) -> method_24256(sb, level)
					.append('#')
					.append(string)
					.append(' ')
					.append(long_)
					.append('/')
					.append(long_ / (long)this.field_19383)
					.append('\n')
		);
		if (list.size() >= 3) {
			for (int i = 1; i < list.size(); i++) {
				ProfilerTiming profilerTiming = (ProfilerTiming)list.get(i);
				method_24256(sb, level)
					.append(profilerTiming.name)
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
					} catch (Exception var9) {
						sb.append("[[ EXCEPTION ").append(var9).append(" ]]");
					}
				}
			}
		}
	}

	private void method_24253(int i, String string, ProfileResultImpl.class_4747 arg, int j, StringBuilder stringBuilder) {
		method_24256(stringBuilder, i)
			.append(string)
			.append(" total:")
			.append(arg.field_21827)
			.append('/')
			.append(arg.field_21828)
			.append(" average: ")
			.append(arg.field_21827 / (long)j)
			.append('/')
			.append(arg.field_21828 / (long)j)
			.append('\n');
		arg.field_21829
			.entrySet()
			.stream()
			.sorted(field_21825)
			.forEach(entry -> this.method_24253(i + 1, (String)entry.getKey(), (ProfileResultImpl.class_4747)entry.getValue(), j, stringBuilder));
	}

	private void method_24260(Map<String, ProfileResultImpl.class_4747> map, StringBuilder stringBuilder, int i) {
		map.forEach((string, arg) -> {
			stringBuilder.append("-- Counter: ").append(string).append(" --\n");
			this.method_24253(0, "root", (ProfileResultImpl.class_4747)arg.field_21829.get("root"), i, stringBuilder);
			stringBuilder.append("\n\n");
		});
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

	static class class_4747 {
		private long field_21827;
		private long field_21828;
		private final Map<String, ProfileResultImpl.class_4747> field_21829 = Maps.<String, ProfileResultImpl.class_4747>newHashMap();

		private class_4747() {
		}

		public void method_24267(Iterator<String> iterator, long l) {
			this.field_21828 += l;
			if (!iterator.hasNext()) {
				this.field_21827 += l;
			} else {
				((ProfileResultImpl.class_4747)this.field_21829.computeIfAbsent(iterator.next(), string -> new ProfileResultImpl.class_4747())).method_24267(iterator, l);
			}
		}
	}
}
