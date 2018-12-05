package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3692;
import net.minecraft.class_3693;
import net.minecraft.class_3696;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfilerSystem implements class_3693 {
	private static final long field_16267 = Duration.ofMillis(100L).toNanos();
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<String> nameList = Lists.<String>newArrayList();
	private final List<Long> timeList = Lists.<Long>newArrayList();
	private final Map<String, Long> field_15731 = Maps.<String, Long>newHashMap();
	private final IntSupplier field_16266;
	private final long field_15732;
	private final int field_15729;
	private String location = "";
	private boolean field_15733;

	public ProfilerSystem(long l, IntSupplier intSupplier) {
		this.field_15732 = l;
		this.field_15729 = intSupplier.getAsInt();
		this.field_16266 = intSupplier;
	}

	@Override
	public void method_16065() {
		if (this.field_15733) {
			LOGGER.error("Profiler tick already started - missing endTick()?");
		} else {
			this.field_15733 = true;
			this.location = "";
			this.nameList.clear();
			this.begin("root");
		}
	}

	@Override
	public void method_16066() {
		if (!this.field_15733) {
			LOGGER.error("Profiler tick already ended - missing startTick()?");
		} else {
			this.end();
			this.field_15733 = false;
			if (!this.location.isEmpty()) {
				LOGGER.error("Profiler tick ended before path was fully popped. Mismatched push/pop?");
			}
		}
	}

	@Override
	public void begin(String string) {
		if (!this.field_15733) {
			LOGGER.error("Cannot push to profiler if profiler tick hasn't started - missing startTick()?");
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
	public void begin(Supplier<String> supplier) {
		this.begin((String)supplier.get());
	}

	@Override
	public void end() {
		if (!this.field_15733) {
			LOGGER.error("Cannot push to profiler if profiler tick hasn't started - missing startTick()?");
		} else if (this.timeList.isEmpty()) {
			LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
		} else {
			long l = SystemUtil.getMeasuringTimeNano();
			long m = (Long)this.timeList.remove(this.timeList.size() - 1);
			this.nameList.remove(this.nameList.size() - 1);
			long n = l - m;
			if (this.field_15731.containsKey(this.location)) {
				this.field_15731.put(this.location, (Long)this.field_15731.get(this.location) + n);
			} else {
				this.field_15731.put(this.location, n);
			}

			if (n > field_16267) {
				LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", this.location, (double)n / 1000000.0);
			}

			this.location = this.nameList.isEmpty() ? "" : (String)this.nameList.get(this.nameList.size() - 1);
		}
	}

	@Override
	public void endBegin(String string) {
		this.end();
		this.begin(string);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void endBegin(Supplier<String> supplier) {
		this.end();
		this.begin(supplier);
	}

	@Override
	public class_3696 method_16064() {
		return new class_3692(this.field_15731, this.field_15732, this.field_15729, SystemUtil.getMeasuringTimeNano(), this.field_16266.getAsInt());
	}
}
