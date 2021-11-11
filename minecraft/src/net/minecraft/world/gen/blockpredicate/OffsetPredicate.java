package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.Products.P1;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;

public abstract class OffsetPredicate implements BlockPredicate {
	protected final Vec3i offset;

	protected static <P extends OffsetPredicate> P1<Mu<P>, Vec3i> registerOffsetField(Instance<P> instance) {
		return instance.group(Vec3i.createOffsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter(predicate -> predicate.offset));
	}

	protected OffsetPredicate(Vec3i offset) {
		this.offset = offset;
	}

	public final boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return this.test(structureWorldAccess.getBlockState(blockPos.add(this.offset)));
	}

	protected abstract boolean test(BlockState state);
}
