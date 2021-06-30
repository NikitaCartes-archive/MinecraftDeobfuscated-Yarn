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

public class Schema808
extends IdentifierNormalizingSchema {
    public Schema808(int i, Schema schema) {
        super(i, schema);
    }

    protected static void targetItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String blockEntityId) {
        schema.register(map, blockEntityId, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
        Schema808.targetItems(schema, map, "minecraft:shulker_box");
        return map;
    }
}

