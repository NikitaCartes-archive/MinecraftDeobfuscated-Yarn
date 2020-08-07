package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SoulSandValleySurfaceBuilder extends AbstractNetherSurfaceBuilder {
	private static final BlockState SOUL_SAND = Blocks.field_10114.getDefaultState();
	private static final BlockState SOUL_SOIL = Blocks.field_22090.getDefaultState();
	private static final BlockState GRAVEL = Blocks.field_10255.getDefaultState();
	private static final ImmutableList<BlockState> field_23924 = ImmutableList.of(SOUL_SAND, SOUL_SOIL);

	public SoulSandValleySurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Override
	protected ImmutableList<BlockState> method_27129() {
		return field_23924;
	}

	@Override
	protected ImmutableList<BlockState> method_27133() {
		return field_23924;
	}

	@Override
	protected BlockState method_27135() {
		return GRAVEL;
	}
}
