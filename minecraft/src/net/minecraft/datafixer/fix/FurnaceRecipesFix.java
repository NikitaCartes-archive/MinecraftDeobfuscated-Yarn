package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class FurnaceRecipesFix extends DataFix {
	public FurnaceRecipesFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.updateBlockEntities(this.getOutputSchema().getTypeRaw(TypeReferences.RECIPE));
	}

	private <R> TypeRewriteRule updateBlockEntities(Type<R> recipeType) {
		Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>> type = DSL.and(
			DSL.optional(DSL.field("RecipesUsed", DSL.and(DSL.compoundList(recipeType, DSL.intType()), DSL.remainderType()))), DSL.remainderType()
		);
		OpticFinder<?> opticFinder = DSL.namedChoice("minecraft:furnace", this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:furnace"));
		OpticFinder<?> opticFinder2 = DSL.namedChoice(
			"minecraft:blast_furnace", this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:blast_furnace")
		);
		OpticFinder<?> opticFinder3 = DSL.namedChoice("minecraft:smoker", this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:smoker"));
		Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:furnace");
		Type<?> type3 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:blast_furnace");
		Type<?> type4 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:smoker");
		Type<?> type5 = this.getInputSchema().getType(TypeReferences.BLOCK_ENTITY);
		Type<?> type6 = this.getOutputSchema().getType(TypeReferences.BLOCK_ENTITY);
		return this.fixTypeEverywhereTyped(
			"FurnaceRecipesFix",
			type5,
			type6,
			blockEntityTyped -> blockEntityTyped.updateTyped(opticFinder, type2, furnaceTyped -> this.updateBlockEntityData(recipeType, type, furnaceTyped))
					.updateTyped(opticFinder2, type3, blastFurnaceTyped -> this.updateBlockEntityData(recipeType, type, blastFurnaceTyped))
					.updateTyped(opticFinder3, type4, smokerTyped -> this.updateBlockEntityData(recipeType, type, smokerTyped))
		);
	}

	private <R> Typed<?> updateBlockEntityData(
		Type<R> recipeType, Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>> recipesUsedType, Typed<?> smelterTyped
	) {
		Dynamic<?> dynamic = smelterTyped.getOrCreate(DSL.remainderFinder());
		int i = dynamic.get("RecipesUsedSize").asInt(0);
		dynamic = dynamic.remove("RecipesUsedSize");
		List<Pair<R, Integer>> list = Lists.<Pair<R, Integer>>newArrayList();

		for (int j = 0; j < i; j++) {
			String string = "RecipeLocation" + j;
			String string2 = "RecipeAmount" + j;
			Optional<? extends Dynamic<?>> optional = dynamic.get(string).result();
			int k = dynamic.get(string2).asInt(0);
			if (k > 0) {
				optional.ifPresent(dynamicx -> {
					Optional<? extends Pair<R, ? extends Dynamic<?>>> optionalx = recipeType.read(dynamicx).result();
					optionalx.ifPresent(pair -> list.add(Pair.of(pair.getFirst(), k)));
				});
			}

			dynamic = dynamic.remove(string).remove(string2);
		}

		return smelterTyped.set(DSL.remainderFinder(), recipesUsedType, Pair.of(Either.left(Pair.of(list, dynamic.emptyMap())), dynamic));
	}
}
