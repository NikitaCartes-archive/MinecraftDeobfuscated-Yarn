package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4157 implements class_4213 {
	private static final Logger field_18496 = LogManager.getLogger();
	private final Short2ObjectMap<class_4156> field_18497 = new Short2ObjectOpenHashMap<>();
	private final Map<class_4158, Set<class_4156>> field_18498 = Maps.<class_4158, Set<class_4156>>newHashMap();
	private final Runnable field_18499;
	private boolean field_19226;

	public class_4157(Runnable runnable) {
		this.field_18499 = runnable;
		this.field_19226 = true;
	}

	public <T> class_4157(Runnable runnable, Dynamic<T> dynamic) {
		this.field_18499 = runnable;

		try {
			this.field_19226 = dynamic.get("Valid").asBoolean(false);
			dynamic.get("Records").asStream().forEach(dynamicx -> this.method_20350(new class_4156(dynamicx, runnable)));
		} catch (Exception var4) {
			field_18496.error("Failed to load POI chunk", (Throwable)var4);
			this.method_20395();
			this.field_19226 = false;
		}
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
		if (this.method_20350(new class_4156(arg, arg2, this.field_18499))) {
			field_18496.debug("Added POI of type {} @ {}", () -> arg2, () -> arg);
			this.field_18499.run();
		}
	}

	private boolean method_20350(class_4156 arg) {
		class_2338 lv = arg.method_19141();
		class_4158 lv2 = arg.method_19142();
		short s = class_4076.method_19454(lv);
		class_4156 lv3 = this.field_18497.get(s);
		if (lv3 != null) {
			if (lv2.equals(lv3.method_19142())) {
				return false;
			} else {
				throw new IllegalStateException("POI data mismatch: already registered at " + lv);
			}
		} else {
			this.field_18497.put(s, arg);
			((Set)this.field_18498.computeIfAbsent(lv2, argx -> Sets.newHashSet())).add(arg);
			return true;
		}
	}

	public void method_19145(class_2338 arg) {
		class_4156 lv = this.field_18497.remove(class_4076.method_19454(arg));
		if (lv == null) {
			field_18496.error("POI data mismatch: never registered at " + arg);
		} else {
			((Set)this.field_18498.get(lv.method_19142())).remove(lv);
			field_18496.debug("Removed POI of type {} @ {}", lv::method_19142, lv::method_19141);
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
		T object = dynamicOps.createList(this.field_18497.values().stream().map(arg -> arg.method_19508(dynamicOps)));
		return dynamicOps.createMap(
			ImmutableMap.of(dynamicOps.createString("Records"), object, dynamicOps.createString("Valid"), dynamicOps.createBoolean(this.field_19226))
		);
	}

	public void method_20353(Consumer<BiConsumer<class_2338, class_4158>> consumer) {
		if (!this.field_19226) {
			Short2ObjectMap<class_4156> short2ObjectMap = new Short2ObjectOpenHashMap<>(this.field_18497);
			this.method_20395();
			consumer.accept((BiConsumer)(arg, arg2) -> {
				short s = class_4076.method_19454(arg);
				class_4156 lv = short2ObjectMap.computeIfAbsent(s, i -> new class_4156(arg, arg2, this.field_18499));
				this.method_20350(lv);
			});
			this.field_19226 = true;
			this.field_18499.run();
		}
	}

	private void method_20395() {
		this.field_18497.clear();
		this.field_18498.clear();
	}
}
