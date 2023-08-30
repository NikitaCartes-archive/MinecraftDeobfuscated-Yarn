package net.minecraft.advancement;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public record AdvancementCriterion<T extends CriterionConditions>(Criterion<T> trigger, T conditions) {
	public static AdvancementCriterion<?> fromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		Identifier identifier = new Identifier(JsonHelper.getString(obj, "trigger"));
		Criterion<?> criterion = Criteria.getById(identifier);
		if (criterion == null) {
			throw new JsonSyntaxException("Invalid criterion trigger: " + identifier);
		} else {
			return fromJson(obj, predicateDeserializer, criterion);
		}
	}

	private static <T extends CriterionConditions> AdvancementCriterion<T> fromJson(
		JsonObject json, AdvancementEntityPredicateDeserializer predicateDeserializer, Criterion<T> trigger
	) {
		T criterionConditions = trigger.conditionsFromJson(JsonHelper.getObject(json, "conditions", new JsonObject()), predicateDeserializer);
		return new AdvancementCriterion<>(trigger, criterionConditions);
	}

	public static Map<String, AdvancementCriterion<?>> criteriaFromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		Map<String, AdvancementCriterion<?>> map = Maps.<String, AdvancementCriterion<?>>newHashMap();

		for (Entry<String, JsonElement> entry : obj.entrySet()) {
			map.put((String)entry.getKey(), fromJson(JsonHelper.asObject((JsonElement)entry.getValue(), "criterion"), predicateDeserializer));
		}

		return map;
	}

	public JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("trigger", ((Identifier)Objects.requireNonNull(Criteria.getId(this.trigger), "Unregistered trigger")).toString());
		JsonObject jsonObject2 = this.conditions.toJson();
		if (jsonObject2.size() != 0) {
			jsonObject.add("conditions", jsonObject2);
		}

		return jsonObject;
	}
}
