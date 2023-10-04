package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ColorCode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class ColoredFallingBlock extends FallingBlock {
	public static final MapCodec<ColoredFallingBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(ColorCode.CODEC.fieldOf("falling_dust_color").forGetter(block -> block.color), createSettingsCodec())
				.apply(instance, ColoredFallingBlock::new)
	);
	private final ColorCode color;

	@Override
	public MapCodec<ColoredFallingBlock> getCodec() {
		return CODEC;
	}

	public ColoredFallingBlock(ColorCode color, AbstractBlock.Settings settings) {
		super(settings);
		this.color = color;
	}

	@Override
	public int getColor(BlockState state, BlockView world, BlockPos pos) {
		return this.color.rgba();
	}
}
