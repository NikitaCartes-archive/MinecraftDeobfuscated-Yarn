package net.minecraft.loot.provider.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.score.ContextLootScoreProvider;
import net.minecraft.loot.provider.score.LootScoreProvider;
import net.minecraft.loot.provider.score.LootScoreProviderTypes;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

public record ScoreLootNumberProvider(LootScoreProvider target, String score, float scale) implements LootNumberProvider {
	public static final MapCodec<ScoreLootNumberProvider> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					LootScoreProviderTypes.CODEC.fieldOf("target").forGetter(ScoreLootNumberProvider::target),
					Codec.STRING.fieldOf("score").forGetter(ScoreLootNumberProvider::score),
					Codec.FLOAT.fieldOf("scale").orElse(1.0F).forGetter(ScoreLootNumberProvider::scale)
				)
				.apply(instance, ScoreLootNumberProvider::new)
	);

	@Override
	public LootNumberProviderType getType() {
		return LootNumberProviderTypes.SCORE;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.target.getRequiredParameters();
	}

	public static ScoreLootNumberProvider create(LootContext.EntityTarget target, String score) {
		return create(target, score, 1.0F);
	}

	public static ScoreLootNumberProvider create(LootContext.EntityTarget target, String score, float scale) {
		return new ScoreLootNumberProvider(ContextLootScoreProvider.create(target), score, scale);
	}

	@Override
	public float nextFloat(LootContext context) {
		ScoreHolder scoreHolder = this.target.getScoreHolder(context);
		if (scoreHolder == null) {
			return 0.0F;
		} else {
			Scoreboard scoreboard = context.getWorld().getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(this.score);
			if (scoreboardObjective == null) {
				return 0.0F;
			} else {
				ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(scoreHolder, scoreboardObjective);
				return readableScoreboardScore == null ? 0.0F : (float)readableScoreboardScore.getScore() * this.scale;
			}
		}
	}
}
