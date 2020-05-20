package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class class_5301 extends IdentifierNormalizingSchema {
	public class_5301(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			TypeReferences.CHUNK_GENERATOR_SETTINGS,
			() -> DSL.fields(
					"dimensions",
					DSL.compoundList(
						DSL.constType(method_28295()),
						DSL.fields(
							"generator",
							DSL.taggedChoiceLazy(
								"type",
								DSL.string(),
								ImmutableMap.of(
									"minecraft:debug",
									DSL::remainder,
									"minecraft:flat",
									() -> DSL.optionalFields(
											"settings",
											DSL.optionalFields("biome", TypeReferences.BIOME.in(schema), "layers", DSL.list(DSL.optionalFields("block", TypeReferences.BLOCK_NAME.in(schema))))
										),
									"minecraft:noise",
									() -> DSL.optionalFields(
											"biome_source",
											DSL.taggedChoiceLazy(
												"type",
												DSL.string(),
												ImmutableMap.of(
													"minecraft:fixed",
													() -> DSL.fields("biome", TypeReferences.BIOME.in(schema)),
													"minecraft:multi_noise",
													() -> DSL.list(DSL.fields("biome", TypeReferences.BIOME.in(schema))),
													"minecraft:checkerboard",
													() -> DSL.fields("biomes", DSL.list(TypeReferences.BIOME.in(schema))),
													"minecraft:vanilla_layered",
													DSL::remainder,
													"minecraft:the_end",
													DSL::remainder
												)
											),
											"settings",
											DSL.or(
												DSL.constType(DSL.string()),
												DSL.optionalFields("default_block", TypeReferences.BLOCK_NAME.in(schema), "default_fluid", TypeReferences.BLOCK_NAME.in(schema))
											)
										)
								)
							)
						)
					)
				)
		);
	}
}
