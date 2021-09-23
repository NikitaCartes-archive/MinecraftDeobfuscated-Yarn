package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public record BlockFilterDecoratorConfig() implements DecoratorConfig {
	private final List<Block> allowed;
	private final List<Block> disallowed;
	private final BlockPos offset;
	public static final Codec<BlockFilterDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.BLOCK.listOf().optionalFieldOf("allowed", List.of()).forGetter(BlockFilterDecoratorConfig::allowed),
					Registry.BLOCK.listOf().optionalFieldOf("disallowed", List.of()).forGetter(BlockFilterDecoratorConfig::disallowed),
					BlockPos.CODEC.fieldOf("offset").forGetter(BlockFilterDecoratorConfig::offset)
				)
				.apply(instance, BlockFilterDecoratorConfig::new)
	);

	public BlockFilterDecoratorConfig(List<Block> list, List<Block> list2, BlockPos blockPos) {
		this.allowed = list;
		this.disallowed = list2;
		this.offset = blockPos;
	}
}
