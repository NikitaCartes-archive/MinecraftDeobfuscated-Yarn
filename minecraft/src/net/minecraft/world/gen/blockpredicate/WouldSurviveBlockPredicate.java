package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;

public class WouldSurviveBlockPredicate implements BlockPredicate {
	public static final Codec<WouldSurviveBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Vec3i.createOffsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter(predicate -> predicate.offset),
					BlockState.CODEC.fieldOf("state").forGetter(predicate -> predicate.state)
				)
				.apply(instance, WouldSurviveBlockPredicate::new)
	);
	private final Vec3i offset;
	private final BlockState state;

	protected WouldSurviveBlockPredicate(Vec3i offset, BlockState state) {
		this.offset = offset;
		this.state = state;
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return this.state.canPlaceAt(structureWorldAccess, blockPos.add(this.offset));
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.WOULD_SURVIVE;
	}
}
