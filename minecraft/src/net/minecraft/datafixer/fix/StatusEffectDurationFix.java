package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class StatusEffectDurationFix extends DataFix {
	private static final Set<String> POTION_ITEM_IDS = Set.of(
		"minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:tipped_arrow"
	);

	public StatusEffectDurationFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Schema schema = this.getInputSchema();
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder(
			"id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType())
		);
		OpticFinder<?> opticFinder2 = type.findField("tag");
		return TypeRewriteRule.seq(
			this.fixTypeEverywhereTyped(
				"EffectDurationEntity", schema.getType(TypeReferences.ENTITY), entityTyped -> entityTyped.update(DSL.remainderFinder(), this::fixEntityStatusEffects)
			),
			this.fixTypeEverywhereTyped(
				"EffectDurationPlayer", schema.getType(TypeReferences.PLAYER), playerTyped -> playerTyped.update(DSL.remainderFinder(), this::fixEntityStatusEffects)
			),
			this.fixTypeEverywhereTyped("EffectDurationItem", type, itemStackTyped -> {
				Optional<Pair<String, String>> optional = itemStackTyped.getOptional(opticFinder);
				if (optional.filter(POTION_ITEM_IDS::contains).isPresent()) {
					Optional<? extends Typed<?>> optional2 = itemStackTyped.getOptionalTyped(opticFinder2);
					if (optional2.isPresent()) {
						Dynamic<?> dynamic = ((Typed)optional2.get()).get(DSL.remainderFinder());
						Typed<?> typed = ((Typed)optional2.get()).set(DSL.remainderFinder(), dynamic.update("CustomPotionEffects", this::fixPotionEffects));
						return itemStackTyped.set(opticFinder2, typed);
					}
				}

				return itemStackTyped;
			})
		);
	}

	private Dynamic<?> fixPotionEffect(Dynamic<?> effectDynamic) {
		return effectDynamic.update("FactorCalculationData", factorCalculationDataDynamic -> {
			int i = factorCalculationDataDynamic.get("effect_changed_timestamp").asInt(-1);
			factorCalculationDataDynamic = factorCalculationDataDynamic.remove("effect_changed_timestamp");
			int j = effectDynamic.get("Duration").asInt(-1);
			int k = i - j;
			return factorCalculationDataDynamic.set("ticks_active", factorCalculationDataDynamic.createInt(k));
		});
	}

	private Dynamic<?> fixPotionEffects(Dynamic<?> effectsDynamic) {
		return effectsDynamic.createList(effectsDynamic.asStream().map(this::fixPotionEffect));
	}

	private Dynamic<?> fixEntityStatusEffects(Dynamic<?> entityDynamic) {
		entityDynamic = entityDynamic.update("Effects", this::fixPotionEffects);
		entityDynamic = entityDynamic.update("ActiveEffects", this::fixPotionEffects);
		return entityDynamic.update("CustomPotionEffects", this::fixPotionEffects);
	}
}
