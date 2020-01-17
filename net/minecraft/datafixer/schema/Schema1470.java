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

public class Schema1470
extends IdentifierNormalizingSchema {
    public Schema1470(int i, Schema schema) {
        super(i, schema);
    }

    protected static void method_5280(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.targetItems(schema));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
        Schema1470.method_5280(schema, map, "minecraft:turtle");
        Schema1470.method_5280(schema, map, "minecraft:cod_mob");
        Schema1470.method_5280(schema, map, "minecraft:tropical_fish");
        Schema1470.method_5280(schema, map, "minecraft:salmon_mob");
        Schema1470.method_5280(schema, map, "minecraft:puffer_fish");
        Schema1470.method_5280(schema, map, "minecraft:phantom");
        Schema1470.method_5280(schema, map, "minecraft:dolphin");
        Schema1470.method_5280(schema, map, "minecraft:drowned");
        schema.register(map, "minecraft:trident", (String string) -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema)));
        return map;
    }
}

