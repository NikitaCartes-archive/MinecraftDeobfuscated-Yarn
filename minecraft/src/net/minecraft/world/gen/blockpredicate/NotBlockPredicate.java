package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

class NotBlockPredicate implements BlockPredicate {
	public static final Codec<NotBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(BlockPredicate.BASE_CODEC.fieldOf("predicate").forGetter(predicate -> predicate.predicate))
				.apply(instance, NotBlockPredicate::new)
	);
	private final BlockPredicate predicate;

	public NotBlockPredicate(BlockPredicate predicate) {
		this.predicate = predicate;
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return !this.predicate.test(structureWorldAccess, blockPos);
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.NOT;
	}
}
