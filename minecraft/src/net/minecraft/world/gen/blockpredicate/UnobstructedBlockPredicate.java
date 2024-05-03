package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.StructureWorldAccess;

record UnobstructedBlockPredicate(Vec3i offset) implements BlockPredicate {
	public static MapCodec<UnobstructedBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(UnobstructedBlockPredicate::offset))
				.apply(instance, UnobstructedBlockPredicate::new)
	);

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.UNOBSTRUCTED;
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return structureWorldAccess.doesNotIntersectEntities(
			null, VoxelShapes.fullCube().offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ())
		);
	}
}
