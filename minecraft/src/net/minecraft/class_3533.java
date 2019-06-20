package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.time.Duration;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3533 implements class_3693 {
	private static final long field_16267 = Duration.ofMillis(100L).toNanos();
	private static final Logger field_15735 = LogManager.getLogger();
	private final List<String> field_15736 = Lists.<String>newArrayList();
	private final LongList field_15730 = new LongArrayList();
	private final Object2LongMap<String> field_15731 = new Object2LongOpenHashMap<>();
	private final Object2LongMap<String> field_19381 = new Object2LongOpenHashMap<>();
	private final IntSupplier field_16266;
	private final long field_15732;
	private final int field_15729;
	private String field_15734 = "";
	private boolean field_15733;

	public class_3533(long l, IntSupplier intSupplier) {
		this.field_15732 = l;
		this.field_15729 = intSupplier.getAsInt();
		this.field_16266 = intSupplier;
	}

	@Override
	public void method_16065() {
		if (this.field_15733) {
			field_15735.error("Profiler tick already started - missing endTick()?");
		} else {
			this.field_15733 = true;
			this.field_15734 = "";
			this.field_15736.clear();
			this.method_15396("root");
		}
	}

	@Override
	public void method_16066() {
		if (!this.field_15733) {
			field_15735.error("Profiler tick already ended - missing startTick()?");
		} else {
			this.method_15407();
			this.field_15733 = false;
			if (!this.field_15734.isEmpty()) {
				field_15735.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", this.field_15734);
			}
		}
	}

	@Override
	public void method_15396(String string) {
		if (!this.field_15733) {
			field_15735.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", string);
		} else {
			if (!this.field_15734.isEmpty()) {
				this.field_15734 = this.field_15734 + ".";
			}

			this.field_15734 = this.field_15734 + string;
			this.field_15736.add(this.field_15734);
			this.field_15730.add(class_156.method_648());
		}
	}

	@Override
	public void method_15400(Supplier<String> supplier) {
		this.method_15396((String)supplier.get());
	}

	@Override
	public void method_15407() {
		if (!this.field_15733) {
			field_15735.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
		} else if (this.field_15730.isEmpty()) {
			field_15735.error("Tried to pop one too many times! Mismatched push() and pop()?");
		} else {
			long l = class_156.method_648();
			long m = this.field_15730.removeLong(this.field_15730.size() - 1);
			this.field_15736.remove(this.field_15736.size() - 1);
			long n = l - m;
			this.field_15731.put(this.field_15734, this.field_15731.getLong(this.field_15734) + n);
			this.field_19381.put(this.field_15734, this.field_19381.getLong(this.field_15734) + 1L);
			if (n > field_16267) {
				field_15735.warn("Something's taking too long! '{}' took aprox {} ms", this.field_15734, (double)n / 1000000.0);
			}

			this.field_15734 = this.field_15736.isEmpty() ? "" : (String)this.field_15736.get(this.field_15736.size() - 1);
		}
	}

	@Override
	public void method_15405(String string) {
		this.method_15407();
		this.method_15396(string);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_15403(Supplier<String> supplier) {
		this.method_15407();
		this.method_15400(supplier);
	}

	@Override
	public class_3696 method_16064() {
		return new class_3692(this.field_15731, this.field_19381, this.field_15732, this.field_15729, class_156.method_648(), this.field_16266.getAsInt());
	}
}
