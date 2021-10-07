package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class WouldSurviveBlockPredicate implements BlockPredicate {
	public static final Codec<WouldSurviveBlockPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockPos.CODEC.optionalFieldOf("offset", BlockPos.ORIGIN).forGetter(wouldSurviveBlockPredicate -> wouldSurviveBlockPredicate.pos),
					BlockState.CODEC.fieldOf("state").forGetter(wouldSurviveBlockPredicate -> wouldSurviveBlockPredicate.state)
				)
				.apply(instance, WouldSurviveBlockPredicate::new)
	);
	private final BlockPos pos;
	private final BlockState state;

	protected WouldSurviveBlockPredicate(BlockPos pos, BlockState state) {
		this.pos = pos;
		this.state = state;
	}

	public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
		return this.state.canPlaceAt(structureWorldAccess, blockPos.add(this.pos));
	}

	@Override
	public BlockPredicateType<?> getType() {
		return BlockPredicateType.WOULD_SURVIVE;
	}
}
