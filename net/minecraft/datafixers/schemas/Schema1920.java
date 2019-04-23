/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;

public class Schema1920
extends SchemaIdentifierNormalize {
    public Schema1920(int i, Schema schema) {
        super(i, schema);
    }

    protected static void method_17343(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
        Schema1920.method_17343(schema, map, "minecraft:campfire");
        return map;
    }
}

