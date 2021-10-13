package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.Products.P1;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public abstract class OffsetPredicate implements BlockPredicate {
	protected final BlockPos offset;

	protected static <P extends OffsetPredicate> P1<Mu<P>, BlockPos> registerOffsetField(Instance<P> instance) {
		return instance.group(BlockPos.CODEC.optionalFieldOf("offset", BlockPos.ORIGIN).forGetter(predicate -> predicate.offset));
	}

	protected OffsetPredicate(BlockPos offset) {
		this.offset = offset;
	}

	public final boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return this.test(structureWorldAccess.getBlockState(blockPos.add(this.offset)));
	}

	protected abstract boolean test(BlockState state);
}
