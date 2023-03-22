package net.minecraft.datafixer.schema;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema2832 extends IdentifierNormalizingSchema {
	public Schema2832(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		schema.registerType(
			false,
			TypeReferences.CHUNK,
			() -> DSL.fields(
					"Level",
					DSL.optionalFields(
						"Entities",
						DSL.list(TypeReferences.ENTITY_TREE.in(schema)),
						"TileEntities",
						DSL.list(DSL.or(TypeReferences.BLOCK_ENTITY.in(schema), DSL.remainder())),
						"TileTicks",
						DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema))),
						"Sections",
						DSL.list(
							DSL.optionalFields(
								"biomes",
								DSL.optionalFields("palette", DSL.list(TypeReferences.BIOME.in(schema))),
								"block_states",
								DSL.optionalFields("palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema)))
							)
						),
						"Structures",
						DSL.optionalFields("Starts", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema)))
					)
				)
		);
		schema.registerType(false, TypeReferences.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, () -> DSL.constType(getIdentifierType()));
		schema.registerType(
			false,
			TypeReferences.WORLD_GEN_SETTINGS,
			() -> DSL.fields(
					"dimensions",
					DSL.compoundList(
						DSL.constType(getIdentifierType()),
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
													() -> DSL.or(
															DSL.fields("preset", TypeReferences.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST.in(schema)),
															DSL.list(DSL.fields("biome", TypeReferences.BIOME.in(schema)))
														),
													"minecraft:checkerboard",
													() -> DSL.fields("biomes", DSL.list(TypeReferences.BIOME.in(schema))),
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
