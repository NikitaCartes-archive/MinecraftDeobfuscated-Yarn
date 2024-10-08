package net.minecraft.loot.provider.number;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.random.Random;

public record BinomialLootNumberProvider(LootNumberProvider n, LootNumberProvider p) implements LootNumberProvider {
	public static final MapCodec<BinomialLootNumberProvider> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					LootNumberProviderTypes.CODEC.fieldOf("n").forGetter(BinomialLootNumberProvider::n),
					LootNumberProviderTypes.CODEC.fieldOf("p").forGetter(BinomialLootNumberProvider::p)
				)
				.apply(instance, BinomialLootNumberProvider::new)
	);

	@Override
	public LootNumberProviderType getType() {
		return LootNumberProviderTypes.BINOMIAL;
	}

	@Override
	public int nextInt(LootContext context) {
		int i = this.n.nextInt(context);
		float f = this.p.nextFloat(context);
		Random random = context.getRandom();
		int j = 0;

		for (int k = 0; k < i; k++) {
			if (random.nextFloat() < f) {
				j++;
			}
		}

		return j;
	}

	@Override
	public float nextFloat(LootContext context) {
		return (float)this.nextInt(context);
	}

	public static BinomialLootNumberProvider create(int n, float p) {
		return new BinomialLootNumberProvider(ConstantLootNumberProvider.create((float)n), ConstantLootNumberProvider.create(p));
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Sets.<ContextParameter<?>>union(this.n.getAllowedParameters(), this.p.getAllowedParameters());
	}
}
