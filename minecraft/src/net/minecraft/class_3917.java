package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3917<T extends class_1703> {
	public static final class_3917<class_1707.class_3912> field_17326 = method_17435("generic_9x3", class_1707.class_3912::new);
	public static final class_3917<class_1707.class_3911> field_17327 = method_17435("generic_9x6", class_1707.class_3911::new);
	public static final class_3917<class_1716> field_17328 = method_17435("generic_3x3", class_1716::new);
	public static final class_3917<class_1706> field_17329 = method_17435("anvil", class_1706::new);
	public static final class_3917<class_1704> field_17330 = method_17435("beacon", class_1704::new);
	public static final class_3917<class_3705> field_17331 = method_17435("blast_furnace", class_3705::new);
	public static final class_3917<class_1708> field_17332 = method_17435("brewing_stand", class_1708::new);
	public static final class_3917<class_1714> field_17333 = method_17435("crafting", class_1714::new);
	public static final class_3917<class_1718> field_17334 = method_17435("enchantment", class_1718::new);
	public static final class_3917<class_3858> field_17335 = method_17435("furnace", class_3858::new);
	public static final class_3917<class_3803> field_17336 = method_17435("grindstone", class_3803::new);
	public static final class_3917<class_1722> field_17337 = method_17435("hopper", class_1722::new);
	public static final class_3917<class_3916> field_17338 = method_17435("lectern", (i, arg) -> new class_3916(i));
	public static final class_3917<class_1726> field_17339 = method_17435("loom", class_1726::new);
	public static final class_3917<class_1728> field_17340 = method_17435("merchant", class_1728::new);
	public static final class_3917<class_1733> field_17341 = method_17435("shulker_box", class_1733::new);
	public static final class_3917<class_3706> field_17342 = method_17435("smoker", class_3706::new);
	public static final class_3917<class_3910> field_17343 = method_17435("cartography", class_3910::new);
	private final class_3917.class_3918<T> field_17344;

	private static <T extends class_1703> class_3917<T> method_17435(String string, class_3917.class_3918<T> arg) {
		return class_2378.method_10226(class_2378.field_17429, string, new class_3917<>(arg));
	}

	private class_3917(class_3917.class_3918<T> arg) {
		this.field_17344 = arg;
	}

	@Environment(EnvType.CLIENT)
	public T method_17434(int i, class_1661 arg) {
		return this.field_17344.create(i, arg);
	}

	interface class_3918<T extends class_1703> {
		@Environment(EnvType.CLIENT)
		T create(int i, class_1661 arg);
	}
}
