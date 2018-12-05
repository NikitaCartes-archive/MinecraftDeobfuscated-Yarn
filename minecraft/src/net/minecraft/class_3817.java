package net.minecraft;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface class_3817<T> {
	Logger LOGGER = LogManager.getLogger();

	T deserialize(Dynamic<?> dynamic);

	static <T, V, U extends class_3817<V>> V deserialize(Dynamic<T> dynamic, Registry<U> registry, String string, V object) {
		U lv = (U)registry.get(new Identifier(dynamic.getString(string)));
		V object2;
		if (lv != null) {
			object2 = lv.deserialize(dynamic);
		} else {
			LOGGER.error("Unknown type {}, replacing with {}", dynamic.getString(string), object);
			object2 = object;
		}

		return object2;
	}
}
