package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class HugeMushroomFeatureConfig implements FeatureConfig {
	public static final Codec<HugeMushroomFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockStateProvider.CODEC.fieldOf("cap_provider").forGetter(hugeMushroomFeatureConfig -> hugeMushroomFeatureConfig.capProvider),
					BlockStateProvider.CODEC.fieldOf("stem_provider").forGetter(hugeMushroomFeatureConfig -> hugeMushroomFeatureConfig.stemProvider),
					Codec.INT.fieldOf("foliage_radius").orElse(2).forGetter(hugeMushroomFeatureConfig -> hugeMushroomFeatureConfig.capSize)
				)
				.apply(instance, HugeMushroomFeatureConfig::new)
	);
	public final BlockStateProvider capProvider;
	public final BlockStateProvider stemProvider;
	public final int capSize;

	public HugeMushroomFeatureConfig(BlockStateProvider capProvider, BlockStateProvider stemProvider, int capSize) {
		this.capProvider = capProvider;
		this.stemProvider = stemProvider;
		this.capSize = capSize;
	}
}
