package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class class_5284 {
	private final ChunkGeneratorConfig field_24516;
	protected BlockState field_24514 = Blocks.STONE.getDefaultState();
	protected BlockState field_24515 = Blocks.WATER.getDefaultState();

	public class_5284(ChunkGeneratorConfig chunkGeneratorConfig) {
		this.field_24516 = chunkGeneratorConfig;
	}

	public BlockState method_28005() {
		return this.field_24514;
	}

	public BlockState method_28006() {
		return this.field_24515;
	}

	public void method_28003(BlockState blockState) {
		this.field_24514 = blockState;
	}

	public void method_28004(BlockState blockState) {
		this.field_24515 = blockState;
	}

	public int getBedrockCeilingY() {
		return 0;
	}

	public int getBedrockFloorY() {
		return 256;
	}

	public ChunkGeneratorConfig method_28007() {
		return this.field_24516;
	}
}
