package net.minecraft.loot.provider.score;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.scoreboard.ScoreHolder;

public record FixedLootScoreProvider(String name) implements LootScoreProvider {
	public static final Codec<FixedLootScoreProvider> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.STRING.fieldOf("name").forGetter(FixedLootScoreProvider::name)).apply(instance, FixedLootScoreProvider::new)
	);

	public static LootScoreProvider create(String name) {
		return new FixedLootScoreProvider(name);
	}

	@Override
	public LootScoreProviderType getType() {
		return LootScoreProviderTypes.FIXED;
	}

	@Override
	public ScoreHolder getScoreHolder(LootContext context) {
		return ScoreHolder.fromName(this.name);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of();
	}
}
