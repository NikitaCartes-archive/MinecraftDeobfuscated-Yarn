package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4157 implements class_4213 {
	private static final Logger field_18496 = LogManager.getLogger();
	private final Short2ObjectMap<class_4156> field_18497 = new Short2ObjectOpenHashMap<>();
	private final Map<class_4158, Set<class_4156>> field_18498 = Maps.<class_4158, Set<class_4156>>newHashMap();
	private final Runnable field_18499;

	public class_4157(Runnable runnable) {
		this.field_18499 = runnable;
	}

	public <T> class_4157(Runnable runnable, Dynamic<T> dynamic) {
		this(runnable);
		dynamic.asStream().forEach(dynamicx -> {
			class_4156 lv = new class_4156(dynamicx, runnable);
			this.field_18497.put(class_4076.method_19454(lv.method_19141()), lv);
			((Set)this.field_18498.computeIfAbsent(lv.method_19142(), arg -> Sets.newHashSet())).add(lv);
		});
	}

	public Stream<class_4156> method_19150(Predicate<class_4158> predicate, class_4153.class_4155 arg) {
		return this.field_18498
			.entrySet()
			.stream()
			.filter(entry -> predicate.test(entry.getKey()))
			.flatMap(entry -> ((Set)entry.getValue()).stream())
			.filter(arg.method_19135());
	}

	public void method_19146(class_2338 arg, class_4158 arg2) {
		short s = class_4076.method_19454(arg);
		class_4156 lv = this.field_18497.get(s);
		if (lv != null) {
			if (!arg2.equals(lv.method_19142())) {
				throw new IllegalStateException("POI data mismatch: already registered at " + arg);
			}
		} else {
			class_4156 lv2 = new class_4156(arg, arg2, this.field_18499);
			this.field_18497.put(s, lv2);
			((Set)this.field_18498.computeIfAbsent(arg2, argx -> Sets.newHashSet())).add(lv2);
			field_18496.debug(String.format("Added POI of type %s @ %s", arg2, arg));
			this.field_18499.run();
		}
	}

	public void method_19145(class_2338 arg) {
		class_4156 lv = this.field_18497.remove(class_4076.method_19454(arg));
		if (lv == null) {
			field_18496.error("POI data mismatch: never registered at " + arg);
		} else {
			((Set)this.field_18498.get(lv.method_19142())).remove(lv);
			field_18496.debug(String.format("Removed POI of type %s @ %s", lv.method_19142(), lv.method_19141()));
			this.field_18499.run();
		}
	}

	public boolean method_19153(class_2338 arg) {
		class_4156 lv = this.field_18497.get(class_4076.method_19454(arg));
		if (lv == null) {
			throw new IllegalStateException("POI never registered at " + arg);
		} else {
			boolean bl = lv.method_19138();
			this.field_18499.run();
			return bl;
		}
	}

	public boolean method_19147(class_2338 arg, Predicate<class_4158> predicate) {
		short s = class_4076.method_19454(arg);
		class_4156 lv = this.field_18497.get(s);
		return lv != null && predicate.test(lv.method_19142());
	}

	public Optional<class_4158> method_19154(class_2338 arg) {
		short s = class_4076.method_19454(arg);
		class_4156 lv = this.field_18497.get(s);
		return lv != null ? Optional.of(lv.method_19142()) : Optional.empty();
	}

	@Override
	public <T> T method_19508(DynamicOps<T> dynamicOps) {
		return dynamicOps.createList(this.field_18497.values().stream().map(arg -> arg.method_19508(dynamicOps)));
	}
}
