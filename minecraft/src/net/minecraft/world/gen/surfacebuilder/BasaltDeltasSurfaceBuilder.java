package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BasaltDeltasSurfaceBuilder extends AbstractNetherSurfaceBuilder {
	private static final BlockState BASALT = Blocks.field_22091.getDefaultState();
	private static final BlockState BLACKSTONE = Blocks.field_23869.getDefaultState();
	private static final BlockState GRAVEL = Blocks.field_10255.getDefaultState();
	private static final ImmutableList<BlockState> field_23918 = ImmutableList.of(BASALT, BLACKSTONE);
	private static final ImmutableList<BlockState> field_23919 = ImmutableList.of(BASALT);

	public BasaltDeltasSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Override
	protected ImmutableList<BlockState> method_27129() {
		return field_23918;
	}

	@Override
	protected ImmutableList<BlockState> method_27133() {
		return field_23919;
	}

	@Override
	protected BlockState method_27135() {
		return GRAVEL;
	}
}
