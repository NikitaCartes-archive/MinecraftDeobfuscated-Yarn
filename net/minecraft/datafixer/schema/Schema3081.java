/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema100;

public class Schema3081
extends IdentifierNormalizingSchema {
    public Schema3081(int i, Schema schema) {
        super(i, schema);
    }

    protected static void register(Schema schema, Map<String, Supplier<TypeTemplate>> map, String id) {
        schema.register(map, id, () -> Schema100.targetItems(schema));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
        Schema3081.register(schema, map, "minecraft:warden");
        return map;
    }
}

