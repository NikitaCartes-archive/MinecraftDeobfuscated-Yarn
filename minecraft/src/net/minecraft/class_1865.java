package net.minecraft;

import com.google.gson.JsonObject;

public interface class_1865<T extends class_1860<?>> {
	class_1865<class_1869> field_9035 = method_17724("crafting_shaped", new class_1869.class_1870());
	class_1865<class_1867> field_9031 = method_17724("crafting_shapeless", new class_1867.class_1868());
	class_1866<class_1849> field_9028 = method_17724("crafting_special_armordye", new class_1866<>(class_1849::new));
	class_1866<class_1850> field_9029 = method_17724("crafting_special_bookcloning", new class_1866<>(class_1850::new));
	class_1866<class_1855> field_9044 = method_17724("crafting_special_mapcloning", new class_1866<>(class_1855::new));
	class_1866<class_1861> field_9039 = method_17724("crafting_special_mapextending", new class_1866<>(class_1861::new));
	class_1866<class_1851> field_9043 = method_17724("crafting_special_firework_rocket", new class_1866<>(class_1851::new));
	class_1866<class_1853> field_9036 = method_17724("crafting_special_firework_star", new class_1866<>(class_1853::new));
	class_1866<class_1854> field_9034 = method_17724("crafting_special_firework_star_fade", new class_1866<>(class_1854::new));
	class_1866<class_1876> field_9037 = method_17724("crafting_special_tippedarrow", new class_1866<>(class_1876::new));
	class_1866<class_1848> field_9038 = method_17724("crafting_special_bannerduplicate", new class_1866<>(class_1848::new));
	class_1866<class_1872> field_9040 = method_17724("crafting_special_shielddecoration", new class_1866<>(class_1872::new));
	class_1866<class_1871> field_9041 = method_17724("crafting_special_shulkerboxcoloring", new class_1866<>(class_1871::new));
	class_1866<class_1873> field_9030 = method_17724("crafting_special_suspiciousstew", new class_1866<>(class_1873::new));
	class_3957<class_3861> field_9042 = method_17724("smelting", new class_3957<>(class_3861::new, 200));
	class_3957<class_3859> field_17084 = method_17724("blasting", new class_3957<>(class_3859::new, 100));
	class_3957<class_3862> field_17085 = method_17724("smoking", new class_3957<>(class_3862::new, 100));
	class_3957<class_3920> field_17347 = method_17724("campfire_cooking", new class_3957<>(class_3920::new, 100));
	class_1865<class_3975> field_17640 = method_17724("stonecutting", new class_3972.class_3973<>(class_3975::new));

	T method_8121(class_2960 arg, JsonObject jsonObject);

	T method_8122(class_2960 arg, class_2540 arg2);

	void method_8124(class_2540 arg, T arg2);

	static <S extends class_1865<T>, T extends class_1860<?>> S method_17724(String string, S arg) {
		return class_2378.method_10226(class_2378.field_17598, string, arg);
	}
}
