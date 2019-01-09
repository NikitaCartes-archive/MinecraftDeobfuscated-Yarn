package net.minecraft;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2798<C extends class_2888, T extends class_2794<C>> implements class_2801<C, T> {
	public static final class_2798<class_2906, class_2912> field_12769 = method_12116("surface", class_2912::new, class_2906::new, true);
	public static final class_2798<class_2900, class_2908> field_12765 = method_12116("caves", class_2908::new, class_2900::new, true);
	public static final class_2798<class_2916, class_2914> field_12770 = method_12116("floating_islands", class_2914::new, class_2916::new, true);
	public static final class_2798<class_2892, class_2891> field_12768 = method_12116("debug", class_2891::new, class_2892::new, false);
	public static final class_2798<class_3232, class_2897> field_12766 = method_12116("flat", class_2897::new, class_3232::new, false);
	private final class_2801<C, T> field_12772;
	private final boolean field_12767;
	private final Supplier<C> field_12771;

	private static <C extends class_2888, T extends class_2794<C>> class_2798<C, T> method_12116(
		String string, class_2801<C, T> arg, Supplier<C> supplier, boolean bl
	) {
		return class_2378.method_10226(class_2378.field_11149, string, new class_2798<>(arg, bl, supplier));
	}

	public class_2798(class_2801<C, T> arg, boolean bl, Supplier<C> supplier) {
		this.field_12772 = arg;
		this.field_12767 = bl;
		this.field_12771 = supplier;
	}

	@Override
	public T create(class_1937 arg, class_1966 arg2, C arg3) {
		return this.field_12772.create(arg, arg2, arg3);
	}

	public C method_12117() {
		return (C)this.field_12771.get();
	}

	@Environment(EnvType.CLIENT)
	public boolean method_12118() {
		return this.field_12767;
	}
}
