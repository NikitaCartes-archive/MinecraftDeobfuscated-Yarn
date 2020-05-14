package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class class_5284 {
	private final ChunkGeneratorConfig config;
	protected BlockState defaultBlock = Blocks.STONE.getDefaultState();
	protected BlockState defaultFluid = Blocks.WATER.getDefaultState();

	public class_5284(ChunkGeneratorConfig config) {
		this.config = config;
	}

	public BlockState getDefaultBlock() {
		return this.defaultBlock;
	}

	public BlockState getDefaultFluid() {
		return this.defaultFluid;
	}

	public void setDefaultBlock(BlockState defaultBlock) {
		this.defaultBlock = defaultBlock;
	}

	public void setDefaultFluid(BlockState defaultFluid) {
		this.defaultFluid = defaultFluid;
	}

	public int getBedrockCeilingY() {
		return 0;
	}

	public int getBedrockFloorY() {
		return 256;
	}

	public ChunkGeneratorConfig getConfig() {
		return this.config;
	}
}
