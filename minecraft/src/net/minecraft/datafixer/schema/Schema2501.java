package net.minecraft.datafixer.schema;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema2501 extends IdentifierNormalizingSchema {
	public Schema2501(int i, Schema schema) {
		super(i, schema);
	}

	private static void targetRecipeUsedField(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
		schema.register(
			map,
			name,
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "RecipesUsed", DSL.compoundList(TypeReferences.RECIPE.in(schema), DSL.constType(DSL.intType()))
				))
		);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		targetRecipeUsedField(schema, map, "minecraft:furnace");
		targetRecipeUsedField(schema, map, "minecraft:smoker");
		targetRecipeUsedField(schema, map, "minecraft:blast_furnace");
		return map;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		Map<String, Supplier<TypeTemplate>> map = ImmutableMap.<String, Supplier<TypeTemplate>>builder()
			.put("default", DSL::remainder)
			.put("largeBiomes", DSL::remainder)
			.put("amplified", DSL::remainder)
			.put("customized", DSL::remainder)
			.put("debug_all_block_states", DSL::remainder)
			.put("default_1_1", DSL::remainder)
			.put(
				"flat",
				() -> DSL.optionalFields("biome", TypeReferences.BIOME.in(schema), "layers", DSL.list(DSL.optionalFields("block", TypeReferences.BLOCK_NAME.in(schema))))
			)
			.put(
				"buffet",
				() -> DSL.optionalFields(
						"biome_source",
						DSL.optionalFields("options", DSL.optionalFields("biomes", DSL.list(TypeReferences.BIOME.in(schema)))),
						"chunk_generator",
						DSL.optionalFields(
							"options", DSL.optionalFields("default_block", TypeReferences.BLOCK_NAME.in(schema), "default_fluid", TypeReferences.BLOCK_NAME.in(schema))
						)
					)
			)
			.build();
		schema.registerType(false, TypeReferences.CHUNK_GENERATOR_SETTINGS, () -> DSL.taggedChoiceLazy("levelType", DSL.string(), map));
	}
}
