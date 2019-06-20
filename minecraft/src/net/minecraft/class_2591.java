package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2591<T extends class_2586> {
	private static final Logger field_11893 = LogManager.getLogger();
	public static final class_2591<class_3866> field_11903 = method_11030("furnace", class_2591.class_2592.method_20528(class_3866::new, class_2246.field_10181));
	public static final class_2591<class_2595> field_11914 = method_11030("chest", class_2591.class_2592.method_20528(class_2595::new, class_2246.field_10034));
	public static final class_2591<class_2646> field_11891 = method_11030(
		"trapped_chest", class_2591.class_2592.method_20528(class_2646::new, class_2246.field_10380)
	);
	public static final class_2591<class_2611> field_11901 = method_11030(
		"ender_chest", class_2591.class_2592.method_20528(class_2611::new, class_2246.field_10443)
	);
	public static final class_2591<class_2619> field_11907 = method_11030("jukebox", class_2591.class_2592.method_20528(class_2619::new, class_2246.field_10223));
	public static final class_2591<class_2601> field_11887 = method_11030("dispenser", class_2591.class_2592.method_20528(class_2601::new, class_2246.field_10200));
	public static final class_2591<class_2608> field_11899 = method_11030("dropper", class_2591.class_2592.method_20528(class_2608::new, class_2246.field_10228));
	public static final class_2591<class_2625> field_11911 = method_11030(
		"sign",
		class_2591.class_2592.method_20528(
			class_2625::new,
			class_2246.field_10121,
			class_2246.field_10411,
			class_2246.field_10231,
			class_2246.field_10284,
			class_2246.field_10544,
			class_2246.field_10330,
			class_2246.field_10187,
			class_2246.field_10088,
			class_2246.field_10391,
			class_2246.field_10401,
			class_2246.field_10587,
			class_2246.field_10265
		)
	);
	public static final class_2591<class_2636> field_11889 = method_11030(
		"mob_spawner", class_2591.class_2592.method_20528(class_2636::new, class_2246.field_10260)
	);
	public static final class_2591<class_2669> field_11897 = method_11030("piston", class_2591.class_2592.method_20528(class_2669::new, class_2246.field_10008));
	public static final class_2591<class_2589> field_11894 = method_11030(
		"brewing_stand", class_2591.class_2592.method_20528(class_2589::new, class_2246.field_10333)
	);
	public static final class_2591<class_2605> field_11912 = method_11030(
		"enchanting_table", class_2591.class_2592.method_20528(class_2605::new, class_2246.field_10485)
	);
	public static final class_2591<class_2640> field_11898 = method_11030(
		"end_portal", class_2591.class_2592.method_20528(class_2640::new, class_2246.field_10027)
	);
	public static final class_2591<class_2580> field_11890 = method_11030("beacon", class_2591.class_2592.method_20528(class_2580::new, class_2246.field_10327));
	public static final class_2591<class_2631> field_11913 = method_11030(
		"skull",
		class_2591.class_2592.method_20528(
			class_2631::new,
			class_2246.field_10481,
			class_2246.field_10388,
			class_2246.field_10042,
			class_2246.field_10509,
			class_2246.field_10337,
			class_2246.field_10472,
			class_2246.field_10241,
			class_2246.field_10581,
			class_2246.field_10177,
			class_2246.field_10101,
			class_2246.field_10432,
			class_2246.field_10208
		)
	);
	public static final class_2591<class_2603> field_11900 = method_11030(
		"daylight_detector", class_2591.class_2592.method_20528(class_2603::new, class_2246.field_10429)
	);
	public static final class_2591<class_2614> field_11888 = method_11030("hopper", class_2591.class_2592.method_20528(class_2614::new, class_2246.field_10312));
	public static final class_2591<class_2599> field_11908 = method_11030(
		"comparator", class_2591.class_2592.method_20528(class_2599::new, class_2246.field_10377)
	);
	public static final class_2591<class_2573> field_11905 = method_11030(
		"banner",
		class_2591.class_2592.method_20528(
			class_2573::new,
			class_2246.field_10154,
			class_2246.field_10045,
			class_2246.field_10438,
			class_2246.field_10452,
			class_2246.field_10547,
			class_2246.field_10229,
			class_2246.field_10612,
			class_2246.field_10185,
			class_2246.field_9985,
			class_2246.field_10165,
			class_2246.field_10368,
			class_2246.field_10281,
			class_2246.field_10602,
			class_2246.field_10198,
			class_2246.field_10406,
			class_2246.field_10062,
			class_2246.field_10202,
			class_2246.field_10599,
			class_2246.field_10274,
			class_2246.field_10050,
			class_2246.field_10139,
			class_2246.field_10318,
			class_2246.field_10531,
			class_2246.field_10267,
			class_2246.field_10604,
			class_2246.field_10372,
			class_2246.field_10054,
			class_2246.field_10067,
			class_2246.field_10370,
			class_2246.field_10594,
			class_2246.field_10279,
			class_2246.field_10537
		)
	);
	public static final class_2591<class_2633> field_11895 = method_11030(
		"structure_block", class_2591.class_2592.method_20528(class_2633::new, class_2246.field_10465)
	);
	public static final class_2591<class_2643> field_11906 = method_11030(
		"end_gateway", class_2591.class_2592.method_20528(class_2643::new, class_2246.field_10613)
	);
	public static final class_2591<class_2593> field_11904 = method_11030(
		"command_block", class_2591.class_2592.method_20528(class_2593::new, class_2246.field_10525, class_2246.field_10395, class_2246.field_10263)
	);
	public static final class_2591<class_2627> field_11896 = method_11030(
		"shulker_box",
		class_2591.class_2592.method_20528(
			class_2627::new,
			class_2246.field_10603,
			class_2246.field_10371,
			class_2246.field_10605,
			class_2246.field_10373,
			class_2246.field_10532,
			class_2246.field_10140,
			class_2246.field_10055,
			class_2246.field_10203,
			class_2246.field_10320,
			class_2246.field_10275,
			class_2246.field_10063,
			class_2246.field_10407,
			class_2246.field_10051,
			class_2246.field_10268,
			class_2246.field_10068,
			class_2246.field_10199,
			class_2246.field_10600
		)
	);
	public static final class_2591<class_2587> field_11910 = method_11030(
		"bed",
		class_2591.class_2592.method_20528(
			class_2587::new,
			class_2246.field_10069,
			class_2246.field_10461,
			class_2246.field_10527,
			class_2246.field_10288,
			class_2246.field_10109,
			class_2246.field_10141,
			class_2246.field_10561,
			class_2246.field_10621,
			class_2246.field_10326,
			class_2246.field_10180,
			class_2246.field_10230,
			class_2246.field_10410,
			class_2246.field_10610,
			class_2246.field_10019,
			class_2246.field_10120,
			class_2246.field_10356
		)
	);
	public static final class_2591<class_2597> field_11902 = method_11030("conduit", class_2591.class_2592.method_20528(class_2597::new, class_2246.field_10502));
	public static final class_2591<class_3719> field_16411 = method_11030("barrel", class_2591.class_2592.method_20528(class_3719::new, class_2246.field_16328));
	public static final class_2591<class_3723> field_16414 = method_11030("smoker", class_2591.class_2592.method_20528(class_3723::new, class_2246.field_16334));
	public static final class_2591<class_3720> field_16415 = method_11030(
		"blast_furnace", class_2591.class_2592.method_20528(class_3720::new, class_2246.field_16333)
	);
	public static final class_2591<class_3722> field_16412 = method_11030("lectern", class_2591.class_2592.method_20528(class_3722::new, class_2246.field_16330));
	public static final class_2591<class_3721> field_16413 = method_11030("bell", class_2591.class_2592.method_20528(class_3721::new, class_2246.field_16332));
	public static final class_2591<class_3751> field_16549 = method_11030("jigsaw", class_2591.class_2592.method_20528(class_3751::new, class_2246.field_16540));
	public static final class_2591<class_3924> field_17380 = method_11030("campfire", class_2591.class_2592.method_20528(class_3924::new, class_2246.field_17350));
	private final Supplier<? extends T> field_11892;
	private final Set<class_2248> field_19315;
	private final Type<?> field_11909;

	@Nullable
	public static class_2960 method_11033(class_2591<?> arg) {
		return class_2378.field_11137.method_10221(arg);
	}

	private static <T extends class_2586> class_2591<T> method_11030(String string, class_2591.class_2592<T> arg) {
		Type<?> type = null;

		try {
			type = class_3551.method_15450().getSchema(DataFixUtils.makeKey(class_155.method_16673().getWorldVersion())).getChoiceType(class_1208.field_5727, string);
		} catch (IllegalStateException var4) {
			if (class_155.field_1125) {
				throw var4;
			}

			field_11893.warn("No data fixer registered for block entity {}", string);
		}

		if (arg.field_19316.isEmpty()) {
			field_11893.warn("Block entity type {} requires at least one valid block to be defined!", string);
		}

		return class_2378.method_10226(class_2378.field_11137, string, arg.method_11034(type));
	}

	public class_2591(Supplier<? extends T> supplier, Set<class_2248> set, Type<?> type) {
		this.field_11892 = supplier;
		this.field_19315 = set;
		this.field_11909 = type;
	}

	@Nullable
	public T method_11032() {
		return (T)this.field_11892.get();
	}

	public boolean method_20526(class_2248 arg) {
		return this.field_19315.contains(arg);
	}

	public static final class class_2592<T extends class_2586> {
		private final Supplier<? extends T> field_11915;
		private final Set<class_2248> field_19316;

		private class_2592(Supplier<? extends T> supplier, Set<class_2248> set) {
			this.field_11915 = supplier;
			this.field_19316 = set;
		}

		public static <T extends class_2586> class_2591.class_2592<T> method_20528(Supplier<? extends T> supplier, class_2248... args) {
			return new class_2591.class_2592<>(supplier, ImmutableSet.copyOf(args));
		}

		public class_2591<T> method_11034(Type<?> type) {
			return new class_2591<>(this.field_11915, this.field_19316, type);
		}
	}
}
