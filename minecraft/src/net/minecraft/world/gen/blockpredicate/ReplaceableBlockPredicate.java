package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

class ReplaceableBlockPredicate implements BlockPredicate {
	public static final ReplaceableBlockPredicate INSTANCE = new ReplaceableBlockPredicate();
	public static final Codec<ReplaceableBlockPredicate> CODEC = Codec.unit((Supplier<ReplaceableBlockPredicate>)(() -> INSTANCE));

	private ReplaceableBlockPredicate() {
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return structureWorldAccess.getBlockState(blockPos).getMaterial().isReplaceable();
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.REPLACEABLE;
	}
}
