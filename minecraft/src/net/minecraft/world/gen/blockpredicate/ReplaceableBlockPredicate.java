package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;

class ReplaceableBlockPredicate extends OffsetPredicate {
	public static final Codec<ReplaceableBlockPredicate> CODEC = RecordCodecBuilder.create(
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
