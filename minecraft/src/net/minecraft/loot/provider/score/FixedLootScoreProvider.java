package net.minecraft.loot.provider.score;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.util.context.ContextParameter;

public record FixedLootScoreProvider(String name) implements LootScoreProvider {
	public static final MapCodec<FixedLootScoreProvider> CODEC = RecordCodecBuilder.mapCodec(
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
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of();
	}
}
