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
import net.minecraft.entity.Entity;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class EntityScoresLootCondition implements LootCondition {
	private final Map<String, UniformLootTableRange> scores;
	private final LootContext.EntityTarget target;

	private EntityScoresLootCondition(Map<String, UniformLootTableRange> scores, LootContext.EntityTarget target) {
		this.scores = ImmutableMap.copyOf(scores);
		this.target = target;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.target.getParameter());
	}

	public boolean method_864(LootContext lootContext) {
		Entity entity = lootContext.get(this.target.getParameter());
		if (entity == null) {
			return false;
		} else {
			Scoreboard scoreboard = entity.world.getScoreboard();

			for (Entry<String, UniformLootTableRange> entry : this.scores.entrySet()) {
				if (!this.entityScoreIsInRange(entity, scoreboard, (String)entry.getKey(), (UniformLootTableRange)entry.getValue())) {
					return false;
				}
			}

			return true;
		}
	}

	protected boolean entityScoreIsInRange(Entity entity, Scoreboard scoreboard, String objective, UniformLootTableRange scoreRange) {
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(objective);
		if (scoreboardObjective == null) {
			return false;
		} else {
			String string = entity.getEntityName();
			return !scoreboard.playerHasObjective(string, scoreboardObjective)
				? false
				: scoreRange.contains(scoreboard.getPlayerScore(string, scoreboardObjective).getScore());
		}
	}

	public static class Factory extends LootCondition.Factory<EntityScoresLootCondition> {
		protected Factory() {
			super(new Identifier("entity_scores"), EntityScoresLootCondition.class);
		}

		public void method_868(JsonObject jsonObject, EntityScoresLootCondition entityScoresLootCondition, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, UniformLootTableRange> entry : entityScoresLootCondition.scores.entrySet()) {
				jsonObject2.add((String)entry.getKey(), jsonSerializationContext.serialize(entry.getValue()));
			}

			jsonObject.add("scores", jsonObject2);
			jsonObject.add("entity", jsonSerializationContext.serialize(entityScoresLootCondition.target));
		}

		public EntityScoresLootCondition method_867(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Set<Entry<String, JsonElement>> set = JsonHelper.getObject(jsonObject, "scores").entrySet();
			Map<String, UniformLootTableRange> map = Maps.<String, UniformLootTableRange>newLinkedHashMap();

			for (Entry<String, JsonElement> entry : set) {
				map.put(entry.getKey(), JsonHelper.deserialize((JsonElement)entry.getValue(), "score", jsonDeserializationContext, UniformLootTableRange.class));
			}

			return new EntityScoresLootCondition(map, JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class));
		}
	}
}