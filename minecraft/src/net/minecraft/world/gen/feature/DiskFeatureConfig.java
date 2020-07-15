package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.UniformIntDistribution;

public class DiskFeatureConfig implements FeatureConfig {
	public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("state").forGetter(diskFeatureConfig -> diskFeatureConfig.state),
					UniformIntDistribution.createValidatedCodec(0, 4, 4).fieldOf("radius").forGetter(diskFeatureConfig -> diskFeatureConfig.radius),
					Codec.intRange(0, 4).fieldOf("half_height").forGetter(diskFeatureConfig -> diskFeatureConfig.ySize),
					BlockState.CODEC.listOf().fieldOf("targets").forGetter(diskFeatureConfig -> diskFeatureConfig.targets)
				)
				.apply(instance, DiskFeatureConfig::new)
	);
	public final BlockState state;
	public final UniformIntDistribution radius;
	public final int ySize;
	public final List<BlockState> targets;

	public DiskFeatureConfig(BlockState state, UniformIntDistribution uniformIntDistribution, int ySize, List<BlockState> targets) {
		this.state = state;
		this.radius = uniformIntDistribution;
		this.ySize = ySize;
		this.targets = targets;
	}
}
