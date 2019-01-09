package net.minecraft;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3304 implements class_3296 {
	private static final Logger field_14295 = LogManager.getLogger();
	private final Map<String, class_3294> field_14293 = Maps.<String, class_3294>newHashMap();
	private final List<class_3302> field_14291 = Lists.<class_3302>newArrayList();
	private final Set<String> field_14292 = Sets.<String>newLinkedHashSet();
	private final class_3264 field_14294;

	public class_3304(class_3264 arg) {
		this.field_14294 = arg;
	}

	public void method_14494(class_3262 arg) {
		for (String string : arg.method_14406(this.field_14294)) {
			this.field_14292.add(string);
			class_3294 lv = (class_3294)this.field_14293.get(string);
			if (lv == null) {
				lv = new class_3294(this.field_14294);
				this.field_14293.put(string, lv);
			}

			lv.method_14475(arg);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Set<String> method_14487() {
		return this.field_14292;
	}

	@Override
	public class_3298 method_14486(class_2960 arg) throws IOException {
		class_3300 lv = (class_3300)this.field_14293.get(arg.method_12836());
		if (lv != null) {
			return lv.method_14486(arg);
		} else {
			throw new FileNotFoundException(arg.toString());
		}
	}

	@Override
	public List<class_3298> method_14489(class_2960 arg) throws IOException {
		class_3300 lv = (class_3300)this.field_14293.get(arg.method_12836());
		if (lv != null) {
			return lv.method_14489(arg);
		} else {
			throw new FileNotFoundException(arg.toString());
		}
	}

	@Override
	public Collection<class_2960> method_14488(String string, Predicate<String> predicate) {
		Set<class_2960> set = Sets.<class_2960>newHashSet();

		for (class_3294 lv : this.field_14293.values()) {
			set.addAll(lv.method_14488(string, predicate));
		}

		List<class_2960> list = Lists.<class_2960>newArrayList(set);
		Collections.sort(list);
		return list;
	}

	private void method_14495() {
		this.field_14293.clear();
		this.field_14292.clear();
	}

	@Override
	public void method_14478(List<class_3262> list) {
		this.method_14495();
		field_14295.info("Reloading ResourceManager: {}", list.stream().map(class_3262::method_14409).collect(Collectors.joining(", ")));

		for (class_3262 lv : list) {
			this.method_14494(lv);
		}

		if (field_14295.isDebugEnabled()) {
			this.method_14497();
		} else {
			this.method_14496();
		}
	}

	@Override
	public void method_14477(class_3302 arg) {
		this.field_14291.add(arg);
		if (field_14295.isDebugEnabled()) {
			field_14295.info(this.method_14493(arg));
		} else {
			arg.method_14491(this);
		}
	}

	private void method_14496() {
		for (class_3302 lv : this.field_14291) {
			lv.method_14491(this);
		}
	}

	private void method_14497() {
		field_14295.info("Reloading all resources! {} listeners to update.", this.field_14291.size());
		List<String> list = Lists.<String>newArrayList();
		Stopwatch stopwatch = Stopwatch.createStarted();

		for (class_3302 lv : this.field_14291) {
			list.add(this.method_14493(lv));
		}

		stopwatch.stop();
		field_14295.info("----");
		field_14295.info("Complete resource reload took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

		for (String string : list) {
			field_14295.info(string);
		}

		field_14295.info("----");
	}

	private String method_14493(class_3302 arg) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		arg.method_14491(this);
		stopwatch.stop();
		return "Resource reload for " + arg.getClass().getSimpleName() + " took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms";
	}
}
