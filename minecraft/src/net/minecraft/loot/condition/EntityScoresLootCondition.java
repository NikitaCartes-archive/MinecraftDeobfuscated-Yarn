package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class EntityScoresLootCondition implements LootCondition {
	private final Map<String, BoundedIntUnaryOperator> scores;
	private final LootContext.EntityTarget target;

	private EntityScoresLootCondition(Map<String, BoundedIntUnaryOperator> scores, LootContext.EntityTarget target) {
		this.scores = ImmutableMap.copyOf(scores);
		this.target = target;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.ENTITY_SCORES;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return (Set<LootContextParameter<?>>)Stream.concat(
				Stream.of(this.target.getParameter()), this.scores.values().stream().flatMap(boundedIntUnaryOperator -> boundedIntUnaryOperator.method_32386().stream())
			)
			.collect(ImmutableSet.toImmutableSet());
	}

	public boolean test(LootContext lootContext) {
		Entity entity = lootContext.get(this.target.getParameter());
		if (entity == null) {
			return false;
		} else {
			Scoreboard scoreboard = entity.world.getScoreboard();

			for (Entry<String, BoundedIntUnaryOperator> entry : this.scores.entrySet()) {
				if (!this.entityScoreIsInRange(lootContext, entity, scoreboard, (String)entry.getKey(), (BoundedIntUnaryOperator)entry.getValue())) {
					return false;
				}
			}

			return true;
		}
	}

	protected boolean entityScoreIsInRange(
		LootContext lootContext, Entity entity, Scoreboard scoreboard, String string, BoundedIntUnaryOperator boundedIntUnaryOperator
	) {
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
		if (scoreboardObjective == null) {
			return false;
		} else {
			String string2 = entity.getEntityName();
			return !scoreboard.playerHasObjective(string2, scoreboardObjective)
				? false
				: boundedIntUnaryOperator.method_32393(lootContext, scoreboard.getPlayerScore(string2, scoreboardObjective).getScore());
		}
	}

	public static class Serializer implements JsonSerializer<EntityScoresLootCondition> {
		public void toJson(JsonObject jsonObject, EntityScoresLootCondition entityScoresLootCondition, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, BoundedIntUnaryOperator> entry : entityScoresLootCondition.scores.entrySet()) {
				jsonObject2.add((String)entry.getKey(), jsonSerializationContext.serialize(entry.getValue()));
			}

			jsonObject.add("scores", jsonObject2);
			jsonObject.add("entity", jsonSerializationContext.serialize(entityScoresLootCondition.target));
		}

		public EntityScoresLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Set<Entry<String, JsonElement>> set = JsonHelper.getObject(jsonObject, "scores").entrySet();
			Map<String, BoundedIntUnaryOperator> map = Maps.<String, BoundedIntUnaryOperator>newLinkedHashMap();

			for (Entry<String, JsonElement> entry : set) {
				map.put(entry.getKey(), JsonHelper.deserialize((JsonElement)entry.getValue(), "score", jsonDeserializationContext, BoundedIntUnaryOperator.class));
			}

			return new EntityScoresLootCondition(map, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class));
		}
	}
}
