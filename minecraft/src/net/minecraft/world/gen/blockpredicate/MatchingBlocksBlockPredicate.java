package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;

class MatchingBlocksBlockPredicate extends OffsetPredicate {
	private final RegistryEntryList<Block> blocks;
	public static final Codec<MatchingBlocksBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> registerOffsetField(instance)
				.and(RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("blocks").forGetter(predicate -> predicate.blocks))
				.apply(instance, MatchingBlocksBlockPredicate::new)
	);

	public MatchingBlocksBlockPredicate(Vec3i offset, RegistryEntryList<Block> blocks) {
		super(offset);
		this.blocks = blocks;
	}

	@Override
	protected boolean test(BlockState state) {
		return state.isIn(this.blocks);
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.MATCHING_BLOCKS;
	}
}
