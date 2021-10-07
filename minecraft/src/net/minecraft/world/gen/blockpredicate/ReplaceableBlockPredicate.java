package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

class ReplaceableBlockPredicate extends OffsetPredicate {
	public static final Codec<ReplaceableBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> method_39013(instance).apply(instance, ReplaceableBlockPredicate::new)
	);

	public ReplaceableBlockPredicate(BlockPos blockPos) {
		super(blockPos);
	}

	@Override
	protected boolean test(BlockState state) {
		return state.getMaterial().isReplaceable();
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.REPLACEABLE;
	}
}
