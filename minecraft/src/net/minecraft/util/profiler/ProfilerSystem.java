package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfilerSystem implements ReadableProfiler {
	private static final long TIMEOUT_NANOSECONDS = Duration.ofMillis(100L).toNanos();
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<String> nameList = Lists.<String>newArrayList();
	private final List<Long> timeList = Lists.<Long>newArrayList();
	private final Map<String, Long> nameDurationMap = Maps.<String, Long>newHashMap();
	private final IntSupplier field_16266;
	private final long field_15732;
	private final int field_15729;
	private String location = "";
	private boolean tickStarted;

	public ProfilerSystem(long l, IntSupplier intSupplier) {
		this.field_15732 = l;
		this.field_15729 = intSupplier.getAsInt();
		this.field_16266 = intSupplier;
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
				LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", this.location);
			}
		}
	}

	@Override
	public void push(String string) {
		if (!this.tickStarted) {
			LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", string);
		} else {
			if (!this.location.isEmpty()) {
				this.location = this.location + ".";
			}

			this.location = this.location + string;
			this.nameList.add(this.location);
			this.timeList.add(SystemUtil.getMeasuringTimeNano());
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
			long l = SystemUtil.getMeasuringTimeNano();
			long m = (Long)this.timeList.remove(this.timeList.size() - 1);
			this.nameList.remove(this.nameList.size() - 1);
			long n = l - m;
			if (this.nameDurationMap.containsKey(this.location)) {
				this.nameDurationMap.put(this.location, (Long)this.nameDurationMap.get(this.location) + n);
			} else {
				this.nameDurationMap.put(this.location, n);
			}

			if (n > TIMEOUT_NANOSECONDS) {
				LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", this.location, (double)n / 1000000.0);
			}

			this.location = this.nameList.isEmpty() ? "" : (String)this.nameList.get(this.nameList.size() - 1);
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

	@Override
	public ProfileResult getResults() {
		return new ProfileResultImpl(this.nameDurationMap, this.field_15732, this.field_15729, SystemUtil.getMeasuringTimeNano(), this.field_16266.getAsInt());
	}
}
