package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

class AlwaysTrueBlockPredicate implements BlockPredicate {
	public static AlwaysTrueBlockPredicate instance = new AlwaysTrueBlockPredicate();
	public static final Codec<AlwaysTrueBlockPredicate> CODEC = Codec.unit((Supplier<AlwaysTrueBlockPredicate>)(() -> instance));

	private AlwaysTrueBlockPredicate() {
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return true;
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.TRUE;
	}
}
