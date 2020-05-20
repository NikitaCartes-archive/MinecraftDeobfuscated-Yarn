package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.registry.Registry;

public class SpringFeatureConfig implements FeatureConfig {
	public static final Codec<SpringFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					FluidState.field_25018.fieldOf("state").forGetter(springFeatureConfig -> springFeatureConfig.state),
					Codec.BOOL.fieldOf("requires_block_below").withDefault(true).forGetter(springFeatureConfig -> springFeatureConfig.requiresBlockBelow),
					Codec.INT.fieldOf("rock_count").withDefault(4).forGetter(springFeatureConfig -> springFeatureConfig.rockCount),
					Codec.INT.fieldOf("hole_count").withDefault(1).forGetter(springFeatureConfig -> springFeatureConfig.holeCount),
					Registry.BLOCK
						.listOf()
						.fieldOf("valid_blocks")
						.xmap(ImmutableSet::copyOf, ImmutableList::copyOf)
						.forGetter(springFeatureConfig -> springFeatureConfig.validBlocks)
				)
				.apply(instance, SpringFeatureConfig::new)
	);
	public final FluidState state;
	public final boolean requiresBlockBelow;
	public final int rockCount;
	public final int holeCount;
	public final Set<Block> validBlocks;

	public SpringFeatureConfig(FluidState state, boolean requiresBlockBelow, int rockCount, int holeCount, Set<Block> validBlocks) {
		this.state = state;
		this.requiresBlockBelow = requiresBlockBelow;
		this.rockCount = rockCount;
		this.holeCount = holeCount;
		this.validBlocks = validBlocks;
	}
}
