package net.minecraft.loot.provider.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.score.ContextLootScoreProvider;
import net.minecraft.loot.provider.score.LootScoreProvider;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class ScoreLootNumberProvider implements LootNumberProvider {
	final LootScoreProvider target;
	final String score;
	final float scale;

	ScoreLootNumberProvider(LootScoreProvider target, String score, float scale) {
		this.target = target;
		this.score = score;
		this.scale = scale;
	}

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
		String string = this.target.getName(context);
		if (string == null) {
			return 0.0F;
		} else {
			Scoreboard scoreboard = context.getWorld().getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(this.score);
			if (scoreboardObjective == null) {
				return 0.0F;
			} else {
				return !scoreboard.playerHasObjective(string, scoreboardObjective)
					? 0.0F
					: (float)scoreboard.getPlayerScore(string, scoreboardObjective).getScore() * this.scale;
			}
		}
	}

	public static class Serializer implements JsonSerializer<ScoreLootNumberProvider> {
		public ScoreLootNumberProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			String string = JsonHelper.getString(jsonObject, "score");
			float f = JsonHelper.getFloat(jsonObject, "scale", 1.0F);
			LootScoreProvider lootScoreProvider = JsonHelper.deserialize(jsonObject, "target", jsonDeserializationContext, LootScoreProvider.class);
			return new ScoreLootNumberProvider(lootScoreProvider, string, f);
		}

		public void toJson(JsonObject jsonObject, ScoreLootNumberProvider scoreLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("score", scoreLootNumberProvider.score);
			jsonObject.add("target", jsonSerializationContext.serialize(scoreLootNumberProvider.target));
			jsonObject.addProperty("scale", scoreLootNumberProvider.scale);
		}
	}
}
