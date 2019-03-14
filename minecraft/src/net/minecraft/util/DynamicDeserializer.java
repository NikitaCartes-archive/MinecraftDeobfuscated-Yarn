package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface DynamicDeserializer<T> {
	Logger LOGGER = LogManager.getLogger();

	T deserialize(Dynamic<?> dynamic);

	static <T, V, U extends DynamicDeserializer<V>> V deserialize(Dynamic<T> dynamic, Registry<U> registry, String string, V object) {
		U dynamicDeserializer = (U)registry.get(new Identifier(dynamic.get(string).asString("")));
		V object2;
		if (dynamicDeserializer != null) {
			object2 = dynamicDeserializer.deserialize(dynamic);
		} else {
			LOGGER.error("Unknown type {}, replacing with {}", dynamic.get(string).asString(""), object);
			object2 = object;
		}

		return object2;
	}
}
