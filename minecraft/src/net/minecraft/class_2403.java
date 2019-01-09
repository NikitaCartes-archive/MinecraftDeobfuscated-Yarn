package net.minecraft;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2403 {
	private static final Logger field_11275 = LogManager.getLogger();
	private final Collection<Path> field_11272;
	private final Path field_11274;
	private final List<class_2405> field_11273 = Lists.<class_2405>newArrayList();

	public class_2403(Path path, Collection<Path> collection) {
		this.field_11274 = path;
		this.field_11272 = collection;
	}

	public Collection<Path> method_10312() {
		return this.field_11272;
	}

	public Path method_10313() {
		return this.field_11274;
	}

	public void method_10315() throws IOException {
		class_2408 lv = new class_2408(this.field_11274, "cache");
		lv.method_16674(this.method_10313().resolve("version.json"));
		Stopwatch stopwatch = Stopwatch.createUnstarted();

		for (class_2405 lv2 : this.field_11273) {
			field_11275.info("Starting provider: {}", lv2.method_10321());
			stopwatch.start();
			lv2.method_10319(lv);
			stopwatch.stop();
			field_11275.info("{} finished after {} ms", lv2.method_10321(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
			stopwatch.reset();
		}

		lv.method_10326();
	}

	public void method_10314(class_2405 arg) {
		this.field_11273.add(arg);
	}

	static {
		class_2966.method_12851();
	}
}
