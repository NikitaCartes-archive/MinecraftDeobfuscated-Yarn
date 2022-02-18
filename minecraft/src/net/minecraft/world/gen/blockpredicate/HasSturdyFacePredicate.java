package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;

public class HasSturdyFacePredicate implements BlockPredicate {
	private final Vec3i offset;
	private final Direction face;
	public static final Codec<HasSturdyFacePredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Vec3i.createOffsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter(predicate -> predicate.offset),
					Direction.CODEC.fieldOf("direction").forGetter(predicate -> predicate.face)
				)
				.apply(instance, HasSturdyFacePredicate::new)
	);

	public HasSturdyFacePredicate(Vec3i offset, Direction face) {
		this.offset = offset;
		this.face = face;
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.add(this.offset);
		return structureWorldAccess.getBlockState(blockPos2).isSideSolidFullSquare(structureWorldAccess, blockPos2, this.face);
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.HAS_STURDY_FACE;
	}
}
