package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3304 implements class_3296 {
	private static final Logger field_14295 = LogManager.getLogger();
	private final Map<String, class_3294> field_14293 = Maps.<String, class_3294>newHashMap();
	private final List<class_3302> field_17935 = Lists.<class_3302>newArrayList();
	private final List<class_3302> field_17936 = Lists.<class_3302>newArrayList();
	private final Set<String> field_14292 = Sets.<String>newLinkedHashSet();
	private final class_3264 field_14294;
	private final Thread field_17937;

	public class_3304(class_3264 arg, Thread thread) {
		this.field_14294 = arg;
		this.field_17937 = thread;
	}

	@Override
	public void method_14475(class_3262 arg) {
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

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_18234(class_2960 arg) {
		class_3300 lv = (class_3300)this.field_14293.get(arg.method_12836());
		return lv != null ? lv.method_18234(arg) : false;
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
	public CompletableFuture<class_3902> method_14478(
		Executor executor, Executor executor2, List<class_3262> list, CompletableFuture<class_3902> completableFuture
	) {
		class_4011 lv = this.method_18232(executor, executor2, completableFuture, list);
		return lv.method_18364();
	}

	@Override
	public void method_14477(class_3302 arg) {
		this.field_17935.add(arg);
		this.field_17936.add(arg);
	}

	protected class_4011 method_18240(Executor executor, Executor executor2, List<class_3302> list, CompletableFuture<class_3902> completableFuture) {
		class_4011 lv;
		if (field_14295.isDebugEnabled()) {
			lv = new class_4010(this, new ArrayList(list), executor, executor2, completableFuture);
		} else {
			lv = class_4014.method_18369(this, new ArrayList(list), executor, executor2, completableFuture);
		}

		this.field_17936.clear();
		return lv;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_4011 method_18230(Executor executor, Executor executor2, CompletableFuture<class_3902> completableFuture) {
		return this.method_18240(executor, executor2, this.field_17936, completableFuture);
	}

	@Override
	public class_4011 method_18232(Executor executor, Executor executor2, CompletableFuture<class_3902> completableFuture, List<class_3262> list) {
		this.method_14495();
		field_14295.info("Reloading ResourceManager: {}", list.stream().map(class_3262::method_14409).collect(Collectors.joining(", ")));

		for (class_3262 lv : list) {
			this.method_14475(lv);
		}

		return this.method_18240(executor, executor2, this.field_17935, completableFuture);
	}
}
