package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

public class BlockFilterDecoratorConfig implements DecoratorConfig {
	public static final Codec<BlockFilterDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(BlockPredicate.BASE_CODEC.fieldOf("predicate").forGetter(blockFilterDecoratorConfig -> blockFilterDecoratorConfig.predicate))
				.apply(instance, BlockFilterDecoratorConfig::new)
	);
	private final BlockPredicate predicate;

	public BlockFilterDecoratorConfig(BlockPredicate predicate) {
		this.predicate = predicate;
	}

	public BlockPredicate getPredicate() {
		return this.predicate;
	}
}
