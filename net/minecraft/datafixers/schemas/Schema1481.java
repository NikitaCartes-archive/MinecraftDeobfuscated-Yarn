/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;

public class Schema1481
extends SchemaIdentifierNormalize {
    public Schema1481(int i, Schema schema) {
        super(i, schema);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
        schema.registerSimple(map, "minecraft:conduit");
        return map;
    }
}

