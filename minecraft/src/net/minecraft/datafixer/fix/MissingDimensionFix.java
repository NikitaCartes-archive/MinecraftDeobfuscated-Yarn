package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FieldFinder;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList.CompoundListType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class MissingDimensionFix extends DataFix {
	public MissingDimensionFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	protected static <A> Type<Pair<A, Dynamic<?>>> extract1(String field, Type<A> type) {
		return DSL.and(DSL.field(field, type), DSL.remainderType());
	}

	protected static <A> Type<Pair<Either<A, Unit>, Dynamic<?>>> extract1Opt(String field, Type<A> type) {
		return DSL.and(DSL.optional(DSL.field(field, type)), DSL.remainderType());
	}

	protected static <A1, A2> Type<Pair<Either<A1, Unit>, Pair<Either<A2, Unit>, Dynamic<?>>>> extract2Opt(
		String field1, Type<A1> type1, String field2, Type<A2> type2
	) {
		return DSL.and(DSL.optional(DSL.field(field1, type1)), DSL.optional(DSL.field(field2, type2)), DSL.remainderType());
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Schema schema = this.getInputSchema();
		Type<?> type = DSL.taggedChoiceType(
			"type",
			DSL.string(),
			ImmutableMap.of(
				"minecraft:debug",
				DSL.remainderType(),
				"minecraft:flat",
				flatGeneratorType(schema),
				"minecraft:noise",
				extract2Opt(
					"biome_source",
					DSL.taggedChoiceType(
						"type",
						DSL.string(),
						ImmutableMap.of(
							"minecraft:fixed",
							extract1("biome", schema.getType(TypeReferences.BIOME)),
							"minecraft:multi_noise",
							DSL.list(extract1("biome", schema.getType(TypeReferences.BIOME))),
							"minecraft:checkerboard",
							extract1("biomes", DSL.list(schema.getType(TypeReferences.BIOME))),
							"minecraft:vanilla_layered",
							DSL.remainderType(),
							"minecraft:the_end",
							DSL.remainderType()
						)
					),
					"settings",
					DSL.or(DSL.string(), extract2Opt("default_block", schema.getType(TypeReferences.BLOCK_NAME), "default_fluid", schema.getType(TypeReferences.BLOCK_NAME)))
				)
			)
		);
		CompoundListType<String, ?> compoundListType = DSL.compoundList(IdentifierNormalizingSchema.getIdentifierType(), extract1("generator", type));
		Type<?> type2 = DSL.and(compoundListType, DSL.remainderType());
		Type<?> type3 = schema.getType(TypeReferences.WORLD_GEN_SETTINGS);
		FieldFinder<?> fieldFinder = new FieldFinder<>("dimensions", type2);
		if (!type3.findFieldType("dimensions").equals(type2)) {
			throw new IllegalStateException();
		} else {
			OpticFinder<? extends List<? extends Pair<String, ?>>> opticFinder = compoundListType.finder();
			return this.fixTypeEverywhereTyped(
				"MissingDimensionFix",
				type3,
				worldGenSettingsTyped -> worldGenSettingsTyped.updateTyped(
						fieldFinder, dimensionsTyped -> dimensionsTyped.updateTyped(opticFinder, dimensionsListTyped -> {
								if (!(dimensionsListTyped.getValue() instanceof List)) {
									throw new IllegalStateException("List exptected");
								} else if (((List)dimensionsListTyped.getValue()).isEmpty()) {
									Dynamic<?> dynamic = worldGenSettingsTyped.get(DSL.remainderFinder());
									Dynamic<?> dynamic2 = this.method_29912(dynamic);
									return DataFixUtils.orElse(compoundListType.readTyped(dynamic2).result().map(Pair::getFirst), dimensionsListTyped);
								} else {
									return dimensionsListTyped;
								}
							})
					)
			);
		}
	}

	protected static Type<? extends Pair<? extends Either<? extends Pair<? extends Either<?, Unit>, ? extends Pair<? extends Either<? extends List<? extends Pair<? extends Either<?, Unit>, Dynamic<?>>>, Unit>, Dynamic<?>>>, Unit>, Dynamic<?>>> flatGeneratorType(
		Schema schema
	) {
		return extract1Opt(
			"settings", extract2Opt("biome", schema.getType(TypeReferences.BIOME), "layers", DSL.list(extract1Opt("block", schema.getType(TypeReferences.BLOCK_NAME))))
		);
	}

	private <T> Dynamic<T> method_29912(Dynamic<T> worldGenSettingsDynamic) {
		long l = worldGenSettingsDynamic.get("seed").asLong(0L);
		return new Dynamic<>(
			worldGenSettingsDynamic.getOps(),
			StructureSeparationDataFix.createDimensionSettings(
				worldGenSettingsDynamic, l, StructureSeparationDataFix.createDefaultOverworldGeneratorSettings(worldGenSettingsDynamic, l), false
			)
		);
	}
}
