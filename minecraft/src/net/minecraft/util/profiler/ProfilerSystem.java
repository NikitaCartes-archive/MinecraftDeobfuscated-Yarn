package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4748;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfilerSystem implements ReadableProfiler {
	private static final long TIMEOUT_NANOSECONDS = Duration.ofMillis(100L).toNanos();
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<String> nameList = Lists.<String>newArrayList();
	private final LongList timeList = new LongArrayList();
	private final Map<String, ProfilerSystem.class_4746> field_21818 = Maps.<String, ProfilerSystem.class_4746>newHashMap();
	private final IntSupplier field_16266;
	private final long field_15732;
	private final int field_15729;
	private String location = "";
	private boolean tickStarted;
	@Nullable
	private ProfilerSystem.class_4746 field_21819;
	private final boolean field_20345;

	public ProfilerSystem(long l, IntSupplier intSupplier, boolean bl) {
		this.field_15732 = l;
		this.field_15729 = intSupplier.getAsInt();
		this.field_16266 = intSupplier;
		this.field_20345 = bl;
	}

	@Override
	public void startTick() {
		if (this.tickStarted) {
			LOGGER.error("Profiler tick already started - missing endTick()?");
		} else {
			this.tickStarted = true;
			this.location = "";
			this.nameList.clear();
			this.push("root");
		}
	}

	@Override
	public void endTick() {
		if (!this.tickStarted) {
			LOGGER.error("Profiler tick already ended - missing startTick()?");
		} else {
			this.pop();
			this.tickStarted = false;
			if (!this.location.isEmpty()) {
				LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", () -> ProfileResult.method_21721(this.location));
			}
		}
	}

	@Override
	public void push(String string) {
		if (!this.tickStarted) {
			LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", string);
		} else {
			if (!this.location.isEmpty()) {
				this.location = this.location + '\u001e';
			}

			this.location = this.location + string;
			this.nameList.add(this.location);
			this.timeList.add(Util.getMeasuringTimeNano());
			this.field_21819 = null;
		}
	}

	@Override
	public void push(Supplier<String> supplier) {
		this.push((String)supplier.get());
	}

	@Override
	public void pop() {
		if (!this.tickStarted) {
			LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
		} else if (this.timeList.isEmpty()) {
			LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
		} else {
			long l = Util.getMeasuringTimeNano();
			long m = this.timeList.removeLong(this.timeList.size() - 1);
			this.nameList.remove(this.nameList.size() - 1);
			long n = l - m;
			ProfilerSystem.class_4746 lv = this.method_24246();
			lv.field_21820 = lv.field_21820 + n;
			lv.field_21821 = lv.field_21821 + 1L;
			if (this.field_20345 && n > TIMEOUT_NANOSECONDS) {
				LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", () -> ProfileResult.method_21721(this.location), () -> (double)n / 1000000.0);
			}

			this.location = this.nameList.isEmpty() ? "" : (String)this.nameList.get(this.nameList.size() - 1);
			this.field_21819 = null;
		}
	}

	@Override
	public void swap(String string) {
		this.pop();
		this.push(string);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void swap(Supplier<String> supplier) {
		this.pop();
		this.push(supplier);
	}

	private ProfilerSystem.class_4746 method_24246() {
		if (this.field_21819 == null) {
			this.field_21819 = (ProfilerSystem.class_4746)this.field_21818.computeIfAbsent(this.location, string -> new ProfilerSystem.class_4746());
		}

		return this.field_21819;
	}

	@Override
	public void method_24270(String string) {
		this.method_24246().field_21822.addTo(string, 1L);
	}

	@Override
	public void method_24271(Supplier<String> supplier) {
		this.method_24246().field_21822.addTo((String)supplier.get(), 1L);
	}

	@Override
	public ProfileResult getResults() {
		return new ProfileResultImpl(this.field_21818, this.field_15732, this.field_15729, Util.getMeasuringTimeNano(), this.field_16266.getAsInt());
	}

	static class class_4746 implements class_4748 {
		private long field_21820;
		private long field_21821;
		private Object2LongOpenHashMap<String> field_21822 = new Object2LongOpenHashMap<>();

		private class_4746() {
		}

		@Override
		public long method_24272() {
			return this.field_21820;
		}

		@Override
		public long method_24273() {
			return this.field_21821;
		}

		@Override
		public Object2LongMap<String> method_24274() {
			return Object2LongMaps.unmodifiable(this.field_21822);
		}
	}
}
