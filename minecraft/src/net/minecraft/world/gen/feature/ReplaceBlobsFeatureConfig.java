package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;

public class ReplaceBlobsFeatureConfig implements FeatureConfig {
	public static final Codec<ReplaceBlobsFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("target").forGetter(replaceBlobsFeatureConfig -> replaceBlobsFeatureConfig.target),
					BlockState.CODEC.fieldOf("state").forGetter(replaceBlobsFeatureConfig -> replaceBlobsFeatureConfig.state),
					IntProvider.createValidatingCodec(0, 12).fieldOf("radius").forGetter(replaceBlobsFeatureConfig -> replaceBlobsFeatureConfig.radius)
				)
				.apply(instance, ReplaceBlobsFeatureConfig::new)
	);
	public final BlockState target;
	public final BlockState state;
	private final IntProvider radius;

	public ReplaceBlobsFeatureConfig(BlockState target, BlockState state, IntProvider radius) {
		this.target = target;
		this.state = state;
		this.radius = radius;
	}

	public IntProvider getRadius() {
		return this.radius;
	}
}
