package net.minecraft.loot.provider.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.score.LootScoreProvider;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class ScoreLootNumberProvider implements LootNumberProvider {
	private final LootScoreProvider field_27925;
	private final String field_27926;
	private final float field_27927;

	private ScoreLootNumberProvider(LootScoreProvider lootScoreProvider, String string, float f) {
		this.field_27925 = lootScoreProvider;
		this.field_27926 = string;
		this.field_27927 = f;
	}

	@Override
	public LootNumberProviderType getType() {
		return LootNumberProviderTypes.SCORE;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.field_27925.method_32477();
	}

	@Override
	public float nextFloat(LootContext context) {
		String string = this.field_27925.getName(context);
		if (string == null) {
			return 0.0F;
		} else {
			Scoreboard scoreboard = context.getWorld().getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(this.field_27926);
			if (scoreboardObjective == null) {
				return 0.0F;
			} else {
				return !scoreboard.playerHasObjective(string, scoreboardObjective)
					? 0.0F
					: (float)scoreboard.getPlayerScore(string, scoreboardObjective).getScore() * this.field_27927;
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
			jsonObject.addProperty("score", scoreLootNumberProvider.field_27926);
			jsonObject.add("target", jsonSerializationContext.serialize(scoreLootNumberProvider.field_27925));
			jsonObject.addProperty("scale", scoreLootNumberProvider.field_27927);
		}
	}
}
