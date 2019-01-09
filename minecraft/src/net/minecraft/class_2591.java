package net.minecraft;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2591<T extends class_2586> {
	private static final Logger field_11893 = LogManager.getLogger();
	public static final class_2591<class_3866> field_11903 = method_11030("furnace", class_2591.class_2592.method_11035(class_3866::new));
	public static final class_2591<class_2595> field_11914 = method_11030("chest", class_2591.class_2592.method_11035(class_2595::new));
	public static final class_2591<class_2646> field_11891 = method_11030("trapped_chest", class_2591.class_2592.method_11035(class_2646::new));
	public static final class_2591<class_2611> field_11901 = method_11030("ender_chest", class_2591.class_2592.method_11035(class_2611::new));
	public static final class_2591<class_2619> field_11907 = method_11030("jukebox", class_2591.class_2592.method_11035(class_2619::new));
	public static final class_2591<class_2601> field_11887 = method_11030("dispenser", class_2591.class_2592.method_11035(class_2601::new));
	public static final class_2591<class_2608> field_11899 = method_11030("dropper", class_2591.class_2592.method_11035(class_2608::new));
	public static final class_2591<class_2625> field_11911 = method_11030("sign", class_2591.class_2592.method_11035(class_2625::new));
	public static final class_2591<class_2636> field_11889 = method_11030("mob_spawner", class_2591.class_2592.method_11035(class_2636::new));
	public static final class_2591<class_2669> field_11897 = method_11030("piston", class_2591.class_2592.method_11035(class_2669::new));
	public static final class_2591<class_2589> field_11894 = method_11030("brewing_stand", class_2591.class_2592.method_11035(class_2589::new));
	public static final class_2591<class_2605> field_11912 = method_11030("enchanting_table", class_2591.class_2592.method_11035(class_2605::new));
	public static final class_2591<class_2640> field_11898 = method_11030("end_portal", class_2591.class_2592.method_11035(class_2640::new));
	public static final class_2591<class_2580> field_11890 = method_11030("beacon", class_2591.class_2592.method_11035(class_2580::new));
	public static final class_2591<class_2631> field_11913 = method_11030("skull", class_2591.class_2592.method_11035(class_2631::new));
	public static final class_2591<class_2603> field_11900 = method_11030("daylight_detector", class_2591.class_2592.method_11035(class_2603::new));
	public static final class_2591<class_2614> field_11888 = method_11030("hopper", class_2591.class_2592.method_11035(class_2614::new));
	public static final class_2591<class_2599> field_11908 = method_11030("comparator", class_2591.class_2592.method_11035(class_2599::new));
	public static final class_2591<class_2573> field_11905 = method_11030("banner", class_2591.class_2592.method_11035(class_2573::new));
	public static final class_2591<class_2633> field_11895 = method_11030("structure_block", class_2591.class_2592.method_11035(class_2633::new));
	public static final class_2591<class_2643> field_11906 = method_11030("end_gateway", class_2591.class_2592.method_11035(class_2643::new));
	public static final class_2591<class_2593> field_11904 = method_11030("command_block", class_2591.class_2592.method_11035(class_2593::new));
	public static final class_2591<class_2627> field_11896 = method_11030("shulker_box", class_2591.class_2592.method_11035(class_2627::new));
	public static final class_2591<class_2587> field_11910 = method_11030("bed", class_2591.class_2592.method_11035(class_2587::new));
	public static final class_2591<class_2597> field_11902 = method_11030("conduit", class_2591.class_2592.method_11035(class_2597::new));
	public static final class_2591<class_3719> field_16411 = method_11030("barrel", class_2591.class_2592.method_11035(class_3719::new));
	public static final class_2591<class_3723> field_16414 = method_11030("smoker", class_2591.class_2592.method_11035(class_3723::new));
	public static final class_2591<class_3720> field_16415 = method_11030("blast_furnace", class_2591.class_2592.method_11035(class_3720::new));
	public static final class_2591<class_3722> field_16412 = method_11030("lectern", class_2591.class_2592.method_11035(class_3722::new));
	public static final class_2591<class_3721> field_16413 = method_11030("bell", class_2591.class_2592.method_11035(class_3721::new));
	public static final class_2591<class_3751> field_16549 = method_11030("jigsaw", class_2591.class_2592.method_11035(class_3751::new));
	public static final class_2591<class_3924> field_17380 = method_11030("campfire", class_2591.class_2592.method_11035(class_3924::new));
	private final Supplier<? extends T> field_11892;
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

		return class_2378.method_10226(class_2378.field_11137, string, arg.method_11034(type));
	}

	public class_2591(Supplier<? extends T> supplier, Type<?> type) {
		this.field_11892 = supplier;
		this.field_11909 = type;
	}

	@Nullable
	public T method_11032() {
		return (T)this.field_11892.get();
	}

	@Nullable
	static class_2586 method_11031(String string) {
		class_2591<?> lv = class_2378.field_11137.method_10223(new class_2960(string));
		return lv == null ? null : lv.method_11032();
	}

	public static final class class_2592<T extends class_2586> {
		private final Supplier<? extends T> field_11915;

		private class_2592(Supplier<? extends T> supplier) {
			this.field_11915 = supplier;
		}

		public static <T extends class_2586> class_2591.class_2592<T> method_11035(Supplier<? extends T> supplier) {
			return new class_2591.class_2592<>(supplier);
		}

		public class_2591<T> method_11034(Type<?> type) {
			return new class_2591<>(this.field_11915, type);
		}
	}
}
