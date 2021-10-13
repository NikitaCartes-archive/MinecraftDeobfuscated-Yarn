package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

class TrueBlockPredicate implements BlockPredicate {
	public static TrueBlockPredicate INSTANCE = new TrueBlockPredicate();
	public static final Codec<TrueBlockPredicate> CODEC = Codec.unit((Supplier<TrueBlockPredicate>)(() -> INSTANCE));

	private TrueBlockPredicate() {
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return true;
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.TRUE;
	}
}
