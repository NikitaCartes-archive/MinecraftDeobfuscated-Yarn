package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

public class MatchingBlockTagPredicate extends OffsetPredicate {
	final Tag<Block> tag;
	public static final Codec<MatchingBlockTagPredicate> CODEC = RecordCodecBuilder.create(
		instance -> registerOffsetField(instance)
				.and(
					Tag.codec(() -> ServerTagManagerHolder.getTagManager().getOrCreateTagGroup(Registry.BLOCK_KEY))
						.fieldOf("tag")
						.forGetter(matchingBlockTagPredicate -> matchingBlockTagPredicate.tag)
				)
				.apply(instance, MatchingBlockTagPredicate::new)
	);

	protected MatchingBlockTagPredicate(Vec3i offset, Tag<Block> tag) {
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