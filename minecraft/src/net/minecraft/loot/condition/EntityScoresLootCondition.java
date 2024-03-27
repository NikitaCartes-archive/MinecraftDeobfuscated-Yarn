package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

public record EntityScoresLootCondition(Map<String, BoundedIntUnaryOperator> scores, LootContext.EntityTarget entity) implements LootCondition {
	public static final MapCodec<EntityScoresLootCondition> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.unboundedMap(Codec.STRING, BoundedIntUnaryOperator.CODEC).fieldOf("scores").forGetter(EntityScoresLootCondition::scores),
					LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(EntityScoresLootCondition::entity)
				)
				.apply(instance, EntityScoresLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.ENTITY_SCORES;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return (Set<LootContextParameter<?>>)Stream.concat(
				Stream.of(this.entity.getParameter()), this.scores.values().stream().flatMap(operator -> operator.getRequiredParameters().stream())
			)
			.collect(ImmutableSet.toImmutableSet());
	}

	public boolean test(LootContext lootContext) {
		Entity entity = lootContext.get(this.entity.getParameter());
		if (entity == null) {
			return false;
		} else {
			Scoreboard scoreboard = lootContext.getWorld().getScoreboard();

			for (Entry<String, BoundedIntUnaryOperator> entry : this.scores.entrySet()) {
				if (!this.entityScoreIsInRange(lootContext, entity, scoreboard, (String)entry.getKey(), (BoundedIntUnaryOperator)entry.getValue())) {
					return false;
				}
			}

			return true;
		}
	}

	protected boolean entityScoreIsInRange(LootContext context, Entity entity, Scoreboard scoreboard, String objectiveName, BoundedIntUnaryOperator range) {
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(objectiveName);
		if (scoreboardObjective == null) {
			return false;
		} else {
			ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(entity, scoreboardObjective);
			return readableScoreboardScore == null ? false : range.test(context, readableScoreboardScore.getScore());
		}
	}

	public static EntityScoresLootCondition.Builder create(LootContext.EntityTarget target) {
		return new EntityScoresLootCondition.Builder(target);
	}

	public static class Builder implements LootCondition.Builder {
		private final ImmutableMap.Builder<String, BoundedIntUnaryOperator> scores = ImmutableMap.builder();
		private final LootContext.EntityTarget target;

		public Builder(LootContext.EntityTarget target) {
			this.target = target;
		}

		public EntityScoresLootCondition.Builder score(String name, BoundedIntUnaryOperator value) {
			this.scores.put(name, value);
			return this;
		}

		@Override
		public LootCondition build() {
			return new EntityScoresLootCondition(this.scores.build(), this.target);
		}
	}
}
