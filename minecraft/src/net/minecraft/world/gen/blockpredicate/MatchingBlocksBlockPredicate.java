package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

class MatchingBlocksBlockPredicate extends OffsetPredicate {
	private final List<Block> blocks;
	public static final Codec<MatchingBlocksBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> registerOffsetField(instance)
				.and(Registry.BLOCK.getCodec().listOf().fieldOf("blocks").forGetter(predicate -> predicate.blocks))
				.apply(instance, MatchingBlocksBlockPredicate::new)
	);

	public MatchingBlocksBlockPredicate(Vec3i offset, List<Block> blocks) {
		super(offset);
		this.blocks = blocks;
	}

	@Override
	protected boolean test(BlockState state) {
		return this.blocks.contains(state.getBlock());
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.MATCHING_BLOCKS;
	}
}
