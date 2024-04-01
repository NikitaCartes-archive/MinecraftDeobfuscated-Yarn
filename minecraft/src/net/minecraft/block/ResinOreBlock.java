package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.BlockView;

public class ResinOreBlock extends ExperienceDroppingBlock {
	public static final MapCodec<ResinOreBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(IntProvider.createValidatingCodec(0, 10).fieldOf("experience").forGetter(block -> block.experienceDropped), createSettingsCodec())
				.apply(instance, ResinOreBlock::new)
	);

	@Override
	public MapCodec<ResinOreBlock> getCodec() {
		return CODEC;
	}

	public ResinOreBlock(IntProvider intProvider, AbstractBlock.Settings settings) {
		super(intProvider, settings);
	}

	@Override
	protected int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		return 2;
	}
}
