package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class FlatChunkGeneratorLayer {
	public static final Codec<FlatChunkGeneratorLayer> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(0, DimensionType.MAX_HEIGHT).fieldOf("height").forGetter(FlatChunkGeneratorLayer::getThickness),
					Registry.BLOCK.fieldOf("block").orElse(Blocks.AIR).forGetter(flatChunkGeneratorLayer -> flatChunkGeneratorLayer.getBlockState().getBlock())
				)
				.apply(instance, FlatChunkGeneratorLayer::new)
	);
	private final Block field_29566;
	private final int thickness;

	public FlatChunkGeneratorLayer(int thickness, Block block) {
		this.thickness = thickness;
		this.field_29566 = block;
	}

	public int getThickness() {
		return this.thickness;
	}

	public BlockState getBlockState() {
		return this.field_29566.getDefaultState();
	}

	public String toString() {
		return (this.thickness != 1 ? this.thickness + "*" : "") + Registry.BLOCK.getId(this.field_29566);
	}
}
