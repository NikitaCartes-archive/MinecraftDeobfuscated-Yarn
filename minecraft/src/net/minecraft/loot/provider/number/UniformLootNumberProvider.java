package net.minecraft.loot.provider.number;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.math.MathHelper;

public record UniformLootNumberProvider(LootNumberProvider min, LootNumberProvider max) implements LootNumberProvider {
	public static final MapCodec<UniformLootNumberProvider> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					LootNumberProviderTypes.CODEC.fieldOf("min").forGetter(UniformLootNumberProvider::min),
					LootNumberProviderTypes.CODEC.fieldOf("max").forGetter(UniformLootNumberProvider::max)
				)
				.apply(instance, UniformLootNumberProvider::new)
	);

	@Override
	public LootNumberProviderType getType() {
		return LootNumberProviderTypes.UNIFORM;
	}

	public static UniformLootNumberProvider create(float min, float max) {
		return new UniformLootNumberProvider(ConstantLootNumberProvider.create(min), ConstantLootNumberProvider.create(max));
	}

	@Override
	public int nextInt(LootContext context) {
		return MathHelper.nextInt(context.getRandom(), this.min.nextInt(context), this.max.nextInt(context));
	}

	@Override
	public float nextFloat(LootContext context) {
		return MathHelper.nextFloat(context.getRandom(), this.min.nextFloat(context), this.max.nextFloat(context));
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return Sets.<LootContextParameter<?>>union(this.min.getRequiredParameters(), this.max.getRequiredParameters());
	}
}
