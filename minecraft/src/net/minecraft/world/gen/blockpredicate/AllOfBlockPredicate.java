package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

class AllOfBlockPredicate extends CombinedBlockPredicate {
	public static final Codec<AllOfBlockPredicate> CODEC = buildCodec(AllOfBlockPredicate::new);

	public AllOfBlockPredicate(List<BlockPredicate> list) {
		super(list);
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		for (BlockPredicate blockPredicate : this.predicates) {
			if (!blockPredicate.test(structureWorldAccess, blockPos)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.ALL_OF;
	}
}
