package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DecoratedFeature extends Feature<DecoratedFeatureConfig> {
	public DecoratedFeature(Codec<DecoratedFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DecoratedFeatureConfig> context) {
		MutableBoolean mutableBoolean = new MutableBoolean();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		DecoratedFeatureConfig decoratedFeatureConfig = context.getConfig();
		ChunkGenerator chunkGenerator = context.getGenerator();
		Random random = context.getRandom();
		ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)decoratedFeatureConfig.feature.get();
		decoratedFeatureConfig.decorator.getPositions(new DecoratorContext(structureWorldAccess, chunkGenerator), random, context.getOrigin()).forEach(blockPos -> {
			Optional<ConfiguredFeature<?, ?>> optional = context.getFeature();
			if (optional.isPresent() && !(configuredFeature.getFeature() instanceof DecoratedFeature)) {
				Biome biome = structureWorldAccess.getBiome(blockPos);
				if (!biome.getGenerationSettings().method_37611((ConfiguredFeature<?, ?>)optional.get())) {
					return;
				}
			}

			if (configuredFeature.method_37767(optional, structureWorldAccess, chunkGenerator, random, blockPos)) {
				mutableBoolean.setTrue();
			}
		});
		return mutableBoolean.isTrue();
	}

	public String toString() {
		return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this));
	}
}
