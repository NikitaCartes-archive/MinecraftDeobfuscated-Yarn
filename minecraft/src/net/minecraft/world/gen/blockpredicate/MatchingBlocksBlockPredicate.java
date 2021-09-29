package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;

class MatchingBlocksBlockPredicate implements BlockPredicate {
	private final List<Block> blocks;
	private final BlockPos pos;
	public static final Codec<MatchingBlocksBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.BLOCK.listOf().fieldOf("blocks").forGetter(matchingBlocksBlockPredicate -> matchingBlocksBlockPredicate.blocks),
					BlockPos.CODEC.fieldOf("offset").forGetter(matchingBlocksBlockPredicate -> matchingBlocksBlockPredicate.pos)
				)
				.apply(instance, MatchingBlocksBlockPredicate::new)
	);

	public MatchingBlocksBlockPredicate(List<Block> blocks, BlockPos pos) {
		this.blocks = blocks;
		this.pos = pos;
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		Block block = structureWorldAccess.getBlockState(blockPos.add(this.pos)).getBlock();
		return this.blocks.contains(block);
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.MATCHING_BLOCKS;
	}
}
