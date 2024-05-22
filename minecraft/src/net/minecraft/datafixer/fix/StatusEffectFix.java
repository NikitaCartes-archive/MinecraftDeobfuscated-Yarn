package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.Util;

public class StatusEffectFix extends DataFix {
	private static final Int2ObjectMap<String> OLD_TO_NEW_IDS = Util.make(new Int2ObjectOpenHashMap<>(), idMap -> {
		idMap.put(1, "minecraft:speed");
		idMap.put(2, "minecraft:slowness");
		idMap.put(3, "minecraft:haste");
		idMap.put(4, "minecraft:mining_fatigue");
		idMap.put(5, "minecraft:strength");
		idMap.put(6, "minecraft:instant_health");
		idMap.put(7, "minecraft:instant_damage");
		idMap.put(8, "minecraft:jump_boost");
		idMap.put(9, "minecraft:nausea");
		idMap.put(10, "minecraft:regeneration");
		idMap.put(11, "minecraft:resistance");
		idMap.put(12, "minecraft:fire_resistance");
		idMap.put(13, "minecraft:water_breathing");
		idMap.put(14, "minecraft:invisibility");
		idMap.put(15, "minecraft:blindness");
		idMap.put(16, "minecraft:night_vision");
		idMap.put(17, "minecraft:hunger");
		idMap.put(18, "minecraft:weakness");
		idMap.put(19, "minecraft:poison");
		idMap.put(20, "minecraft:wither");
		idMap.put(21, "minecraft:health_boost");
		idMap.put(22, "minecraft:absorption");
		idMap.put(23, "minecraft:saturation");
		idMap.put(24, "minecraft:glowing");
		idMap.put(25, "minecraft:levitation");
		idMap.put(26, "minecraft:luck");
		idMap.put(27, "minecraft:unluck");
		idMap.put(28, "minecraft:slow_falling");
		idMap.put(29, "minecraft:conduit_power");
		idMap.put(30, "minecraft:dolphins_grace");
		idMap.put(31, "minecraft:bad_omen");
		idMap.put(32, "minecraft:hero_of_the_village");
		idMap.put(33, "minecraft:darkness");
	});
	private static final Set<String> POTION_ITEM_IDS = Set.of(
		"minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:tipped_arrow"
	);

	public StatusEffectFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	private static <T> Optional<Dynamic<T>> updateId(Dynamic<T> dynamic, String idKey) {
		return dynamic.get(idKey).asNumber().result().map(oldId -> OLD_TO_NEW_IDS.get(oldId.intValue())).map(dynamic::createString);
	}

	private static <T> Dynamic<T> renameKeyAndUpdateId(Dynamic<T> dynamic, String oldKey, Dynamic<T> dynamic2, String newKey) {
		Optional<Dynamic<T>> optional = updateId(dynamic, oldKey);
		return dynamic2.replaceField(oldKey, newKey, optional);
	}

	private static <T> Dynamic<T> renameKeyAndUpdateId(Dynamic<T> dynamic, String oldKey, String newKey) {
		return renameKeyAndUpdateId(dynamic, oldKey, dynamic, newKey);
	}

	private static <T> Dynamic<T> fixEffect(Dynamic<T> effectDynamic) {
		effectDynamic = renameKeyAndUpdateId(effectDynamic, "Id", "id");
		effectDynamic = effectDynamic.renameField("Ambient", "ambient");
		effectDynamic = effectDynamic.renameField("Amplifier", "amplifier");
		effectDynamic = effectDynamic.renameField("Duration", "duration");
		effectDynamic = effectDynamic.renameField("ShowParticles", "show_particles");
		effectDynamic = effectDynamic.renameField("ShowIcon", "show_icon");
		Optional<Dynamic<T>> optional = effectDynamic.get("HiddenEffect").result().map(StatusEffectFix::fixEffect);
		return effectDynamic.replaceField("HiddenEffect", "hidden_effect", optional);
	}

	private static <T> Dynamic<T> fixEffectList(Dynamic<T> dynamic, String oldEffectListKey, String newEffectListKey) {
		Optional<Dynamic<T>> optional = dynamic.get(oldEffectListKey)
			.asStreamOpt()
			.result()
			.map(oldEffects -> dynamic.createList(oldEffects.map(StatusEffectFix::fixEffect)));
		return dynamic.replaceField(oldEffectListKey, newEffectListKey, optional);
	}

	private static <T> Dynamic<T> fixSuspiciousStewEffect(Dynamic<T> effectDynamicIn, Dynamic<T> effectDynamicOut) {
		effectDynamicOut = renameKeyAndUpdateId(effectDynamicIn, "EffectId", effectDynamicOut, "id");
		Optional<Dynamic<T>> optional = effectDynamicIn.get("EffectDuration").result();
		return effectDynamicOut.replaceField("EffectDuration", "duration", optional);
	}

	private static <T> Dynamic<T> fixSuspiciousStewEffect(Dynamic<T> effectDynamic) {
		return fixSuspiciousStewEffect(effectDynamic, effectDynamic);
	}

