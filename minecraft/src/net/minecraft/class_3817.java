package net.minecraft;

import com.mojang.datafixers.Dynamic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface class_3817<T> {
	Logger field_16866 = LogManager.getLogger();

	T deserialize(Dynamic<?> dynamic);

	static <T, V, U extends class_3817<V>> V method_16758(Dynamic<T> dynamic, class_2378<U> arg, String string, V object) {
		U lv = (U)arg.method_10223(new class_2960(dynamic.get(string).asString("")));
		V object2;
		if (lv != null) {
			object2 = lv.deserialize(dynamic);
		} else {
			field_16866.error("Unknown type {}, replacing with {}", dynamic.get(string).asString(""), object);
			object2 = object;
		}

		return object2;
	}
}
