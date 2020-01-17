/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema100;

public class Schema2100
extends IdentifierNormalizingSchema {
    public Schema2100(int i, Schema schema) {
        super(i, schema);
    }

    protected static void method_21746(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.targetItems(schema));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
        Schema2100.method_21746(schema, map, "minecraft:bee");
        Schema2100.method_21746(schema, map, "minecraft:bee_stinger");
        return map;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
        schema.register(map, "minecraft:beehive", () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Bees", DSL.list(DSL.optionalFields("EntityData", TypeReferences.ENTITY_TREE.in(schema)))));
        return map;
    }
}

