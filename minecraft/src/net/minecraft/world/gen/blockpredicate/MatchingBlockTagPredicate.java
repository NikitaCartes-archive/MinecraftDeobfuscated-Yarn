package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

public class MatchingBlockTagPredicate extends OffsetPredicate {
	final TagKey<Block> tag;
	public static final Codec<MatchingBlockTagPredicate> CODEC = RecordCodecBuilder.create(
		instance -> registerOffsetField(instance)
				.and(TagKey.unprefixedCodec(Registry.BLOCK_KEY).fieldOf("tag").forGetter(predicate -> predicate.tag))
				.apply(instance, MatchingBlockTagPredicate::new)
	);

	protected MatchingBlockTagPredicate(Vec3i offset, TagKey<Block> tag) {
		super(offset);
		this.tag = tag;
	}

	@Override
	protected boolean test(BlockState state) {
		return state.isIn(this.tag);
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.MATCHING_BLOCK_TAG;
	}
}
