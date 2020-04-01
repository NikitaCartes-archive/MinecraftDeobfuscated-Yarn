package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.poi.PointOfInterestType;

public class OverworldChunkGeneratorConfig extends ChunkGeneratorConfig {
	private final int field_13224;
	private final int field_13223;
	private final int forcedBiome = -1;
	private final int field_13221;
	public static final List<BlockState> field_23567 = (List<BlockState>)Registry.BLOCK
		.stream()
		.filter(block -> !block.method_26477() && !block.hasBlockEntity())
		.map(Block::getDefaultState)
		.filter(blockState -> !PointOfInterestType.method_26442(blockState))
		.collect(ImmutableList.toImmutableList());
	public static final List<BlockState> field_23568 = (List<BlockState>)Registry.BLOCK
		.stream()
		.filter(block -> !block.method_26477() && !block.hasBlockEntity() && !block.hasDynamicBounds())
		.map(Block::getDefaultState)
		.filter(blockState -> !PointOfInterestType.method_26442(blockState) && blockState.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN))
		.collect(ImmutableList.toImmutableList());

	public OverworldChunkGeneratorConfig() {
		this.field_13221 = 63;
		this.field_13224 = 4;
		this.field_13223 = 4;
	}

	public OverworldChunkGeneratorConfig(Random random) {
		this.field_13221 = random.nextInt(128);
		this.field_13224 = random.nextInt(8) + 1;
		this.field_13223 = random.nextInt(8) + 1;
		this.defaultBlock = this.method_26576(random);
		this.defaultFluid = this.method_26575(random);
	}

	public int getBiomeSize() {
		return this.field_13224;
	}

	public int getRiverSize() {
		return this.field_13223;
	}

	public int getForcedBiome() {
		return -1;
	}

	@Override
	public int getBedrockFloorY() {
		return 0;
	}
}