	private Typed<?> fixEntityEffects(Typed<?> entityTyped, TypeReference entityTypeReference, String entityId, Function<Dynamic<?>, Dynamic<?>> effectsFixer) {
		Type<?> type = this.getInputSchema().getChoiceType(entityTypeReference, entityId);
		Type<?> type2 = this.getOutputSchema().getChoiceType(entityTypeReference, entityId);
		return entityTyped.updateTyped(DSL.namedChoice(entityId, type), type2, matchingEntityTyped -> matchingEntityTyped.update(DSL.remainderFinder(), effectsFixer));
	}

	private TypeRewriteRule makeBlockEntitiesRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.BLOCK_ENTITY);
		return this.fixTypeEverywhereTyped(
			"BlockEntityMobEffectIdFix", type, typed -> this.fixEntityEffects(typed, TypeReferences.BLOCK_ENTITY, "minecraft:beacon", dynamic -> {
					dynamic = renameKeyAndUpdateId(dynamic, "Primary", "primary_effect");
					return renameKeyAndUpdateId(dynamic, "Secondary", "secondary_effect");
				})
		);
	}

	private static <T> Dynamic<T> fixStewEffectsKey(Dynamic<T> dynamic) {
		Dynamic<T> dynamic2 = dynamic.emptyMap();
		Dynamic<T> dynamic3 = fixSuspiciousStewEffect(dynamic, dynamic2);
		if (!dynamic3.equals(dynamic2)) {
			dynamic = dynamic.set("stew_effects", dynamic.createList(Stream.of(dynamic3)));
		}

		return dynamic.remove("EffectId").remove("EffectDuration");
	}

	private static <T> Dynamic<T> fixCustomPotionEffectsKey(Dynamic<T> dynamic) {
		return fixEffectList(dynamic, "CustomPotionEffects", "custom_potion_effects");
	}

	private static <T> Dynamic<T> fixEffectsKey(Dynamic<T> dynamic) {
		return fixEffectList(dynamic, "Effects", "effects");
	}

	private static Dynamic<?> fixActiveEffectsKey(Dynamic<?> dynamic) {
		return fixEffectList(dynamic, "ActiveEffects", "active_effects");
	}

	private TypeRewriteRule makeEntitiesRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.ENTITY);
		return this.fixTypeEverywhereTyped("EntityMobEffectIdFix", type, entityTyped -> {
			entityTyped = this.fixEntityEffects(entityTyped, TypeReferences.ENTITY, "minecraft:mooshroom", StatusEffectFix::fixStewEffectsKey);
			entityTyped = this.fixEntityEffects(entityTyped, TypeReferences.ENTITY, "minecraft:arrow", StatusEffectFix::fixCustomPotionEffectsKey);
			entityTyped = this.fixEntityEffects(entityTyped, TypeReferences.ENTITY, "minecraft:area_effect_cloud", StatusEffectFix::fixEffectsKey);
			return entityTyped.update(DSL.remainderFinder(), StatusEffectFix::fixActiveEffectsKey);
		});
	}

	private TypeRewriteRule makePlayersRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.PLAYER);
		return this.fixTypeEverywhereTyped("PlayerMobEffectIdFix", type, typed -> typed.update(DSL.remainderFinder(), StatusEffectFix::fixActiveEffectsKey));
	}

	private static <T> Dynamic<T> fixSuspiciousStewEffects(Dynamic<T> tagTyped) {
		Optional<Dynamic<T>> optional = tagTyped.get("Effects")
			.asStreamOpt()
			.result()
			.map(effects -> tagTyped.createList(effects.map(StatusEffectFix::fixSuspiciousStewEffect)));
		return tagTyped.replaceField("Effects", "effects", optional);
	}

	private TypeRewriteRule makeItemStacksRule() {
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder(
			"id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType())
		);
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		OpticFinder<?> opticFinder2 = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemStackMobEffectIdFix",
			type,
			itemStackTyped -> {
				Optional<Pair<String, String>> optional = itemStackTyped.getOptional(opticFinder);
				if (optional.isPresent()) {
					String string = (String)((Pair)optional.get()).getSecond();
					if (string.equals("minecraft:suspicious_stew")) {
						return itemStackTyped.updateTyped(opticFinder2, tagTyped -> tagTyped.update(DSL.remainderFinder(), StatusEffectFix::fixSuspiciousStewEffects));
					}

					if (POTION_ITEM_IDS.contains(string)) {
						return itemStackTyped.updateTyped(
							opticFinder2,
							tagTyped -> tagTyped.update(DSL.remainderFinder(), tagDynamic -> fixEffectList(tagDynamic, "CustomPotionEffects", "custom_potion_effects"))
						);
					}
				}

				return itemStackTyped;
			}
		);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return TypeRewriteRule.seq(this.makeBlockEntitiesRule(), this.makeEntitiesRule(), this.makePlayersRule(), this.makeItemStacksRule());
	}
}
