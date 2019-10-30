package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class TernarySurfaceConfig implements SurfaceConfig {
	private final BlockState topMaterial;
	private final BlockState underMaterial;
	private final BlockState underwaterMaterial;

	public TernarySurfaceConfig(BlockState topMaterial, BlockState underMaterial, BlockState underwaterMaterial) {
		this.topMaterial = topMaterial;
		this.underMaterial = underMaterial;
		this.underwaterMaterial = underwaterMaterial;
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
		BlockState blockState = (BlockState)dynamic.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState2 = (BlockState)dynamic.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		BlockState blockState3 = (BlockState)dynamic.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
		return new TernarySurfaceConfig(blockState, blockState2, blockState3);
	}
}
