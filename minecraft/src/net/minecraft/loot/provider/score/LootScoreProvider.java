package net.minecraft.loot.provider.score;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;

public interface LootScoreProvider {
	@Nullable
	String getName(LootContext context);

	LootScoreProviderType getType();

	Set<LootContextParameter<?>> getRequiredParameters();
}
