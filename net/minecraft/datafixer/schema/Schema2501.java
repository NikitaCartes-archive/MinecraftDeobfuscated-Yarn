/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.schema;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class Schema2501
extends IdentifierNormalizingSchema {
    public Schema2501(int i, Schema schema) {
        super(i, schema);
    }

    private static void targetRecipeUsedField(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "RecipesUsed", DSL.compoundList(TypeReferences.RECIPE.in(schema), DSL.constType(DSL.intType()))));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
        Schema2501.targetRecipeUsedField(schema, map, "minecraft:furnace");
        Schema2501.targetRecipeUsedField(schema, map, "minecraft:smoker");
        Schema2501.targetRecipeUsedField(schema, map, "minecraft:blast_furnace");
        return map;
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);
        ImmutableMap<String, Supplier<TypeTemplate>> map = ImmutableMap.builder().put("default", DSL::remainder).put("largeBiomes", DSL::remainder).put("amplified", DSL::remainder).put("customized", DSL::remainder).put("debug_all_block_states", DSL::remainder).put("default_1_1", DSL::remainder).put("flat", () -> DSL.optionalFields("biome", TypeReferences.BIOME.in(schema), "layers", DSL.list(DSL.optionalFields("block", TypeReferences.BLOCK_NAME.in(schema))))).put("buffet", () -> DSL.optionalFields("biome_source", DSL.optionalFields("options", DSL.optionalFields("biomes", DSL.list(TypeReferences.BIOME.in(schema)))), "chunk_generator", DSL.optionalFields("options", DSL.optionalFields("default_block", TypeReferences.BLOCK_NAME.in(schema), "default_fluid", TypeReferences.BLOCK_NAME.in(schema))))).build();
        schema.registerType(false, TypeReferences.CHUNK_GENERATOR_SETTINGS, () -> DSL.taggedChoiceLazy("levelType", DSL.string(), map));
    }
}

