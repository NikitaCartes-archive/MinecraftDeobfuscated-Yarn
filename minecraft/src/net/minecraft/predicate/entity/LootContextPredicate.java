package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;

/**
 * A list of loot conditions applied to entities. All conditions must match for this
 * unified conditions to {@linkplain #test match}. Mainly used by advancements.
 */
public class LootContextPredicate {
	public static final LootContextPredicate EMPTY = new LootContextPredicate(new LootCondition[0]);
	private final LootCondition[] conditions;
	private final Predicate<LootContext> combinedCondition;

	LootContextPredicate(LootCondition[] conditions) {
		this.conditions = conditions;
		this.combinedCondition = LootConditionTypes.matchingAll(conditions);
	}

	public static LootContextPredicate create(LootCondition... conditions) {
		return new LootContextPredicate(conditions);
	}

	@Nullable
	public static LootContextPredicate fromJson(
		String key, AdvancementEntityPredicateDeserializer predicateDeserializer, @Nullable JsonElement json, LootContextType contextType
	) {
		if (json != null && json.isJsonArray()) {
			LootCondition[] lootConditions = predicateDeserializer.loadConditions(
				json.getAsJsonArray(), predicateDeserializer.getAdvancementId() + "/" + key, contextType
			);
			return new LootContextPredicate(lootConditions);
		} else {
			return null;
		}
	}

	public boolean test(LootContext context) {
		return this.combinedCondition.test(context);
	}

	public JsonElement toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
		return (JsonElement)(this.conditions.length == 0 ? JsonNull.INSTANCE : predicateSerializer.conditionsToJson(this.conditions));
	}

	public static JsonElement toPredicatesJsonArray(LootContextPredicate[] predicates, AdvancementEntityPredicateSerializer predicateSerializer) {
		if (predicates.length == 0) {
			return JsonNull.INSTANCE;
		} else {
			JsonArray jsonArray = new JsonArray();

			for (LootContextPredicate lootContextPredicate : predicates) {
				jsonArray.add(lootContextPredicate.toJson(predicateSerializer));
			}

			return jsonArray;
		}
	}
}
