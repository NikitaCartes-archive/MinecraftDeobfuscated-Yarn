package net.minecraft;

import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
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

	public class_4180(File file, BiFunction<Runnable, Dynamic<?>, R> biFunction, Function<Runnable, R> function) {
		super(file);
		this.field_18694 = biFunction;
		this.field_18695 = function;
	}

	protected void method_19290(BooleanSupplier booleanSupplier) {
		while (!this.field_18693.isEmpty() && booleanSupplier.getAsBoolean()) {
			class_1923 lv = class_4076.method_18677(this.field_18693.firstLong()).method_18692();
			class_2487 lv2 = new class_2487();

			for (int i = 0; i < 16; i++) {
				long l = class_4076.method_18681(lv, i).method_18694();
				this.field_18693.remove(l);
				Optional<R> optional = this.field_18692.get(l);
				if (optional != null && optional.isPresent()) {
					lv2.method_10566(Integer.toString(i), ((class_4213)optional.get()).method_19508(class_2509.field_11560));
				}
			}

			try {
				this.method_17910(lv, lv2);
			} catch (IOException var8) {
				field_18691.error("Error writing data to disk", (Throwable)var8);
			}
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
		try {
			class_2487 lv = this.method_17911(arg);

			for (int i = 0; i < 16; i++) {
				long l = class_4076.method_18681(arg, i).method_18694();
				this.field_18692.put(l, Optional.empty());
				if (lv != null) {
					try {
						class_2520 lv2 = lv.method_10580(Integer.toString(i));
						if (lv2 != null) {
							this.field_18692.put(l, Optional.of(this.field_18694.apply((Runnable)() -> this.method_19288(l), new Dynamic<>(class_2509.field_11560, lv2))));
							this.method_19291(l);
						}
					} catch (NumberFormatException var7) {
						field_18691.error("Error reading data from disk", (Throwable)var7);
					}
				}
			}
		} catch (IOException var8) {
			field_18691.error("Error reading data from disk", (Throwable)var8);
		}
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
}
