package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class TernarySurfaceConfig implements SurfaceConfig {
	private final BlockState topMaterial;
	private final BlockState underMaterial;
	private final BlockState underwaterMaterial;

	public TernarySurfaceConfig(BlockState blockState, BlockState blockState2, BlockState blockState3) {
		this.topMaterial = blockState;
		this.underMaterial = blockState2;
		this.underwaterMaterial = blockState3;
	}

	@Override
	public BlockState getTopMaterial() {
		return this.topMaterial;
	}

	@Override
	public BlockState getUnderMaterial() {
		return this.underMaterial;
	}

	public BlockState getUnderwaterMaterial() {
		return this.underwaterMaterial;
	}

	public static TernarySurfaceConfig deserialize(Dynamic<?> dynamic) {
		BlockState blockState = (BlockState)dynamic.get("top_material").map(BlockState::deserialize).orElse(Blocks.field_10124.getDefaultState());
		BlockState blockState2 = (BlockState)dynamic.get("under_material").map(BlockState::deserialize).orElse(Blocks.field_10124.getDefaultState());
		BlockState blockState3 = (BlockState)dynamic.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.field_10124.getDefaultState());
		return new TernarySurfaceConfig(blockState, blockState2, blockState3);
	}
}
