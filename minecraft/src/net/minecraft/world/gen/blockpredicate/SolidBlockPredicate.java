package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3i;

@Deprecated
public class SolidBlockPredicate extends OffsetPredicate {
	public static final MapCodec<SolidBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> registerOffsetField(instance).apply(instance, SolidBlockPredicate::new)
	);

	public SolidBlockPredicate(Vec3i vec3i) {
		super(vec3i);
	}

	@Override
	protected boolean test(BlockState state) {
		return state.isSolid();
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.SOLID;
	}
}
