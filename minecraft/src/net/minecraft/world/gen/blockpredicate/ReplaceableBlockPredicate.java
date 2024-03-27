package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;

class ReplaceableBlockPredicate extends OffsetPredicate {
	public static final MapCodec<ReplaceableBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> registerOffsetField(instance).apply(instance, ReplaceableBlockPredicate::new)
	);

	public ReplaceableBlockPredicate(Vec3i vec3i) {
		super(vec3i);
	}

	@Override
	protected boolean test(BlockState state) {
		return state.isReplaceable();
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.REPLACEABLE;
	}
}
