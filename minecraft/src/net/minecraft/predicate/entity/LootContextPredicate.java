package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Util;

/**
 * A list of loot conditions applied to entities. All conditions must match for this
 * unified conditions to {@linkplain #test match}. Mainly used by advancements.
 */
public class LootContextPredicate {
	private final List<LootCondition> conditions;
	private final Predicate<LootContext> combinedCondition;

	LootContextPredicate(List<LootCondition> conditions) {
		if (conditions.isEmpty()) {
			throw new IllegalArgumentException("ContextAwarePredicate must have at least one condition");
		} else {
			this.conditions = conditions;
			this.combinedCondition = LootConditionTypes.matchingAll(conditions);
		}
	}

	public static LootContextPredicate create(LootCondition... conditions) {
		return new LootContextPredicate(List.of(conditions));
	}

	public static Optional<Optional<LootContextPredicate>> fromJson(
		String key, AdvancementEntityPredicateDeserializer predicateDeserializer, @Nullable JsonElement json, LootContextType contextType
	) {
		if (json != null && json.isJsonArray()) {
			List<LootCondition> list = predicateDeserializer.loadConditions(json.getAsJsonArray(), predicateDeserializer.getAdvancementId() + "/" + key, contextType);
			return list.isEmpty() ? Optional.of(Optional.empty()) : Optional.of(Optional.of(new LootContextPredicate(list)));
		} else {
			return Optional.empty();
		}
	}

	public boolean test(LootContext context) {
		return this.combinedCondition.test(context);
	}

	public JsonElement toJson() {
		return Util.getResult(LootConditionTypes.CODEC.listOf().encodeStart(JsonOps.INSTANCE, this.conditions), IllegalStateException::new);
	}

	public static JsonElement toPredicatesJsonArray(List<LootContextPredicate> list) {
		if (list.isEmpty()) {
			return JsonNull.INSTANCE;
		} else {
			JsonArray jsonArray = new JsonArray();

			for (LootContextPredicate lootContextPredicate : list) {
				jsonArray.add(lootContextPredicate.toJson());
			}

			return jsonArray;
		}
	}
}
