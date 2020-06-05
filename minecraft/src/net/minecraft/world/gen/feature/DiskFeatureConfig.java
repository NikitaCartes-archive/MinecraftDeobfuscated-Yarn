package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;

public class DiskFeatureConfig implements FeatureConfig {
	public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("state").forGetter(diskFeatureConfig -> diskFeatureConfig.state),
					Codec.INT.fieldOf("radius").withDefault(0).forGetter(diskFeatureConfig -> diskFeatureConfig.radius),
					Codec.INT.fieldOf("y_size").withDefault(0).forGetter(diskFeatureConfig -> diskFeatureConfig.ySize),
					BlockState.CODEC.listOf().fieldOf("targets").forGetter(diskFeatureConfig -> diskFeatureConfig.targets)
				)
				.apply(instance, DiskFeatureConfig::new)
	);
	public final BlockState state;
	public final int radius;
	public final int ySize;
	public final List<BlockState> targets;

	public DiskFeatureConfig(BlockState state, int radius, int ySize, List<BlockState> targets) {
		this.state = state;
		this.radius = radius;
		this.ySize = ySize;
		this.targets = targets;
	}
}
