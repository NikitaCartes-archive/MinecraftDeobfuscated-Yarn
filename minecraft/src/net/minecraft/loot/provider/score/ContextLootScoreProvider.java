package net.minecraft.loot.provider.score;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;

public record ContextLootScoreProvider(LootContext.EntityTarget target) implements LootScoreProvider {
	public static final Codec<ContextLootScoreProvider> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(ContextLootScoreProvider::target))
				.apply(instance, ContextLootScoreProvider::new)
	);
	public static final Codec<ContextLootScoreProvider> INLINE_CODEC = LootContext.EntityTarget.CODEC
		.xmap(ContextLootScoreProvider::new, ContextLootScoreProvider::target);

	public static LootScoreProvider create(LootContext.EntityTarget target) {
		return new ContextLootScoreProvider(target);
	}

	@Override
	public LootScoreProviderType getType() {
		return LootScoreProviderTypes.CONTEXT;
	}

	@Nullable
	@Override
	public String getName(LootContext context) {
		Entity entity = context.get(this.target.getParameter());
		return entity != null ? entity.getEntityName() : null;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.target.getParameter());
	}
}
