package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableCopperBulbBlock extends CopperBulbBlock implements Oxidizable {
	public static final MapCodec<OxidizableCopperBulbBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(OxidizableCopperBulbBlock::getDegradationLevel), createSettingsCodec()
				)
				.apply(instance, OxidizableCopperBulbBlock::new)
	);
	private final Oxidizable.OxidationLevel oxidationLevel;

	@Override
	protected MapCodec<OxidizableCopperBulbBlock> getCodec() {
		return CODEC;
	}

	public OxidizableCopperBulbBlock(Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
		super(settings);
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
