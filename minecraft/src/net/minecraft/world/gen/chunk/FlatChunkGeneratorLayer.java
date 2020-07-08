package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;

public class FlatChunkGeneratorLayer {
	public static final Codec<FlatChunkGeneratorLayer> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(0, 256).fieldOf("height").forGetter(FlatChunkGeneratorLayer::getThickness),
					Registry.BLOCK.fieldOf("block").orElse(Blocks.AIR).forGetter(flatChunkGeneratorLayer -> flatChunkGeneratorLayer.getBlockState().getBlock())
				)
				.apply(instance, FlatChunkGeneratorLayer::new)
	);
	private final BlockState blockState;
	private final int thickness;
	private int startY;

	public FlatChunkGeneratorLayer(int thickness, Block block) {
		this.thickness = thickness;
		this.blockState = block.getDefaultState();
	}

	public int getThickness() {
		return this.thickness;
	}

	public BlockState getBlockState() {
		return this.blockState;
	}

	public int getStartY() {
		return this.startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public String toString() {
		return (this.thickness != 1 ? this.thickness + "*" : "") + Registry.BLOCK.getId(this.blockState.getBlock());
	}
}
