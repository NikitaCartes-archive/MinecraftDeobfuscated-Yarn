package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableTrapdoorBlock extends TrapdoorBlock implements Oxidizable {
	public static final MapCodec<OxidizableTrapdoorBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					BlockSetType.CODEC.fieldOf("block_set_type").forGetter(TrapdoorBlock::getBlockSetType),
					Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(OxidizableTrapdoorBlock::getDegradationLevel),
					createSettingsCodec()
				)
				.apply(instance, OxidizableTrapdoorBlock::new)
	);
	private final Oxidizable.OxidationLevel oxidationLevel;

	@Override
	public MapCodec<OxidizableTrapdoorBlock> getCodec() {
		return CODEC;
	}

	protected OxidizableTrapdoorBlock(BlockSetType type, Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
		super(type, settings);
		this.oxidationLevel = oxidationLevel;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.tickDegradation(state, world, pos, random);
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
	}

	public Oxidizable.OxidationLevel getDegradationLevel() {
		return this.oxidationLevel;
	}
}
