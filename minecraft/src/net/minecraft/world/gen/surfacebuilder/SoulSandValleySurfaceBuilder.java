package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.class_5164;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SoulSandValleySurfaceBuilder extends class_5164 {
	private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
	private static final BlockState SOUL_SOIL = Blocks.SOUL_SOIL.getDefaultState();
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final ImmutableList<BlockState> field_23924 = ImmutableList.of(SOUL_SAND, SOUL_SOIL);

	public SoulSandValleySurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
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
