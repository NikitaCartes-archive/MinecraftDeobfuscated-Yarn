package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_2378<T> implements class_2359<T> {
	protected static final Logger field_11139 = LogManager.getLogger();
	private static final Map<class_2960, Supplier<?>> field_11140 = Maps.<class_2960, Supplier<?>>newLinkedHashMap();
	public static final class_2385<class_2385<?>> field_11144 = new class_2370<>();
	public static final class_2378<class_3414> field_11156 = method_10224("sound_event", new class_2370<>(), () -> class_3417.field_15197);
	public static final class_2348<class_3611> field_11154 = method_10224("fluid", new class_2348<>("empty"), () -> class_3612.field_15906);
	public static final class_2378<class_1291> field_11159 = method_10224("mob_effect", new class_2370<>(), () -> class_1294.field_5926);
	public static final class_2348<class_2248> field_11146 = method_10224("block", new class_2348<>("air"), () -> class_2246.field_10124);
	public static final class_2378<class_1887> field_11160 = method_10224("enchantment", new class_2370<>(), () -> class_1893.field_9130);
	public static final class_2378<class_1299<?>> field_11145 = method_10224("entity_type", new class_2370<>(), () -> class_1299.field_6046);
	public static final class_2348<class_1792> field_11142 = method_10224("item", new class_2348<>("air"), () -> class_1802.field_8162);
	public static final class_2378<class_1842> field_11143 = method_10224("potion", new class_2348<>("empty"), () -> class_1847.field_8984);
	public static final class_2378<class_2939<?>> field_11157 = method_10224("carver", new class_2370<>(), () -> class_2939.field_13304);
	public static final class_2378<class_3523<?>> field_11147 = method_10224("surface_builder", new class_2370<>(), () -> class_3523.field_15701);
	public static final class_2378<class_3031<?>> field_11138 = method_10224("feature", new class_2370<>(), () -> class_3031.field_13517);
	public static final class_2378<class_3284<?>> field_11148 = method_10224("decorator", new class_2370<>(), () -> class_3284.field_14250);
	public static final class_2378<class_1959> field_11153 = method_10224("biome", new class_2370<>(), () -> class_1972.field_9469);
	public static final class_2378<class_2396<? extends class_2394>> field_11141 = method_10224("particle_type", new class_2370<>(), () -> class_2398.field_11217);
	public static final class_2378<class_1969<?, ?>> field_11151 = method_10224("biome_source_type", new class_2370<>(), () -> class_1969.field_9402);
	public static final class_2378<class_2591<?>> field_11137 = method_10224("block_entity_type", new class_2370<>(), () -> class_2591.field_11903);
	public static final class_2378<class_2798<?, ?>> field_11149 = method_10224("chunk_generator_type", new class_2370<>(), () -> class_2798.field_12766);
	public static final class_2378<class_2874> field_11155 = method_10224("dimension_type", new class_2370<>(), () -> class_2874.field_13072);
	public static final class_2348<class_1535> field_11150 = method_10224("motive", new class_2348<>("kebab"), () -> class_1535.field_7146);
	public static final class_2378<class_2960> field_11158 = method_10224("custom_stat", new class_2370<>(), () -> class_3468.field_15428);
	public static final class_2348<class_2806> field_16643 = method_10224("chunk_status", new class_2348<>("empty"), () -> class_2806.field_12798);
	public static final class_2378<class_3195<?>> field_16644 = method_10224("structure_feature", new class_2370<>(), () -> class_3420.field_16709);
	public static final class_2378<class_3773> field_16645 = method_10224("structure_piece", new class_2370<>(), () -> class_3773.field_16915);
	public static final class_2378<class_3827> field_16792 = method_10224("rule_test", new class_2370<>(), () -> class_3827.field_16982);
	public static final class_2378<class_3828> field_16794 = method_10224("structure_processor", new class_2370<>(), () -> class_3828.field_16986);
	public static final class_2378<class_3816> field_16793 = method_10224("structure_pool_element", new class_2370<>(), () -> class_3816.field_16972);
	public static final class_2378<class_3917<?>> field_17429 = method_10224("menu", new class_2370<>(), () -> class_3917.field_17329);
	public static final class_2378<class_3448<?>> field_11152 = method_10247("stat_type", new class_2370<>());
	public static final class_2348<class_3854> field_17166 = method_10224("villager_type", new class_2348<>("plains"), () -> class_3854.field_17073);
	public static final class_2348<class_3852> field_17167 = method_10224("villager_profession", new class_2348<>("none"), () -> class_3852.field_17051);

	private static <T> void method_10227(String string, Supplier<T> supplier) {
		field_11140.put(new class_2960(string), supplier);
	}

	private static <T, R extends class_2385<T>> R method_10224(String string, R arg, Supplier<T> supplier) {
		method_10227(string, supplier);
		method_10247(string, arg);
		return arg;
	}

	private static <T> class_2378<T> method_10247(String string, class_2385<T> arg) {
		field_11144.method_10272(new class_2960(string), arg);
		return arg;
	}

	@Nullable
	public abstract class_2960 method_10221(T object);

	public abstract int method_10249(@Nullable T object);

	@Nullable
	public abstract T method_10223(@Nullable class_2960 arg);

	public abstract Set<class_2960> method_10235();

	@Nullable
	public abstract T method_10240(Random random);

	public Stream<T> method_10220() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	public abstract boolean method_10250(class_2960 arg);

	public static <T> T method_10226(class_2378<? super T> arg, String string, T object) {
		return method_10230(arg, new class_2960(string), object);
	}

	public static <T> T method_10230(class_2378<? super T> arg, class_2960 arg2, T object) {
		return ((class_2385)arg).method_10272(arg2, object);
	}

	public static <T> T method_10231(class_2378<? super T> arg, int i, String string, T object) {
		return ((class_2385)arg).method_10273(i, new class_2960(string), object);
	}

	static {
		field_11140.entrySet().forEach(entry -> {
			if (((Supplier)entry.getValue()).get() == null) {
				field_11139.error("Unable to bootstrap registry '{}'", entry.getKey());
			}
		});
		field_11144.forEach(arg -> {
			if (arg.method_10274()) {
				field_11139.error("Registry '{}' was empty after loading", field_11144.method_10221(arg));
				if (class_155.field_1125) {
					throw new IllegalStateException("Registry: '" + field_11144.method_10221(arg) + "' is empty, not allowed, fix me!");
				}
			}

			if (arg instanceof class_2348) {
				class_2960 lv = ((class_2348)arg).method_10137();
				Validate.notNull(arg.method_10223(lv), "Missing default of DefaultedMappedRegistry: " + lv);
			}
		});
	}
}
