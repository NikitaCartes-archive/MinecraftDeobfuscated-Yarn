package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.UniformIntDistribution;

public class NetherrackReplaceBlobsFeatureConfig implements FeatureConfig {
	public static final Codec<NetherrackReplaceBlobsFeatureConfig> field_25848 = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("target").forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.field_25849),
					BlockState.CODEC.fieldOf("state").forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.field_25850),
					UniformIntDistribution.CODEC.fieldOf("radius").forGetter(netherrackReplaceBlobsFeatureConfig -> netherrackReplaceBlobsFeatureConfig.field_25851)
				)
				.apply(instance, NetherrackReplaceBlobsFeatureConfig::new)
	);
	public final BlockState field_25849;
	public final BlockState field_25850;
	private final UniformIntDistribution field_25851;

	public NetherrackReplaceBlobsFeatureConfig(BlockState blockState, BlockState blockState2, UniformIntDistribution uniformIntDistribution) {
		this.field_25849 = blockState;
		this.field_25850 = blockState2;
		this.field_25851 = uniformIntDistribution;
	}

	public UniformIntDistribution method_30405() {
		return this.field_25851;
	}
}
