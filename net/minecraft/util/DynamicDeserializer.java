/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface DynamicDeserializer<T> {
    public static final Logger LOGGER = LogManager.getLogger();

    public T deserialize(Dynamic<?> var1);

    public static <T, V, U extends DynamicDeserializer<V>> V deserialize(Dynamic<T> dynamic, Registry<U> registry, String string, V object) {
        Object object2;
        DynamicDeserializer dynamicDeserializer = (DynamicDeserializer)registry.get(new Identifier(dynamic.get(string).asString("")));
        if (dynamicDeserializer != null) {
            object2 = dynamicDeserializer.deserialize(dynamic);
        } else {
            LOGGER.error("Unknown type {}, replacing with {}", (Object)dynamic.get(string).asString(""), (Object)object);
            object2 = object;
        }
        return (V)object2;
    }
}

