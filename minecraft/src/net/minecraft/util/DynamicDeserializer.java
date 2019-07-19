package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface DynamicDeserializer<T> {
	Logger LOGGER = LogManager.getLogger();

	T deserialize(Dynamic<?> dynamic);

	static <T, V, U extends DynamicDeserializer<V>> V deserialize(Dynamic<T> dynamic2, Registry<U> dynamic, String registry, V typeFieldName) {
		U dynamicDeserializer = (U)dynamic.get(new Identifier(dynamic2.get(registry).asString("")));
		V object;
		if (dynamicDeserializer != null) {
			object = dynamicDeserializer.deserialize(dynamic2);
		} else {
			LOGGER.error("Unknown type {}, replacing with {}", dynamic2.get(registry).asString(""), typeFieldName);
			object = typeFieldName;
		}

		return object;
	}
}
