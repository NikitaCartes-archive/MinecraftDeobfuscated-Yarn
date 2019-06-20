package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4180<R extends class_4213> extends class_2867 {
	private static final Logger field_18691 = LogManager.getLogger();
	private final Long2ObjectMap<Optional<R>> field_18692 = new Long2ObjectOpenHashMap<>();
	private final LongLinkedOpenHashSet field_18693 = new LongLinkedOpenHashSet();
	private final BiFunction<Runnable, Dynamic<?>, R> field_18694;
	private final Function<Runnable, R> field_18695;
	private final DataFixer field_19228;
	private final class_4284 field_19229;

	public class_4180(File file, BiFunction<Runnable, Dynamic<?>, R> biFunction, Function<Runnable, R> function, DataFixer dataFixer, class_4284 arg) {
		super(file);
		this.field_18694 = biFunction;
		this.field_18695 = function;
		this.field_19228 = dataFixer;
		this.field_19229 = arg;
	}

	protected void method_19290(BooleanSupplier booleanSupplier) {
		while (!this.field_18693.isEmpty() && booleanSupplier.getAsBoolean()) {
			class_1923 lv = class_4076.method_18677(this.field_18693.firstLong()).method_18692();
			this.method_20370(lv);
		}
	}

	@Nullable
	protected Optional<R> method_19293(long l) {
		return this.field_18692.get(l);
	}

	protected Optional<R> method_19294(long l) {
		class_4076 lv = class_4076.method_18677(l);
		if (this.method_19292(lv)) {
			return Optional.empty();
		} else {
			Optional<R> optional = this.method_19293(l);
			if (optional != null) {
				return optional;
			} else {
				this.method_19289(lv.method_18692());
				optional = this.method_19293(l);
				if (optional == null) {
					throw new IllegalStateException();
				} else {
					return optional;
				}
			}
		}
	}

	protected boolean method_19292(class_4076 arg) {
		return class_1937.method_8476(class_4076.method_18688(arg.method_18683()));
	}

	protected R method_19295(long l) {
		Optional<R> optional = this.method_19294(l);
		if (optional.isPresent()) {
			return (R)optional.get();
		} else {
			R lv = (R)this.field_18695.apply((Runnable)() -> this.method_19288(l));
			this.field_18692.put(l, Optional.of(lv));
			return lv;
		}
	}

	private void method_19289(class_1923 arg) {
		this.method_20368(arg, class_2509.field_11560, this.method_20621(arg));
	}

	@Nullable
	private class_2487 method_20621(class_1923 arg) {
		try {
			return this.method_17911(arg);
		} catch (IOException var3) {
			field_18691.error("Error reading chunk {} data from disk", arg, var3);
			return null;
		}
	}

	private <T> void method_20368(class_1923 arg, DynamicOps<T> dynamicOps, @Nullable T object) {
		if (object == null) {
			for (int i = 0; i < 16; i++) {
				this.field_18692.put(class_4076.method_18681(arg, i).method_18694(), Optional.empty());
			}
		} else {
			Dynamic<T> dynamic = new Dynamic<>(dynamicOps, object);
			int j = method_20369(dynamic);
			int k = class_155.method_16673().getWorldVersion();
			boolean bl = j != k;
			Dynamic<T> dynamic2 = this.field_19228.update(this.field_19229.method_20329(), dynamic, j, k);
			OptionalDynamic<T> optionalDynamic = dynamic2.get("Sections");

			for (int l = 0; l < 16; l++) {
				long m = class_4076.method_18681(arg, l).method_18694();
				Optional<R> optional = optionalDynamic.get(Integer.toString(l))
					.get()
					.map(dynamicx -> (class_4213)this.field_18694.apply((Runnable)() -> this.method_19288(m), dynamicx));
				this.field_18692.put(m, optional);
				optional.ifPresent(argx -> {
					this.method_19291(m);
					if (bl) {
						this.method_19288(m);
					}
				});
			}
		}
	}

	private void method_20370(class_1923 arg) {
		Dynamic<class_2520> dynamic = this.method_20367(arg, class_2509.field_11560);
		class_2520 lv = dynamic.getValue();
		if (lv instanceof class_2487) {
			try {
				this.method_17910(arg, (class_2487)lv);
			} catch (IOException var5) {
				field_18691.error("Error writing data to disk", (Throwable)var5);
			}
		} else {
			field_18691.error("Expected compound tag, got {}", lv);
		}
	}

	private <T> Dynamic<T> method_20367(class_1923 arg, DynamicOps<T> dynamicOps) {
		Map<T, T> map = Maps.<T, T>newHashMap();

		for (int i = 0; i < 16; i++) {
			long l = class_4076.method_18681(arg, i).method_18694();
			this.field_18693.remove(l);
			Optional<R> optional = this.field_18692.get(l);
			if (optional != null && optional.isPresent()) {
				map.put(dynamicOps.createString(Integer.toString(i)), ((class_4213)optional.get()).method_19508(dynamicOps));
			}
		}

		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("Sections"),
					dynamicOps.createMap(map),
					dynamicOps.createString("DataVersion"),
					dynamicOps.createInt(class_155.method_16673().getWorldVersion())
				)
			)
		);
	}

	protected void method_19291(long l) {
	}

	protected void method_19288(long l) {
		Optional<R> optional = this.field_18692.get(l);
		if (optional != null && optional.isPresent()) {
			this.field_18693.add(l);
		} else {
			field_18691.warn("No data for position: {}", class_4076.method_18677(l));
		}
	}

	private static int method_20369(Dynamic<?> dynamic) {
		return ((Number)dynamic.get("DataVersion").asNumber().orElse(1945)).intValue();
	}

	public void method_20436(class_1923 arg) {
		if (!this.field_18693.isEmpty()) {
			for (int i = 0; i < 16; i++) {
				long l = class_4076.method_18681(arg, i).method_18694();
				if (this.field_18693.contains(l)) {
					this.method_20370(arg);
					return;
				}
			}
		}
	}
}
