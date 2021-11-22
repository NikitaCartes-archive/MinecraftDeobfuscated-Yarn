package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.intprovider.IntProvider;

public record DiskFeatureConfig(BlockState state, IntProvider radius, int halfHeight, List<BlockState> targets) implements FeatureConfig {
	public static final Codec<DiskFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("state").forGetter(DiskFeatureConfig::state),
					IntProvider.createValidatingCodec(0, 8).fieldOf("radius").forGetter(DiskFeatureConfig::radius),
					Codec.intRange(0, 4).fieldOf("half_height").forGetter(DiskFeatureConfig::halfHeight),
					BlockState.CODEC.listOf().fieldOf("targets").forGetter(DiskFeatureConfig::targets)
				)
				.apply(instance, DiskFeatureConfig::new)
	);
}
