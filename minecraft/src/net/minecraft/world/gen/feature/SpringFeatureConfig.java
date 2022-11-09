package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;

public class SpringFeatureConfig implements FeatureConfig {
	public static final Codec<SpringFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					FluidState.CODEC.fieldOf("state").forGetter(config -> config.state),
					Codec.BOOL.fieldOf("requires_block_below").orElse(true).forGetter(config -> config.requiresBlockBelow),
					Codec.INT.fieldOf("rock_count").orElse(4).forGetter(config -> config.rockCount),
					Codec.INT.fieldOf("hole_count").orElse(1).forGetter(config -> config.holeCount),
					RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("valid_blocks").forGetter(config -> config.validBlocks)
				)
				.apply(instance, SpringFeatureConfig::new)
	);
	public final FluidState state;
	public final boolean requiresBlockBelow;
	public final int rockCount;
	public final int holeCount;
	public final RegistryEntryList<Block> validBlocks;

	public SpringFeatureConfig(FluidState state, boolean requiresBlockBelow, int rockCount, int holeCount, RegistryEntryList<Block> validBlocks) {
		this.state = state;
		this.requiresBlockBelow = requiresBlockBelow;
		this.rockCount = rockCount;
		this.holeCount = holeCount;
		this.validBlocks = validBlocks;
	}
}
