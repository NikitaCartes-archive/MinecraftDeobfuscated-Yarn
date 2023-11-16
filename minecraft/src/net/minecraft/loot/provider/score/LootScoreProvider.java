package net.minecraft.loot.provider.score;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.scoreboard.ScoreHolder;

public interface LootScoreProvider {
	@Nullable
	ScoreHolder getScoreHolder(LootContext context);

	LootScoreProviderType getType();

	Set<LootContextParameter<?>> getRequiredParameters();
}
