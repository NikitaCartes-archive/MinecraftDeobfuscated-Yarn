package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ShipwreckFeature extends StructureFeature<ShipwreckFeatureConfig> {
	public ShipwreckFeature(Codec<ShipwreckFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<ShipwreckFeatureConfig> getStructureStartFactory() {
		return ShipwreckFeature.Start::new;
	}

	public static class Start extends StructureStart<ShipwreckFeatureConfig> {
		public Start(StructureFeature<ShipwreckFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			int i,
			int j,
			Biome biome,
			ShipwreckFeatureConfig shipwreckFeatureConfig
		) {
			BlockRotation blockRotation = BlockRotation.random(this.random);
			BlockPos blockPos = new BlockPos(i * 16, 90, j * 16);
			ShipwreckGenerator.addParts(structureManager, blockPos, blockRotation, this.children, this.random, shipwreckFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}
