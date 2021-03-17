package net.minecraft;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.ReadableProfiler;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class class_5970 {
	private final class_5951 field_29614;
	private final Supplier<ReadableProfiler> field_29615;

	public class_5970(class_5951 arg, Supplier<ReadableProfiler> supplier) {
		this.field_29614 = arg;
		this.field_29615 = supplier;
	}

	public class_5970(String string, Supplier<ReadableProfiler> supplier) {
		this(new class_5951(string), supplier);
	}

	public class_5965 method_34799(String... strings) {
		if (strings.length == 0) {
			throw new IllegalArgumentException("Expected at least one path node, got no values");
		} else {
			String string = StringUtils.join((Object[])strings, '\u001e');
			return class_5965.method_34776(this.field_29614, () -> {
				ProfilerSystem.LocatedInfo locatedInfo = ((ReadableProfiler)this.field_29615.get()).method_34696(string);
				return locatedInfo == null ? -1.0 : (double)locatedInfo.getTotalTime() / 1000000.0;
			});
		}
	}
}
